package me.chanjar.weixin.mp.util.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutImageMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMusicMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTransferCustomerServiceMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutVideoMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutVoiceMessage;

public class XStreamTransformer {

  protected static final Map<Class<?>, XStream> CLASS_2_XSTREAM_INSTANCE = configXStreamInstance();

  /**
   * xml -> pojo
   */
  @SuppressWarnings("unchecked")
  public static <T> T fromXml(Class<T> clazz, String xml) {
    T object = (T) CLASS_2_XSTREAM_INSTANCE.get(clazz).fromXML(xml);
    return object;
  }

  @SuppressWarnings("unchecked")
  public static <T> T fromXml(Class<T> clazz, InputStream is) {
    T object = (T) CLASS_2_XSTREAM_INSTANCE.get(clazz).fromXML(is);
    return object;
  }

  /**
   * 注册扩展消息的解析器
   * @param clz 类型
   * @param xStream xml解析器
   */
  public static void register(Class<?> clz, XStream xStream) {
    CLASS_2_XSTREAM_INSTANCE.put(clz,xStream);
  }


  /**
   * pojo -> xml
   */
  public static <T> String toXml(Class<T> clazz, T object) {
    return CLASS_2_XSTREAM_INSTANCE.get(clazz).toXML(object);
  }

  private static Map<Class<?>, XStream> configXStreamInstance() {
    Map<Class<?>, XStream> map = new HashMap<>();
    map.put(WxMpXmlMessage.class, config_WxMpXmlMessage());
    map.put(WxMpXmlOutMusicMessage.class, config_WxMpXmlOutMusicMessage());
    map.put(WxMpXmlOutNewsMessage.class, config_WxMpXmlOutNewsMessage());
    map.put(WxMpXmlOutTextMessage.class, config_WxMpXmlOutTextMessage());
    map.put(WxMpXmlOutImageMessage.class, config_WxMpXmlOutImageMessage());
    map.put(WxMpXmlOutVideoMessage.class, config_WxMpXmlOutVideoMessage());
    map.put(WxMpXmlOutVoiceMessage.class, config_WxMpXmlOutVoiceMessage());
    map.put(WxMpXmlOutTransferCustomerServiceMessage.class, config_WxMpXmlOutTransferCustomerServiceMessage());

    return map;
  }

  private static XStream config_WxMpXmlMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlMessage.class);
    xstream.processAnnotations(WxMpXmlMessage.ScanCodeInfo.class);
    xstream.processAnnotations(WxMpXmlMessage.SendPicsInfo.class);
    xstream.processAnnotations(WxMpXmlMessage.SendPicsInfo.Item.class);
    xstream.processAnnotations(WxMpXmlMessage.SendLocationInfo.class);

    xstream.aliasField("MsgID", WxMpXmlMessage.class, "msgId");
    return xstream;
  }

  private static XStream config_WxMpXmlOutImageMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutImageMessage.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutNewsMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutNewsMessage.class);
    xstream.processAnnotations(WxMpXmlOutNewsMessage.Item.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutMusicMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutMusicMessage.class);
    xstream.processAnnotations(WxMpXmlOutMusicMessage.Music.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutTextMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutTextMessage.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutVideoMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutVideoMessage.class);
    xstream.processAnnotations(WxMpXmlOutVideoMessage.Video.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutVoiceMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutVoiceMessage.class);
    return xstream;
  }

  private static XStream config_WxMpXmlOutTransferCustomerServiceMessage() {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(WxMpXmlOutMessage.class);
    xstream.processAnnotations(WxMpXmlOutTransferCustomerServiceMessage.class);
    xstream.processAnnotations(WxMpXmlOutTransferCustomerServiceMessage.TransInfo.class);
    return xstream;
  }

}
