package com.tmall.wireless.jandfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.os.Build;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class MethodReplaceProxy implements IMethodReplace {

    private IMethodReplace realReplace;

    private MethodReplaceProxy() {
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21) {
            realReplace = new MethodReplaceDalvik4_0();
        } else if (Build.VERSION.SDK_INT == 21) {
            realReplace = new MethodReplace5_0();
        } else if (Build.VERSION.SDK_INT == 22) {
            realReplace = new MethodReplace5_1();
        } else if (Build.VERSION.SDK_INT >= 23) {
            realReplace = new MethodReplace6_0();
        }
    }

    public static MethodReplaceProxy instance() {
        return Holder.instance;
    }

    @Override
    public void replace(Method src, Method dest) throws Exception {
        checkMethod(src, dest);
        realReplace.replace(src, dest);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        checkConstructor(src, dest);
        realReplace.replace(src, dest);
    }

    private static class Holder {
        static MethodReplaceProxy instance = new MethodReplaceProxy();
    }

    private void checkMethod(Method src, Method dest) {
        if (src == null || dest == null)
            throw new IllegalArgumentException();
        if (src.getReturnType() != dest.getReturnType()) {
            throw new RuntimeException("返回类型必须一致");
        }

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new RuntimeException("异常类型必须一致");
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new RuntimeException("参数类型必须一致");
        }

    }

    private void checkConstructor(Constructor src, Constructor dest) {
        if (src == null || dest == null)
            throw new IllegalArgumentException();

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new RuntimeException("异常类型必须一致");
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new RuntimeException("参数类型必须一致");
        }

    }

    private boolean checkClasses(Class[] srcClasses, Class[] destClasses) {
        if (srcClasses == null && destClasses == null) {
            return true;
        }
        if (srcClasses == null || destClasses == null) {
            return false;
        }
        if (srcClasses.length != destClasses.length) {
            return false;
        }

        for (int i = 0, size = srcClasses.length; i < size; i++) {
            if (srcClasses[i] != destClasses[i]) {
                return false;
            }
        }
        return true;
    }

}
