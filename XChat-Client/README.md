## 项目简介
项目是一个IM客户端，支持私聊、群聊，后端由Netty开发

## 踩坑

1、LitePal数据库升级问题，DEBUG期间直接卸载App，否则数据Bean结构改变后就报错

2、Netty的数据帧格式问题，引入ProtoBuf，需要先解析成二进制格式帧对象，再通过ProtoBuf解码

3、网络请求Token拦截器问题

4、EventBus的粘性事件与先后顺序问题

5、键盘抖动问题

TODO：

1、会话列表

2、相互对话

