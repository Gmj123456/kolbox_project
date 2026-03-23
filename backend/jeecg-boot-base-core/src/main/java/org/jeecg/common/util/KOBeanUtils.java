package org.jeecg.common.util;

import org.jeecg.common.interfaces.IGetter;
import org.jeecg.common.interfaces.ISetter;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class KOBeanUtils {

    /***
     *  使用lambda表达式,转换方法引用为属性名
     *
     * @param fn
     * @return
     */
    public static <T> String convertToFieldName(IGetter<T> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if(methodName.startsWith("get")){
            prefix = "get";
        }
        else if(methodName.startsWith("is")){
            prefix = "is";
        }
        // 非法get命名
        if(prefix == null){
            return null;
        }
        // 截取get/is之后的字符串并转换首字母为小写
        return substringAfter(methodName, prefix);
    }

    /***
     * 转换setter方法引用为属性名
     * @param fn
     * @return
     */
    public static <T,R> String convertToFieldName(ISetter<T,R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        String prefix = "set";
        // 非法set命名
        if(!methodName.startsWith(prefix)){
            return null;
        }
        // 截取set之后的字符串并转换首字母为小写
        return substringAfter(methodName, prefix);
    }

    /***
     * 获取类对应的Lambda
     * @param fn
     * @return
     */
    private static SerializedLambda getSerializedLambda(Serializable fn){
        //先检查缓存中是否已存在
        SerializedLambda lambda = null;
        try{
            //提取SerializedLambda并缓存
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(fn);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return lambda;
    }

    /**
     * 截取prefix之后的字符串并转换首字母为小写
     *
     * @param methodName
     * @param prefix
     * @return
     */
    private static String substringAfter(String methodName,String prefix){
        if (prefix == null || methodName == null){
            return null;
        }
        String tempName = methodName.substring(prefix.length());
        //首字母转小写
        return toLowerCaseFirstOne(tempName);
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s)
    {
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s)
    {
        if(Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

}
