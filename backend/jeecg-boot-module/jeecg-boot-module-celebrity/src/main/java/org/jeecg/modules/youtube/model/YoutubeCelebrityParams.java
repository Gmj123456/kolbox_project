package org.jeecg.modules.youtube.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.youtube.entity.YoutubeCelebrity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: YT网红
 * @Author: nqr
 * @Date: 2023-11-22
 * @Version: V1.0
 */
@Data
public class YoutubeCelebrityParams {
    private String brandId;
    private List<YoutubeCelebrityModel> celebrityModels;
}
