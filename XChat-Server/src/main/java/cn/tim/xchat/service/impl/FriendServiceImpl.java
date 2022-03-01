package cn.tim.xchat.service.impl;

import cn.tim.xchat.entity.FriendRequest;
import cn.tim.xchat.entity.MyFriend;
import cn.tim.xchat.entity.UserInfo;
import cn.tim.xchat.enums.ResultEnum;
import cn.tim.xchat.enums.business.RequestFriendEnum;
import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.repository.FriendRequestRepository;
import cn.tim.xchat.repository.MyFriendRepository;
import cn.tim.xchat.repository.UserInfoRepository;
import cn.tim.xchat.service.BusinessMsgService;
import cn.tim.xchat.service.FriendService;
import cn.tim.xchat.utils.KeyUtil;
import cn.tim.xchat.utils.RegexUtil;
import cn.tim.xchat.utils.SpringUtil;
import cn.tim.xchat.vo.FriendRequestVO;
import cn.tim.xchat.vo.FriendVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class FriendServiceImpl implements FriendService {
    @Resource
    MyFriendRepository friendRepository;

    @Resource
    UserInfoRepository userInfoRepository;

    @Resource
    FriendRequestRepository friendRequestRepository;

    @Resource
    BusinessMsgService businessMsgService;

    AtomicInteger count = new AtomicInteger(0);

    @Override
    public List<FriendVO> getAllFriend(String userId) {
        List<MyFriend> friendList = friendRepository.findAllByMyUserId(userId);
        ArrayList<FriendVO> ret = Lists.newArrayListWithCapacity(friendList.size());
        FriendVO friendVO;
        if(friendList.size() > 0) {
            for(MyFriend myFriend: friendList){
                UserInfo userInfo = userInfoRepository.getById(myFriend.getMyFriendId());
                friendVO = new FriendVO();
                friendVO.setUserId(userInfo.getId());
                friendVO.setEmail(userInfo.getEmail());
                friendVO.setFaceBigImage(userInfo.getFaceImageBig());
                friendVO.setFaceImage(userInfo.getFaceImage());
                friendVO.setQrCode(userInfo.getQrcode());
                friendVO.setNickname(userInfo.getNickname());
                friendVO.setUsername(userInfo.getUsername());

                friendVO.setNotes(myFriend.getNotes());
                friendVO.setItemId(myFriend.getId());
                ret.add(friendVO);
            }
        }
        return ret;
    }

    @Override
    public FriendRequestVO requestFriend(String userId, String reqUserNameOrEmail) {
        log.info("count = " + count.getAndIncrement());
        boolean isEmail = RegexUtil.isEmail(reqUserNameOrEmail);
        UserInfo userInfo;
        if(isEmail){
            userInfo = userInfoRepository.findUserInfoByEmail(reqUserNameOrEmail);
        }else {
            userInfo = userInfoRepository.findUserInfoByUsername(reqUserNameOrEmail);
        }
        // 先查找有没有用户
        if(userInfo != null) {
            // 再查找是否已经是好友
            MyFriend myFriend = friendRepository.findMyFriendByMyUserIdAndMyFriendId(userId, userInfo.getId());
            if(myFriend == null) {
                // 是否已经申请过了
                FriendRequest friendRequest = friendRequestRepository.
                        findFriendRequestBySendUserIdAndAcceptUserId(userId, userInfo.getId());
                if(friendRequest == null){
                    friendRequest = new FriendRequest();
                    friendRequest.setId(KeyUtil.genUserKey());
                    friendRequest.setRequestDatetime(Instant.now());
                    friendRequest.setSendUserId(userId);
                    friendRequest.setAcceptUserId(userInfo.getId());
                    friendRequest.setArgeeRet(RequestFriendEnum.UNHAND.getCode());
                    FriendRequest save = friendRequestRepository.save(friendRequest);
                    log.info("申请好友 Success, save = " + save);

                    // 如果对方在线，就发送提醒
                    businessMsgService.sendNewFriendRequest(friendRequest);
                    FriendRequestVO friendRequestVO = new FriendRequestVO();
                    BeanUtils.copyProperties(userInfoRepository.getById(
                            friendRequest.getAcceptUserId()), friendRequestVO);
                    friendRequestVO.setIsMyRequest(0);
                    friendRequestVO.setItemId(friendRequest.getId());
                    friendRequestVO.setNotes(null);
                    friendRequestVO.setArgeeState(RequestFriendEnum.UNHAND.getCode());
                    return friendRequestVO;
                }else {
                    throw new XChatException(ResultEnum.REPEAT_REQUEST_FRIENDS);
                }
            }else {
                throw new XChatException(ResultEnum.YOU_ARE_ALREADY_FRIENDS);
            }
        }else {
            throw new XChatException(ResultEnum.USER_DOES_NOT_EXIST);
        }
    }
}
