package org.jeecg.modules.instagram.model;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.model.KolAllotTagModel;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: IG网红标签关联视图模型
 * @Author: xyc
 * @Date: 2024年12月3日 19:13:34
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IgAllotTagModel extends KolAllotTagModel {

    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String igUsername;
}
