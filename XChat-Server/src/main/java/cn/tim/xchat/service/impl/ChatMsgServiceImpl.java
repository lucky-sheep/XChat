package cn.tim.xchat.service.impl;

import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.core.enums.MsgSignEnum;
import cn.tim.xchat.core.enums.MsgTypeEnum;
import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.ChatMsg;
import cn.tim.xchat.repository.ChatMsgRepository;
import cn.tim.xchat.service.ChatMsgService;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatMsgServiceImpl implements ChatMsgService {

    @Resource
    ChatMsgRepository chatMsgRepository;

    @Override
    public void sendUserNewMsgBeforeUserOnline(String userId, Channel currentChannel) {
        List<ChatMsg> chatMsg = chatMsgRepository.findAllByAcceptUserIdAndSignFlag(userId, MsgSignEnum.UN_RECEIVE.getCode());
        DataContentSerializer.DataContent.ChatMessage.Builder chatMsgBuilder = DataContentSerializer.DataContent.ChatMessage
                .newBuilder();
        DataContentSerializer.DataContent.Builder msgBuilder = DataContentSerializer.DataContent.newBuilder();
        for(ChatMsg msg: chatMsg){
            packMsgByType(chatMsgBuilder, msg);
            currentChannel.writeAndFlush(
                    msgBuilder.setAction(MsgActionEnum.CHAT.type)
                            .setChatMessage(chatMsgBuilder)
                            .setSenderId(msg.getSendUserId())
                            .setReceiveId(msg.getAcceptUserId())
                            .setTimestamp((int) msg.getCreateTime().getEpochSecond())
                            .build()
            );
            msg.setSignFlag(MsgSignEnum.RECEIVED.getCode());
        }
        // 更新本地数据库
        chatMsgRepository.saveAll(chatMsg);
    }

    @Override
    public void handleReceivedMsg(DataContentSerializer.DataContent dataContent, Channel currentChannel) {
        String receiveId = dataContent.getReceiveId();
        String senderId = dataContent.getSenderId();
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        int type = chatMessage.getType();
        ChatMsg chatMsg = unpackMsgByType(dataContent);

    }


    /**
     * 根据消息类型从Protobuf解封装
     * @param dataContent protobuf
     * @return chatMsg
     */
    private ChatMsg unpackMsgByType(DataContentSerializer.DataContent dataContent) {
        ChatMsg chatMsg = new ChatMsg();
        // TODO 根据消息类型从Protobuf解封装
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        int type = chatMessage.getType();
        if(type == MsgTypeEnum.TEXT.getCode()){
            // save

        }
        return chatMsg;
    }

    /**
     * 根据消息类型封装Protobuf
     * @param chatMsgBuilder builder
     * @param msg 文本消息
     */
    private void packMsgByType(DataContentSerializer.DataContent.ChatMessage.Builder chatMsgBuilder, ChatMsg msg) {
        int type = msg.getType();
        chatMsgBuilder.setType(type);
        if(type == MsgTypeEnum.TEXT.getCode()){
            chatMsgBuilder.setText(msg.getMsg());
        }
    }
}
