package org.jeecg.modules.instagram.storepromotion.model;

import lombok.Data;
import org.jeecg.modules.instagram.storecelebrity.entity.MessageTemplate;
import org.jeecg.modules.instagram.storepromotion.entity.StoreSensitiveWord;

import java.util.List;

@Data
public class StoreSensitiveWordModel extends StoreSensitiveWord {

    /**
    *  出现次数
    * */

    private Integer keywordNameCount;

   private List<StoreSensitiveWordModel> storeSensitiveWordModel;
   private MessageTemplate messageTemplate;
}
