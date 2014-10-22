package me.chanjar.weixin.common.util.http;

import java.io.IOException;

import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * 简单的POST请求执行器，请求的参数是String, 返回的结果也是String
 * @author Daniel Qian
 *
 */
public class SimplePostRequestExecutor implements RequestExecutor<String, String> {

  @Override
  public String execute(String uri, String postEntity) throws WxErrorException, ClientProtocolException, IOException {
    HttpPost httpPost = new HttpPost(uri);
    if (postEntity != null) {
      StringEntity entity = new StringEntity(postEntity, Consts.UTF_8);
      httpPost.setEntity(entity);
    }
    CloseableHttpResponse response = httpclient.execute(httpPost);
    String responseContent = Utf8ResponseHandler.INSTANCE.handleResponse(response);
    WxError error = WxError.fromJson(responseContent);
    if (error.getErrorCode() != 0) {
      throw new WxErrorException(error);
    }
    return responseContent;
  }

}
