import { ref, unref, onMounted,computed } from 'vue'
import { filterObj } from '/@/utils/common/compUtils';
import { copyTextToClipboard } from '@/hooks/web/useCopyToClipboard';
import { useMessage } from '/@/hooks/web/useMessage';
const { createMessage: $message } = useMessage();
export function useTable(apiFn, pageOffset = 0, options: any = {}) {
  const newLayout = ref(false)
  const loading = ref(false)
  const dataSource = ref([])
  const sTableHeight = ref(0)
  const  isorter = ref({
    column: 'createTime',
    order: 'desc'
  })
  const pagination= ref({
    defaultPageSize:20,
    showQuickJumper: true,
    showSizeChanger: true,
    pageSizeOptions: ['20', '30', '50', ],
    current:1,
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条'
    },
    total: 0,
    pageSize: 20,
    
  
   
  })
  const selectedRowKeys = ref([])
  const rowSelection = {
    columnWidth: 40,
    // Surely Vue 跨页保持勾选
    preserveSelectedRowKeys: true,
    onChange: handleRowSelectionChange,
  };
  const atableRowSelection = computed(() => {
    return {
      selectedRowKeys: unref(selectedRowKeys),
      columnWidth: 40,
      // Ant Table 跨页保持勾选
      preserveSelectedRowKeys: true,
      onChange: aTablehandleRowSelectionChange,
    }
  });
  const copyFn = (text) => {
    console.log(text)
    const success = copyTextToClipboard(text);
      if (success) {
        $message.success('复制成功！');
      } else {
        $message.error('复制失败！');
      }
      return success;
  }
  const awaitFetchParamFn = (extraParams) => {
    const params = {
      pageNo: pagination.value.current,
      pageSize: pagination.value.pageSize,
      ...filterObj(unref(queryParam.value)),

      ...unref(isorter),
      ...extraParams,
    }
    return params
  }
  // 查询参数
  const queryParam = ref({})
  
  // 展开的行
  const expandedRowKeys = ref([])
  
  // 自定义参数处理函数
  const beforeFetch = options.beforeFetch || ((params) => params)
  
  // 展开数据查询函数（可选）
  const expandApiFn = options.expandApiFn || null
  
  // fetchTable 完成后的回调（可选）
  const afterFetch = options.afterFetch || null
  const tableType = options.tableType || 'stable'
  /**
   * 表格数据获取
   */
  async function fetchTable(pageNo,extraParams={}) {
    if (pageNo === 1 ){
      pagination.value.current = 1;
    }
    let params = awaitFetchParamFn(extraParams)
    
    // 调用自定义参数处理函数
    params = beforeFetch(params)

    
    loading.value = true
    try {
      const res = await apiFn(params)

      // 处理不同的响应格式
      dataSource.value = res.records || res.result
      pagination.value.total = res.total
      if (afterFetch && typeof afterFetch === 'function') {
        await afterFetch(params,res)
      }
      
      return res
    } catch (err) {
      console.error('useTable error:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  function pageChange(page, pageSize) {

    console.log(page, pageSize)
    pagination.value.current = page.current
    pagination.value.pageSize = page.pageSize;

    fetchTable(undefined, {})
  }
  function vxeTablePageChange(page, pageSize) {

    console.log(page, pageSize)
    pagination.value.current = page
    pagination.value.pageSize = pageSize;

    fetchTable(undefined, {})
  }

  /**
   * 重置查询
   */
  function resetTable() {
    pagination.value.current = 1
    queryParam.value = {}
    fetchTable(1)
  }

  function filterOption(input, option) {

    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }

  /**
   * 计算表格高度
   */
  function calcTableHeight(type:'stable'){
    console.log(type)
    const formHeight = (document.querySelector('.searchForm') as HTMLElement)?.offsetHeight  || 0;
    const tablepagination = document.querySelector('.ant-pagination') ? (document.querySelector('.ant-pagination') as HTMLElement).offsetHeight + 32  : 0;
    console.log(tablepagination,formHeight)
    let tableHeight = (document.body as HTMLElement).clientHeight - 39 - formHeight  - pageOffset - tablepagination - 40 - 50 - 6 - 6 - 24;
    sTableHeight.value = tableHeight;
    const kolPagination = (document.querySelector('.kol-pagination') as HTMLElement)?.offsetHeight + 10  || 0;
   
    sTableHeight.value = tableHeight -  kolPagination;
    tableHeight = tableHeight - kolPagination;
    const tableBody = type == 'stable' ? document.querySelector('.surely-table-body') as HTMLElement | null : document.querySelector('.ant-table-body') as HTMLElement | null;

    console.log(tableHeight)
    if (tableBody) {
      tableBody.style.height = tableHeight + 'px';
     
    }
  }
  function newCalcTableHeight(type:'stable'){
    console.log(type)
    const formHeight = (document.querySelector('.searchForm') as HTMLElement)?.offsetHeight  || 0;
    const tablepagination = document.querySelector('.ant-pagination') ? (document.querySelector('.ant-pagination') as HTMLElement).offsetHeight + 32  : 0;
    console.log(tablepagination,formHeight)
    let tableHeight = (document.body as HTMLElement).clientHeight - 39 - formHeight  - pageOffset - tablepagination - 40 - 50 - 6 - 6 - 24 - 24 - 12;
    sTableHeight.value = tableHeight;
    const kolPagination = (document.querySelector('.kol-pagination') as HTMLElement)?.offsetHeight + 10  || 0;
   
    sTableHeight.value = tableHeight -  kolPagination;
    tableHeight = tableHeight - kolPagination;
    const tableBody = type == 'stable' ? document.querySelector('.surely-table-body') as HTMLElement | null : document.querySelector('.ant-table-body') as HTMLElement | null;

    console.log(tableHeight)
    if (tableBody) {
      tableBody.style.height = tableHeight + 'px';
     
    }
  }


function aTablehandleRowSelectionChange(_keys){
  console.log(_keys)
  selectedRowKeys.value = _keys
}


/**
 * 多选处理
 * @param _keys 选择的行key
 * @param selectedRows 选择的行数据
 */

  function handleRowSelectionChange(_keys, selectedRows) {
    console.log(_keys, selectedRows)
    selectedRowKeys.value = selectedRows.map(item => item.id)

  }

  function searchQuery () {
    selectedRowKeys.value = [];
    atableRowSelection.value.selectedRowKeys = [];
    fetchTable(1)
  }
  function searchReset () {
    queryParam.value = {}
    selectedRowKeys.value = [];
    atableRowSelection.value.selectedRowKeys = [];
    fetchTable(1)
  }
  
  /**
   * 更新分页配置
   * @param config 要更新的分页配置项
   */
  function updatePagination(config) {
    Object.assign(pagination.value, config)
    console.log(pagination.value)
  }
    /**
   * 更新分页配置
   * @param config 要更新的分页配置项
   */
    function updateSort(config) {
      Object.assign(isorter.value, config)
    }
    
  /**
   * 处理展开行
   * @param expanded 是否展开
   * @param record 当前行数据
   */
  async function handleExpand(expanded, record) {
    if (expanded) {
      // 添加到展开列表
      if (!expandedRowKeys.value.includes(record.id)) {
        expandedRowKeys.value.push(record.id)
      }
      
      // 如果提供了展开数据查询函数，则加载子数据
      if (expandApiFn && typeof expandApiFn === 'function') {
        try {
          loading.value = true
          const childData = await expandApiFn(record)
          
          // 将子数据添加到对应的父节点
          const parentIndex = dataSource.value.findIndex(item => item.id === record.id)
          if (parentIndex !== -1) {
            dataSource.value[parentIndex].children = childData
          }
        } catch (err) {
          console.error('handleExpand error:', err)
        } finally {
          loading.value = false
        }
      }
    } else {
      // 从展开列表中移除
      const index = expandedRowKeys.value.indexOf(record.id)
      if (index > -1) {
        expandedRowKeys.value.splice(index, 1)
      }
    }
  }
  
  /**
   * 查询展开的数据（刷新已展开行的子数据）
   */
  async function fetchExpandedData() {
    if (!expandApiFn || expandedRowKeys.value.length === 0) {
      return
    }
    
    try {
      loading.value = true
      
      // 遍历所有展开的行，重新加载子数据
      for (const id of expandedRowKeys.value) {
        const record = dataSource.value.find(item => item.id === id)
        if (record) {
          const childData = await expandApiFn(record)
          record.children = childData
        }
      }
    } catch (err) {
      console.error('fetchExpandedData error:', err)
    } finally {
      loading.value = false
    }
  }
  
  onMounted(() => {
    if (newLayout.value){
      newCalcTableHeight(tableType);
    }else{

      calcTableHeight(tableType);
    }
  });

  return {
    newLayout,
    pageChange,
    isorter,
    unref,
    filterObj,
    loading,
    dataSource,
    pagination,
    queryParam,
    sTableHeight,
    fetchTable,
    vxeTablePageChange,
    resetTable,
    rowSelection,
    selectedRowKeys,
    filterOption,
    searchQuery,
    searchReset,
    updatePagination,
    updateSort,
    calcTableHeight,
    // 展开数据相关
    expandedRowKeys,
    awaitFetchParamFn,
    handleExpand,
    fetchExpandedData,
    copyFn,
    atableRowSelection
  }
}
