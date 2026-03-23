package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecg.modules.kol.entity.KolAttribute;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 15:22:48
 * @Version: V1.0
 */
@Data
public class KolAttributeModel extends KolAttribute {
    private String tagId;
    private String tagName;
    private String attributeId;
}
