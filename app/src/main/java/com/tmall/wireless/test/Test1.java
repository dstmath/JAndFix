package com.tmall.wireless.test;

/**
 * Created by jingchaoqinjc on 17/3/20.
 */

public class Test1 {

    public Test1() {
        System.out.println("Test1");
    }

    public String string() {
        return "Test1";
    }

    private String privateString(){
        return "privateTest1";
    }

    protected String protectedString(){
        return "protectedTest1";
    }


    public static String staticString() {
        return "StaticTest1";
    }

}
