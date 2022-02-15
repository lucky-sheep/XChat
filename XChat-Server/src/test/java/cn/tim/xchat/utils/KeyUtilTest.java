package cn.tim.xchat.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class KeyUtilTest {

    @Test
    public void genUserKey() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(KeyUtil.genUserKey());
        }
    }
}