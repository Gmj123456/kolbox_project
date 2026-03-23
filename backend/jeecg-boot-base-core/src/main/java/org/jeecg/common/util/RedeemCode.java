package org.jeecg.common.util;

import java.util.Random;

/**
 * @Description: 随机产生16位兑换码(大写字母和数字)
 * @Date: 2020/11/4
 * @author: zhoushen
 */
public class RedeemCode {
    private String[] str_array = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    //对外方法生成随机16位大写字母和数字兑换码
    public String getRedeemCode(){
        StringBuffer redeemCodeBuffer = new StringBuffer();
        Random random = new Random();
        for(int i = 0; i < 16 ; i++ ){
            Integer index = random.nextInt(str_array.length);
            redeemCodeBuffer.append(str_array[index]);
        }
        String redeemCodeStr = redeemCodeBuffer.toString();
        System.out.println(redeemCodeStr);
        return redeemCodeStr;
    }

}
