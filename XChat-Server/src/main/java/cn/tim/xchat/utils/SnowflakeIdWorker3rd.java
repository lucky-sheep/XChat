package cn.tim.xchat.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将产生的Id类型更改为Integer 32bit <br>
 * 把时间戳的单位改为分钟,使用25个比特的时间戳（分钟） <br>
 * 去掉机器ID和数据中心ID <br>
 * 7个比特作为自增值，即2的7次方等于128。
 */
public class SnowflakeIdWorker3rd {

    /** 分钟内序列(0~127) */
    private int sequence = 0;
    private int laterSequence = 0;

    /** 上次生成ID的时间戳 */
    private int lastTimestamp = -1;

    private final MinuteCounter counter = new MinuteCounter();

    /** 预支时间标志位 */
    boolean isAdvance = false;

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized int nextId() {
        int timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if(timestamp > counter.get()) {
            counter.set(timestamp);
            isAdvance = false;
        }
        long sequenceBits = 7L;
        int epoch = 25771200;
        if (lastTimestamp == timestamp || isAdvance) {
            int sequenceMask = ~(-1 << sequenceBits);
            if(!isAdvance) {
                sequence = (sequence + 1) & sequenceMask;
            }

            // 分钟内自增列溢出
            if (sequence == 0) {
                // 预支下一个分钟,获得新的时间戳
                isAdvance = true;
                int laterTimestamp = counter.get();
                if (laterSequence == 0) {
                    laterTimestamp = counter.incrementAndGet();
                }

                int nextId = ((laterTimestamp - epoch) << sequenceBits) //
                        | laterSequence;
                laterSequence = (laterSequence + 1) & sequenceMask;
                return nextId;
            }
        }
        // 时间戳改变，分钟内序列重置
        else {
            sequence = 0;
            laterSequence = 0;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成32位的ID
        return ((timestamp - epoch) << sequenceBits) //
                | sequence;
    }

    /**
     * 返回以分钟为单位的当前时间
     * @return 当前时间(分钟)
     */
    protected int timeGen() {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000 / 60);
        return Integer.parseInt(timestamp);
    }

    static class MinuteCounter {
        private static final int MASK = 0x7FFFFFFF;
        private final AtomicInteger atom;

        public MinuteCounter() {
            atom = new AtomicInteger(0);
        }

        public final int incrementAndGet() {
            return atom.incrementAndGet() & MASK;
        }

        public int get() {
            return atom.get() & MASK;
        }

        public void set(int newValue) {
            atom.set(newValue & MASK);
        }
    }

}

