## 项目简介
项目是一个IM客户端，支持私聊、群聊，后端由Netty开发

## 踩坑

1、LitePal数据库升级问题，DEBUG期间直接卸载App，否则数据Bean结构改变后就报错

2、Netty的数据帧格式问题，引入ProtoBuf，需要先解析成二进制格式帧对象，再通过ProtoBuf解码

2、Channel与对应的UserID自动释放问题

3、网络请求Token拦截器问题

4、EventBus的粘性事件问题

5、键盘抖动问题

6、启动优化与白屏优化

## 实际效果
完善之后在 bilibili 可看到完整的效果视频

## TODO

1、会话列表UI

2、相互对话UI

3、压力测试 + 阿里云部署

## 压测结果
