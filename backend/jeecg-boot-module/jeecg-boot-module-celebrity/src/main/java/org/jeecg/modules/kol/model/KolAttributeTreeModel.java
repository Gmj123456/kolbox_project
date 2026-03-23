package org.jeecg.modules.kol.model;

import lombok.Data;
import org.jeecg.modules.kol.entity.KolAttribute;

import java.util.List;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName KolAttributeTreeModel.java
 * @Description TODO
 * @createTime 2025年07月28日 11:04:00
 */
@Data
public class KolAttributeTreeModel extends KolAttribute {
    private List<KolAttributeTreeModel> children;
}
