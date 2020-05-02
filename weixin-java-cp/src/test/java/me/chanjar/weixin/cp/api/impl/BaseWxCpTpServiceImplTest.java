package me.chanjar.weixin.cp.api.impl;

import com.google.gson.JsonObject;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.WxCpTpService;
import me.chanjar.weixin.cp.bean.WxCpTpAuthInfo;
import me.chanjar.weixin.cp.bean.WxCpTpCorp;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.constant.WxCpApiPathConsts;
import org.testng.annotations.Test;

import static me.chanjar.weixin.cp.constant.WxCpApiPathConsts.Tp.GET_AUTH_INFO;
import static me.chanjar.weixin.cp.constant.WxCpApiPathConsts.Tp.GET_PERMANENT_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * 测试代码.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2019-08-18
 */
public class BaseWxCpTpServiceImplTest {
  private WxCpTpService tpService = spy(new WxCpTpServiceImpl());

  @Test
  public void testCheckSignature() {
  }

  @Test
  public void testGetSuiteAccessToken() {
  }

  @Test
  public void testGetSuiteTicket() {
  }

  @Test
  public void testTestGetSuiteTicket() {
  }

  @Test
  public void testJsCode2Session() {
  }

  @Test
  public void testGetCorpToken() {
  }

  @Test
  public void testGetPermanentCode() throws WxErrorException {
    String returnJson = "{\n" +
      "    \"errcode\":0 ,\n" +
      "    \"errmsg\":\"ok\" ,\n" +
      "    \"access_token\": \"xxxxxx\", \n" +
      "    \"expires_in\": 7200, \n" +
      "    \"permanent_code\": \"xxxx\", \n" +
      "    \"dealer_corp_info\": \n" +
      "    {\n" +
      "        \"corpid\": \"xxxx\",\n" +
      "        \"corp_name\": \"name\"\n" +
      "    },\n" +
      "    \"auth_corp_info\": \n" +
      "    {\n" +
      "        \"corpid\": \"xxxx\",\n" +
      "        \"corp_name\": \"name\",\n" +
      "        \"corp_type\": \"verified\",\n" +
      "        \"corp_square_logo_url\": \"yyyyy\",\n" +
      "        \"corp_user_max\": 50,\n" +
      "        \"corp_agent_max\": 30,\n" +
      "        \"corp_full_name\":\"full_name\",\n" +
      "        \"verified_end_time\":1431775834,\n" +
      "        \"subject_type\": 1,\n" +
      "        \"corp_wxqrcode\": \"zzzzz\",\n" +
      "        \"corp_scale\": \"1-50人\",\n" +
      "        \"corp_industry\": \"IT服务\",\n" +
      "        \"corp_sub_industry\": \"计算机软件/硬件/信息服务\",\n" +
      "        \"location\":\"广东省广州市\"\n" +
      "    },\n" +
      "    \"auth_info\":\n" +
      "    {\n" +
      "        \"agent\" :\n" +
      "        [\n" +
      "            {\n" +
      "                \"agentid\":1,\n" +
      "                \"name\":\"NAME\",\n" +
      "                \"round_logo_url\":\"xxxxxx\",\n" +
      "                \"square_logo_url\":\"yyyyyy\",\n" +
      "                \"appid\":1,\n" +
      "                \"privilege\":\n" +
      "                {\n" +
      "                    \"level\":1,\n" +
      "                    \"allow_party\":[1,2,3],\n" +
      "                    \"allow_user\":[\"zhansan\",\"lisi\"],\n" +
      "                    \"allow_tag\":[1,2,3],\n" +
      "                    \"extra_party\":[4,5,6],\n" +
      "                    \"extra_user\":[\"wangwu\"],\n" +
      "                    \"extra_tag\":[4,5,6]\n" +
      "                }\n" +
      "            },\n" +
      "            {\n" +
      "                \"agentid\":2,\n" +
      "                \"name\":\"NAME2\",\n" +
      "                \"round_logo_url\":\"xxxxxx\",\n" +
      "                \"square_logo_url\":\"yyyyyy\",\n" +
      "                \"appid\":5\n" +
      "            }\n" +
      "        ]\n" +
      "    },\n" +
      "    \"auth_user_info\":\n" +
      "    {\n" +
      "        \"userid\":\"aa\",\n" +
      "        \"name\":\"xxx\",\n" +
      "        \"avatar\":\"http://xxx\"\n" +
      "    }\n" +
      "}\n";

    final WxCpTpConfigStorage configStorage = new WxCpTpDefaultConfigImpl();
    tpService.setWxCpTpConfigStorage(configStorage);

    JsonObject jsonObject = new JsonObject();
    String authCode = "";
    jsonObject.addProperty("auth_code", authCode);
    doReturn(returnJson).when(tpService).post(configStorage.getApiUrl(GET_PERMANENT_CODE), jsonObject.toString());

    final WxCpTpCorp tpCorp = tpService.getPermanentCode(authCode);
    assertThat(tpCorp.getPermanentCode()).isEqualTo("xxxx");

    final WxCpTpPermanentCodeInfo tpPermanentCodeInfo = tpService.getPermanentCodeInfo(authCode);
    assertThat(tpPermanentCodeInfo.getAuthInfo().getAgents().get(0).getAgentId()).isEqualTo(1);

  }

  @Test
  public void testGetPermanentCodeInfo() throws WxErrorException{
    String returnJson = "{\n" +
      "  \"access_token\": \"u6SoEWyrEmworJ1uNzddbiXh42mCLNU_mdd6b01Afo2LKmyi-WdaaYqhEGFZjB1RGZ-rhjLcAJ86ger7b7Q0gowSw9iIDR8dm49aVH_MztzmQttP3XFG7np1Dxs_VQkVwhhRmfRpEonAmK1_JWIFqayJXXiPUS3LsFd3tWpE7rxmsRa7Ev2ml2htbRp_qGUjtFTErKoDsnNGSka6_RqFPA\", \n" +
      "  \"expires_in\": 7200, \n" +
      "  \"permanent_code\": \"lMLlxss77ntxzuEl1i1_AQ3-6-cvqMLYs209YNWVruk\", \n" +
      "  \"auth_corp_info\": {\n" +
      "    \"corpid\": \"xxxcorpid\", \n" +
      "    \"corp_name\": \"xxxx有限公司\", \n" +
      "    \"corp_type\": \"unverified\", \n" +
      "    \"corp_round_logo_url\": \"http://p.qpic.cn/pic_wework/3777001839/4046834be7a5f2711feaaa3cc4e691e1bcb1e526cb4544b5/0\", \n" +
      "    \"corp_square_logo_url\": \"https://p.qlogo.cn/bizmail/EsvsszIt9hJrjrx8QKXuIs0iczdnV4icaPibLIViaukn1iazCay8L1UXtibA/0\", \n" +
      "    \"corp_user_max\": 200, \n" +
      "    \"corp_agent_max\": 300, \n" +
      "    \"corp_wxqrcode\": \"http://p.qpic.cn/pic_wework/211781738/a9af41a60af7519775dd7ac846a4942979dc4a14b8bb2c72/0\", \n" +
      "    \"corp_full_name\": \"xxxx有限公司\", \n" +
      "    \"subject_type\": 1, \n" +
      "    \"corp_scale\": \"1-50人\", \n" +
      "    \"corp_industry\": \"生活服务\", \n" +
      "    \"corp_sub_industry\": \"租赁和商务服务\", \n" +
      "    \"location\": \"北京市\"\n" +
      "  }, \n" +
      "  \"auth_info\": {\n" +
      "    \"agent\": [\n" +
      "      {\n" +
      "        \"agentid\": 1000012, \n" +
      "        \"name\": \"xxxxx\", \n" +
      "        \"square_logo_url\": \"http://wx.qlogo.cn/mmhead/Q3auHgzwzM4ZCtdxicN8ghMOtTv7M7rLPKmeZ3amic00btdwbNmicaW3Q/0\", \n" +
      "        \"privilege\": {\n" +
      "          \"level\": 1, \n" +
      "          \"allow_party\": [ ], \n" +
      "          \"allow_user\": [\n" +
      "            \"yuanqixun\"\n" +
      "          ], \n" +
      "          \"allow_tag\": [ ], \n" +
      "          \"extra_party\": [ ], \n" +
      "          \"extra_user\": [ ], \n" +
      "          \"extra_tag\": [ ]\n" +
      "        }\n" +
      "      }\n" +
      "    ]\n" +
      "  }, \n" +
      "  \"auth_user_info\": {\n" +
      "    \"userid\": \"yuanqixun\", \n" +
      "    \"name\": \"袁启勋\", \n" +
      "    \"avatar\": \"http://wework.qpic.cn/bizmail/ZYqy8EswiaFyPnk7gy7eiafoicz3TL35f4bAvCf2eSe6RVYSK7aPDFxcw/0\"\n" +
      "  }\n" +
      "}";

    final WxCpTpConfigStorage configStorage = new WxCpTpDefaultConfigImpl();
    tpService.setWxCpTpConfigStorage(configStorage);
    JsonObject jsonObject = new JsonObject();
    String authCode = "";
    jsonObject.addProperty("auth_code", authCode);
    doReturn(returnJson).when(tpService).post(configStorage.getApiUrl(GET_PERMANENT_CODE), jsonObject.toString());
    final WxCpTpPermanentCodeInfo tpPermanentCodeInfo = tpService.getPermanentCodeInfo(authCode);
    assertThat(tpPermanentCodeInfo.getAuthInfo().getAgents().get(0).getAgentId()).isEqualTo(1000012);
    assertNotNull(tpPermanentCodeInfo.getAuthInfo().getAgents().get(0).getSquareLogoUrl());
    assertNotNull(tpPermanentCodeInfo.getAuthCorpInfo().getCorpSquareLogoUrl());
  }

  @Test
  public void testGetAuthInfo() throws WxErrorException{
    String returnJson = "{\n" +
      "    \"errcode\":0 ,\n" +
      "    \"errmsg\":\"ok\" ,\n" +
      "    \"dealer_corp_info\": \n" +
      "    {\n" +
      "        \"corpid\": \"xxxx\",\n" +
      "        \"corp_name\": \"name\"\n" +
      "    },\n" +
      "    \"auth_corp_info\": \n" +
      "    {\n" +
      "        \"corpid\": \"xxxx\",\n" +
      "        \"corp_name\": \"name\",\n" +
      "        \"corp_type\": \"verified\",\n" +
      "        \"corp_square_logo_url\": \"yyyyy\",\n" +
      "        \"corp_user_max\": 50,\n" +
      "        \"corp_agent_max\": 30,\n" +
      "        \"corp_full_name\":\"full_name\",\n" +
      "        \"verified_end_time\":1431775834,\n" +
      "        \"subject_type\": 1,\n" +
      "        \"corp_wxqrcode\": \"zzzzz\",\n" +
      "        \"corp_scale\": \"1-50人\",\n" +
      "        \"corp_industry\": \"IT服务\",\n" +
      "        \"corp_sub_industry\": \"计算机软件/硬件/信息服务\",\n" +
      "        \"location\":\"广东省广州市\"\n" +
      "    },\n" +
      "    \"auth_info\":\n" +
      "    {\n" +
      "        \"agent\" :\n" +
      "        [\n" +
      "            {\n" +
      "                \"agentid\":1,\n" +
      "                \"name\":\"NAME\",\n" +
      "                \"round_logo_url\":\"xxxxxx\",\n" +
      "                \"square_logo_url\":\"yyyyyy\",\n" +
      "                \"appid\":1,\n" +
      "                \"privilege\":\n" +
      "                {\n" +
      "                    \"level\":1,\n" +
      "                    \"allow_party\":[1,2,3],\n" +
      "                    \"allow_user\":[\"zhansan\",\"lisi\"],\n" +
      "                    \"allow_tag\":[1,2,3],\n" +
      "                    \"extra_party\":[4,5,6],\n" +
      "                    \"extra_user\":[\"wangwu\"],\n" +
      "                    \"extra_tag\":[4,5,6]\n" +
      "                }\n" +
      "            },\n" +
      "            {\n" +
      "                \"agentid\":2,\n" +
      "                \"name\":\"NAME2\",\n" +
      "                \"round_logo_url\":\"xxxxxx\",\n" +
      "                \"square_logo_url\":\"yyyyyy\",\n" +
      "                \"appid\":5\n" +
      "            }\n" +
      "        ]\n" +
      "    }\n" +
      "}\n";

    final WxCpTpConfigStorage configStorage = new WxCpTpDefaultConfigImpl();
    tpService.setWxCpTpConfigStorage(configStorage);
    JsonObject jsonObject = new JsonObject();
    String authCorpId = "xxxxx";
    String permanentCode = "xxxxx";
    jsonObject.addProperty("auth_corpid", authCorpId);
    jsonObject.addProperty("permanent_code", permanentCode);
    doReturn(returnJson).when(tpService).post(configStorage.getApiUrl(GET_AUTH_INFO), jsonObject.toString());
    WxCpTpAuthInfo authInfo = tpService.getAuthInfo(authCorpId,permanentCode);
    assertNotNull(authInfo.getAuthCorpInfo().getCorpId());
  }

  @Test
  public void testGet() {
  }

  @Test
  public void testPost() {
  }

  @Test
  public void testExecute() {
  }

  @Test
  public void testExecuteInternal() {
  }

  @Test
  public void testSetWxCpTpConfigStorage() {
  }

  @Test
  public void testSetRetrySleepMillis() {
  }

  @Test
  public void testSetMaxRetryTimes() {
  }

  @Test
  public void testGetTmpDirFile() {
  }

  @Test
  public void testSetTmpDirFile() {
  }

  @Test
  public void testGetRequestHttp() {
  }
}
