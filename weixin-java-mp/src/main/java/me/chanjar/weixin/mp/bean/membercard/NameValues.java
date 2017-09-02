package me.chanjar.weixin.mp.bean.membercard;

import java.io.Serializable;

/**
 * Created by YuJian on 2017/7/11.
 */
public class NameValues implements Serializable{
  private static final long serialVersionUID = -8529369702944594330L;

  private String name;

  private String value;

  private String[] valueList;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String[] getValueList() {
    return valueList;
  }

  public void setValueList(String[] valueList) {
    this.valueList = valueList;
  }
}
