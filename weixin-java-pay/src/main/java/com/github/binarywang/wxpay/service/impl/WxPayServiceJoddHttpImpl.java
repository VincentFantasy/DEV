package com.github.binarywang.wxpay.service.impl;

import com.github.binarywang.wxpay.bean.WxPayApiData;
import com.github.binarywang.wxpay.exception.WxPayException;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.http.ProxyInfo.ProxyType;
import jodd.http.net.SSLSocketHttpConnectionProvider;
import jodd.http.net.SocketHttpConnectionProvider;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;

/**
 * 微信支付请求实现类，jodd-http实现
 * Created by Binary Wang on 2016/7/28.
 *
 * @author binarywang (https://github.com/binarywang)
 */
public class WxPayServiceJoddHttpImpl extends WxPayServiceAbstractImpl {

  @Override
  protected String post(String url, String requestStr, boolean useKey) throws WxPayException {
    try {
      HttpRequest request = HttpRequest
        .post(url)
        .timeout(this.getConfig().getHttpTimeout())
        .connectionTimeout(this.getConfig().getHttpConnectionTimeout())
        .bodyText(requestStr);

      if (useKey) {
        SSLContext sslContext = this.getConfig().getSslContext();
        if (null == sslContext) {
          sslContext = this.getConfig().initSSLContext();
        }
        final SSLSocketHttpConnectionProvider provider = new SSLSocketHttpConnectionProvider(sslContext);
        request.withConnectionProvider(provider);
      }

      if (StringUtils.isNotBlank(this.getConfig().getHttpProxyHost()) && this.getConfig().getHttpProxyPort() > 0) {
        ProxyInfo httpProxy = new ProxyInfo(ProxyType.HTTP, this.getConfig().getHttpProxyHost(), this.getConfig().getHttpProxyPort(),
          this.getConfig().getHttpProxyUsername(), this.getConfig().getHttpProxyPassword());
        HttpConnectionProvider provider = request.connectionProvider();
        if (null == provider) {
          provider = new SocketHttpConnectionProvider();
        }
        provider.useProxy(httpProxy);
        request.withConnectionProvider(provider);
      }

      String responseString = this.getResponseString(request.send());

      this.log.info("\n【请求地址】：{}\n【请求数据】：{}\n【响应数据】：{}", url, requestStr, responseString);
      wxApiData.set(new WxPayApiData(url, requestStr, responseString, null));
      return responseString;
    } catch (Exception e) {
      this.log.error("\n【请求地址】：{}\n【请求数据】：{}\n【异常信息】：{}", url, requestStr, e.getMessage());
      wxApiData.set(new WxPayApiData(url, requestStr, null, e.getMessage()));
      throw new WxPayException(e.getMessage(), e);
    }
  }

  private String getResponseString(HttpResponse response) throws WxPayException {
    try {
      this.log.debug("【微信服务器响应头信息】：\n{}", response.toString(false));
    } catch (NullPointerException e) {
      throw new WxPayException("response.toString() 居然抛出空指针异常了", e);
    }

    String responseString = response.bodyText();

    if (StringUtils.isBlank(responseString)) {
      throw new WxPayException("响应信息为空");
    }

    if (StringUtils.isBlank(response.charset())) {
      responseString = new String(responseString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    return responseString;
  }


}
