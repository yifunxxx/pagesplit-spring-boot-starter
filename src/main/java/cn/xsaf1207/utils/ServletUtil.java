package cn.xsaf1207.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * servlet工具类
 *
 * @author yifun
 */
public class ServletUtil
{
    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return TypeConvertUtil.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return TypeConvertUtil.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return TypeConvertUtil.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }


    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }



}

