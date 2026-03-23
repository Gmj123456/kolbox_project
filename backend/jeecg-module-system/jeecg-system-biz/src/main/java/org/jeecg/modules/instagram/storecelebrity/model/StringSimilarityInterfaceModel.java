package org.jeecg.modules.instagram.storecelebrity.model;

import lombok.Data;

/**
 * 调用python接口查询邮件标题相似度返回true或false
 * @Author: zhoushen
 */
@Data
public class StringSimilarityInterfaceModel {

    public static final String URL = "http://121.89.235.221:8801";

    public static final String ENCODING = "UTF-8";
    public static final String PARAMS = "params";
    public static final String PARAMS_NUM = "num";
    public static final String PARAMS_TITLE = "title";
    public static final String PARAMS_TITLELIST = "titleList";
}
