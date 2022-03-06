package cn.tim.xchat.chat.send;

import cn.tim.xchat.chat.send.CallBackAsync;

public interface SendMsgAction {
    /**
     * 发送文本类型消息
     * @param acceptUserId
     * @param msg
     * @return send ret
     */
    boolean sendTextMsg(String acceptUserId, String msg, CallBackAsync callBackAsync);


    boolean sendVoiceMSg(String acceptUserId);
}
