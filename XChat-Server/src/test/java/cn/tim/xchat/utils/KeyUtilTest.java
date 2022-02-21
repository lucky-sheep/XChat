package cn.tim.xchat.utils;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class KeyUtilTest {

    @Test
    public void genUserKey() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(KeyUtil.genUserKey());
        }
    }

    @Test
    public void list() {
        ArrayList<Object> objects = new ArrayList<>();
        for(Object o: objects){
            System.out.println(o);
        }
    }
}