package cn.tim.xchat.exception;

import cn.tim.xchat.enums.ResultEnum;
import lombok.Getter;

@Getter
public class XChatException extends RuntimeException{
    private Integer code;

    public XChatException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public XChatException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}