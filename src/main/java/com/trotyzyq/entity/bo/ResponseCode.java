package com.trotyzyq.entity.bo;

/**
 * 返回码
 *
 * @author sjl
 */
public enum ResponseCode {
    /**正常返回*/
    NORMAL(1), // 正常返回
    /**操作异常*/
    INVALID_OPERATION(2), // 操作异常
    /**参数异常*/
    INVALID_INPUT(3), // 参数异常
    /**登录过期*/
    INVALID_EXPIRED(4), // 登录过期
    /**系统异常*/
    SERVER_ERROR(5), // 系统异常
    /**没有权限*/
    NO_POWER(6), // 没有权限
    /**启用或禁用失败**/
    STATUS_CHANGE_FAIL(7),//状态更改失败
    /**启用或禁用失败**/
    ACCOUNT_DISABLE(8),//账户禁用

    /**汇率变化**/
    RATE_CHANGE(9);//汇率变化


    private int code;
    ResponseCode(int code) {
        this.code = code;
    }

    /**
     * 获取 code.
     * 类型 $field.typeName
     *
     * @return code 的值
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置 code.
     *
     * @param code 设置 code 的值
     */
    public void setCode(int code) {
        this.code = code;
    }
}
