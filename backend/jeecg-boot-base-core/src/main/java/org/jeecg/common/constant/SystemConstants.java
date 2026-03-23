package org.jeecg.common.constant;

/**
 * 实训常量类
 */
public interface SystemConstants {

    //--控制器方法注解常量------------------------------------------------------------------------------------------------
    /**
     * 日志及API 注解常量-分页查询
     */
    public static final String PAGE_LIST_QUERY = "分页列表查询";

    /**
     * 日志及API 注解常量-添加数据
     */
    public static final String ADD = "添加";

    /**
     * 日志及API 注解常量-编辑数据
     */
    public static final String EDIT = "编辑";

    /**
     * 日志及API 注解常量-通过id删除数据
     */
    public static final String DELETE_BY_ID = "通过id删除";

    /**
     * 日志及API 注解常量-批量删除数据
     */
    public static final String DELETE_BATCH = "批量删除";

    /**
     * 日志及API 注解常量-通过id查询
     */
    public static final String QUERY_BY_ID = "通过id查询";
    public static final String QUERY_BY_TAGID_PRODUCTID = "通过TagID与产品ID查询";
    public static final String QUERY_BY_NAMES = "通过";
    public static final String QUERY_PRODUCT_BY_RELATIONSTATUS = "通过历史合作、历史选中状态查询产品列表";

    //--分页相关参数-----------------------------------------------------------------------------------------------------

    /**
     * 当前页码
     */
    public static final String PAGE_NO = "pageNo";
    /**
     * 当前页码 默认值
     */
    public static final String PAGE_NO_DEFAULT = "1";
    /**
     * 每页记录数
     */
    public static final String PAGE_SIZE = "pageSize";
    /**
     * 每页记录数默认值
     */
    public static final String PAGE_SIZE_DEFAULT = "10";

    //--提示消息常量-----------------------------------------------------------------------------------------------------

    /**
     * 添加成功消息
     */
    public static final String ADD_SUCCESS = "添加成功！";

    /**
     * 编辑成功消息
     */
    public static final String EDIT_SUCCESS = "编辑成功！";

    /**
     * 删除成功消息
     */
    public static final String DELETE_SUCCESS = "删除成功！";

    /**
     * 批量删除成功消息
     */
    public static final String DELETE_BATCH_SUCCESS = "批量删除成功！";

    /**
     * 未找到对应数据
     */
    public static final String QUERY_EMPTY = "未找到对应数据！";

    /**
     * 操作失败
     */
    public static final String OPERATE_FAIL = "操作失败！";

    //--请求路径常量-----------------------------------------------------------------------------------------------------

    /**
     * 添加URL
     */
    public static final String ADD_URL = "/add";
    /**
     * 列表URL
     */
    public static final String LIST_URL = "/list";

    /**
     * 编辑URL
     */
    public static final String EDIT_URL = "/edit";

    /**
     * 删除URL
     */
    public static final String DELETE_URL = "/delete";

    /**
     * 批量删除URL
     */
    public static final String DELETE_BATCH_URL = "/deleteBatch";

    /**
     * 根据id查询 URL
     */
    public static final String QUERY_BY_ID_URL = "/queryById";


    /**
     * 导入Excel URL
     */
    public static final String IMPORT_EXCEL_URL = "/importExcel";

    /**
     * 导出Excel URL
     */
    public static final String EXPORT_EXCEL_URL = "/exportXls";


    //--其他常用常量-----------------------------------------------------------------------------------------------------

    /**
     * 主键id
     */
    public static final String KEY_ID = "id";

    /**
     * 多个主键id
     */
    public static final String KEY_IDS = "ids";

    /**
     * 逗号分隔符
     */
    public static final String SPLIT_CHAR_COMMA = ",";

    /**
     * 树形结构顶级id
     */
    public static final String ROOT_PARENT_ID_VALUE = "0";

    /**
     * 是否存在查询标识
     */
    public static final String HAS_QUERY = "hasQuery";
    /**
     * 是否存在查询标识为是
     */
    public static final String HAS_QUERY_TRUE = "1";
    /**
     * 是否存在查询标识为否
     */
    public static final String HAS_QUERY_FALSE = "0";
}
