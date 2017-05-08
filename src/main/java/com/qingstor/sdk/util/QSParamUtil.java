package com.qingstor.sdk.util;

import com.qingstor.sdk.annotation.ParamAnnotation;
import com.qingstor.sdk.constant.QSConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class QSParamUtil {
    public static Map<String, Object> getRequestParams(Object model, String location)
            throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> retParametersMap = new HashMap<>();
        if (model == null)
            return retParametersMap;
        Method[] methods = model.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ParamAnnotation annotation = method.getAnnotation(ParamAnnotation.class);
            if (annotation == null || !annotation.location().equals(location))
                continue;
            Object value = method.invoke(model, (Object[]) null);
            if (value == null || value.getClass().equals(scala.None$.class))
                continue;

            if (!location.equals(QSConstants.ParamsLocationParam())
                    && !location.equals(QSConstants.ParamsLocationHeader())) {
                retParametersMap.put(annotation.name(), value);
                continue;
            }

            // Parameters in header or params can only be String
            if (value.getClass().equals(scala.Some.class))
                value = ((scala.Some)value).get();
            Class cls = value.getClass();
            if (cls.equals(Integer.class) || cls.equals(Long.class) || cls.equals(Double.class)
                    || cls.equals(Float.class) || cls.equals(Boolean.class) || cls.equals(Character.class))
                value = String.valueOf(value);
            if (cls.equals(ZonedDateTime.class))
                value = value.toString();
            retParametersMap.put(annotation.name(), value);
        }
        return retParametersMap;
    }

    public static Map<String, String> getResponseParams(Object model, String location) {
        Map<String, String> retParametersMap = new HashMap<>();
        if (model == null)
            return retParametersMap;
        Method[] methods = model.getClass().getMethods();
        for (Method method : methods) {
            ParamAnnotation annotation = method.getAnnotation(ParamAnnotation.class);
            if (annotation == null)
                continue;
            if (annotation.location().equals(location))
                retParametersMap.put(annotation.name(), method.getName());
        }
        return retParametersMap;
    }

    public static Object invokeMethod(Object model, String methodName, Object[] params)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method;
        if (methodName.startsWith("get"))
            method = model.getClass().getMethod(methodName);
        else {
            String fieldName;
            Class<?> fieldType = null;
            if (methodName.startsWith("set")) {
                fieldName = methodName.replaceFirst("set", "");
                fieldName = String.valueOf(fieldName.charAt(0)).toLowerCase() + fieldName.substring(1);
            }
            else {
                fieldName = methodName;
            }
            try {
                fieldType = model.getClass().getDeclaredField(fieldName).getType();
            } catch (NoSuchFieldException e) {
                fieldType = model.getClass().getSuperclass().getDeclaredField(fieldName).getType();
            }
            method = model.getClass().getMethod(methodName, fieldType);
        }

        if (params == null)
            return method.invoke(model, (Object []) null);
        return method.invoke(model, params);
    }
}
