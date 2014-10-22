package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.bean.WxAccessToken;

/**
 * 微信客户端配置存储
 * @author chanjarster
 *
 */
public interface WxMpConfigStorage {

  public void updateAccessToken(WxAccessToken accessToken);
  
  public void updateAccessToken(String accessToken, int expiresIn);
  
  public String getAccessToken();
  
  public String getAppId();
  
  public String getSecret();
  
  public String getToken();

  public String getAesKey();

  public int getExpiresIn();

}
