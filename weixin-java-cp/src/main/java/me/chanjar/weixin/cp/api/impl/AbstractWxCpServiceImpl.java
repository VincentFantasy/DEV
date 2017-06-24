package me.chanjar.weixin.cp.api.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.StandardSessionManager;
import me.chanjar.weixin.common.session.WxSession;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.RandomUtils;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.common.util.http.*;
import me.chanjar.weixin.common.util.json.GsonHelper;
import me.chanjar.weixin.cp.api.*;
import me.chanjar.weixin.cp.bean.*;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public abstract class AbstractWxCpServiceImpl<H, P> implements WxCpService, RequestHttp<H, P> {
  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  private WxCpUserService userService = new WxCpUserServiceImpl(this);
  private WxCpDepartmentService departmentService = new WxCpDepartmentServiceImpl(this);
  private WxCpMediaService mediaService;
  private WxCpMenuService menuService;
  private WxCpOauth2Service oauth2Service;
  private WxCpTagService tagService;

  /**
   * 全局的是否正在刷新access token的锁
   */
  protected final Object globalAccessTokenRefreshLock = new Object();

  /**
   * 全局的是否正在刷新jsapi_ticket的锁
   */
  protected final Object globalJsapiTicketRefreshLock = new Object();

  protected WxCpConfigStorage configStorage;


  protected WxSessionManager sessionManager = new StandardSessionManager();
  /**
   * 临时文件目录
   */
  protected File tmpDirFile;
  private int retrySleepMillis = 1000;
  private int maxRetryTimes = 5;

  @Override
  public boolean checkSignature(String msgSignature, String timestamp, String nonce, String data) {
    try {
      return SHA1.gen(this.configStorage.getToken(), timestamp, nonce, data)
        .equals(msgSignature);
    } catch (Exception e) {
      this.log.error("Checking signature failed, and the reason is :" + e.getMessage());
      return false;
    }
  }

  @Override
  public String getAccessToken() throws WxErrorException {
    return getAccessToken(false);
  }


  @Override
  public String getJsapiTicket() throws WxErrorException {
    return getJsapiTicket(false);
  }

  @Override
  public String getJsapiTicket(boolean forceRefresh) throws WxErrorException {
    if (forceRefresh) {
      this.configStorage.expireJsapiTicket();
    }
    if (this.configStorage.isJsapiTicketExpired()) {
      synchronized (this.globalJsapiTicketRefreshLock) {
        if (this.configStorage.isJsapiTicketExpired()) {
          String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";
          String responseContent = execute(SimpleGetRequestExecutor.create(this), url, null);
          JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
          JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();
          String jsapiTicket = tmpJsonObject.get("ticket").getAsString();
          int expiresInSeconds = tmpJsonObject.get("expires_in").getAsInt();
          this.configStorage.updateJsapiTicket(jsapiTicket,
            expiresInSeconds);
        }
      }
    }
    return this.configStorage.getJsapiTicket();
  }

  @Override
  public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException {
    long timestamp = System.currentTimeMillis() / 1000;
    String noncestr = RandomUtils.getRandomStr();
    String jsapiTicket = getJsapiTicket(false);
    String signature = SHA1.genWithAmple(
      "jsapi_ticket=" + jsapiTicket,
      "noncestr=" + noncestr,
      "timestamp=" + timestamp,
      "url=" + url
    );
    WxJsapiSignature jsapiSignature = new WxJsapiSignature();
    jsapiSignature.setTimestamp(timestamp);
    jsapiSignature.setNonceStr(noncestr);
    jsapiSignature.setUrl(url);
    jsapiSignature.setSignature(signature);

    // Fixed bug
    jsapiSignature.setAppId(this.configStorage.getCorpId());

    return jsapiSignature;
  }

  @Override
  public WxCpMessageSendResult messageSend(WxCpMessage message) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send";
    return WxCpMessageSendResult.fromJson(this.post(url, message.toJson()));
  }

  @Override
  @Deprecated
  public void menuCreate(WxMenu menu) throws WxErrorException {
    menuCreate(this.configStorage.getAgentId(), menu);
  }

  @Override
  @Deprecated
  public void menuCreate(Integer agentId, WxMenu menu) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?agentid="
      + agentId;
    post(url, menu.toJson());
  }

  @Override
  @Deprecated
  public void menuDelete() throws WxErrorException {
    menuDelete(this.configStorage.getAgentId());
  }

  @Override
  @Deprecated
  public void menuDelete(Integer agentId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/delete?agentid=" + agentId;
    get(url, null);
  }

  @Override
  @Deprecated
  public WxMenu menuGet() throws WxErrorException {
    return menuGet(this.configStorage.getAgentId());
  }

  @Override
  @Deprecated
  public WxMenu menuGet(Integer agentId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/get?agentid=" + agentId;
    try {
      String resultContent = get(url, null);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据
      if (e.getError().getErrorCode() == 46003) {
        return null;
      }
      throw e;
    }
  }

  @Override
  @Deprecated
  public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream)
    throws WxErrorException, IOException {
    return mediaUpload(mediaType, FileUtils.createTmpFile(inputStream, UUID.randomUUID().toString(), fileType));
  }

  @Override
  @Deprecated
  public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?type=" + mediaType;
    return execute(MediaUploadRequestExecutor.create(this), url, file);
  }

  @Override
  @Deprecated
  public File mediaDownload(String mediaId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/media/get";
    return execute(
      MediaDownloadRequestExecutor.create(this,
        this.configStorage.getTmpDirFile()),
      url, "media_id=" + mediaId);
  }

  @Override
  @Deprecated
  public void userAuthenticated(String userId) throws WxErrorException {
    this.getUserService().authenticate(userId);
  }

  @Override
  @Deprecated
  public void userCreate(WxCpUser user) throws WxErrorException {
    this.getUserService().create(user);
  }

  @Override
  @Deprecated
  public void userUpdate(WxCpUser user) throws WxErrorException {
    this.getUserService().update(user);
  }

  @Override
  @Deprecated
  public void userDelete(String userid) throws WxErrorException {
    this.getUserService().delete(userid);
  }

  @Override
  @Deprecated
  public void userDelete(String[] userids) throws WxErrorException {
    this.getUserService().delete(userids);
  }

  @Override
  @Deprecated
  public WxCpUser userGet(String userid) throws WxErrorException {
    return this.getUserService().getById(userid);
  }

  @Override
  @Deprecated
  public List<WxCpUser> userList(Integer departId, Boolean fetchChild, Integer status) throws WxErrorException {
    return  this.getUserService().listByDepartment(departId, fetchChild, status);
  }

  @Override
  @Deprecated
  public List<WxCpUser> departGetUsers(Integer departId, Boolean fetchChild, Integer status) throws WxErrorException {
    return  this.getUserService().listSimpleByDepartment(departId, fetchChild, status);
  }

  @Override
  @Deprecated
  public String tagCreate(String tagName) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/create";
    JsonObject o = new JsonObject();
    o.addProperty("tagname", tagName);
    String responseContent = post(url, o.toString());
    JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
    return tmpJsonElement.getAsJsonObject().get("tagid").getAsString();
  }

  @Override
  @Deprecated
  public void tagUpdate(String tagId, String tagName) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/update";
    JsonObject o = new JsonObject();
    o.addProperty("tagid", tagId);
    o.addProperty("tagname", tagName);
    post(url, o.toString());
  }

  @Override
  @Deprecated
  public void tagDelete(String tagId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete?tagid=" + tagId;
    get(url, null);
  }

  @Override
  @Deprecated
  public List<WxCpTag> tagGet() throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/list";
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
    return WxCpGsonBuilder.INSTANCE.create()
      .fromJson(
        tmpJsonElement.getAsJsonObject().get("taglist"),
        new TypeToken<List<WxCpTag>>() {
        }.getType()
      );
  }

  @Override
  @Deprecated
  public List<WxCpUser> tagGetUsers(String tagId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?tagid=" + tagId;
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
    return WxCpGsonBuilder.INSTANCE.create()
      .fromJson(
        tmpJsonElement.getAsJsonObject().get("userlist"),
        new TypeToken<List<WxCpUser>>() {
        }.getType()
      );
  }

  @Override
  @Deprecated
  public void tagAddUsers(String tagId, List<String> userIds, List<String> partyIds) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("tagid", tagId);
    if (userIds != null) {
      JsonArray jsonArray = new JsonArray();
      for (String userId : userIds) {
        jsonArray.add(new JsonPrimitive(userId));
      }
      jsonObject.add("userlist", jsonArray);
    }
    if (partyIds != null) {
      JsonArray jsonArray = new JsonArray();
      for (String userId : partyIds) {
        jsonArray.add(new JsonPrimitive(userId));
      }
      jsonObject.add("partylist", jsonArray);
    }
    post(url, jsonObject.toString());
  }

  @Override
  @Deprecated
  public void tagRemoveUsers(String tagId, List<String> userIds) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("tagid", tagId);
    JsonArray jsonArray = new JsonArray();
    for (String userId : userIds) {
      jsonArray.add(new JsonPrimitive(userId));
    }
    jsonObject.add("userlist", jsonArray);
    post(url, jsonObject.toString());
  }

  @Override
  @Deprecated
  public String oauth2buildAuthorizationUrl(String state) {
    return this.oauth2buildAuthorizationUrl(
      this.configStorage.getOauth2redirectUri(),
      state
    );
  }

  @Override
  @Deprecated
  public String oauth2buildAuthorizationUrl(String redirectUri, String state) {
    String url = "https://open.weixin.qq.com/connect/oauth2/authorize?";
    url += "appid=" + this.configStorage.getCorpId();
    url += "&redirect_uri=" + URIUtil.encodeURIComponent(redirectUri);
    url += "&response_type=code";
    url += "&scope=snsapi_base";
    if (state != null) {
      url += "&state=" + state;
    }
    url += "#wechat_redirect";
    return url;
  }

  @Override
  @Deprecated
  public String[] oauth2getUserInfo(String code) throws WxErrorException {
    return oauth2getUserInfo(this.configStorage.getAgentId(), code);
  }

  @Override
  @Deprecated
  public String[] oauth2getUserInfo(Integer agentId, String code) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?"
      + "code=" + code
      + "&agentid=" + agentId;
    String responseText = get(url, null);
    JsonElement je = new JsonParser().parse(responseText);
    JsonObject jo = je.getAsJsonObject();
    return new String[]{GsonHelper.getString(jo, "UserId"), GsonHelper.getString(jo, "DeviceId"), GsonHelper.getString(jo, "OpenId")};
  }

  @Override
  public int invite(String userId, String inviteTips) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/invite/send";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("userid", userId);
    if (StringUtils.isNotEmpty(inviteTips)) {
      jsonObject.addProperty("invite_tips", inviteTips);
    }
    String responseContent = post(url, jsonObject.toString());
    JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
    return tmpJsonElement.getAsJsonObject().get("type").getAsInt();
  }

  @Override
  public String[] getCallbackIp() throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/getcallbackip";
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
    JsonArray jsonArray = tmpJsonElement.getAsJsonObject().get("ip_list").getAsJsonArray();
    String[] ips = new String[jsonArray.size()];
    for (int i = 0; i < jsonArray.size(); i++) {
      ips[i] = jsonArray.get(i).getAsString();
    }
    return ips;
  }

  @Override
  public String get(String url, String queryParam) throws WxErrorException {
    return execute(SimpleGetRequestExecutor.create(this), url, queryParam);
  }

  @Override
  public String post(String url, String postData) throws WxErrorException {
    return execute(SimplePostRequestExecutor.create(this), url, postData);
  }

  /**
   * 向微信端发送请求，在这里执行的策略是当发生access_token过期时才去刷新，然后重新执行请求，而不是全局定时请求
   */
  @Override
  public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
    int retryTimes = 0;
    do {
      try {
        return this.executeInternal(executor, uri, data);
      } catch (WxErrorException e) {
        if (retryTimes + 1 > this.maxRetryTimes) {
          this.log.warn("重试达到最大次数【{}】", this.maxRetryTimes);
          //最后一次重试失败后，直接抛出异常，不再等待
          throw new RuntimeException("微信服务端异常，超出重试次数");
        }

        WxError error = e.getError();
        /*
         * -1 系统繁忙, 1000ms后重试
         */
        if (error.getErrorCode() == -1) {
          int sleepMillis = this.retrySleepMillis * (1 << retryTimes);
          try {
            this.log.debug("微信系统繁忙，{} ms 后重试(第{}次)", sleepMillis, retryTimes + 1);
            Thread.sleep(sleepMillis);
          } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
          }
        } else {
          throw e;
        }
      }
    } while (retryTimes++ < this.maxRetryTimes);

    this.log.warn("重试达到最大次数【{}】", this.maxRetryTimes);
    throw new RuntimeException("微信服务端异常，超出重试次数");
  }

  protected synchronized <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
    if (uri.contains("access_token=")) {
      throw new IllegalArgumentException("uri参数中不允许有access_token: " + uri);
    }
    String accessToken = getAccessToken(false);

    String uriWithAccessToken = uri + (uri.contains("?") ? "&" : "?") + "access_token=" + accessToken;

    try {
      T result = executor.execute(uriWithAccessToken, data);
      this.log.debug("\n[URL]:  {}\n[PARAMS]: {}\n[RESPONSE]: {}", uriWithAccessToken, data, result);
      return result;
    } catch (WxErrorException e) {
      WxError error = e.getError();
      /*
       * 发生以下情况时尝试刷新access_token
       * 40001 获取access_token时AppSecret错误，或者access_token无效
       * 42001 access_token超时
       * 40014 不合法的access_token，请开发者认真比对access_token的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口
       */
      if (error.getErrorCode() == 42001 || error.getErrorCode() == 40001 || error.getErrorCode() == 40014) {
        // 强制设置wxCpConfigStorage它的access token过期了，这样在下一次请求里就会刷新access token
        this.configStorage.expireAccessToken();
        return execute(executor, uri, data);
      }

      if (error.getErrorCode() != 0) {
        this.log.error("\n[URL]:  {}\n[PARAMS]: {}\n[RESPONSE]: {}", uriWithAccessToken, data, error);
        throw new WxErrorException(error);
      }
      return null;
    } catch (IOException e) {
      this.log.error("\n[URL]:  {}\n[PARAMS]: {}\n[EXCEPTION]: {}", uriWithAccessToken, data, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setWxCpConfigStorage(WxCpConfigStorage wxConfigProvider) {
    this.configStorage = wxConfigProvider;
    this.initHttp();
  }

  @Override
  public void setRetrySleepMillis(int retrySleepMillis) {
    this.retrySleepMillis = retrySleepMillis;
  }


  @Override
  public void setMaxRetryTimes(int maxRetryTimes) {
    this.maxRetryTimes = maxRetryTimes;
  }

  @Override
  public WxSession getSession(String id) {
    if (this.sessionManager == null) {
      return null;
    }
    return this.sessionManager.getSession(id);
  }

  @Override
  public WxSession getSession(String id, boolean create) {
    if (this.sessionManager == null) {
      return null;
    }
    return this.sessionManager.getSession(id, create);
  }

  @Override
  public void setSessionManager(WxSessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @Override
  public String replaceParty(String mediaId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceparty";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("media_id", mediaId);
    return post(url, jsonObject.toString());
  }

  @Override
  public String replaceUser(String mediaId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceuser";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("media_id", mediaId);
    return post(url, jsonObject.toString());
  }

  @Override
  public String getTaskResult(String joinId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?jobid=" + joinId;
    return get(url, null);
  }

  public File getTmpDirFile() {
    return this.tmpDirFile;
  }

  public void setTmpDirFile(File tmpDirFile) {
    this.tmpDirFile = tmpDirFile;
  }

  @Override
  public WxCpDepartmentService getDepartmentService() {
    return departmentService;
  }

  @Override
  public WxCpMediaService getMediaService() {
    return mediaService;
  }

  @Override
  public WxCpMenuService getMenuService() {
    return menuService;
  }

  @Override
  public WxCpOauth2Service getOauth2Service() {
    return oauth2Service;
  }

  @Override
  public WxCpTagService getTagService() {
    return tagService;
  }

  @Override
  public WxCpUserService getUserService() {
    return userService;
  }

  @Override
  @Deprecated
  public Integer departCreate(WxCpDepart depart) throws WxErrorException {
    return this.getDepartmentService().create(depart);
  }

  @Override
  @Deprecated
  public void departUpdate(WxCpDepart depart) throws WxErrorException {
    this.getDepartmentService().update(depart);
  }

  @Override
  @Deprecated
  public void departDelete(Integer departId) throws WxErrorException {
    this.getDepartmentService().delete(departId);
  }

  @Override
  @Deprecated
  public List<WxCpDepart> departGet() throws WxErrorException {
    return this.getDepartmentService().listAll();
  }
}
