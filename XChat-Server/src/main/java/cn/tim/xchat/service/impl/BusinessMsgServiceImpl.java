package cn.tim.xchat.service.impl;

import cn.tim.xchat.core.enums.MsgTypeEnum;
import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.FriendRequest;
import cn.tim.xchat.entity.MyFriend;
import cn.tim.xchat.entity.UserInfo;
import cn.tim.xchat.enums.business.RequestFriendEnum;
import cn.tim.xchat.netty.UserChannelHelper;
import cn.tim.xchat.repository.FriendRequestRepository;
import cn.tim.xchat.repository.MyFriendRepository;
import cn.tim.xchat.repository.UserInfoRepository;
import cn.tim.xchat.service.BusinessMsgService;
import cn.tim.xchat.utils.KeyUtil;
import cn.tim.xchat.vo.FriendRequestVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BusinessMsgServiceImpl implements BusinessMsgService {
    @Resource
    FriendRequestRepository friendRequestRepository;

    @Resource
    UserInfoRepository userInfoRepository;

    @Resource
    MyFriendRepository myFriendRepository;

    @Override
    public DataContentSerializer.DataContent getUserNewFriendRequest(String userId) {
        List<FriendRequestVO> ret = Lists.newArrayList();
        // 查找未处理的好友请求
        List<FriendRequest> requestList = friendRequestRepository.findAllByAcceptUserIdOrSendUserIdAndArgeeRet(
                userId, userId, RequestFriendEnum.UNHAND.getCode());

        FriendRequestVO friendVO;
        UserInfo userInfo;
        for(FriendRequest friendRequest: requestList) {
            boolean isMyRequest = false;
            Optional<UserInfo> userInfoOpt = Optional.empty();
            // 获取对方信息
            if(userId.equals(friendRequest.getSendUserId())){
                userInfoOpt = userInfoRepository.findById(friendRequest.getAcceptUserId());
                isMyRequest = true;
            }else if(userId.equals(friendRequest.getAcceptUserId())){
                userInfoOpt = userInfoRepository.findById(friendRequest.getSendUserId());
            }

            if(userInfoOpt.isEmpty()){
                log.error("未找到发起好友请求的用户，严重错误！");
                friendRequestRepository.deleteById(friendRequest.getSendUserId());
                continue;
            }

            userInfo = userInfoOpt.get();
            friendVO = getFriendVO(userInfo);
            friendVO.setItemId(friendRequest.getId());
            friendVO.setNotes(null);
            friendVO.setArgeeState(friendRequest.getArgeeRet());
            friendVO.setIsMyRequest(isMyRequest ? 0: 1);
            ret.add(friendVO);
        }

        DataContentSerializer.DataContent.ChatMessage chatMessage =
                DataContentSerializer.DataContent.ChatMessage.newBuilder()
                .setType(MsgTypeEnum.FRIEND_REQUEST_NEW.getCode())
                .setText(JSON.toJSONString(ret))
                .build();

        return DataContentSerializer.DataContent.newBuilder()
                .setAction(MsgActionEnum.BUSINESS.getCode())
                .setChatMessage(chatMessage)
                .build();
    }

    @Override
    public void handleFriendRequestMsg(DataContentSerializer.DataContent dataContent, Channel currentChannel) {
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        JSONObject dataJson = JSON.parseObject(chatMessage.getText());
        String itemId = dataJson.getString("itemId");
        int state = dataJson.getInteger("state");
        FriendRequest friendRequest = friendRequestRepository.getById(itemId);
        friendRequest.setArgeeRet(state);
        String acceptUserId = friendRequest.getAcceptUserId();
        String sendUserId = friendRequest.getSendUserId();
        // 保存好友请求表
        friendRequestRepository.save(friendRequest);

        UserChannelHelper.isOnlineAndSendMsg(sendUserId, getUserFriendRequestRet(sendUserId, friendRequest));

        // 保存好友关系 -> 只有同意的情况下才保存
        if(friendRequest.getArgeeRet() == RequestFriendEnum.AGREE.getCode()){
            MyFriend myFriend = new MyFriend();
            MyFriend tmpSA = myFriendRepository.findMyFriendByMyUserIdAndMyFriendId(friendRequest.getSendUserId(),
                    friendRequest.getAcceptUserId());
            MyFriend tmpAS = myFriendRepository.findMyFriendByMyUserIdAndMyFriendId(friendRequest.getAcceptUserId(),
                    friendRequest.getSendUserId());

            if(tmpSA != null && tmpAS != null) {
                log.error("严重错误，多次同意已经存在的好友请求!");
                return;
            }

            // 注意是两条数据
            myFriend.setId(KeyUtil.genUserKey());
            myFriend.setMyUserId(friendRequest.getSendUserId());
            myFriend.setMyFriendId(friendRequest.getAcceptUserId());
            myFriendRepository.save(myFriend);

            myFriend.setId(KeyUtil.genUserKey());
            myFriend.setMyUserId(friendRequest.getAcceptUserId());
            myFriend.setMyFriendId(friendRequest.getSendUserId());
            myFriendRepository.save(myFriend);
        }else if(friendRequest.getArgeeRet() == RequestFriendEnum.REFUSE.getCode()){
            log.info("用户" + friendRequest.getAcceptUserId() + "拒绝了" + friendRequest.getSendUserId());
        }
    }

    @Override
    public void sendNewFriendRequest(FriendRequest friendRequest) {
        UserChannelHelper.isOnlineAndSendMsg(friendRequest.getAcceptUserId(),
                getUserNewFriendRequest(friendRequest.getAcceptUserId()));

        // 不向发起者发送消息
//        UserChannelHelper.isOnlineAndSendMsg(friendRequest.getSendUserId(),
//                getUserNewFriendRequest(friendRequest.getSendUserId()));
    }

    @Override
    public DataContentSerializer.DataContent getUserFriendRequestRet(String userId, FriendRequest friendRequest) {
        FriendRequestVO friendVO;
        boolean isMyRequest = false;
        Optional<UserInfo> userInfoOpt = Optional.empty();
        // 获取对方信息
        if(userId.equals(friendRequest.getSendUserId())){
            userInfoOpt = userInfoRepository.findById(friendRequest.getAcceptUserId());
            isMyRequest = true;
        }else if(userId.equals(friendRequest.getAcceptUserId())){
            userInfoOpt = userInfoRepository.findById(friendRequest.getSendUserId());
        }

        if(userInfoOpt.isEmpty()){
            log.error("未找到发起好友请求的用户，严重错误！");
            friendRequestRepository.deleteById(friendRequest.getSendUserId());
            return null;
        }

        UserInfo userInfo = userInfoOpt.get();
        friendVO = getFriendVO(userInfo);
        friendVO.setItemId(friendRequest.getId());
        friendVO.setNotes(null);
        friendVO.setArgeeState(friendRequest.getArgeeRet());
        friendVO.setIsMyRequest(isMyRequest ? 0: 1);

        DataContentSerializer.DataContent.ChatMessage chatMessage =
                DataContentSerializer.DataContent.ChatMessage.newBuilder()
                        .setType(MsgTypeEnum.FRIEND_REQUEST_RET.getCode())
                        .setText(JSON.toJSONString(friendVO))
                        .build();

        return DataContentSerializer.DataContent.newBuilder()
                .setAction(MsgActionEnum.BUSINESS.getCode())
                .setChatMessage(chatMessage)
                .build();
    }

    static FriendRequestVO getFriendVO(UserInfo userInfo) {
        FriendRequestVO friendVO = new FriendRequestVO();
        friendVO.setUserId(userInfo.getId());
        friendVO.setEmail(userInfo.getEmail());
        friendVO.setFaceBigImage(userInfo.getFaceImageBig());
        friendVO.setFaceImage(userInfo.getFaceImage());
        friendVO.setQrCode(userInfo.getQrcode());
        friendVO.setNickname(userInfo.getNickname());
        friendVO.setUsername(userInfo.getUsername());
        return friendVO;
    }
}
