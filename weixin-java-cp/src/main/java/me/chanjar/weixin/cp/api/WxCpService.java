package me.chanjar.weixin.cp.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.*;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.common.exception.WxErrorException;

/**
 * 微信API的Service
 */
public interface WxCpService {

  /**
   * <pre>
   * 验证推送过来的消息的正确性
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=验证消息真实性
   * </pre>
   *
   * @param msgSignature
   * @param timestamp
   * @param nonce
   * @param data         微信传输过来的数据，有可能是echoStr，有可能是xml消息
   * @return
   */
  public boolean checkSignature(String msgSignature, String timestamp, String nonce, String data);

  /**
   * <pre>
   *   用在二次验证的时候
   *   企业在员工验证成功后，调用本方法告诉企业号平台该员工关注成功。
   * </pre>
   *
   * @param userId
   */
  public void userAuthenticated(String userId) throws WxErrorException;

  /**
   * <pre>
   * 获取access_token，本方法线程安全
   * 且在多线程同时刷新时只刷新一次，避免超出2000次/日的调用次数上限
   * 另：本service的所有方法都会在access_token过期是调用此方法
   * 程序员在非必要情况下尽量不要主动调用此方法
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=获取access_token
   * </pre>
   *
   * @throws me.chanjar.weixin.common.exception.WxErrorException
   */
  public void accessTokenRefresh() throws WxErrorException;

  /**
   * <pre>
   * 上传多媒体文件
   * 上传的多媒体文件有格式和大小限制，如下：
   *   图片（image）: 1M，支持JPG格式
   *   语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式
   *   视频（video）：10MB，支持MP4格式
   *   缩略图（thumb）：64KB，支持JPG格式
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=上传下载多媒体文件
   * </pre>
   *
   * @param mediaType   媒体类型, 请看{@link WxConsts}
   * @param fileType    文件类型，请看{@link WxConsts}
   * @param inputStream 输入流
   * @throws WxErrorException
   */
  public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream)
      throws WxErrorException, IOException;

  /**
   * @param mediaType
   * @param file
   * @throws WxErrorException
   * @see #mediaUpload(String, String, InputStream)
   */
  public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException;

  /**
   * <pre>
   * 下载多媒体文件
   * 根据微信文档，视频文件下载不了，会返回null
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=上传下载多媒体文件
   * </pre>
   *
   * @return 保存到本地的临时文件
   * @throws WxErrorException
   * @params media_id
   */
  public File mediaDownload(String media_id) throws WxErrorException;

  /**
   * <pre>
   * 发送消息
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=发送消息
   * </pre>
   *
   * @param message
   * @throws WxErrorException
   */
  public void messageSend(WxCpMessage message) throws WxErrorException;

  /**
   * <pre>
   * 自定义菜单创建接口
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=自定义菜单创建接口
   * </pre>
   *
   * @param menu
   * @throws WxErrorException
   */
  public void menuCreate(WxMenu menu) throws WxErrorException;

  /**
   * <pre>
   * 自定义菜单删除接口
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=自定义菜单删除接口
   * </pre>
   *
   * @throws WxErrorException
   */
  public void menuDelete() throws WxErrorException;

  /**
   * <pre>
   * 自定义菜单查询接口
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=自定义菜单查询接口
   * </pre>
   *
   * @return
   * @throws WxErrorException
   */
  public WxMenu menuGet() throws WxErrorException;

  /**
   * <pre>
   * 部门管理接口 - 创建部门
   * 最多支持创建500个部门
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=部门管理接口
   * </pre>
   *
   * @param depart 部门
   * @return 部门id
   * @throws WxErrorException
   */
  public Integer departCreate(WxCpDepart depart) throws WxErrorException;

  /**
   * <pre>
   * 部门管理接口 - 查询所有部门
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=部门管理接口
   * </pre>
   *
   * @return
   * @throws WxErrorException
   */
  public List<WxCpDepart> departGet() throws WxErrorException;

  /**
   * <pre>
   * 部门管理接口 - 修改部门名
   * 详情请见: http://mp.weixin.qq.com/wiki/index.php?title=部门管理接口
   * 如果id为0(未部门),1(黑名单),2(星标组)，或者不存在的id，微信会返回系统繁忙的错误
   * </pre>
   *
   * @param group 要更新的group，group的id,name必须设置
   * @throws WxErrorException
   */
  public void departUpdate(WxCpDepart group) throws WxErrorException;

  /**
   * <pre>
   * 部门管理接口 - 删除部门
   * </pre>
   *
   * @param departId
   * @throws WxErrorException
   */
  public void departDelete(Integer departId) throws WxErrorException;

  /**
   * http://qydev.weixin.qq.com/wiki/index.php?title=管理成员#.E8.8E.B7.E5.8F.96.E9.83.A8.E9.97.A8.E6.88.90.E5.91.98
   *
   * @param departId   必填。部门id
   * @param fetchChild 非必填。1/0：是否递归获取子部门下面的成员
   * @param status     非必填。0获取全部员工，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加
   * @return
   * @throws WxErrorException
   */
  public List<WxCpUser> departGetUsers(Integer departId, Boolean fetchChild, Integer status) throws WxErrorException;

  /**
   * 新建用户
   *
   * @param user
   * @throws WxErrorException
   */
  public void userCreate(WxCpUser user) throws WxErrorException;

  /**
   * 更新用户
   *
   * @param user
   * @throws WxErrorException
   */
  public void userUpdate(WxCpUser user) throws WxErrorException;

  /**
   * 删除用户
   *
   * @param userid
   * @throws WxErrorException
   */
  public void userDelete(String userid) throws WxErrorException;

  /**
   * 获取用户
   *
   * @param userid
   * @return
   * @throws WxErrorException
   */
  public WxCpUser userGet(String userid) throws WxErrorException;

  /**
   * 创建标签
   *
   * @param tagName
   * @return
   */
  public String tagCreate(String tagName) throws WxErrorException;

  /**
   * 更新标签
   *
   * @param tagId
   * @param tagName
   */
  public void tagUpdate(String tagId, String tagName) throws WxErrorException;

  /**
   * 删除标签
   *
   * @param tagId
   */
  public void tagDelete(String tagId) throws WxErrorException;

  /**
   * 获得标签列表
   *
   * @return
   */
  public List<WxCpTag> tagGet() throws WxErrorException;

  /**
   * 获取标签成员
   *
   * @param tagId
   * @return
   */
  public List<WxCpUser> tagGetUsers(String tagId) throws WxErrorException;

  /**
   * 增加标签成员
   *
   * @param tagId
   * @param userIds
   */
  public void tagAddUsers(String tagId, List<String> userIds) throws WxErrorException;

  /**
   * 移除标签成员
   *
   * @param tagId
   * @param userIds
   */
  public void tagRemoveUsers(String tagId, List<String> userIds) throws WxErrorException;

  /**
   * 注入 {@link WxCpConfigStorage} 的实现
   *
   * @param wxConfigProvider
   */
  public void setWxCpConfigStorage(WxCpConfigStorage wxConfigProvider);
}
