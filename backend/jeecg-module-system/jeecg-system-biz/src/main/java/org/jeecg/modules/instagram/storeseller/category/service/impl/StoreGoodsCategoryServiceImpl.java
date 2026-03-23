package org.jeecg.modules.instagram.storeseller.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.constant.enums.GoodsCategoryType;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.instagram.storeseller.category.entity.StoreGoodsCategory;
import org.jeecg.modules.instagram.storeseller.category.mapper.StoreGoodsCategoryMapper;
import org.jeecg.modules.instagram.storeseller.category.service.IStoreGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @Description: 商家商品类目表
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Service
public class StoreGoodsCategoryServiceImpl extends ServiceImpl<StoreGoodsCategoryMapper, StoreGoodsCategory> implements IStoreGoodsCategoryService {

    @Autowired
    private  StoreGoodsCategoryMapper storeGoodsCategoryMapper;

    @Override
    public void addCategory(StoreGoodsCategory storeGoodsCategory) {
        //----------------------------------------------------------------------
        if(GoodsCategoryType.ISROOT.equals(storeGoodsCategory.getCategoryType())) {
            storeGoodsCategory.setParentId("");
        }
        //----------------------------------------------------------------------
        String pid = storeGoodsCategory.getParentId();
        if(oConvertUtils.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            this.storeGoodsCategoryMapper.setCategoryLeaf(pid, 0);
        }
        storeGoodsCategory.setCreateTime(new Date());
        storeGoodsCategory.setDelFlag(false);
        storeGoodsCategory.setLeaf(true);
        this.save(storeGoodsCategory);
    }

    @Override
    public void editCategory(StoreGoodsCategory storeGoodsCategory) {
        StoreGoodsCategory p = this.getById(storeGoodsCategory.getId());
        //TODO 该节点判断是否还有子节点
        if(p==null) {
            throw new JeecgBootException("未找到菜单信息");
        }else {
            storeGoodsCategory.setUpdateTime(new Date());
            //----------------------------------------------------------------------
            //Step1.判断是否是一级菜单，是的话清空父菜单ID
            if(GoodsCategoryType.ISROOT.getCode()==storeGoodsCategory.getCategoryType()) {
                storeGoodsCategory.setParentId("");
            }
            //Step2.判断菜单下级是否有菜单，无则设置为叶子节点
            int count = (int) this.count(new QueryWrapper<StoreGoodsCategory>().lambda().eq(StoreGoodsCategory::getParentId, storeGoodsCategory.getId()));
            if(count==0) {
                storeGoodsCategory.setLeaf(true);
            }
            //----------------------------------------------------------------------
            this.updateById(storeGoodsCategory);

            //如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
            String pid = storeGoodsCategory.getParentId();
            if((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid)&&oConvertUtils.isNotEmpty(p.getParentId())) {
                //a.设置新的父菜单不为叶子节点
                this.storeGoodsCategoryMapper.setCategoryLeaf(pid, 0);
                //b.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
                int cc = (int) this.count(new QueryWrapper<StoreGoodsCategory>().lambda().eq(StoreGoodsCategory::getParentId, p.getParentId()));
                if(cc==0) {
                    if(oConvertUtils.isNotEmpty(p.getParentId())) {
                        this.storeGoodsCategoryMapper.setCategoryLeaf(p.getParentId(), 1);
                    }
                }

            }
        }
    }

    @Override
    public void deleteCategory(String id) {
        StoreGoodsCategory storeGoodsCategory= this.getById(id);
        if(storeGoodsCategory==null) {
            throw new JeecgBootException("未找到菜单信息");
        }
        String pid = storeGoodsCategory.getParentId();
        int count = (int) this.count(new QueryWrapper<StoreGoodsCategory>().lambda().eq(StoreGoodsCategory::getParentId, pid));
        if(count==1) {
            //若父节点无其他子节点，则该父节点是叶子节点
            this.storeGoodsCategoryMapper.setCategoryLeaf(pid, 1);
        }
        storeGoodsCategory.setDelFlag(true);
        this.updateById(storeGoodsCategory);

    }

    @Override
    public List<StoreGoodsCategory> queryGoodsCategoryList(String id) {
        return storeGoodsCategoryMapper.queryGoodsCategoryList(id);
    }
}
