package org.jeecg.modules.tiktok.model;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.model.KolAllotTagModel;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description:  TK网红标签关联视图模型
 * @Author: xyc
 * @Date: 2024-12-23 11:47:33
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TkAllotTagModel extends KolAllotTagModel {

    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String uniqueId;
}
