package cn.binarywang.wx.miniapp.builder;

import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;

/**
 * 图片消息builder.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public final class ImageBuilder extends BaseBuilder<ImageBuilder> {
  private String mediaId;

  public ImageBuilder() {
    this.msgType = WxMaConstants.KefuMsgType.IMAGE;
  }

  public ImageBuilder mediaId(String mediaId) {
    this.mediaId = mediaId;
    return this;
  }

  @Override
  public WxMaKefuMessage build() {
    WxMaKefuMessage m = super.build();
    m.setImage(new WxMaKefuMessage.KfImage(this.mediaId));
    return m;
  }
}
