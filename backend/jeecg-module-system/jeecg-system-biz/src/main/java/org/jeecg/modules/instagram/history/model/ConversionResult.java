package org.jeecg.modules.instagram.history.model;

import lombok.Data;
import org.jeecg.modules.instagram.history.entity.KolHistoryCelebrityDetail;

import java.util.List;

/**
 * @Author: nqr
 * @Date: 2025/11/27 15:40
 * @Description:
 **/
@Data
public class ConversionResult {
    private List<KolHistoryCelebrityDetailFeishuModel> successModels;
    private List<KolHistoryCelebrityDetailFeishuModel> failedModels;
    private List<KolHistoryCelebrityDetail> importData;
    private String result;
    private boolean success;

    public ConversionResult(List<KolHistoryCelebrityDetail> importData, List<KolHistoryCelebrityDetailFeishuModel> successModels,
                            List<KolHistoryCelebrityDetailFeishuModel> failedModels) {
        this.importData = importData;
        this.successModels = successModels;
        this.failedModels = failedModels;
    }
}