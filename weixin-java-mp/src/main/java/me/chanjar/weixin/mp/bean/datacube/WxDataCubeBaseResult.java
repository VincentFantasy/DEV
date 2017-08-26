package me.chanjar.weixin.mp.bean.datacube;

import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.chanjar.weixin.common.util.ToStringUtils;

import java.io.Serializable;

/**
 * <pre>
 *  统计接口的共用属性类
 *  Created by Binary Wang on 2016/8/25.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">binarywang(Binary Wang)</a>
 */
public abstract class WxDataCubeBaseResult implements Serializable {
  private static final long serialVersionUID = 8780389911053297600L;
  protected static final JsonParser JSON_PARSER = new JsonParser();

  /**
   * ref_date
   * 数据的日期，需在begin_date和end_date之间
   */
  @SerializedName("ref_date")
  private String refDate;

  @Override
  public String toString() {
    return ToStringUtils.toSimpleString(this);
  }

  public String getRefDate() {
    return this.refDate;
  }

  public void setRefDate(String refDate) {
    this.refDate = refDate;
  }

}
