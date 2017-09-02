package me.chanjar.weixin.mp.bean.device;

import me.chanjar.weixin.common.util.json.WxGsonBuilder;

import java.io.Serializable;

/**
 * Created by keungtung on 14/12/2016.
 */
public abstract class AbstractDeviceBean implements Serializable{
  private static final long serialVersionUID = 4359729626772515385L;

  public String toJson() {
    return WxGsonBuilder.create().toJson(this);
  }
}
