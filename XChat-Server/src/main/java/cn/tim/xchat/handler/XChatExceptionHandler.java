package cn.tim.xchat.handler;

import cn.tim.xchat.exception.XChatException;
import cn.tim.xchat.vo.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class XChatExceptionHandler {
    /**
     * 处理控制层 or 服务层异常
     * @param e 捕获的异常
     * @return 处理结果
     */
    @ExceptionHandler(value = XChatException.class)
    @ResponseBody
    public R handlerXChatException(XChatException e){
        return R.error(e);
    }
}