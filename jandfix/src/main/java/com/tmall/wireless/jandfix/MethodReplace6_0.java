package com.tmall.wireless.jandfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class MethodReplace6_0 implements IMethodReplace {

    static Field artMethodField;

    static {
        try {
            Class absMethodClass = Class.forName("java.lang.reflect.AbstractMethod");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void replace(Method src, Method dest) throws Exception {
        //static 需要提前初始化
        if (Modifier.isStatic(src.getModifiers())) {
            Class.forName(src.getDeclaringClass().getName(), true, this.getClass().getClassLoader());
            Class.forName(dest.getDeclaringClass().getName(), true, this.getClass().getClassLoader());
        }
        long artMethodSrc = (long) artMethodField.get(src);
        long artMethodDest = (long) artMethodField.get(dest);

        replaceReal(artMethodSrc, artMethodDest);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        long artMethodSrc = (long) artMethodField.get(src);
        long artMethodDest = (long) artMethodField.get(dest);
        replaceReal(artMethodSrc, artMethodDest);
    }

    private void replaceReal(long src, long dest) throws Exception {
        int methodSize = MethodSizeUtils.methodSize();
        int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
        int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
        // index 0 is declaring_class, declaring_class need not replace.
        for (int i = 0, size = methodSize / 4; i < size; i++) {
            if (i != methodIndexOffsetIndex && i != declaringClassOffsetIndex) {
                int value = UnsafeProxy.getIntVolatile(dest + i * 4);
                UnsafeProxy.putIntVolatile(src + i * 4, value);
            }
        }
    }
}
