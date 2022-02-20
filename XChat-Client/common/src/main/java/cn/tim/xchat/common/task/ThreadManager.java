package cn.tim.xchat.common.task;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {

    private static ThreadPoolExecutor executor;
    private ThreadManager(){}
    private static ThreadManager INSTANCE;
    public static ThreadManager getInstance(){
        if(INSTANCE == null) {
            synchronized (ThreadManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ThreadManager();
                    executor = new ThreadPoolExecutor(2, 4,
                            10, TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>());
                }
            }
        }
        return INSTANCE;
    }

    public void runTask(Runnable runnable){
        executor.execute(runnable);
    }

    public void destroy(){
        executor.shutdownNow();
    }
}
