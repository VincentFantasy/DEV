package me.chanjar.weixin.cp.api;

import java.util.Map;

import me.chanjar.weixin.cp.bean.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.WxCpXmlOutMessage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 测试消息路由器
 * @author Daniel Qian
 *
 */
@Test
public class WxCpMessageRouterTest {
  
  @Test(enabled = false)
  public void prepare(boolean async, StringBuffer sb, WxCpMessageRouter router) {
    router
      .rule()
        .async(async)
        .msgType(WxConsts.XML_MSG_TEXT).event(WxConsts.EVT_CLICK).eventKey("KEY_1").content("CONTENT_1")
        .handler(new WxEchoCpMessageHandler(sb, "COMBINE_4"))
      .end()
      .rule()
        .async(async)
        .msgType(WxConsts.XML_MSG_TEXT).event(WxConsts.EVT_CLICK).eventKey("KEY_1")
        .handler(new WxEchoCpMessageHandler(sb, "COMBINE_3"))
      .end()
      .rule()
        .async(async)
        .msgType(WxConsts.XML_MSG_TEXT).event(WxConsts.EVT_CLICK)
        .handler(new WxEchoCpMessageHandler(sb, "COMBINE_2"))
      .end()
      .rule().async(async).msgType(WxConsts.XML_MSG_TEXT).handler(new WxEchoCpMessageHandler(sb, WxConsts.XML_MSG_TEXT)).end()
      .rule().async(async).event(WxConsts.EVT_CLICK).handler(new WxEchoCpMessageHandler(sb, WxConsts.EVT_CLICK)).end()
      .rule().async(async).eventKey("KEY_1").handler(new WxEchoCpMessageHandler(sb, "KEY_1")).end()
      .rule().async(async).content("CONTENT_1").handler(new WxEchoCpMessageHandler(sb, "CONTENT_1")).end()
      .rule().async(async).rContent(".*bc.*").handler(new WxEchoCpMessageHandler(sb, "abcd")).end()
      .rule().async(async).handler(new WxEchoCpMessageHandler(sb, "ALL")).end();
    ;
  }
  
  @Test(dataProvider="messages-1")
  public void testSync(WxCpXmlMessage message, String expected) {
    StringBuffer sb = new StringBuffer();
    WxCpMessageRouter router = new WxCpMessageRouter();
    prepare(false, sb, router);
    router.route(message);
    Assert.assertEquals(sb.toString(), expected);
  }
  
  @Test(dataProvider="messages-1")
  public void testAsync(WxCpXmlMessage message, String expected) throws InterruptedException {
    StringBuffer sb = new StringBuffer();
    WxCpMessageRouter router = new WxCpMessageRouter();
    prepare(true,  sb, router);
    router.route(message);
    Thread.sleep(500l);
    Assert.assertEquals(sb.toString(), expected);
  }
  
  public void testConcurrency() throws InterruptedException {
    final WxCpMessageRouter router = new WxCpMessageRouter();
    router.rule().handler(new WxCpMessageHandler() {
      @Override
      public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context) {
        return null;
      }
    }).end();
    
    final WxCpXmlMessage m = new WxCpXmlMessage();
    Runnable r = new Runnable() {
      @Override
      public void run() {
        router.route(m);
        try {
          Thread.sleep(1000l);
        } catch (InterruptedException e) {
        }
      }
    };
    for (int i = 0; i < 10; i++) {
      new Thread(r).start();
    }
    
    Thread.sleep(1000l * 2);
  }
  @DataProvider(name="messages-1")
  public Object[][] messages2() {
    WxCpXmlMessage message1 = new WxCpXmlMessage();
    message1.setMsgType(WxConsts.XML_MSG_TEXT);
  
    WxCpXmlMessage message2 = new WxCpXmlMessage();
    message2.setEvent(WxConsts.EVT_CLICK);
    
    WxCpXmlMessage message3 = new WxCpXmlMessage();
    message3.setEventKey("KEY_1");
    
    WxCpXmlMessage message4 = new WxCpXmlMessage();
    message4.setContent("CONTENT_1");
    
    WxCpXmlMessage message5 = new WxCpXmlMessage();
    message5.setContent("BLA");
    
    WxCpXmlMessage message6 =  new WxCpXmlMessage();
    message6.setContent("abcd");
    
    WxCpXmlMessage c2 = new WxCpXmlMessage();
    c2.setMsgType(WxConsts.XML_MSG_TEXT);
    c2.setEvent(WxConsts.EVT_CLICK);
    
    WxCpXmlMessage c3 = new WxCpXmlMessage();
    c3.setMsgType(WxConsts.XML_MSG_TEXT);
    c3.setEvent(WxConsts.EVT_CLICK);
    c3.setEventKey("KEY_1");
    
    WxCpXmlMessage c4 = new WxCpXmlMessage();
    c4.setMsgType(WxConsts.XML_MSG_TEXT);
    c4.setEvent(WxConsts.EVT_CLICK);
    c4.setEventKey("KEY_1");
    c4.setContent("CONTENT_1");
    
    return new Object[][] {
        new Object[] { message1, WxConsts.XML_MSG_TEXT + "," },
        new Object[] { message2, WxConsts.EVT_CLICK + "," },
        new Object[] { message3, "KEY_1," },
        new Object[] { message4, "CONTENT_1," },
        new Object[] { message5, "ALL," },
        new Object[] { message6, "abcd," },
        new Object[] { c2, "COMBINE_2," },
        new Object[] { c3, "COMBINE_3," },
        new Object[] { c4, "COMBINE_4," }
    };
    
  }

  public static class WxEchoCpMessageHandler implements WxCpMessageHandler {

    private StringBuffer sb;
    private String echoStr;

    public WxEchoCpMessageHandler(StringBuffer sb, String echoStr) {
      this.sb = sb;
      this.echoStr = echoStr;
    }

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context) {
      sb.append(this.echoStr).append(',');
      return null;
    }

  }

}
