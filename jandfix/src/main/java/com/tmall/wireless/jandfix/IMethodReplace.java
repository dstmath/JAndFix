package com.tmall.wireless.jandfix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public interface IMethodReplace {

    void replace(Method src, Method dest) throws Exception;

    void replace(Constructor src, Constructor dest) throws Exception;

}
