package org.jeecg.modules.kol.model;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @Author: nqr
 * @Date: 2025/6/27 09:44
 * @Description:
 **/

@Data
public class TypeData {
    private String typeId;
    private String celebrityPrivateId;
    private List<CategoryItem> list;

    @Data
    public static class CategoryItem {
        private String typeId;
        private Integer level;
        private Integer rank;
        private Boolean isSel;
        private String attributeId;
        private String attributeName;
        private String attributeEnName;
        private String categoryId;
        private String categoryName;
        private String categoryEnName;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            CategoryItem that = (CategoryItem) o;
            return Objects.equals(attributeId, that.attributeId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(attributeId);
        }
    }
}