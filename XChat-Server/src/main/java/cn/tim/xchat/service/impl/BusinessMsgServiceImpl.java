package cn.tim.xchat.service.impl;

import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.FriendRequest;
import cn.tim.xchat.repository.FriendRequestRepository;
import cn.tim.xchat.service.BusinessMsgService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusinessMsgServiceImpl implements BusinessMsgService {
    @Resource
    FriendRequestRepository friendRequestRepository;

    @Override
    public List<DataContentSerializer.DataContent> getUserFriendRequest(String userId) {
        List<DataContentSerializer.DataContent> ret = Lists.newArrayList();
        List<FriendRequest> requestList = friendRequestRepository.findAllByAcceptUserId(userId);
        for(FriendRequest friendRequest: requestList) {

        }
        return null;
    }
}
