package cn.binarywang.wx.miniapp.api.impl;

import cn.binarywang.wx.miniapp.api.WxMaLiveService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.live.*;
import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.GsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.binarywang.wx.miniapp.constant.WxMaApiUrlConstants.Broadcast.*;

/**
 * <pre>
 *  Created by yjwang on 2020/4/5.
 * </pre>
 *
 * @author <a href="https://github.com/yjwang3300300">yjwang</a>
 */
@Slf4j
@RequiredArgsConstructor
public class WxMaLiveServiceImpl implements WxMaLiveService {
  private static final String ERR_CODE = "errcode";
  private static final String ROOM_ID = "roomId";
  private final WxMaService wxMaService;

  @Override
  public WxMaCreateRoomResult createRoom(WxMaLiveRoomInfo roomInfo) throws WxErrorException {
    String responseContent = this.wxMaService.post(Room.CREATE_ROOM, WxMaGsonBuilder.create().toJson(roomInfo));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }

    return WxMaGsonBuilder.create().fromJson(responseContent, WxMaCreateRoomResult.class);
  }

  @Override
  public boolean deleteRoom(Integer roomId) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put("id", roomId);
    String responseContent = this.wxMaService.post(Room.DELETE_ROOM, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public boolean editRoom(WxMaLiveRoomInfo roomInfo) throws WxErrorException {
    String responseContent = this.wxMaService.post(Room.EDIT_ROOM, WxMaGsonBuilder.create().toJson(roomInfo));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public String getPushUrl(Integer roomId) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    String responseContent = this.wxMaService.get(Room.GET_PUSH_URL, Joiner.on("&").withKeyValueSeparator("=").join(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return jsonObject.get("pushAddr").getAsString();
  }

  @Override
  public String getSharedCode(Integer roomId, String params) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    if (null != params) {
      map.put("params", params);
    }
    String responseContent = this.wxMaService.get(Room.GET_SHARED_CODE, Joiner.on("&").withKeyValueSeparator("=").join(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return jsonObject.get("cdnUrl").getAsString();
  }

  @Override
  public List<WxMaLiveResult.RoomInfo> getLiveInfos() throws WxErrorException {
    List<WxMaLiveResult.RoomInfo> results = new ArrayList<>();
    int start = 0;
    Integer limit = 80;
    Integer total = 0;
    WxMaLiveResult liveInfo;
    do {
      if (total != 0 && total <= start) {
        break;
      }
      liveInfo = getLiveInfo(start, limit);
      if (liveInfo == null) {
        return null;
      }
      results.addAll(liveInfo.getRoomInfos());
      total = liveInfo.getTotal();
      start = results.size();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        log.error("InterruptedException", e);
      }
    } while (results.size() <= total);

    return results;
  }

  @Override
  public WxMaLiveResult getLiveInfo(Integer start, Integer limit) throws WxErrorException {
    JsonObject jsonObject = getLiveInfo(start, limit, null);
    return WxMaLiveResult.fromJson(jsonObject.toString());
  }


  @Override
  public WxMaLiveResult getLiveReplay(String action, Integer roomId, Integer start, Integer limit) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(4);
    map.put("action", action);
    map.put("room_id", roomId);
    JsonObject jsonObject = getLiveInfo(start, limit, map);
    return WxMaLiveResult.fromJson(jsonObject.toString());
  }

  private JsonObject getLiveInfo(Integer start, Integer limit, Map<String, Object> map) throws WxErrorException {
    if (map == null) {
      map = new HashMap<>(2);
    }
    map.put("start", start);
    map.put("limit", limit);
    String responseContent = wxMaService.post(GET_LIVE_INFO, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return jsonObject;
  }

  @Override
  public WxMaLiveResult getLiveReplay(Integer roomId, Integer start, Integer limit) throws WxErrorException {
    return getLiveReplay("get_replay", roomId, start, limit);
  }

  @Override
  public boolean addGoodsToRoom(Integer roomId, List<Integer> goodsIds) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    map.put("ids", goodsIds);
    String responseContent = this.wxMaService.post(Room.ADD_GOODS, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public boolean addAssistant(Integer roomId, List<WxMaLiveAssistantInfo> users) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    map.put("users", users);
    String responseContent = this.wxMaService.post(Room.ADD_ASSISTANT, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public boolean modifyAssistant(Integer roomId, String username, String nickname) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    map.put("username", username);
    map.put("nickname", nickname);
    String responseContent = this.wxMaService.post(Room.MODIFY_ASSISTANT, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public boolean removeAssistant(Integer roomId, String username) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    map.put("username", username);
    String responseContent = this.wxMaService.post(Room.REMOVE_ASSISTANT, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return true;
  }

  @Override
  public List<WxMaAssistantResult.Assistant> getAssistantList(Integer roomId) throws WxErrorException {
    Map<String, Object> map = new HashMap<>(2);
    map.put(ROOM_ID, roomId);
    String responseContent = this.wxMaService.post(Room.GET_ASSISTANT_LIST, WxMaGsonBuilder.create().toJson(map));
    JsonObject jsonObject = GsonParser.parse(responseContent);
    if (jsonObject.get(ERR_CODE).getAsInt() != 0) {
      throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
    }
    return WxMaAssistantResult.fromJson(responseContent).getList();
  }

}
