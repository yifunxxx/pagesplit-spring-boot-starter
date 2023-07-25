package cn.yifun.model;

import java.io.Serializable;

/**
 * @author yifun
 */
public class PageR<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 失败 */
    public static final int FAIL = 500;

    private int code;

    private String msg;

    private T data;

    private int total;

    public static <T> PageR<T> ok()
    {
        return restResult(null, SUCCESS, "查询成功", 0);
    }

    public static <T> PageR<T> ok(T data)
    {
        return restResult(data, SUCCESS, "查询成功", 0);
    }

    public static <T> PageR<T> ok(T data, int total)
    {
        return restResult(data, SUCCESS, "查询成功", total);
    }

    public static <T> PageR<T> ok(T data, String msg)
    {
        return restResult(data, SUCCESS, msg, 0);
    }

    public static <T> PageR<T> ok(T data, String msg, int total)
    {
        return restResult(data, SUCCESS, msg, total);
    }

    public static <T> PageR<T> fail()
    {
        return restResult(null, FAIL, "查询失败", 0);
    }

    public static <T> PageR<T> fail(String msg)
    {
        return restResult(null, FAIL, msg, 0);
    }

    public static <T> PageR<T> fail(T data)
    {
        return restResult(data, FAIL, "查询失败", 0);
    }

    public static <T> PageR<T> fail(T data, String msg)
    {
        return restResult(data, FAIL, msg, 0);
    }

    public static <T> PageR<T> fail(int code, String msg)
    {
        return restResult(null, code, msg, 0);
    }

    private static <T> PageR<T> restResult(T data, int code, String msg, int total)
    {
        PageR<T> apiResult = new PageR<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setTotal(total);
        return apiResult;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
