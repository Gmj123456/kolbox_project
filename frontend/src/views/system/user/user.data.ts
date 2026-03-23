import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { getAllRolesListNoByTenant, getAllTenantList } from './user.api';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
export const columns: BasicColumn[] = [
  {
    title: '用户账号',
    dataIndex: 'username',
   
    key: 'username',
  },
  {
    title: '头像',
    dataIndex: 'avatar',
    width:80,
    key: 'avatar',
  },
  {
    title: '昵称',
    dataIndex: 'realname',
   
    key: 'realname',
  },
  {
    title: '邮箱 ',
    dataIndex: 'email',
   
    key: 'email',
  },
  // {
  //   title: '等级',
  //   dataIndex: 'gradeName',
  //   width:100,
  //   key: 'gradeName',
  // },
  // {
  //   title: '过期日期',
  //   dataIndex: 'expirationDate',
  
  //   key: 'expirationDate',
  // },
  {
    title: '性别',
    dataIndex: 'sex_dictText',
    width:80,
    key: 'sex_dictText',
   
  },
  {
    title: '用户类型',
    dataIndex: 'userType',
    width:100,
    key: 'userType',
   
  },
  {
    title: '手机号',
    dataIndex: 'phone',
  
    key: 'phone',
  },
  // {
  //   title: '部门',
  
  //   dataIndex: 'orgCode',
  //   key: 'orgCode',
  // },
  // {
  //   title: '状态',
  //   dataIndex: 'status_dictText',
  //   width: 80,
  //   key: 'status_dictText',
  // },
  {
    title: '是否离职',
    dataIndex: 'isLeave',
    width: 80,
    key: 'isLeave',
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 200,
    key: 'action',
   
  },

];

export const recycleColumns: BasicColumn[] = [
  {
    title: '用户账号',
    dataIndex: 'username',
    width: 100,
  },
  {
    title: '用户姓名',
    dataIndex: 'realname',
    width: 100,
  },
  {
    title: '头像',
    dataIndex: 'avatar',
    width: 80,
    customRender: render.renderAvatar,
  },
  {
    title: '性别',
    dataIndex: 'sex',
    width: 80,
    sorter: true,
    customRender: ({ text }) => {
      return render.renderDict(text, 'sex');
    },
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '账号',
    field: 'username',
    component: 'JInput',
    colProps: { span: 4 },
  },
  {
    label: '名字',
    field: 'realname',
    component: 'JInput',
   colProps: { span: 4 },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
      placeholder: '请选择性别',
      stringToNumber: true,
    },
    //colProps: { span: 6 },
  },
  {
    label: '手机号码',
    field: 'phone',
    component: 'Input',
    //colProps: { span: 6 },
  },
  {
    label: '用户状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'user_status',
      placeholder: '请选择状态',
      stringToNumber: true,
    },
   //colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '用户账号',
    field: 'username',
    component: 'Input',
    required: true,
    dynamicDisabled: ({ values }) => {
      return !!values.id;
    },
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('sys_user', 'username', model, schema, true),
  },
  {
    label: '用户类型',
    field: 'userType',
    component: 'JDictSelectTag',
    required: true,
    componentProps: {
      dictCode: "user_type",
      stringToNumber: true,
    },

  },
  {
    label: '是否离职',
    field: 'isLeave',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        { label: '是', value: 1 },
        { label: '否', value: 0 },
      ],
    },

  },
  {
    label: '登录密码',
    field: 'password',
    component: 'StrengthMeter',
    componentProps:{
      autocomplete: 'new-password',
    },
    rules: [
      {
        required: true,
        message: '请输入登录密码',
      },
     
    ],
  },
  {
    label: '确认密码',
    field: 'confirmPassword',
    component: 'InputPassword',
    dynamicRules: ({ values }) => rules.confirmPassword(values, true),
  },
  {
    label: '用户姓名',
    field: 'realname',
    required: true,
    component: 'Input',
  },
  {
    label: '工号',
    field: 'workNo',
    required: false,
    component: 'Input',
    dynamicRules: ({ model, schema }) => rules.duplicateCheckRule('sys_user', 'work_no', model, schema, false),
  },
/*  {
    label: '职务',
    field: 'post',
    required: false,
    component: 'JSelectPosition',
    componentProps: {
      labelKey: 'name',
    },
  },*/
  {
    label: '职务',
    field: 'positionType',
    required: false,
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "user_position"
    },
  },
  {
    label: '角色',
    field: 'selectedroles',
    component: 'ApiSelect',
    componentProps: {
      mode: 'multiple',
      api: getAllRolesListNoByTenant,
      labelField: 'roleName',
      valueField: 'id',
      immediate: false,
    },
  },
  {
    label: '所属部门',
    field: 'selecteddeparts',
    component: 'JSelectDept',
    componentProps: ({ formActionType, formModel }) => {
      return {
        sync: false,
        checkStrictly: true,
        defaultExpandLevel: 2,

        onSelect: (options, values) => {
          const { updateSchema } = formActionType;
          //所属部门修改后更新负责部门下拉框数据
          updateSchema([
            {
              field: 'departIds',
              componentProps: { options },
            },
            //修改主岗位和兼职岗位的参数
            {
              field: 'mainDepPostId',
              componentProps: { params: { departIds: values?values.value.join(","): "" } },
            },
            {
              field: 'otherDepPostId',
              componentProps: { params: { departIds: values?values.value.join(","): "" } },
            }
          ]);
          //update-begin---author:wangshuai---date:2024-05-11---for:【issues/1222】用户编辑界面“所属部门”与“负责部门”联动出错整---
          if(!values){
            formModel.departIds = [];
            formModel.mainDepPostId = "";
            formModel.otherDepPostId = "";
            return;
          }
          //update-end---author:wangshuai---date:2024-05-11---for:【issues/1222】用户编辑界面“所属部门”与“负责部门”联动出错整---
          //所属部门修改后更新负责部门数据
          formModel.departIds && (formModel.departIds = formModel.departIds.filter((item) => values.value.indexOf(item) > -1));
        },
      };
    },
  },
  {
    label: '主岗位',
    field: 'mainDepPostId',
    component: 'JSelectDepartPost',
    componentProps: {
      rowKey: 'id',
      multiple: false
    },
    ifShow:  ({ values }) => {
      if(!values.selecteddeparts){
        return false;
      }
      return !(values.selecteddeparts instanceof Array && values.selecteddeparts.length == 0);
    },
  },
  {
    label: '兼职岗位',
    field: 'otherDepPostId',
    component: 'JSelectDepartPost',
    componentProps: {
      rowKey: 'id',
    },
    ifShow:  ({ values }) => {
      if(!values.selecteddeparts){
        return false;
      }
      return !(values.selecteddeparts instanceof Array && values.selecteddeparts.length == 0);
    },
  },
  {
    label: '租户',
    field: 'relTenantIds',
    component: 'JSearchSelect',
    componentProps: {
      dict:"sys_tenant,name,id",
      async: true,
      multiple: true
    },
  },
  {
    label: '身份',
    field: 'userIdentity',
    component: 'RadioGroup',
    defaultValue: 1,
    componentProps: ({ formModel }) => {
      return {
        options: [
          { label: '普通用户', value: 1, key: '1' },
          { label: '上级', value: 2, key: '2' },
        ],
        onChange: () => {
          formModel.userIdentity == 1 && (formModel.departIds = []);
        },
      };
    },
  },
  {
    label: '负责部门',
    field: 'departIds',
    component: 'Select',
    componentProps: {
      mode: 'multiple',
    },
    ifShow: ({ values }) => values.userIdentity == 2,
  },
  {
    label: '头像',
    field: 'avatar',
    component: 'JImageUpload',
    componentProps: {
      fileMax: 1,
    },
  },
  {
    label: '生日',
    field: 'birthday',
    component: 'DatePicker',
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
      placeholder: '请选择性别',
      stringToNumber: true,
    },
  },
  {
    label: '邮箱',
    field: 'email',
    component: 'Input',
    required: false,
  
  },
  {
    label: '手机号码',
    field: 'phone',
    component: 'Input',
    required: false,
    dynamicRules: ({ model, schema }) => {
      return [
        // { ...rules.duplicateCheckRule('sys_user', 'phone', model, schema, true)[0], trigger: 'blur' },
        { pattern: /^1[3456789]\d{9}$/, message: '手机号码格式有误', trigger: 'blur' },
      ];
    },
  },
  {
    label: '座机',
    field: 'telephone',
    component: 'Input',
    rules: [{ pattern: /^0\d{2,3}-[1-9]\d{6,7}$/, message: '请输入正确的座机号码' }],
  },
  {
    label: '工作流引擎',
    field: 'activitiSync',
    defaultValue: 1,
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'activiti_sync',
      type: 'radio',
      stringToNumber: true,
    },
  },
];

export const formPasswordSchema: FormSchema[] = [
  {
    label: '用户账号',
    field: 'username',
    component: 'Input',
    componentProps: { readOnly: true },
  },
  {
    label: '登录密码',
    field: 'password',
    component: 'StrengthMeter',
    componentProps: {
      placeholder: '请输入登录密码',
    },
    rules: [
      {
        required: true,
        message: '请输入登录密码',
      },
     
    ],
  },
  {
    label: '确认密码',
    field: 'confirmPassword',
    component: 'InputPassword',
    dynamicRules: ({ values }) => rules.confirmPassword(values, true),
  },
];



//租户用户列表
export const userTenantColumns: BasicColumn[] = [
  {
    title: '用户账号',
    dataIndex: 'username',
    width: 120,
  },
  {
    title: '用户姓名',
    dataIndex: 'realname',
    width: 100,
  },
  {
    title: '头像',
    dataIndex: 'avatar',
    width: 120,
    customRender: render.renderAvatar,
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width: 100,
  },
  {
    title: '部门',
    width: 150,
    dataIndex: 'orgCodeTxt',
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ text }) => {
      if (text === '1') {
        return '正常';
      } else if (text === '3') {
        return '审批中';
      } else {
        return '已拒绝';
      }
    },
  },
];

//用户租户搜索表单
export const userTenantFormSchema: FormSchema[] = [
  {
    label: '账号',
    field: 'username',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '名字',
    field: 'realname',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
      placeholder: '请选择性别',
      stringToNumber: true,
    },
    colProps: { span: 6 },
  },
];
