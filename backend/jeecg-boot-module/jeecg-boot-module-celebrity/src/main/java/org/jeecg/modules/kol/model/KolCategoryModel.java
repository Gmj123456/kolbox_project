package org.jeecg.modules.kol.model;

import lombok.Data;
import org.jeecg.modules.kol.entity.KolCategory;

import java.util.List;

/**
 * @Description: 网红标签分类类目表
 * @Author: xyc
 * @Date: 2024年11月22日 15:22:48
 * @Version: V1.0
 */
@Data
public class KolCategoryModel extends KolCategory {
   private List<KolCategoryModel> children;
   private List<TypeData> dataList;
}
