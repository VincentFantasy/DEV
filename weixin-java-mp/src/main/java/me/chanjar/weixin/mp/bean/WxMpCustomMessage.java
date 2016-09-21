package me.chanjar.weixin.mp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.chanjar.weixin.mp.bean.custombuilder.ImageBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.MusicBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.NewsBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.TextBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.VideoBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.VoiceBuilder;
import me.chanjar.weixin.mp.bean.custombuilder.WxCardBuilder;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
 * 客服消息
 * @author chanjarster
 *
 */
public class WxMpCustomMessage implements Serializable {
  private static final long serialVersionUID = -9196732086954365246L;

  private String toUser;
  private String msgType;
  private String content;
  private String mediaId;
  private String thumbMediaId;
  private String title;
  private String description;
  private String musicUrl;
  private String hqMusicUrl;
  private String kfAccount;
  private String cardId;
  private List<WxArticle> articles = new ArrayList<>();

  public String getToUser() {
    return this.toUser;
  }
  public void setToUser(String toUser) {
    this.toUser = toUser;
  }
  public String getMsgType() {
    return this.msgType;
  }

  /**
   * <pre>
   * 请使用
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_TEXT}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_IMAGE}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_VOICE}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_MUSIC}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_VIDEO}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_NEWS}
   * {@link me.chanjar.weixin.common.api.WxConsts#CUSTOM_MSG_WXCARD}
   * </pre>
   * @param msgType
   */
  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }
  public String getContent() {
    return this.content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public String getMediaId() {
    return this.mediaId;
  }
  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }
  public String getThumbMediaId() {
    return this.thumbMediaId;
  }
  public void setThumbMediaId(String thumbMediaId) {
    this.thumbMediaId = thumbMediaId;
  }
  public String getTitle() {
    return this.title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return this.description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getMusicUrl() {
    return this.musicUrl;
  }
  public void setMusicUrl(String musicUrl) {
    this.musicUrl = musicUrl;
  }
  public String getHqMusicUrl() {
    return this.hqMusicUrl;
  }
  public void setHqMusicUrl(String hqMusicUrl) {
    this.hqMusicUrl = hqMusicUrl;
  }

  public String getCardId() {
    return this.cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public List<WxArticle> getArticles() {
    return this.articles;
  }
  public void setArticles(List<WxArticle> articles) {
    this.articles = articles;
  }

  public String toJson() {
    return WxMpGsonBuilder.INSTANCE.create().toJson(this);
  }

  public static class WxArticle {

    private String title;
    private String description;
    private String url;
    private String picUrl;

    public String getTitle() {
      return this.title;
    }
    public void setTitle(String title) {
      this.title = title;
    }
    public String getDescription() {
      return this.description;
    }
    public void setDescription(String description) {
      this.description = description;
    }
    public String getUrl() {
      return this.url;
    }
    public void setUrl(String url) {
      this.url = url;
    }
    public String getPicUrl() {
      return this.picUrl;
    }
    public void setPicUrl(String picUrl) {
      this.picUrl = picUrl;
    }

  }

  /**
   * 获得文本消息builder
   */
  public static TextBuilder TEXT() {
    return new TextBuilder();
  }

  /**
   * 获得图片消息builder
   */
  public static ImageBuilder IMAGE() {
    return new ImageBuilder();
  }

  /**
   * 获得语音消息builder
   */
  public static VoiceBuilder VOICE() {
    return new VoiceBuilder();
  }

  /**
   * 获得视频消息builder
   */
  public static VideoBuilder VIDEO() {
    return new VideoBuilder();
  }

  /**
   * 获得音乐消息builder
   */
  public static MusicBuilder MUSIC() {
    return new MusicBuilder();
  }

  /**
   * 获得图文消息builder
   */
  public static NewsBuilder NEWS() {
    return new NewsBuilder();
  }


  /**
   * 获得卡券消息builder
   */
  public static WxCardBuilder WXCARD() {
    return new WxCardBuilder();
  }

  public String getKfAccount() {
    return this.kfAccount;
  }

  public void setKfAccount(String kfAccount) {
    this.kfAccount = kfAccount;
  }
}
