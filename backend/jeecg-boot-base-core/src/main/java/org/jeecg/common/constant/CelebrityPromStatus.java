package org.jeecg.common.constant;

public interface CelebrityPromStatus {
    /**
     * 0：待寄样
     */
    public static final Integer PROM_STATUS_0 = 0;
    /**
     * 1：待上线
     */
    public static final Integer PROM_STATUS_1 = 1;
    /**
     * 2：已上线
     */
    public static final Integer PROM_STATUS_2 = 2;
    /**
     * 3：已完成
     */
    public static final Integer PROM_STATUS_3 = 3;
    /**
     * -1：已终止
     */
    public static final Integer PROM_STATUS_FINISH = -1;
    /**
     * 推广费
     */
    public static final String PRO_MAMT = "promAmt";
    /**
     * 网红费
     */
    public static final String CEL_PRICE = "celPrice";
    /**
     * 税费
     */
    public static final String TAX_AMT = "taxAmt";

    /**
     * 税费
     */
    public static final String CELEBRITY_NAME = "celebrityName";
    /**
     * 税费
     */
    public static final String MESSAGE = "message";

    /**
     * 网红列表最大返回记录数 默认为5k
     */
    public static final String KOL_MAX_RECORD = "kolMaxRecord";

    /**
     * 最大返回记录数默认值 默认为5k
     */
    public static final int KOL_MAX_RECORD_DEFAULT = 5000;

    /**
     * 网红标签分配超期天数
     */
    public static final String KOL_ALLOT_MAX_DAYS = "kolAllotMaxDays";
    public static final String TOTAL = "total";
    /**
     * 分配超期天数 默认30天
     */
    public static final int KOL_ALLOT_MAX_DAYS_DEFAULT = 15;
    public static final int TOTAL_DEFAULT = 10000;



}
