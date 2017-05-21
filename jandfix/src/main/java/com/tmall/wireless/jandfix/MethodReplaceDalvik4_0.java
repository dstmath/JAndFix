package com.tmall.wireless.jandfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class MethodReplaceDalvik4_0 implements IMethodReplace {

    private final static int DIRECT_METHOD_OFFSET = 25;
    private final static int VIRTUAL_METHOD_OFFSET = 27;
//    private final static int METHOD_SIZE_BYTE = 56;

    static Field methodSlotField;
    static Field constructSlotField;
    static int methodSize = 56;

    static {
        try {
            methodSlotField = Method.class.getDeclaredField("slot");
            methodSlotField.setAccessible(true);

            constructSlotField = Constructor.class.getDeclaredField("slot");
            constructSlotField.setAccessible(true);

            int directMethodAddr = UnsafeProxy.getIntVolatile(MethodSizeCase.class, DIRECT_METHOD_OFFSET * 4);
            int declaringClassAddr = (int) UnsafeProxy.getObjectAddress(MethodSizeCase.class);
            int count = 0;
            int elementCount = 0;
            //通过扫面内存来确认Method的结构体大小
            for (int i = 0; i < 100; i++) {
                int value = UnsafeProxy.getIntVolatile(directMethodAddr + i * 4);
                if (value == declaringClassAddr) {
                    count++;
                }
                if (count == 2) {
                    break;
                }
                if (count == 1) {
                    elementCount++;
                }
            }
            methodSize = 4 * elementCount;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(Method src, Method dest) throws Exception {
        Class classSrc = src.getDeclaringClass();
        Class classDest = dest.getDeclaringClass();
        long virtualMethodSrcAddr = 0;
        long virtualMethodDestAddr = 0;

        if (isDirect(src)) {
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, DIRECT_METHOD_OFFSET * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, DIRECT_METHOD_OFFSET * 4);
        } else {
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, VIRTUAL_METHOD_OFFSET * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, VIRTUAL_METHOD_OFFSET * 4);
        }

        //slot is methodIndex in action
        /*
        *
        * Convert a slot number to a method pointer.
        Method* dvmSlotToMethod(ClassObject* clazz, int slot)
        {
            if (slot < 0) {
                slot = -(slot+1);
                assert(slot < clazz->directMethodCount);
                return &clazz->directMethods[slot];
            } else {
                assert(slot < clazz->virtualMethodCount);
                return &clazz->virtualMethods[slot];
            }
        }
        */
        int slotSrc = ((Integer) methodSlotField.get(src)).intValue();
        if (slotSrc < 0) {
            slotSrc = -(slotSrc + 1);
        }
        int slotDest = ((Integer) methodSlotField.get(dest)).intValue();
        if (slotDest < 0) {
            slotDest = -(slotDest + 1);
        }

        replaceReal(virtualMethodSrcAddr + slotSrc * methodSize, virtualMethodDestAddr + slotDest * methodSize, src.getDeclaringClass());
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        Class classSrc = src.getDeclaringClass();
        Class classDest = dest.getDeclaringClass();
        long virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, DIRECT_METHOD_OFFSET * 4);
        long virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, DIRECT_METHOD_OFFSET * 4);

        //slot is methodIndex in action
        /*
        *
        * Convert a slot number to a method pointer.
        Method* dvmSlotToMethod(ClassObject* clazz, int slot)
        {
            if (slot < 0) {
                slot = -(slot+1);
                assert(slot < clazz->directMethodCount);
                return &clazz->directMethods[slot];
            } else {
                assert(slot < clazz->virtualMethodCount);
                return &clazz->virtualMethods[slot];
            }
        }
        */
        int slotSrc = ((Integer) constructSlotField.get(src)).intValue();
        if (slotSrc < 0) {
            slotSrc = -(slotSrc + 1);
        }
        int slotDest = ((Integer) constructSlotField.get(dest)).intValue();
        if (slotDest < 0) {
            slotDest = -(slotDest + 1);
        }

        replaceReal(virtualMethodSrcAddr + slotSrc * methodSize, virtualMethodDestAddr + slotDest * methodSize, src.getDeclaringClass());
    }

    private void replaceReal(long src, long dest, Class declaringClass) throws Exception {
        //why 1? index 0 is declaring_class, declaring_class need not replace.
        int declaringClassAddr = (int) UnsafeProxy.getObjectAddress(declaringClass);
        for (int i = 0, size = methodSize / 4; i < size; i++) {
            int srcValue = UnsafeProxy.getIntVolatile(src + i * 4);
            if (srcValue != declaringClassAddr) {
                int value = UnsafeProxy.getIntVolatile(dest + i * 4);
                UnsafeProxy.putIntVolatile(src + i * 4, value);
            }
        }
    }

    private boolean isDirect(Method method) {
        int modifier = method.getModifiers();
        return Modifier.isFinal(modifier) || Modifier.isStatic(modifier)
                || Modifier.isPrivate(modifier);
    }
}
