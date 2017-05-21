package com.tmall.wireless.jandfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class MethodReplace5_1 implements IMethodReplace {

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
        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);
        replaceReal(o1, o2);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);
        replaceReal(o1, o2);
    }


    private void replaceReal(Object src, Object dest) throws Exception {
        int methodSize = MethodSizeUtils.methodSize();
        //methodIndex need not replace,becase the process of finding method in vtable
        int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
        int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
        //why 1? index 0 is declaring_class, declaring_class need not replace.
        for (int i = 0, size = methodSize / 4; i < size; i++) {
            if (i != methodIndexOffsetIndex && i != declaringClassOffsetIndex) {
                int value = UnsafeProxy.getIntVolatile(dest, i * 4);
                UnsafeProxy.putIntVolatile(src, i * 4, value);
            }
        }
    }
}
