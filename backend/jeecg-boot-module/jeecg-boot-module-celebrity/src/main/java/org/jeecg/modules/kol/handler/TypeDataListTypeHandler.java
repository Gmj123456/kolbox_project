package org.jeecg.modules.kol.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jeecg.modules.kol.model.TypeData;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/5/24 17:24
 * @Description:
 **/
public class TypeDataListTypeHandler extends BaseTypeHandler<List<TypeData>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<TypeData> parameter, JdbcType jdbcType) throws SQLException {
        // 将 List<ProductCategory> 对象转换为 JSON 字符串存储到数据库
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<TypeData> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库读取 JSON 字符串并转换为 List<ProductCategory> 对象
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public List<TypeData> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public List<TypeData> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    /**
     * 解析 JSON 字符串为 List<ProductCategory>
     */
    private List<TypeData> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, new TypeReference<List<TypeData>>() {
            });
        } catch (Exception e) {
            // 如果解析失败，返回 null 或者抛出异常，根据业务需要调整
            return null;
        }
    }
}