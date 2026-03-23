package org.jeecg.modules.kol.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 网红顾问基础简易模型
 * @Author: xyc
 * @Date: 2024年12月4日 17:22:16
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class KolCounselorModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户姓名
     */
    private String realname;

    /**
     * 用户简称
     */
    private String shortName;
}
