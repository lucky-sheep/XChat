package cn.tim.xchat.core.model;

import cn.tim.xchat.core.enums.MsgTypeEnum;
import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.utils.KeyUtil;
import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

public class DataContentSerializerTest {

    private byte[] protoBytes;
    private byte[] jsonBytes;

    /**
     * protobuf -> 141, time = 71
     * ============================
     * fastjson -> 236, time = 110
     * ============================
     */
    @Test
    public void serialize() {
        String testId = KeyUtil.genUniqueKey();
        String url = "https://zouchanglin.cn/" + KeyUtil.genUniqueKey();
        long start = System.currentTimeMillis();
        DataContentSerializer.DataContent.ChatMessage.Builder msgBuilder
                = DataContentSerializer.DataContent.ChatMessage.newBuilder();
        DataContentSerializer.DataContent.ChatMessage message = msgBuilder
                .setType(MsgTypeEnum.TEXT.getCode())
                .setAt("@" + testId)
                .setReply(testId)
                .setUrl(url)
                .build();
        DataContentSerializer.DataContent.Builder builder = DataContentSerializer.DataContent.newBuilder()
                .setSenderId(testId)
                .setReceiveId(testId)
                .setAction(MsgActionEnum.CONNECT.getCode())
                .setTimestamp((int) (System.currentTimeMillis() / 1000))
                .setChatMessage(message);
        DataContentSerializer.DataContent dataContent = builder.build();
        protoBytes = dataContent.toByteArray();

        long end = System.currentTimeMillis();
        System.out.println("protobuf -> " + protoBytes.length + ", time = " + (end - start));
        System.out.println("============================");

        start = end;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MsgTypeEnum.TEXT.getCode());
        chatMessage.setAt("@" + testId);
        chatMessage.setUrl(url);
        chatMessage.setReply(testId);

        DataContent content = new DataContent();
        content.setSenderId(testId);
        content.setReceiveId(testId);
        content.setAction(MsgActionEnum.CONNECT.getCode());
        content.setTimestamp((int) (System.currentTimeMillis() / 1000));
        content.setChatMessage(chatMessage);

        jsonBytes = JSON.toJSONString(content).getBytes();
        end = System.currentTimeMillis();
        System.out.println("fastjson -> " + jsonBytes.length + ", time = " + (end - start));
        System.out.println("============================");
    }

    /**
     * protobuf -> time = 9
     * fastjson -> time = 69
     */
    @Test
    public void deserialize() throws InvalidProtocolBufferException {
        serialize();
        long start = System.currentTimeMillis();
        DataContentSerializer.DataContent dataContent = DataContentSerializer.DataContent.parseFrom(protoBytes);
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        long end = System.currentTimeMillis();
        System.out.println(dataContent);
        System.out.println("protobuf -> time = " + (end - start));

        start = end;
        DataContent content = JSON.parseObject(new String(jsonBytes), DataContent.class);
        end = System.currentTimeMillis();
        System.out.println("fastjson -> time = " + (end - start));

        System.out.println(content);
    }


    @Test
    public void xx(){
        DataContentSerializer.DataContent.Builder builder = DataContentSerializer.DataContent.newBuilder()
                .setAction(MsgActionEnum.KEEPALIVE.type);
        DataContentSerializer.DataContent dataContent = builder.build();
        System.out.println(dataContent.toByteArray().length);
        System.out.println(new String(dataContent.toByteArray()));
        System.out.println(dataContent.toByteArray()[0]);
        System.out.println(dataContent.toByteArray()[1]);
    }
}