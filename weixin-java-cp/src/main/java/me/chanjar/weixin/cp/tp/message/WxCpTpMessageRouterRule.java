package me.chanjar.weixin.cp.tp.message;

import lombok.Data;
import me.chanjar.weixin.common.api.WxErrorExceptionHandler;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.message.WxCpMessageMatcher;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;

import java.util.*;

/**
 * The type Wx cp message router rule.
 *
 * @author Daniel Qian
 */
@Data
public class WxCpTpMessageRouterRule {
  private final WxCpTpMessageRouter routerBuilder;

  private boolean async = true;

  private WxCpMessageMatcher matcher;

  private boolean reEnter = false;

  private List<WxCpTpMessageHandler> handlers = new ArrayList<>();

  private List<WxCpTpMessageInterceptor> interceptors = new ArrayList<>();
  private String suiteId;
  private String infoType;
  private String authCode;
  private String suiteTicket;

  /**
   * Instantiates a new Wx cp message router rule.
   *
   * @param routerBuilder the router builder
   */
  protected WxCpTpMessageRouterRule(WxCpTpMessageRouter routerBuilder) {
    this.routerBuilder = routerBuilder;
  }

  /**
   * 设置是否异步执行，默认是true
   *
   * @param async the async
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule async(boolean async) {
    this.async = async;
    return this;
  }

  /**
   * 匹配 Message infoType
   *
   * @param infoType info
   */
  public WxCpTpMessageRouterRule infoType(String infoType) {
    this.infoType = infoType;
    return this;
  }

  /**
   * 如果消息匹配某个matcher，用在用户需要自定义更复杂的匹配规则的时候
   *
   * @param matcher the matcher
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule matcher(WxCpMessageMatcher matcher) {
    this.matcher = matcher;
    return this;
  }

  /**
   * 设置微信消息拦截器
   *
   * @param interceptor the interceptor
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule interceptor(WxCpTpMessageInterceptor interceptor) {
    return interceptor(interceptor, (WxCpTpMessageInterceptor[]) null);
  }

  /**
   * 设置微信消息拦截器
   *
   * @param interceptor       the interceptor
   * @param otherInterceptors the other interceptors
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule interceptor(WxCpTpMessageInterceptor interceptor, WxCpTpMessageInterceptor... otherInterceptors) {
    this.interceptors.add(interceptor);
    if (otherInterceptors != null && otherInterceptors.length > 0) {
      Collections.addAll(this.interceptors, otherInterceptors);
    }
    return this;
  }

  /**
   * 设置微信消息处理器
   *
   * @param handler the handler
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule handler(WxCpTpMessageHandler handler) {
    return handler(handler, (WxCpTpMessageHandler[]) null);
  }

  /**
   * 设置微信消息处理器
   *
   * @param handler       the handler
   * @param otherHandlers the other handlers
   * @return the wx cp message router rule
   */
  public WxCpTpMessageRouterRule handler(WxCpTpMessageHandler handler, WxCpTpMessageHandler... otherHandlers) {
    this.handlers.add(handler);
    if (otherHandlers != null && otherHandlers.length > 0) {
      Collections.addAll(this.handlers, otherHandlers);
    }
    return this;
  }

  /**
   * 规则结束，代表如果一个消息匹配该规则，那么它将不再会进入其他规则
   *
   * @return the wx cp message router
   */
  public WxCpTpMessageRouter end() {
    this.routerBuilder.getRules().add(this);
    return this.routerBuilder;
  }

  /**
   * 规则结束，但是消息还会进入其他规则
   *
   * @return the wx cp message router
   */
  public WxCpTpMessageRouter next() {
    this.reEnter = true;
    return end();
  }

  /**
   * Test boolean.
   *
   * @param wxMessage the wx message
   * @return the boolean
   */
  protected boolean test(WxCpTpXmlMessage wxMessage) {
    return
      (this.suiteId == null || this.suiteId.equals(wxMessage.getSuiteId()))
        &&
        (this.infoType == null || this.infoType.equals(wxMessage.getInfoType()))
        &&
        (this.suiteTicket == null || this.suiteTicket.equalsIgnoreCase(wxMessage.getSuiteTicket()))
        &&
        (this.authCode == null || this.authCode.equalsIgnoreCase(wxMessage.getAuthCode()))
      ;
  }

  /**
   * 处理微信推送过来的消息
   *
   * @param wxMessage        the wx message
   * @param context          the context
   * @param wxCpService      the wx cp service
   * @param sessionManager   the session manager
   * @param exceptionHandler the exception handler
   * @return true 代表继续执行别的router，false 代表停止执行别的router
   */
  protected WxCpXmlOutMessage service(WxCpTpXmlMessage wxMessage,
                                      Map<String, Object> context,
                                      WxCpTpService wxCpService,
                                      WxSessionManager sessionManager,
                                      WxErrorExceptionHandler exceptionHandler) {
    if (context == null) {
      context = new HashMap<>(2);
    }

    try {
      // 如果拦截器不通过
      for (WxCpTpMessageInterceptor interceptor : this.interceptors) {
        if (!interceptor.intercept(wxMessage, context, wxCpService, sessionManager)) {
          return null;
        }
      }

      // 交给handler处理
      WxCpXmlOutMessage res = null;
      for (WxCpTpMessageHandler handler : this.handlers) {
        // 返回最后handler的结果
        res = handler.handle(wxMessage, context, wxCpService, sessionManager);
      }
      return res;

    } catch (WxErrorException e) {
      exceptionHandler.handle(e);
    }

    return null;

  }


}
