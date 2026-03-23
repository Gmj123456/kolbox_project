import { defHttp } from '/@/utils/http/axios';

enum Api {
  getCommonCountry = '/storecountry/storeCountry/getCommonCountry',
  queryAllCelebrityCounselor = '/sys/user/queryAllCelebrityCounselor',
  brandList = '/kolBrand/listAll',
  productAttribute = '/kol/attribute/queryProductAttributes',
  productList = '/kol/kolProduct/listAll',
  personalTagsList = '/personalTags/listAll',
  productCategoryList = '/kol/category/loadTreeDataAll',
}


export const getCommonCountryApi = (params?: any) =>
  defHttp.post({
    url: Api.getCommonCountry,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const queryAllCelebrityCounselorApi = (params?: any) =>
  defHttp.get({
    url: Api.queryAllCelebrityCounselor,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getBrandListApi = (params?: any) =>
  defHttp.get({
    url: Api.brandList,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getProductAttributeApi = (params?: any) =>
  defHttp.get({
    url: Api.productAttribute,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getProductListApi = (params?: any) =>
  defHttp.get({
    url: Api.productList,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getPersonalTagsListApi = (params?: any) =>
  defHttp.get({
    url: Api.personalTagsList,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getProductCategoryListApi = (params?: any) =>
  defHttp.get({
    url: Api.productCategoryList,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });