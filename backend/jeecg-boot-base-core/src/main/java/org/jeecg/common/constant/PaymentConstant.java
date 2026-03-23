package org.jeecg.common.constant;

/**
 * 支付常量
 */
public interface PaymentConstant {
    /**
     * 推广国家国币
     */
    public final static String PROMOTION_COUNTRY = "0";

    /**
     * rmb
     */
    public final static String RMB = "1";

    /**
     * RMB货币符号
     */
    public final static String RMB_SYMBOL = "¥";

    /**
     * RMB货币符号
     */
    public final static String DOLLER_SYMBOL = "$";
    /**
     * 定金
     */
    public static final Integer PAY_TYPE_DEPOSIT = 0;

    /**
     * 意向金
     */
    public static final Integer PAY_TYPE_EARNEST = 1;

    /**
     * 退款
     */
    public static final Integer PAY_TYPE_REFUND = 2;
    /**
     * 尾款
     */
    public static final Integer PAY_TYPE_FINAL = 3;
    /**
     * 全款
     */
    public static final Integer PAY_TYPE_ALL = 4;
    /**
     * 税款
     */
    public static final Integer LEVY = 5;


    /**
     * 线上支付
     */
    public static final Integer PAY_OFF_LINE = 0;

    /**
     * 线下支付
     */
    public static final Integer PAY_ON_LINE = 1;
}
