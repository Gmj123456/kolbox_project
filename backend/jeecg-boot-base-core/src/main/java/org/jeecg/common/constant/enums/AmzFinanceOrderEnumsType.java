package org.jeecg.common.constant.enums;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName AmzFinanceOrderType.java
 * @Description TODO
 * @createTime 2022年11月15日 13:44:00
 */
public enum AmzFinanceOrderEnumsType {
    ORDER(1, "order"),
    REFUND(2, "Refund"),
    EMPTY(3, ""),
    ADJUSTMENT(4, "Adjustment"),
    DEALFEE(5, "Deal Fee"),
    FBAINVENTORYFEE(6, "FBA Inventory Fee"),
    SERVICEFEE(7, "Service Fee"),
    TRANSFER(8, "Transfer");

    private final int code;
    private final String info;

    AmzFinanceOrderEnumsType(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
