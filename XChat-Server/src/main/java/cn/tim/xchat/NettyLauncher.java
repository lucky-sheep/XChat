package cn.tim.xchat;

import cn.tim.xchat.netty.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyLauncher implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			try {
				WebSocketServer.getInstance().start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
