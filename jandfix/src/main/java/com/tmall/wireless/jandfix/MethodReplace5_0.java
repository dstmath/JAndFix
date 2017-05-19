package com.tmall.wireless.jandfix;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class MethodReplace5_0 implements IMethodReplace {

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
        Class c = src.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                if (!f.getName().equals("declaringClass") && !f.getName().equals("methodIndex"))
                    f.set(src, f.get(dest));
            }
            c = c.getSuperclass();
        }

//        Class classClass = Class.forName("java.lang.reflect.ArtMethod");
//        Field accessFlagsFiled = classClass.getDeclaredField("accessFlags");
//        accessFlagsFiled.setAccessible(true);
//        int accessFlags = (Integer) accessFlagsFiled.get(src);
//        if (Modifier.isPrivate(accessFlags)) {
//            accessFlags = accessFlags & (~Modifier.PRIVATE) | (Modifier.PUBLIC);
//            accessFlagsFiled.set(src, accessFlags);
//        }
    }
}
