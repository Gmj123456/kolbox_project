<template>
  <Tinymce ref="tinymceRef" v-bind="mergedProps" :showImageUpload="false" @change="onChange" />
</template>

<script lang="ts">
  import { computed, defineComponent, nextTick, ref } from 'vue';

  import { Tinymce } from '/@/components/Tinymce';
  import { propTypes } from '/@/utils/propTypes';
  import { Form } from 'ant-design-vue';
  import { defHttp } from '/@/utils/http/axios';

  export default defineComponent({
    name: 'KolEditor',
    components: { Tinymce },
    // 不将 attrs 的属性绑定到 html 标签上
    inheritAttrs: false,
    props: {
      value: propTypes.string.def(''),
      disabled: propTypes.bool.def(false),
      //是否聚焦
      autoFocus: propTypes.bool.def(true),
      // 工具栏配置
      toolbar: propTypes.string.def('undo redo | fontselect fontsizeselect | bold italic underline strikethrough forecolor backcolor | textAlign numlist bullist outdent indent | blockquote removeformat | link image | preview'),
    },
    emits: ['change', 'update:value', 'changeBlur'],
    setup(props, { emit, attrs }) {
      const formItemContext = Form.useInjectFormItemContext();
      const tinymceRef = ref();
      
      // 图片上传大小限制 5MB
      const maxSize = 1024 * 1024 * 5;
      
      // 默认的编辑器配置
      const defaultOptions = {
        // 禁用强制根块元素，使 Enter 键直接生成 <br>
        // forced_root_block: false,
        forced_root_block: "div",
        // 允许在 pre 标签中使用 br
        br_in_pre: true,
        // 移除粘贴样式
        paste_remove_styles: true,
        paste_remove_spans: true,
        paste_preprocess: (_plugin, args) => {
          console.log('粘贴的内容:',args.content);
          // 将粘贴内容中的 p 标签和 div 标签转换为 br
          args.content = args.content
           .replace(/<\/(div|p)>\s*<(div|p)[^>]*>/gi, '<br><br>');
        },
        branding: false,
        content_style: "body { font-size: 14px; }",
        toolbar_location: 'bottom', // 工具栏在底部
        
        // 图片上传处理器
        images_upload_handler: async (blobInfo, success, failure) => {
          // 检查文件大小
          if (blobInfo.blob().size > maxSize) {
            failure("文件体积过大，最大支持5MB");
            return;
          }
          
          // 检查文件类型
          const imgType = blobInfo.blob().type;
          if (
            imgType !== "image/png" &&
            imgType !== "image/jpg" &&
            imgType !== "image/jpeg"
          ) {
            failure("图片格式错误，只支持 png 或 jpg");
            return;
          }
          
          // 使用 uploadFile 方法上传图片
          const uploadParams = {
            file: blobInfo.blob(),
         
          };
          
         const res =   await defHttp.uploadFile(
            { url: 'https://p.kolbox.com/common/file' + "/upload" },
            uploadParams,
            { isReturnResponse: true },
           
          )
          console.log("res", res);
          if (res.success) {
                // success('https://image.kolbox.com/' + res.message);
              return 'https://image.kolbox.com/' + res.message

            } else {
               return null
            }
        },
        
        // setup 函数：注册自定义按钮和事件
        setup: (editor) => {
          // 注册文本对齐菜单按钮
          editor.ui.registry.addMenuButton("textAlign", {
            icon: "align-left",
            tooltip: "文本对齐",
            fetch: (callback) => {
              const items = [
                {
                  type: "menuitem",
                  text: "左对齐",
                  onAction: () => editor.execCommand("JustifyLeft"),
                },
                {
                  type: "menuitem",
                  text: "居中",
                  onAction: () => editor.execCommand("JustifyCenter"),
                },
                {
                  type: "menuitem",
                  text: "右对齐",
                  onAction: () => editor.execCommand("JustifyRight"),
                },
                {
                  type: "menuitem",
                  text: "两端对齐",
                  onAction: () => editor.execCommand("JustifyFull"),
                },
              ];
              callback(items);
            },
          });
          editor.on('beforeinput', (e) => {
            if (e.inputType === 'insertParagraph') {
              e.preventDefault();
              editor.insertContent('<br>');
            }
          });

          // 兜底（防止 beforeinput 不触发）
          editor.on('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();

              const rng = editor.selection.getRng();
              const br = editor.getDoc().createElement('br');

              rng.insertNode(br);

              // 光标移动到 <br> 后
              rng.setStartAfter(br);
              rng.setEndAfter(br);
              editor.selection.setRng(rng);
            }
          });
          // // 监听 Enter 键，确保生成 <br> 标签
          // editor.on('keydown', (e) => {
          //   // 如果按的是 Enter 键（不是 Shift+Enter）
          //   if (e.keyCode === 13 && !e.shiftKey) {
          //     // 阻止默认行为（生成段落）
          //     e.preventDefault();
          //     // 插入 <br> 标签
          //     editor.execCommand('mceInsertContent', false, '<br>');
          //     // 移动光标到新行
          //     // editor.selection.setCursorLocation();
          //   }
          // });
          
          // 监听失焦事件
          editor.on("blur", () => {
            emit("changeBlur");
          });
          
          // 如果用户通过 attrs 传递了自定义的 setup，也要执行
          const userOptions = attrs.options as any;
          if (userOptions && typeof userOptions.setup === 'function') {
            userOptions.setup(editor);
          }
        },
      };
      
      // 合并 props、attrs 和默认配置
      const mergedProps = computed(() => {
        const userOptions = (attrs.options as any) || {};
        
        // 深度合并 options，用户的 setup 会在 defaultOptions.setup 中被调用
        const mergedOptions = {
          ...defaultOptions,
          ...userOptions,
          setup: defaultOptions.setup, // 使用我们的 setup（它会调用用户的 setup）
        };
        
        return Object.assign({}, props, attrs, { options: mergedOptions });
      });
      
      // value change 事件
      function onChange(value) {
        emit('change', value);
        emit('update:value', value);
        // update-begin--author:liaozhiyang---date:20240429---for：【QQYUN-9110】组件有值校验没消失
        nextTick(() => {
          formItemContext?.onFieldChange();
        });
        // update-end--author:liaozhiyang---date:20240429---for：【QQYUN-9110】组件有值校验没消失
      }
      
      /**
       * 获取纯文本内容（去除所有 HTML 标签）
       * @returns {string} 纯文本内容
       */
      function getPlainText() {
        if (!tinymceRef.value) {
          return '';
        }
        
        // 获取 Tinymce 编辑器实例
        // tinymceRef.value.editorRef 就是编辑器实例
        const editor = tinymceRef.value.editorRef;
        if (!editor) {
          return '';
        }
        
        // 使用 Tinymce 的 API 获取纯文本内容
        const text = editor.getContent({ format: 'text' });
        return text || '';
      }
      
      /**
       * 向编辑器插入内容
       * @param {string} text - 要插入的内容（支持 HTML）
       */
      function insertText(text) {
        if (!tinymceRef.value) {
          console.error('TinyMCE editor ref is not available');
          return;
        }
        
        // 获取 Tinymce 编辑器实例
        const editor = tinymceRef.value.editorRef;
        
        if (editor && !editor.destroyed) {
          editor.insertContent(text);
        } else {
          console.error('TinyMCE editor is not available');
        }
      }

      return {
        tinymceRef,
        mergedProps,
        onChange,
        getPlainText,
        insertText,
      };
    },
    
  });
</script>

<style lang="less" scoped></style>
