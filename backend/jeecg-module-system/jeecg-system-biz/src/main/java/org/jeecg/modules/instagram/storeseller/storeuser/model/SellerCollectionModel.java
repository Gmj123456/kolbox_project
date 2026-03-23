package org.jeecg.modules.instagram.storeseller.storeuser.model;

import lombok.Data;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.SellerCollection;

@Data
public class SellerCollectionModel extends SellerCollection {

    private String account;
    private Integer status;
    private  String collectionId;
}
