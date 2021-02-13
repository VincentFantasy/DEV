package me.chanjar.weixin.cp.config.impl;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.bean.WxCpProviderToken;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于内存的微信配置provider，在实际生产环境中应该将这些配置持久化.
 *
 * @author someone
 */
public class WxCpTpDefaultConfigImpl implements WxCpTpConfigStorage, Serializable {
  private static final long serialVersionUID = 6678780920621872824L;

  private volatile String corpId;
  private volatile String corpSecret;
  /**
   * 服务商secret
   */
  private volatile String providerSecret;
  private volatile String providerToken;
  private volatile long providerTokenExpiresTime;

  private volatile String suiteId;
  private volatile String suiteSecret;

  private volatile String token;
  private volatile String suiteAccessToken;
  private volatile long suiteAccessTokenExpiresTime;
  private volatile String aesKey;

  private volatile String suiteTicket;
  private volatile long suiteTicketExpiresTime;
  private volatile String oauth2redirectUri;

  private volatile Map<String, String> authCorpAccessTokenMap = new HashMap<>();
  private volatile Map<String, Long> authCorpAccessTokenExpireTimeMap = new HashMap<>();

  private volatile Map<String, String> authCorpJsApiTicketMap = new HashMap<>();
  private volatile Map<String, Long> authCorpJsApiTicketExpireTimeMap = new HashMap<>();

  private volatile Map<String, String> authSuiteJsApiTicketMap = new HashMap<>();
  private volatile Map<String, Long> authSuiteJsApiTicketExpireTimeMap = new HashMap<>();

  private volatile String httpProxyHost;
  private volatile int httpProxyPort;
  private volatile String httpProxyUsername;
  private volatile String httpProxyPassword;

  private volatile File tmpDirFile;

  private volatile ApacheHttpClientBuilder apacheHttpClientBuilder;

  private volatile String baseApiUrl;

  // locker
  private final transient Map<String, Lock> providerAccessTokenLocker = new ConcurrentHashMap<>();
  private final transient Map<String, Lock> suiteAccessTokenLocker = new ConcurrentHashMap<>();
  private final transient Map<String, Lock> accessTokenLocker = new ConcurrentHashMap<>();
  private final transient Map<String, Lock> authCorpJsapiTicketLocker = new ConcurrentHashMap<>();
  private final transient Map<String, Lock> authSuiteJsapiTicketLocker = new ConcurrentHashMap<>();

  @Override
  public void setBaseApiUrl(String baseUrl) {
    this.baseApiUrl = baseUrl;
  }

  @Override
  public String getApiUrl(String path) {
    if (baseApiUrl == null) {
      baseApiUrl = "https://qyapi.weixin.qq.com";
    }
    return baseApiUrl + path;
  }

  @Override
  public String getSuiteAccessToken() {
    return this.suiteAccessToken;
  }

  @Override
  public WxAccessToken getSuiteAccessTokenEntity() {
    WxAccessToken accessToken = new WxAccessToken();
    int expiresIn = Math.toIntExact((this.suiteAccessTokenExpiresTime - System.currentTimeMillis()) / 1000L);
    accessToken.setExpiresIn(expiresIn <= 0 ? -1 : expiresIn);
    accessToken.setAccessToken(this.suiteAccessToken);
    return accessToken;
  }

  public void setSuiteAccessToken(String suiteAccessToken) {
    this.suiteAccessToken = suiteAccessToken;
  }

  @Override
  public boolean isSuiteAccessTokenExpired() {
    return System.currentTimeMillis() > this.suiteAccessTokenExpiresTime;
  }

  @Override
  public void expireSuiteAccessToken() {
    this.suiteAccessTokenExpiresTime = 0;
  }

  @Override
  public synchronized void updateSuiteAccessToken(WxAccessToken suiteAccessToken) {
    updateSuiteAccessToken(suiteAccessToken.getAccessToken(), suiteAccessToken.getExpiresIn());
  }

  @Override
  public synchronized void updateSuiteAccessToken(String suiteAccessToken, int expiresInSeconds) {
    this.suiteAccessToken = suiteAccessToken;
    this.suiteAccessTokenExpiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
  }

  @Deprecated
  public void setSuiteAccessTokenExpiresTime(long suiteAccessTokenExpiresTime) {
    this.suiteAccessTokenExpiresTime = suiteAccessTokenExpiresTime;
  }

  @Override
  public String getSuiteTicket() {
    return this.suiteTicket;
  }

  @Override
  public boolean isSuiteTicketExpired() {
    return System.currentTimeMillis() > this.suiteTicketExpiresTime;
  }

  @Override
  public void expireSuiteTicket() {
    this.suiteTicketExpiresTime = 0;
  }

  @Override
  public synchronized void updateSuiteTicket(String suiteTicket, int expiresInSeconds) {
    this.suiteTicket = suiteTicket;
    // 预留200秒的时间
    this.suiteTicketExpiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
  }


  @Deprecated
  public void setSuiteTicket(String suiteTicket) {
    this.suiteTicket = suiteTicket;
  }

  @Deprecated
  public long getSuiteTicketExpiresTime() {
    return this.suiteTicketExpiresTime;
  }

  @Deprecated
  public void setSuiteTicketExpiresTime(long suiteTicketExpiresTime) {
    this.suiteTicketExpiresTime = suiteTicketExpiresTime;
  }

  @Override
  public String getSuiteId() {
    return this.suiteId;
  }

  @Deprecated
  public void setSuiteId(String corpId) {
    this.suiteId = corpId;
  }

  @Override
  public String getSuiteSecret() {
    return this.suiteSecret;
  }

  @Deprecated
  public void setSuiteSecret(String corpSecret) {
    this.suiteSecret = corpSecret;
  }

  @Override
  public String getToken() {
    return this.token;
  }

  @Deprecated
  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public String getAesKey() {
    return this.aesKey;
  }

  @Deprecated
  public void setAesKey(String aesKey) {
    this.aesKey = aesKey;
  }


  @Override
  public String getCorpId() {
    return this.corpId;
  }

  @Deprecated
  public void setCorpId(String corpId) {
    this.corpId = corpId;
  }

  @Override
  public String getCorpSecret() {
    return this.corpSecret;
  }

  @Override
  public String getProviderSecret() {
    return providerSecret;
  }

  @Deprecated
  public void setCorpSecret(String corpSecret) {
    this.corpSecret = corpSecret;
  }


  @Override
  public String getAccessToken(String authCorpId) {
    return authCorpAccessTokenMap.get(authCorpId);
  }

  @Override
  public WxAccessToken getAccessTokenEntity(String authCorpId) {
    String accessToken = authCorpAccessTokenMap.getOrDefault(authCorpId, StringUtils.EMPTY);
    Long expire = authCorpAccessTokenExpireTimeMap.getOrDefault(authCorpId, 0L);
    WxAccessToken accessTokenEntity = new WxAccessToken();
    accessTokenEntity.setAccessToken(accessToken);
    accessTokenEntity.setExpiresIn(Math.toIntExact(expire));
    return accessTokenEntity;
  }

  @Override
  public boolean isAccessTokenExpired(String authCorpId) {
    return System.currentTimeMillis() > authCorpAccessTokenExpireTimeMap.get(authCorpId);
  }

	@Override
	public void expireAccessToken(String authCorpId) {
    authCorpAccessTokenMap.remove(authCorpId);
    authCorpAccessTokenExpireTimeMap.remove(authCorpId);
	}

	@Override
  public void updateAccessToken(String authCorpId, String accessToken, int expiredInSeconds) {
    authCorpAccessTokenMap.put(authCorpId, accessToken);
    // 预留200秒的时间
    authCorpAccessTokenExpireTimeMap.put(authCorpId, System.currentTimeMillis() + (expiredInSeconds - 200) * 1000L);
  }


  @Override
  public String getAuthCorpJsApiTicket(String authCorpId) {
    return this.authCorpJsApiTicketMap.get(authCorpId);
  }

  @Override
  public boolean isAuthCorpJsApiTicketExpired(String authCorpId) {
    Long t = this.authCorpJsApiTicketExpireTimeMap.get(authCorpId);
    if (t == null) {
      return System.currentTimeMillis() > t;
    } else {
      return true;
    }
  }

  @Override
  public void expireAuthCorpJsApiTicket(String authCorpId) {
    this.authCorpJsApiTicketMap.remove(authCorpId);
    this.authCorpJsApiTicketExpireTimeMap.remove(authCorpId);
  }

  @Override
  public void updateAuthCorpJsApiTicket(String authCorpId, String jsApiTicket, int expiredInSeconds) {
    // 应该根据不同的授权企业做区分
    authCorpJsApiTicketMap.put(authCorpId, jsApiTicket);
    // 预留200秒的时间
    authCorpJsApiTicketExpireTimeMap.put(authCorpId, System.currentTimeMillis() + (expiredInSeconds - 200) * 1000L);
  }

  @Override
  public String getAuthSuiteJsApiTicket(String authCorpId) {
    return authSuiteJsApiTicketMap.get(authCorpId);
  }

  @Override
  public boolean isAuthSuiteJsApiTicketExpired(String authCorpId) {
    Long t = authSuiteJsApiTicketExpireTimeMap.get(authCorpId);
    if (t == null) {
      return System.currentTimeMillis() > t;
    } else {
      return true;
    }
  }

  @Override
  public void expireAuthSuiteJsApiTicket(String authCorpId) {
    this.authSuiteJsApiTicketMap.remove(authCorpId);
    this.authSuiteJsApiTicketExpireTimeMap.remove(authCorpId);
  }

  @Override
  public void updateAuthSuiteJsApiTicket(String authCorpId, String jsApiTicket, int expiredInSeconds) {
    // 应该根据不同的授权企业做区分
    authSuiteJsApiTicketMap.put(authCorpId, jsApiTicket);
    // 预留200秒的时间
    authSuiteJsApiTicketExpireTimeMap.put(authCorpId, System.currentTimeMillis() + (expiredInSeconds - 200) * 1000L);
  }

  @Override
  public boolean isProviderTokenExpired() {
    return System.currentTimeMillis() > providerTokenExpiresTime;
  }

  @Override
  public void updateProviderToken(String providerToken, int expiredInSeconds) {
    this.providerToken = providerToken;
    this.providerTokenExpiresTime = System.currentTimeMillis() + expiredInSeconds * 1000L;
  }

  @Override
  public String getProviderToken() {
    return providerToken;
  }

  @Override
  public WxCpProviderToken getProviderTokenEntity() {
    return null;
  }

  @Override
  public void expireProviderToken() {
    this.providerTokenExpiresTime = 0L;
  }

  public void setOauth2redirectUri(String oauth2redirectUri) {
    this.oauth2redirectUri = oauth2redirectUri;
  }

  @Override
  public String getHttpProxyHost() {
    return this.httpProxyHost;
  }

  public void setHttpProxyHost(String httpProxyHost) {
    this.httpProxyHost = httpProxyHost;
  }

  @Override
  public int getHttpProxyPort() {
    return this.httpProxyPort;
  }

  public void setHttpProxyPort(int httpProxyPort) {
    this.httpProxyPort = httpProxyPort;
  }

  @Override
  public String getHttpProxyUsername() {
    return this.httpProxyUsername;
  }

  public void setHttpProxyUsername(String httpProxyUsername) {
    this.httpProxyUsername = httpProxyUsername;
  }

  @Override
  public String getHttpProxyPassword() {
    return this.httpProxyPassword;
  }

  public void setHttpProxyPassword(String httpProxyPassword) {
    this.httpProxyPassword = httpProxyPassword;
  }

  @Override
  public String toString() {
    return WxCpGsonBuilder.create().toJson(this);
  }

  @Override
  public File getTmpDirFile() {
    return this.tmpDirFile;
  }

  @Override
  public Lock getProviderAccessTokenLock() {
    return this.providerAccessTokenLocker
      .computeIfAbsent(String.join(":", this.suiteId, this.corpId), key -> new ReentrantLock());
  }

  @Override
  public Lock getSuiteAccessTokenLock() {
    return this.suiteAccessTokenLocker.computeIfAbsent(this.suiteId, key -> new ReentrantLock());
  }

  @Override
  public Lock getAccessTokenLock(String authCorpId) {
    return this.accessTokenLocker
      .computeIfAbsent(String.join(":", this.suiteId, authCorpId), key -> new ReentrantLock());
  }

  @Override
  public Lock getAuthCorpJsapiTicketLock(String authCorpId) {
    return this.authCorpJsapiTicketLocker
      .computeIfAbsent(String.join(":", this.suiteId, authCorpId), key -> new ReentrantLock());
  }

  @Override
  public Lock getSuiteJsapiTicketLock(String authCorpId) {
    return this.authSuiteJsapiTicketLocker
      .computeIfAbsent(String.join(":", this.suiteId, authCorpId), key -> new ReentrantLock());
  }

  public void setTmpDirFile(File tmpDirFile) {
    this.tmpDirFile = tmpDirFile;
  }

  @Override
  public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
    return this.apacheHttpClientBuilder;
  }

  @Override
  public boolean autoRefreshToken() {
    return true;
  }

  public void setApacheHttpClientBuilder(ApacheHttpClientBuilder apacheHttpClientBuilder) {
    this.apacheHttpClientBuilder = apacheHttpClientBuilder;
  }
}
