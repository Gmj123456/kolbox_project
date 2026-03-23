package org.jeecg.modules.youtube.model;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.kol.model.KolAllotTagModel;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: YT网红标签关联视图模型
 * @Author: xyc
 * @Date: 2024-12-26 09:00:55
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class YtAllotTagModel extends KolAllotTagModel {

    /**
     * 账号唯一id
     */
    @Excel(name = "账号", width = 15)
    @Schema(title =  "账号")
    private String account;

    /**
     * 账号名称
     */
    @Excel(name = "账号名称", width = 15)
    @Schema(title =  "账号名称")
    private String username;
}
