package org.jeecg.smallTools;

import jakarta.annotation.Resource;
import org.jeecg.JeecgSystemApplication;
import org.jeecg.modules.instagram.storepromotion.model.StoreSellerPromotionModel;
import org.jeecg.modules.instagram.storepromotion.service.IStoreSellerPromotionService;
import org.jeecg.modules.instagram.storepromotion.service.impl.StoreSellerPromotionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = JeecgSystemApplication.class)
public class TestWechat {
    @Resource
    private IStoreSellerPromotionService storeSellerPromotionService;

    @Test
    public void test() {
        // 准备测试数据
        StoreSellerPromotionModel model = new StoreSellerPromotionModel();
        model.setSellerName("Ulike");
        model.setProductUrl("https://www.amazon.com/dp/B0GL3TLN18/ref=sr_1_1?ie=UTF8&sr=1-1&qid=1770109783&content-id=amzn1.sym.3");
        model.setPromPlatform("0,1,2");
        model.setPromRequirement("美国tiktok白人女性博主，年龄25+，模特/歌手/宝妈，主页内容口播较多且比较积极大方自信、声音很大、口播能力较强的那种，CPM＜20\n" +
                "受众画像：25-34占比50%以上，US占比65%以上且前2国家是洛杉矶和华盛顿，评论人群都是30+，女性占比大于65%\n" +
                "注意不要黑人、黑人混血、亚洲人、情景剧及纯带货达人\n" +
                "1M粉丝以下：2k以内\n" +
                "1-5M粉丝：2-5k\n" +
                "5-10M粉丝：5-7k\n" +
                "10M粉丝+：另外商议\n" +
                "需要提供tcm链接或后台数据截图");
        model.setPromBudget("1000");
        model.setContactInfo("13800138000");
        model.setRemark("这是备注");
        storeSellerPromotionService.sendWeChatWorkNotification(model);
    }
}
