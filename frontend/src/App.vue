<template>
  <ConfigProvider :theme="appTheme" :locale="getAntdLocale">
    <AppProvider>
      <RouterView />
    </AppProvider>
  </ConfigProvider>
</template>

<script lang="ts" setup>
  import { watch, ref } from 'vue';
  import { theme } from 'ant-design-vue';
  import { ConfigProvider } from 'ant-design-vue';
  import { AppProvider } from '/@/components/Application';
  import { useTitle } from '/@/hooks/web/useTitle';
  import { useLocale } from '/@/locales/useLocale';
  import { useAppStore } from '/@/store/modules/app';
  import { useRootSetting } from '/@/hooks/setting/useRootSetting';
  import { ThemeEnum } from '/@/enums/appEnum';
  import { changeTheme } from '/@/logics/theme/index';

  const appStore = useAppStore();
  // 解决日期时间国际化问题
  import 'dayjs/locale/zh-cn';
  // support Multi-language
  const { getAntdLocale } = useLocale();

  useTitle();
  /**
   * 2024-04-07
   * liaozhiyang
   * 暗黑模式下默认文字白色，白天模式默认文字 #333
   * */
  const modeAction = (data) => {
    if (data.token) {
      if (getDarkMode.value === ThemeEnum.DARK) {
        Object.assign(data.token, { colorTextBase: 'fff' });
      } else {
        Object.assign(data.token, { colorTextBase: '#333' });
      }

      // 定义主题色 css 变量
      if (data.token.colorPrimary) {
        document.documentElement.style.setProperty('--j-global-primary-color', data.token.colorPrimary);
      }
    }
  };
  // update-begin--author:liaozhiyang---date:20231218---for：【QQYUN-6366】升级到antd4.x
  const appTheme: any = ref({});
  const { getDarkMode } = useRootSetting();
  watch(
    () => getDarkMode.value,
    (newValue) => {
      delete appTheme.value.algorithm;
      if (newValue === ThemeEnum.DARK) {
        appTheme.value.algorithm = theme.darkAlgorithm;
      }
      // update-begin--author:liaozhiyang---date:20240322---for：【QQYUN-8570】生产环境暗黑模式下主题色不生效
      if (import.meta.env.PROD) {
        changeTheme(appStore.getProjectConfig.themeColor);
      }
      // update-end--author:liaozhiyang---date:20240322---for：【QQYUN-8570】生产环境暗黑模式下主题色不生效
      modeAction(appTheme.value);
      appTheme.value = {
        ...appTheme.value,
      };
    },
    { immediate: true }
  );
  watch(
    appStore.getProjectConfig,
    (newValue) => {
      const primary = newValue.themeColor;
      const result = {
        ...appTheme.value,
        ...{
          token: {
            colorPrimary: primary,
            wireframe: true,
            fontSize: 12,
            colorTextBase: '#333',
            colorSuccess: '#55D187',
            colorInfo: primary,
            borderRadius: 4,
            sizeStep: 4,
            sizeUnit: 4,
            colorWarning: '#EFBD47',
            colorError: '#ED6F6F',
            fontFamily: 'Verdana, PingFangSC-Regular, PingFang SC, -apple-system, BlinkMacSystemFont, Segoe UI, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol'
          
          },
        },
      };
      appTheme.value = result;
      modeAction(result);
    },
    { immediate: true }
  );
  setTimeout(() => {
    appStore.getProjectConfig?.themeColor && changeTheme(appStore.getProjectConfig.themeColor);
  }, 300);
  // update-end--author:liaozhiyang---date:20231218---for：【QQYUN-6366】升级到antd4.x

</script>
<style lang="less">
  #app{
    font-family: Verdana, PingFangSC-Regular, PingFang SC, -apple-system, BlinkMacSystemFont,
    Segoe UI, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial,
    sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol !important;
  }
  .impowerBox .info{
    width: 254px !important;
  }
  .ant-modal-confirm-title{
    font-size: 16px !important;
  }
  .ant-modal-confirm-content{
    font-size: 14px !important;
  }
  .ant-empty .ant-empty-image{
    height: 50px !important;
  }
  .vxe-table--render-default .vxe-cell{
    padding-left:8px! important;
    padding-right:8px! important;
  
  }
  .ant-modal-title{
    color: #0b1019 !important;
  }
  .ant-tag{
    font-size: 12px !important;
  }
  .ant-table-cell {
    color: #0b1019 !important;
  }
  .ant-table .ant-table-thead tr th{
    background: #f0f3fe !important;
 
    color: #0b1019 !important;
    
    font-weight: 700 !important;
  }
.ant-pagination-options .ant-select{
  width: 100px !important;
 }
  a[disabled="false"] {
  color: #3155ED !important;
  cursor: pointer !important;
  pointer-events: auto !important;
}

/* 禁用状态：disabled="true" 或仅 disabled（无值） */
a[disabled="true"],
a[disabled]:not([disabled="false"]) {
  color: rgba(51, 51, 51, 0.25) !important;
  cursor: not-allowed !important;
  pointer-events: none !important;
  text-decoration: none !important;
}
.el-popover {
  min-width: 400px !important;
  max-width: 650px !important;
}
.more-filter-button span {
  user-select: none !important;
}
.main-filter-closed {
  display: inline-flex !important;
  align-items: center !important; 
}
.el-cascader-node.in-active-path,
.el-cascader-node.is-active,
.el-cascader-node.is-selectable.in-checked-path {
  color: #3155ED !important;
  font-weight: normal !important;
}
.el-cascader-node:not(.is-disabled):focus,
.el-cascader-node:not(.is-disabled):hover {
  background-color: #f2f8ff !important;
}
.el-checkbox__input.is-checked .el-checkbox__inner,
.el-checkbox__input.is-indeterminate .el-checkbox__inner {
  // color: #3155ED !important;
  background-color: #3155ED !important;
  border-color: #3155ED !important;
}
.surely-table {

.surely-table-horizontal-scroll + div {
  visibility: hidden !important;
}
  .surely-table-body-viewport-container + div {
    // 使用CSS的clip属性来隐藏元素。这样可以根据元素的尺寸和位置来裁剪显示区域，将其设为与元素相同大小的矩形，即可隐藏该元素。
    clip: rect(0, 0, 0, 0);
  }

}
.surely-table{ 
  .surely-table-header-cell{
    background: #f0f3fe ;
    color: #0b1019 ;
    font-weight: 700 ;
    font-size: 12px;
  }
  .surely-table-row {
    font-size: 12px;
    color: #0b1019 ;
  }
}
.surely-table-expanded-row{
  border-bottom: 0px !important;
}
.surely-table-expanded-row .surely-table-header .surely-table-header-cell{
  background: #f0f3fe ;
  color: #0b1019 ;
  font-weight: 700 ;
  font-size: 12px;
}
.surely-table-expanded-row > .surely-table-cell-content{
  padding: 0px !important;
}
.surely-table-expanded-row .surely-table-body-cell{
  background: #fff !important;
  // border-bottom: 0px !important;
}
.surely-table-expanded-row  .surely-table-cell-content:first-child {
  padding: 0px 
}
.surely-table-expanded-row .surely-table-cell-content{

  border-bottom: 0px !important;
}
  // update-begin--author:liaozhiyang---date:20230803---for：【QQYUN-5839】windi会影响到html2canvas绘制的图片样式
  img {
    display: inline-block;
  }
  // update-end--author:liaozhiyang---date:20230803---for：【QQYUN-5839】windi会影响到html2canvas绘制的图片样式
  .jeecg-layout-content .ant-card-body{
    padding: 12px;
  }
  .ant-form-item{
    margin-bottom: 8px;
  }
  a {
    color: #3155ED;
    &:hover {
      color: #3155ED;
      // text-decoration: underline;
    }
  }
  .tox-toolbar__primary{
    background: none !important;
  }
  .tox-toolbar__overflow{
    background: none !important;
  }
</style>
