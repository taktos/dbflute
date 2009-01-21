/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.s2dao.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.seasar.dbflute.s2dao.beans.exception.TnIllegalPropertyRuntimeException;

/**
 * {Refers to a S2Dao's class and Extends it}
 * @author jflute
 */
public interface TnPropertyDesc {

    TnBeanDesc getBeanDesc();
    
    String getPropertyName();

    Class<?> getPropertyType();

    Method getReadMethod();

    void setReadMethod(Method readMethod);

    boolean hasReadMethod();

    Method getWriteMethod();

    void setWriteMethod(Method writeMethod);

    boolean hasWriteMethod();

    boolean isReadable();

    boolean isWritable();

    Field getField();

    void setField(Field field);

    Object getValue(Object target) throws IllegalStateException;

    void setValue(Object target, Object value) throws TnIllegalPropertyRuntimeException, IllegalStateException;

    Object convertIfNeed(Object value);
}
