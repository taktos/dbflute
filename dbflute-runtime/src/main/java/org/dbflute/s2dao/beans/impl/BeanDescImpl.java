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
package org.dbflute.s2dao.beans.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dbflute.helper.StringKeyMap;
import org.dbflute.s2dao.beans.BeanDesc;
import org.dbflute.s2dao.beans.PropertyDesc;
import org.dbflute.s2dao.beans.exception.ConstructorNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.FieldNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.MethodNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.PropertyNotFoundRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ConstructorUtil;
import org.seasar.framework.util.DoubleConversionUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.FloatConversionUtil;
import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.LongConversionUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ShortConversionUtil;
import org.seasar.framework.util.StringUtil;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class BeanDescImpl implements BeanDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class<?>[] EMPTY_PARAM_TYPES = new Class<?>[0];

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Class<?> beanClass;
    private Constructor<?>[] constructors;
    
    private StringKeyMap<PropertyDesc> propertyDescMap = StringKeyMap.createAsCaseInsensitive();
    private Map<String, Method[]> methodsMap = new ConcurrentHashMap<String, Method[]>();
    private Map<String, Field> fieldMap = new ConcurrentHashMap<String, Field>();

    private transient Set<String> invalidPropertyNames = new HashSet<String>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BeanDescImpl(Class<?> beanClass) {
        if (beanClass == null) {
            String msg = "The argument 'beanClass' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        this.beanClass = beanClass;
        constructors = beanClass.getConstructors();
        setupPropertyDescs();
        setupMethods();
        setupFields();
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public Class<?> getBeanClass() {
        return beanClass;
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public Constructor<?> getSuitableConstructor(Object[] args) throws ConstructorNotFoundRuntimeException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        Constructor<?> constructor = findSuitableConstructor(args);
        if (constructor != null) {
            return constructor;
        }
        constructor = findSuitableConstructorAdjustNumber(args);
        if (constructor != null) {
            return constructor;
        }
        throw new ConstructorNotFoundRuntimeException(beanClass, args);
    }

    public Constructor<?> getConstructor(final Class<?>[] paramTypes) {
        for (int i = 0; i < constructors.length; ++i) {
            if (Arrays.equals(paramTypes, constructors[i].getParameterTypes())) {
                return constructors[i];
            }
        }
        throw new ConstructorNotFoundRuntimeException(beanClass, paramTypes);
    }

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    public boolean hasPropertyDesc(String propertyName) {
        return getPropertyDescInternally(propertyName) != null;
    }

    public PropertyDesc getPropertyDesc(String propertyName) throws PropertyNotFoundRuntimeException {
        PropertyDesc pd = getPropertyDescInternally(propertyName);
        if (pd == null) {
            throw new PropertyNotFoundRuntimeException(beanClass, propertyName);
        }
        return pd;
    }

    private PropertyDesc getPropertyDescInternally(String propertyName) {
        return propertyDescMap.get(propertyName);
    }

    public int getPropertyDescSize() {
        return propertyDescMap.size();
    }

    public List<String> getProppertyNameList() {
        return new ArrayList<String>(propertyDescMap.keySet());
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public boolean hasField(String fieldName) {
        return fieldMap.get(fieldName) != null;
    }

    public Field getField(String fieldName) {
        Field field = (Field) fieldMap.get(fieldName);
        if (field == null) {
            throw new FieldNotFoundRuntimeException(beanClass, fieldName);
        }
        return field;
    }

    public int getFieldSize() {
        return fieldMap.size();
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public Method getMethod(final String methodName) {
        return getMethod(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethodNoException(final String methodName) {
        return getMethodNoException(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethod(final String methodName, final Class<?>[] paramTypes) {
        Method method = getMethodNoException(methodName, paramTypes);
        if (method != null) {
            return method;
        }
        throw new MethodNotFoundRuntimeException(beanClass, methodName, paramTypes);
    }

    public Method getMethodNoException(final String methodName, final Class<?>[] paramTypes) {
        final Method[] methods = (Method[]) methodsMap.get(methodName);
        if (methods == null) {
            return null;
        }
        for (int i = 0; i < methods.length; ++i) {
            if (Arrays.equals(paramTypes, methods[i].getParameterTypes())) {
                return methods[i];
            }
        }
        return null;
    }

    public Method[] getMethods(String methodName) throws MethodNotFoundRuntimeException {

        Method[] methods = (Method[]) methodsMap.get(methodName);
        if (methods == null) {
            throw new MethodNotFoundRuntimeException(beanClass, methodName, null);
        }
        return methods;
    }

    public boolean hasMethod(String methodName) {
        return methodsMap.get(methodName) != null;
    }

    public String[] getMethodNames() {
        return (String[]) methodsMap.keySet().toArray(new String[methodsMap.size()]);
    }

    //
    //    public String[] getConstructorParameterNames(final Class[] parameterTypes) {
    //        return getConstructorParameterNames(getConstructor(parameterTypes));
    //    }
    //
    //    public String[] getConstructorParameterNames(final Constructor constructor) {
    //        if (constructorParameterNamesCache == null) {
    //            constructorParameterNamesCache = createConstructorParameterNamesCache();
    //        }
    //
    //        if (!constructorParameterNamesCache.containsKey(constructor)) {
    //            throw new ConstructorNotFoundRuntimeException(beanClass, constructor.getParameterTypes());
    //        }
    //        return (String[]) constructorParameterNamesCache.get(constructor);
    //
    //    }
    //
    //    public String[] getMethodParameterNamesNoException(final String methodName, final Class[] parameterTypes) {
    //        return getMethodParameterNamesNoException(getMethod(methodName, parameterTypes));
    //    }
    //
    //    public String[] getMethodParameterNames(final String methodName, final Class[] parameterTypes) {
    //        return getMethodParameterNames(getMethod(methodName, parameterTypes));
    //    }
    //
    //    public String[] getMethodParameterNames(final Method method) {
    //        String[] names = getMethodParameterNamesNoException(method);
    //        if (names == null || names.length != method.getParameterTypes().length) {
    //            throw new IllegalDiiguRuntimeException();
    //        }
    //        return names;
    //    }
    //
    //    public String[] getMethodParameterNamesNoException(final Method method) {
    //        if (methodParameterNamesCache == null) {
    //            methodParameterNamesCache = createMethodParameterNamesCache();
    //        }
    //
    //        if (!methodParameterNamesCache.containsKey(method)) {
    //            throw new MethodNotFoundRuntimeException(beanClass, method.getName(), method.getParameterTypes());
    //        }
    //        return (String[]) methodParameterNamesCache.get(method);
    //    }
    //
    //    private Map createConstructorParameterNamesCache() {
    //        final Map map = new HashMap();
    //        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
    //        for (int i = 0; i < constructors.length; ++i) {
    //            final Constructor constructor = constructors[i];
    //            if (constructor.getParameterTypes().length == 0) {
    //                map.put(constructor, EMPTY_STRING_ARRAY);
    //                continue;
    //            }
    //            final CtClass clazz = ClassPoolUtil.toCtClass(pool, constructor.getDeclaringClass());
    //            final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, constructor.getParameterTypes());
    //            try {
    //                final String[] names = getParameterNames(clazz.getDeclaredConstructor(parameterTypes));
    //                map.put(constructor, names);
    //            } catch (final NotFoundException e) {
    //                _log.debug("The constructor was not found: class=" + beanClass.getName() + " constructor="
    //                        + constructor);
    //            }
    //        }
    //        return map;
    //    }
    //
    //    private Map createMethodParameterNamesCache() {
    //        final Map map = new HashMap();
    //        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
    //        for (final Iterator it = methodsCache.values().iterator(); it.hasNext();) {
    //            final Method[] methods = (Method[]) it.next();
    //            for (int i = 0; i < methods.length; ++i) {
    //                final Method method = methods[i];
    //                if (method.getParameterTypes().length == 0) {
    //                    map.put(methods[i], EMPTY_STRING_ARRAY);
    //                    continue;
    //                }
    //                final CtClass clazz = ClassPoolUtil.toCtClass(pool, method.getDeclaringClass());
    //                final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, method.getParameterTypes());
    //                try {
    //                    final String[] names = getParameterNames(clazz.getDeclaredMethod(method.getName(), parameterTypes));
    //                    map.put(methods[i], names);
    //                } catch (final NotFoundException e) {
    //                    _log.debug("The method was not found: class=" + beanClass.getName() + " method=" + method);
    //                }
    //            }
    //        }
    //        return map;
    //    }
    //
    //    private String[] getParameterNames(final CtBehavior behavior) throws NotFoundException {
    //        final MethodInfo methodInfo = behavior.getMethodInfo();
    //        final ParameterAnnotationsAttribute attribute = (ParameterAnnotationsAttribute) methodInfo
    //                .getAttribute(ParameterAnnotationsAttribute.visibleTag);
    //        if (attribute == null) {
    //            return null;
    //        }
    //        final int numParameters = behavior.getParameterTypes().length;
    //        final String[] parameterNames = new String[numParameters];
    //        final Annotation[][] annotationsArray = attribute.getAnnotations();
    //        if (annotationsArray == null || annotationsArray.length != numParameters) {
    //            return null;
    //        }
    //        for (int i = 0; i < numParameters; ++i) {
    //            final String parameterName = getParameterName(annotationsArray[i]);
    //            if (parameterName == null) {
    //                return null;
    //            }
    //            parameterNames[i] = parameterName;
    //        }
    //        return parameterNames;
    //    }
    //
    //    private String getParameterName(final Annotation[] annotations) {
    //        Annotation nameAnnotation = null;
    //        for (int i = 0; i < annotations.length; ++i) {
    //            final Annotation annotation = annotations[i];
    //            if (PARAMETER_NAME_ANNOTATION.equals(annotation.getTypeName())) {
    //                nameAnnotation = annotation;
    //                break;
    //            }
    //        }
    //        if (nameAnnotation == null) {
    //            return null;
    //        }
    //        return ((StringMemberValue) nameAnnotation.getMemberValue("value")).getValue();
    //    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    public Object newInstance(Object[] args) throws ConstructorNotFoundRuntimeException {
        Constructor<?> constructor = getSuitableConstructor(args);
        return ConstructorUtil.newInstance(constructor, args);
    }

    public Object getFieldValue(String fieldName, Object target) throws FieldNotFoundRuntimeException {
        Field field = getField(fieldName);
        return FieldUtil.get(field, target);
    }

    public Object invoke(Object target, String methodName, Object[] args) {
        Method method = getSuitableMethod(methodName, args);
        return MethodUtil.invoke(method, target, args);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    private Constructor<?> findSuitableConstructor(Object[] args) {
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || ClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return constructors[i];
        }
        return null;
    }

    private Constructor<?> findSuitableConstructorAdjustNumber(Object[] args) {
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || ClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return constructors[i];
        }
        return null;
    }

    private static boolean adjustNumber(Class<?>[] paramTypes, Object[] args, int index) {
        if (paramTypes[index].isPrimitive()) {
            if (paramTypes[index] == int.class) {
                args[index] = IntegerConversionUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == double.class) {
                args[index] = DoubleConversionUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == long.class) {
                args[index] = LongConversionUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == short.class) {
                args[index] = ShortConversionUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == float.class) {
                args[index] = FloatConversionUtil.toFloat(args[index]);
                return true;
            }
        } else {
            if (paramTypes[index] == Integer.class) {
                args[index] = IntegerConversionUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == Double.class) {
                args[index] = DoubleConversionUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == Long.class) {
                args[index] = LongConversionUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == Short.class) {
                args[index] = ShortConversionUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == Float.class) {
                args[index] = FloatConversionUtil.toFloat(args[index]);
                return true;
            }
        }
        return false;
    }

    private void setupPropertyDescs() {
        Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (MethodUtil.isBridgeMethod(m) || MethodUtil.isSyntheticMethod(m)) {
                continue;
            }
            String methodName = m.getName();
            if (methodName.startsWith("get")) {
                if (m.getParameterTypes().length != 0 || methodName.equals("getClass")
                        || m.getReturnType() == void.class) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("is")) {
                if (m.getParameterTypes().length != 0 || !m.getReturnType().equals(Boolean.TYPE)
                        && !m.getReturnType().equals(Boolean.class)) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(2));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("set")) {
                if (m.getParameterTypes().length != 1 || methodName.equals("setClass")
                        || m.getReturnType() != void.class) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupWriteMethod(m, propertyName);
            }
        }
        for (Iterator<String> i = invalidPropertyNames.iterator(); i.hasNext();) {
            propertyDescMap.remove(i.next());
        }
        invalidPropertyNames.clear();
    }

    private static String decapitalizePropertyName(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {

            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private void addPropertyDesc(PropertyDesc propertyDesc) {
        if (propertyDesc == null) {
            String msg = "The argument 'propertyDesc' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        propertyDescMap.put(propertyDesc.getPropertyName(), propertyDesc);
    }

    private void setupReadMethod(Method readMethod, String propertyName) {
        Class<?> propertyType = readMethod.getReturnType();
        PropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setReadMethod(readMethod);
            }
        } else {
            propDesc = new PropertyDescImpl(propertyName, propertyType, readMethod, null, null, this);
            addPropertyDesc(propDesc);
        }
    }

    private void setupWriteMethod(Method writeMethod, String propertyName) {
        Class<?> propertyType = writeMethod.getParameterTypes()[0];
        PropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setWriteMethod(writeMethod);
            }
        } else {
            propDesc = new PropertyDescImpl(propertyName, propertyType, null, writeMethod, null, this);
            addPropertyDesc(propDesc);
        }
    }

    private Method getSuitableMethod(String methodName, Object[] args) throws MethodNotFoundRuntimeException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        Method[] methods = getMethods(methodName);
        Method method = findSuitableMethod(methods, args);
        if (method != null) {
            return method;
        }
        method = findSuitableMethodAdjustNumber(methods, args);
        if (method != null) {
            return method;
        }
        throw new MethodNotFoundRuntimeException(beanClass, methodName, args);
    }

    private Method findSuitableMethod(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || ClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    private Method findSuitableMethodAdjustNumber(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || ClassUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    private void setupMethods() {
        Map<String, List<Method>> methodListMap = new LinkedHashMap<String, List<Method>>();
        Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (MethodUtil.isBridgeMethod(method) || MethodUtil.isSyntheticMethod(method)) {
                continue;
            }
            String methodName = method.getName();
            List<Method> list = (List<Method>) methodListMap.get(methodName);
            if (list == null) {
                list = new ArrayList<Method>();
                methodListMap.put(methodName, list);
            }
            list.add(method);
        }
        final Set<String> keySet = methodListMap.keySet();
        for (String key : keySet) {
            final List<Method> methodList = methodListMap.get(key);
            methodsMap.put(key, methodList.toArray(new Method[methodList.size()]));
        }
    }

    /*
     * private void setupField() { for (Class clazz = beanClass_; clazz !=
     * Object.class && clazz != null; clazz = clazz.getSuperclass()) {
     * 
     * Field[] fields = clazz.getDeclaredFields(); for (int i = 0; i <
     * fields.length; ++i) { Field field = fields[i]; String fname =
     * field.getName(); if (!fieldCache_.containsKey(fname)) {
     * fieldCache_.put(fname, field); } } } }
     */
    private void setupFields() {
        setupFields(beanClass);
    }

    private void setupFields(Class<?> targetClass) {
        if (targetClass.isInterface()) {
            setupFieldsByInterface(targetClass);
        } else {
            setupFieldsByClass(targetClass);
        }
    }

    private void setupFieldsByInterface(Class<?> interfaceClass) {
        addFields(interfaceClass);
        Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            String fname = field.getName();
            if (!fieldMap.containsKey(fname)) {
                field.setAccessible(true);
                fieldMap.put(fname, field);
                if (FieldUtil.isInstanceField(field)) {
                    if (hasPropertyDesc(fname)) {
                        PropertyDesc pd = getPropertyDesc(field.getName());
                        pd.setField(field);
                    } else if (FieldUtil.isPublicField(field)) {
                        PropertyDesc pd = new PropertyDescImpl(field.getName(), field.getType(), null, null, field,
                                this);
                        propertyDescMap.put(fname, pd);
                    }
                }
            }
        }
    }

    private void setupFieldsByClass(Class<?> targetClass) {
        addFields(targetClass);
        Class<?>[] interfaces = targetClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
        Class<?> superClass = targetClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            setupFieldsByClass(superClass);
        }
    }
}
