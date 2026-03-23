package org.jeecg.modules.instagram.storeseller.storeuser.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.email.entity.StoreUserContactEmailSignature;
import org.jeecg.modules.instagram.storeseller.storeuser.entity.StoreUserContactEmail;

import java.util.List;

/**
 * @Description: 网红顾问建联邮箱表
 * @Author: dongruyang
 * @Date: 2024-01-15
 * @Version: V1.0
 */
@Data
@TableName("store_user_contact_email")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreUserContactEmailModel extends StoreUserContactEmail {
List<StoreUserContactEmailSignature> signatureList;
}
