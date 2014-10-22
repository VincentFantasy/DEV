package me.chanjar.weixin.mp.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.common.util.http.*;
import me.chanjar.weixin.common.util.json.GsonHelper;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.result.*;
import me.chanjar.weixin.mp.util.http.QrCodeRequestExecutor;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class WxMpServiceImpl implements WxMpService {

  /**
   * 全局的是否正在刷新Access Token的flag
   * true: 正在刷新
   * false: 没有刷新
   */
  protected static final AtomicBoolean GLOBAL_ACCESS_TOKEN_REFRESH_FLAG = new AtomicBoolean(false);
  
  protected static final CloseableHttpClient httpclient = HttpClients.createDefault();
  
  protected WxMpConfigStorage wxMpConfigStorage;
  
  protected final ThreadLocal<Integer> retryTimes = new ThreadLocal<Integer>();

  public boolean checkSignature(String timestamp, String nonce, String signature) {
    try {
      return SHA1.gen(wxMpConfigStorage.getToken(), timestamp, nonce).equals(signature);
    } catch (Exception e) {
      return false;
    }
  }
  
  public void accessTokenRefresh() throws WxErrorException {
    if (!GLOBAL_ACCESS_TOKEN_REFRESH_FLAG.getAndSet(true)) {
      try {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
            + "&appid=" + wxMpConfigStorage.getAppId()
            + "&secret=" + wxMpConfigStorage.getSecret()
            ;
        try {
          HttpGet httpGet = new HttpGet(url);
          CloseableHttpResponse response = httpclient.execute(httpGet);
          String resultContent = new BasicResponseHandler().handleResponse(response);
          WxError error = WxError.fromJson(resultContent);
          if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
          }
          WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
          wxMpConfigStorage.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
        } catch (ClientProtocolException e) {
          throw new RuntimeException(e);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } finally {
        GLOBAL_ACCESS_TOKEN_REFRESH_FLAG.set(false);
      }
    } else {
      // 每隔100ms检查一下是否刷新完毕了
      while (GLOBAL_ACCESS_TOKEN_REFRESH_FLAG.get()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
      }
      // 刷新完毕了，就没他什么事儿了
    }
  }
  
  public void customMessageSend(WxMpCustomMessage message) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
    execute(new SimplePostRequestExecutor(), url, message.toJson());
  }
  
  public void menuCreate(WxMenu menu) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/menu/create";
    execute(new SimplePostRequestExecutor(), url, menu.toJson());
  }
  
  public void menuDelete() throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/menu/delete";
    execute(new SimpleGetRequestExecutor(), url, null);
  }

  public WxMenu menuGet() throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/menu/get";
    try {
      String resultContent = execute(new SimpleGetRequestExecutor(), url, null);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据
      if (e.getError().getErrorCode() == 46003) {
        return null;
      }
      throw e;
    }
  }

  public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream) throws WxErrorException, IOException {
    return mediaUpload(mediaType,FileUtils.createTmpFile(inputStream, UUID.randomUUID().toString(), fileType));
  }
  
  public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException {
    String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?type=" + mediaType;
    return execute(new MediaUploadRequestExecutor(), url, file);
  }
  
  public File mediaDownload(String media_id) throws WxErrorException {
    String url = "http://file.api.weixin.qq.com/cgi-bin/media/get";
    return execute(new MediaDownloadRequestExecutor(), url, "media_id=" + media_id);
  }

  public WxMpMassUploadResult massNewsUpload(WxMpMassNews news) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews";
    String responseContent = execute(new SimplePostRequestExecutor(), url, news.toJson());
    return WxMpMassUploadResult.fromJson(responseContent);
  }
  
  public WxMpMassUploadResult massVideoUpload(WxMpMassVideo video) throws WxErrorException {
    String url = "http://file.api.weixin.qq.com/cgi-bin/media/uploadvideo";
    String responseContent = execute(new SimplePostRequestExecutor(), url, video.toJson());
    return WxMpMassUploadResult.fromJson(responseContent);
  }
  
  public WxMpMassSendResult massGroupMessageSend(WxMpMassGroupMessage message) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall";
    String responseContent = execute(new SimplePostRequestExecutor(), url, message.toJson());
    return WxMpMassSendResult.fromJson(responseContent);
  }

  public WxMpMassSendResult massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send";
    String responseContent = execute(new SimplePostRequestExecutor(), url, message.toJson());
    return WxMpMassSendResult.fromJson(responseContent);
  }
  
  public WxMpGroup groupCreate(String name) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/groups/create";
    JsonObject json = new JsonObject();
    JsonObject groupJson = new JsonObject();
    json.add("group", groupJson);
    groupJson.addProperty("name", name);
    
    String responseContent = execute(
        new SimplePostRequestExecutor(), 
        url, 
        json.toString());
    return WxMpGroup.fromJson(responseContent);
  }

  public List<WxMpGroup> groupGet() throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/groups/get";
    String responseContent = execute(new SimpleGetRequestExecutor(), url, null);
    /*
     * 操蛋的微信API，创建时返回的是 { group : { id : ..., name : ...} }
     * 查询时返回的是 { groups : [ { id : ..., name : ..., count : ... }, ... ] }
     */
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxMpGsonBuilder.INSTANCE.create().fromJson(tmpJsonElement.getAsJsonObject().get("groups"), new TypeToken<List<WxMpGroup>>(){}.getType());
  }
  
  public long userGetGroup(String openid) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/groups/getid";
    JsonObject o = new JsonObject();
    o.addProperty("openid", openid);
    String responseContent = execute(new SimplePostRequestExecutor(), url, o.toString());
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return GsonHelper.getAsLong(tmpJsonElement.getAsJsonObject().get("groupid"));
  }
  
  public void groupUpdate(WxMpGroup group) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/groups/update";
    execute(new SimplePostRequestExecutor(), url, group.toJson());
  }
  
  public void userUpdateGroup(String openid, long to_groupid) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/groups/members/update";
    JsonObject json = new JsonObject();
    json.addProperty("openid", openid);
    json.addProperty("to_groupid", to_groupid);
    execute(new SimplePostRequestExecutor(), url, json.toString());
  }
  
  public void userUpdateRemark(String openid, String remark) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark";
    JsonObject json = new JsonObject();
    json.addProperty("openid", openid);
    json.addProperty("remark", remark);
    execute(new SimplePostRequestExecutor(), url, json.toString());
  }
  
  public WxMpUser userInfo(String openid, String lang) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/user/info";
    lang = lang == null ? "zh_CN" : lang;
    String responseContent = execute(new SimpleGetRequestExecutor(), url, "openid=" + openid + "&lang=" + lang);
    return WxMpUser.fromJson(responseContent);
  }
  
  public WxMpUserList userList(String next_openid) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/user/get";
    String responseContent = execute(new SimpleGetRequestExecutor(), url, next_openid == null ? null : "next_openid=" + next_openid);
    return WxMpUserList.fromJson(responseContent);
  }
  
  public WxMpQrCodeTicket qrCodeCreateTmpTicket(int scene_id, Integer expire_seconds) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    JsonObject json = new JsonObject();
    json.addProperty("action_name", "QR_SCENE");
    if(expire_seconds != null) {
      json.addProperty("expire_seconds", expire_seconds);
    }
    JsonObject actionInfo = new JsonObject();
    JsonObject scene = new JsonObject();
    scene.addProperty("scene_id", scene_id);
    actionInfo.add("scene", scene);
    json.add("action_info", actionInfo);
    String responseContent = execute(new SimplePostRequestExecutor(), url, json.toString());
    return WxMpQrCodeTicket.fromJson(responseContent);
  }
  
  public WxMpQrCodeTicket qrCodeCreateLastTicket(int scene_id) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    JsonObject json = new JsonObject();
    json.addProperty("action_name", "QR_LIMIT_SCENE");
    JsonObject actionInfo = new JsonObject();
    JsonObject scene = new JsonObject();
    scene.addProperty("scene_id", scene_id);
    actionInfo.add("scene", scene);
    json.add("action_info", actionInfo);
    String responseContent = execute(new SimplePostRequestExecutor(), url, json.toString());
    return WxMpQrCodeTicket.fromJson(responseContent);
  }
  
  public File qrCodePicture(WxMpQrCodeTicket ticket) throws WxErrorException {
    String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode";
    return execute(new QrCodeRequestExecutor(), url, ticket);
  }
  
  public String shortUrl(String long_url) throws WxErrorException {
    String url = "https://api.weixin.qq.com/cgi-bin/shorturl";
    JsonObject o = new JsonObject();
    o.addProperty("action", "long2short");
    o.addProperty("long_url", long_url);
    String responseContent = execute(new SimplePostRequestExecutor(), url, o.toString());
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return tmpJsonElement.getAsJsonObject().get("short_url").getAsString();
  }
  
  /**
   * 向微信端发送请求，在这里执行的策略是当发生access_token过期时才去刷新，然后重新执行请求，而不是全局定时请求
   * @param executor
   * @param uri
   * @param data
   * @return
   * @throws WxErrorException
   */
  public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
    if (StringUtils.isBlank(wxMpConfigStorage.getAccessToken())) {
      accessTokenRefresh();
    }
    String accessToken = wxMpConfigStorage.getAccessToken();
    
    String uriWithAccessToken = uri;
    uriWithAccessToken += uri.indexOf('?') == -1 ? "?access_token=" + accessToken : "&access_token=" + accessToken;
    
    try {
      return executor.execute(uriWithAccessToken, data);
    } catch (WxErrorException e) {
      WxError error = e.getError();
      /*
       * 发生以下情况时尝试刷新access_token
       * 40001 获取access_token时AppSecret错误，或者access_token无效
       * 42001 access_token超时
       */
      if (error.getErrorCode() == 42001 || error.getErrorCode() == 40001) {
        accessTokenRefresh();
        return execute(executor, uri, data);
      }
      /**
       * -1 系统繁忙, 1000ms后重试
       */
      if (error.getErrorCode() == -1) {
        if(retryTimes.get() == null) {
          retryTimes.set(0);
        }
        if (retryTimes.get() > 4) {
          retryTimes.set(0);
          throw new RuntimeException("微信服务端异常，超出重试次数");
        }
        int sleepMillis = 1000 *  (1 << retryTimes.get());
        try {
          System.out.println("微信系统繁忙，" + sleepMillis + "ms后重试");
          Thread.sleep(sleepMillis);
          retryTimes.set(retryTimes.get() + 1);
          return execute(executor, uri, data);
        } catch (InterruptedException e1) {
          throw new RuntimeException(e1);
        }
      }
      if (error.getErrorCode() != 0) {
        throw new WxErrorException(error);
      }
      return null;
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void setWxMpConfigStorage(WxMpConfigStorage wxConfigProvider) {
    this.wxMpConfigStorage = wxConfigProvider;
  }

}
