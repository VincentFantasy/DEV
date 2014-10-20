package me.chanjar.weixin.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import me.chanjar.weixin.api.WxConsts;
import me.chanjar.weixin.util.xml.MediaIdMarshaller;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class WxXmlOutImageMessage extends WxXmlOutMessage {
  
  @XmlElement(name="Image")
  @XmlJavaTypeAdapter(MediaIdMarshaller.class)
  private String mediaId;

  public WxXmlOutImageMessage() {
    this.msgType = WxConsts.XML_MSG_IMAGE;
  }
  
  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }
  
}
