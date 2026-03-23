package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import org.jeecg.modules.instagram.storepromotion.entity.StorePackagePurchaseConsume;

@Data
public class StorePackagePurchaseConsumeModel extends StorePackagePurchaseConsume {
    private String purchaseId;
    private String packageName;
    private String emailContent;
    private String emailTitle;
    private String sendEmail;
}
