package me.chanjar.weixin.enterprise.bean.result;

import me.chanjar.weixin.enterprise.util.json.WxCpGsonBuilder;

/**
 * <pre>
 * 上传群发用的素材的结果
 * 视频和图文消息需要在群发前上传素材
 * </pre>
 * @author Daniel Qian
 *
 */
public class WxMassUploadResult {

  private String type;
  private String mediaId;
  private long createdAt;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public static WxMassUploadResult fromJson(String json) {
    return WxCpGsonBuilder.create().fromJson(json, WxMassUploadResult.class);
  }

  @Override
  public String toString() {
    return "WxUploadResult [type=" + type + ", media_id=" + mediaId + ", created_at=" + createdAt + "]";
  }

}
