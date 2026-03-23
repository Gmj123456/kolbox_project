package org.jeecg.modules.kol.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 标签品牌表
 * @Author: dongruyang
 * @Date: 2024-07-18
 * @Version: V1.0
 */
@Data
public class KolTagBrandModel {

  /**
   * tag名称
   */
  @Excel(name = "标签")
  private String tagName;
  /**
   * 品牌名称
   */
  @Excel(name = "品牌")
  private String brandName;
  /**
   * 产品备注
   */
  @Excel(name = "产品备注")
  private String tagProduct;

  private String errorMsg;

  @Excel(name = "平台")
  @TableField(exist = false)
  private String platformTypeStr;

  private Integer platformType;

  public Integer getPlatformType() {
    if (platformTypeStr == null || "".equals(platformTypeStr)) {
      return 0;
    }
    if (platformTypeStr.equals("IG")) {
      return 0;
    } else if (platformTypeStr.equals("YT")) {
      return 1;
    } else {
      return 2;
    }
  }
}
