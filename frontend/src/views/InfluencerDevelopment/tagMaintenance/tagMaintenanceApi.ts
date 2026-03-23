import { defHttp } from '/@/utils/http/axios';

enum Api {
  TagList = '/kol/tags/listNew',
  TagDelete = '/kol/tags/delete',
  StoreTags = '/storetags/storeTags/list',
  AssociatedTags = '/kol/tags/associatedTags',
  BrandList = '/kolBrand/listAll',
  ProductList = '/kol/kolProduct/listAll',
  QueryTagsByIds = '/kol/tags/queryByIds',
  BindingProducts = '/kol/kolTagProduct/bindingProducts',
  AttributeTypeList = '/kol/kolAttributeType/listAll',
  AttributeList = '/kol/attribute/getKolAttribute',
  CategoryTree = '/kol/category/loadTreeDataAll',
  TagProductQuery = '/kol/kolTagProduct/queryByTagId',
  TagAdd = '/kol/tags/addNew',
  TagEdit = '/kol/tags/editNew',
  copyTags = '/kol/tags/copyTags',
}

export const fetchTagListApi = (params) =>
  defHttp.get({
    url: Api.TagList,
    params,
  });

export const deleteTagApi = (id: string | number) =>
  defHttp.delete({
    url: `${Api.TagDelete}/${id}`,
  });

export const getStoreTagsListApi = (params?) =>
  defHttp.get({
    url: Api.StoreTags,
    params,
  });

export const associatedTagsApi = (params) =>
  defHttp.get({
    url: Api.AssociatedTags,
    params,
  });

export const getBrandListApi = (params?) =>
  defHttp.get({
    url: Api.BrandList,
    params,
  });

export const getProductListApi = (params?) =>
  defHttp.get({
    url: Api.ProductList,
    params,
  });

export const queryTagsByIdsApi = (data) =>
  defHttp.post({
    url: Api.QueryTagsByIds,
    data,
  }, { isTransformResponse: false });

export const bindingProductsApi = (data) =>
  defHttp.post({
    url: Api.BindingProducts,
    data,
  },{ isTransformResponse: false });

export const getAttributeTypeListApi = (params?) =>
  defHttp.get({
    url: Api.AttributeTypeList,
    params,
  });

export const getAttributeListApi = (params?) =>
  defHttp.get({
    url: Api.AttributeList,
    params,
  });

export const getCategoryTreeApi = (params?) =>
  defHttp.get({
    url: Api.CategoryTree,
    params,
  });

export const queryTagProductByTagIdApi = (params) =>
  defHttp.get({
    url: Api.TagProductQuery,
    params,
  });

export const addTagApi = (data) =>
  defHttp.post({
    url: Api.TagAdd,
    data,
  },{isTransformResponse:false});

export const editTagApi = (data) =>
  defHttp.put({
    url: Api.TagEdit,
    data,
  },{isTransformResponse:false});
  export const copyTagsApi = (data) =>
    defHttp.post({
      url: Api.copyTags,
      data,
      headers: {
        ignoreCancelToken: true,
      },
    });

