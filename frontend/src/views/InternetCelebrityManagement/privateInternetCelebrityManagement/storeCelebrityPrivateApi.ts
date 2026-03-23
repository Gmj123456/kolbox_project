import { defHttp } from '/@/utils/http/axios';

enum Api {
  DEMO_LIST = '/storeCelebrityPrivate/storeCelebrityPrivate/getCelebrityPrivateList',
  attribute = "/kol/attribute/queryProductAttributes",
  productList = "/kol/kolProduct/listAll",
  getCelebrityPrivateURL = "/kolsysuserfeishusheet/queryByUser?spreadSheetType=PrivateCelebrity",
  getHistoryURL =  "/kolsysuserfeishusheet/queryByUser?spreadSheetType=Product",
  syncPrivateCelebrity = "/kol/celebrity/syncPrivateCelebrity",
  syncProduct = "/kol/kolProduct/syncProduct",
}

/**
 * @description: Get sample list value
 */

export const demoListApi = (params) =>
  defHttp.get({
    url: Api.DEMO_LIST,
    params,
    headers: {
      ignoreCancelToken: true,
     
    },
  
});

export const attributeApi = (params) =>
  defHttp.get({
    url: Api.attribute,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const productListApi = (params) =>
  defHttp.get({
    url: Api.productList,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const getCelebrityPrivateURLApi = (params) =>
  defHttp.get({
    url: Api.getCelebrityPrivateURL,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  },{isTransformResponse:false});

export const getHistoryURLApi = (params) =>
  defHttp.get({
    url: Api.getHistoryURL,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  },{isTransformResponse:false});

export const syncPrivateCelebrityApi = (params) =>
  defHttp.get({
    url: Api.syncPrivateCelebrity,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

export const syncProductApi = (params) =>
  defHttp.get({
    url: Api.syncProduct,
    params,
    headers: {
      ignoreCancelToken: true,
    },
  });

