package cn.tim.xchat.service.impl;

import cn.tim.xchat.core.enums.MsgTypeEnum;
import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.FriendRequest;
import cn.tim.xchat.entity.UserInfo;
import cn.tim.xchat.enums.business.RequestFriendEnum;
import cn.tim.xchat.repository.FriendRequestRepository;
import cn.tim.xchat.repository.UserInfoRepository;
import cn.tim.xchat.service.BusinessMsgService;
import cn.tim.xchat.vo.FriendRequestVO;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
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

    @Override
    public DataContentSerializer.DataContent getUserFriendRequest(String userId) {
        List<FriendRequestVO> ret = Lists.newArrayList();
        // 查找未处理的好友请求
        List<FriendRequest> requestList = friendRequestRepository.findAllByAcceptUserIdAndArgeeRet(userId,
                RequestFriendEnum.UNHAND.ordinal());

        FriendRequestVO friendVO;
        UserInfo userInfo;
        for(FriendRequest friendRequest: requestList) {
            Optional<UserInfo> userInfoOpt = userInfoRepository.findById(friendRequest.getSendUserId());
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
            ret.add(friendVO);
        }

        DataContentSerializer.DataContent.ChatMessage chatMessage =
                DataContentSerializer.DataContent.ChatMessage.newBuilder()
                .setType(MsgTypeEnum.FRIEND_REQUEST.ordinal())
                .setText(JSON.toJSONString(ret))
                .build();

        return DataContentSerializer.DataContent.newBuilder()
                .setAction(MsgActionEnum.BUSINESS.getCode())
                .setChatMessage(chatMessage)
                .build();
    }

    static FriendRequestVO getFriendVO(UserInfo userInfo) {
        FriendRequestVO friendVO = new FriendRequestVO();
//        friendVO.setItemId();
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
