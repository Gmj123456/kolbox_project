import { defHttp } from '/@/utils/http/axios';

enum Api {
  queryStoreSellerPromotionManagementList = '/storeSellerPromotion/queryStoreSellerPromotionManagementList',
  mediaUploadsEdit = '/storePromotionGoodsCelebrity/editGoodsCelebrity',
  editGoodsCelebrityRemark = '/storePromotionGoodsCelebrity/editGoodsCelebrityRemark',
  deleteBack = '/storeSellerPromotion/delete',
  getBusinessPeople = '/sys/user/getBusinessPeople',
  deleteBatch = '/storeSellerPromotion/deleteBatch',
  checkStatus = '/storeSellerPromotion/checkStatus',
  innerTableList = '/storepromotiongoods/storePromotionGoods/getList',
  queryByPromId = '/storepromotiongoods/storePromotionGoods/queryByPromId',
  deleteCelebrity = '/storePromotionGoodsCelebrity/handleDelete',
  delCelebrityBatch = '/storePromotionGoodsCelebrity/removeCelebrityBatch',
  checkCelebrityStatus = '/storePromotionGoodsCelebrity/checkCelebrityStatus',
  exportList = '/storepromotiongoods/storePromotionGoods/exportList',
}

export const queryStoreSellerPromotionManagementListApi = (params?: any) =>
  
  defHttp.get({
    url: Api.queryStoreSellerPromotionManagementList,
    params: { ...params, isNewData: 1 },
  });

export const mediaUploadsEditApi = (data?: any) =>
  defHttp.put({
    url: Api.mediaUploadsEdit,
    data,
  });

export const editGoodsCelebrityRemarkApi = (data?: any) =>
  defHttp.put({
    url: Api.editGoodsCelebrityRemark,
    data,
  },{isTransformResponse:false});

export const deleteBackApi = (id: string) =>
  defHttp.delete({
    url: Api.deleteBack + '?id=' + id,
  },{isTransformResponse:false});

export const getBusinessPeopleApi = (params?: any) =>
  defHttp.get({
    url: Api.getBusinessPeople,
    params,
  });

export const deleteBatchApi = (params?: any) =>
  defHttp.delete({
    url: Api.deleteBatch,
    params,
  });

export const checkStatusApi = (params?: any) =>
  defHttp.get({
    url: Api.checkStatus,
    params,
  });

export const innerTableListApi = (params?: any) =>
  defHttp.get({
    url: Api.innerTableList,
    params,
  });

export const queryByPromIdApi = (params?: any) =>
  defHttp.get({
    url: Api.queryByPromId,
    params,
  });

export const deleteCelebrityApi = (params?: any) =>
  defHttp.delete({
    url: Api.deleteCelebrity,
    params,
  });

export const delCelebrityBatchApi = (params?: any) =>
  defHttp.delete({
    url: Api.delCelebrityBatch,
    params,
  });

export const checkCelebrityStatusApi = (params?: any) =>
  defHttp.get({
    url: Api.checkCelebrityStatus,
    params,
  });

export const exportListApi = (params?: any) =>
  defHttp.get({
    url: Api.exportList,
    params,
    responseType: 'blob',
  });

export const updateGoodsCelebrityBatchApi = (data?: any) =>
  defHttp.post({
    url: '/storePromotionGoodsCelebrity/updateGoodsCelebrityBatch',
    data,
  });

export const getStoreTagsListApi = (params?: any) =>
  defHttp.get({
    url: '/storetags/storeTags/list',
    params,
  });

export const selectTaxesApi = (params?: any) =>
  defHttp.get({
    url: '/storePromotionGoodsCelebrity/selectTaxes',
    params,
  });

export const queryStoreSellerPromotionListApi = (params?: any) =>
  defHttp.get({
    url: '/storeSellerPromotion/list',
    params: { ...params, isNewData: 1 },
  });
export const merchantDemandListApi = (params?: any) =>
  defHttp.get({
    url: '/storeSellerPromotion/sellerList',
    params,
  });
export const deleteStoreSellerPromotionApi = (params?: any) =>
  defHttp.delete({
    url: '/storeSellerPromotion/delete',
    params,
  });

export const getPayStatusApi = (params?: any) =>
  defHttp.get({
    url: '/storesellerconsume/storeSellerConsume/getPayStatus',
    params,
  });

export const getCountryListApi = (params?: any) =>
  defHttp.get({
    url: '/storecountry/storeCountry/getCountryList',
    params,
  });

export const checkCelebrityStatusForListApi = (params?: any) =>
  defHttp.get({
    url: '/storePromotionGoodsCelebrity/checkStatus',
    params,
  });

