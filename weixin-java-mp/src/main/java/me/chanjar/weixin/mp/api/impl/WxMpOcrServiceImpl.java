package me.chanjar.weixin.mp.api.impl;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpOcrService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrBankCardResult;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrBizLicenseResult;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrCommResult;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrDrivingLicenseResult;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrDrivingResult;
import me.chanjar.weixin.mp.bean.ocr.WxMpOcrIdCardResult;
import me.chanjar.weixin.mp.util.requestexecuter.ocr.OcrDiscernRequestExecutor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.BANK_CARD;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.BIZ_LICENSE;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.COMM;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.DRIVING;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.DRIVING_LICENSE;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILEIDCARD;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILE_BANK_CARD;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILE_BIZ_LICENSE;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILE_COMM;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILE_DRIVING;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.FILE_DRIVING_LICENSE;
import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Ocr.IDCARD;

/**
 * ocr 接口实现.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2019-06-22
 */
@RequiredArgsConstructor
public class WxMpOcrServiceImpl implements WxMpOcrService {
  private final WxMpService mainService;

  @Override
  public WxMpOcrIdCardResult idCard(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(IDCARD.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrIdCardResult.fromJson(result);
  }

  @Override
  public WxMpOcrIdCardResult idCard(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILEIDCARD.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrIdCardResult.fromJson(result);
  }

  @Override
  public WxMpOcrBankCardResult bankCard(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(BANK_CARD.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrBankCardResult.fromJson(result);
  }

  @Override
  public WxMpOcrBankCardResult bankCard(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILE_BANK_CARD.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrBankCardResult.fromJson(result);
  }

  @Override
  public WxMpOcrDrivingResult driving(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(DRIVING.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrDrivingResult.fromJson(result);
  }

  @Override
  public WxMpOcrDrivingResult driving(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILE_DRIVING.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrDrivingResult.fromJson(result);
  }

  @Override
  public WxMpOcrDrivingLicenseResult drivingLicense(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(DRIVING_LICENSE.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrDrivingLicenseResult.fromJson(result);
  }

  @Override
  public WxMpOcrDrivingLicenseResult drivingLicense(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILE_DRIVING_LICENSE.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrDrivingLicenseResult.fromJson(result);
  }

  @Override
  public WxMpOcrBizLicenseResult bizLicense(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(BIZ_LICENSE.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrBizLicenseResult.fromJson(result);
  }

  @Override
  public WxMpOcrBizLicenseResult bizLicense(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILE_BIZ_LICENSE.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrBizLicenseResult.fromJson(result);
  }

  @Override
  public WxMpOcrCommResult comm(String imgUrl) throws WxErrorException {
    try {
      imgUrl = URLEncoder.encode(imgUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // ignore cannot happen
    }

    final String result = this.mainService.get(String.format(COMM.getUrl(this.mainService.getWxMpConfigStorage()),
      imgUrl), null);
    return WxMpOcrCommResult.fromJson(result);
  }

  @Override
  public WxMpOcrCommResult comm(File imgFile) throws WxErrorException {
    String result = this.mainService.execute(OcrDiscernRequestExecutor.create(this.mainService.getRequestHttp()),
      FILE_COMM.getUrl(this.mainService.getWxMpConfigStorage()), imgFile);
    return WxMpOcrCommResult.fromJson(result);
  }
}
