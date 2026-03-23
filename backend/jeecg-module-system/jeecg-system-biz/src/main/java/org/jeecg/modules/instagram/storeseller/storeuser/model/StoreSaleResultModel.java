package org.jeecg.modules.instagram.storeseller.storeuser.model;

import lombok.Data;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreSaleResult;

@Data
public class StoreSaleResultModel extends StoreSaleResult {



        /**
         * 本次销售开始数量
         */
        private Integer saleStartNum;

        /**
         * 本次销售结束数量
         */
        private Integer saleEndNum;


        /**
         * 销售日期开始时间
         */
        private String startSaleDate;

        /**
         * 销售日期结束时间
         */
        private String endSaleDate;



}
