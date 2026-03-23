package org.jeecg.modules.instagram.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


public class XmlUtils {

    /**
     * xml转map 不带属性
     *
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map xmlToMap(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xmlToMap(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 带属性
     *
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map xmlToMapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xmlToMapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            //根节点只有一个文本内容
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 不带属性
     *
     * @param element
     * @return
     */
    private static Object xmlToMap(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<Element> elements = element.elements();
        if (elements.size() == 0) {
            map.put(element.getName(), element.getText());
            if (!element.isRootElement()) {
                return element.getText();
            }
        } else if (elements.size() == 1) {
            map.put(elements.get(0).getName(), xmlToMap(elements.get(0)));
        } else {
            // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
            // 构造一个map用来去重
            Map<String, Element> tempMap = new LinkedHashMap<String, Element>();
            for (Element ele : elements) {
                tempMap.put(ele.getName(), ele);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = tempMap.get(string).getNamespace();
                List<Element> elements2 = element.elements(new QName(string,
                        namespace));
                // 如果同名的数目大于1则表示要构建list
                if (elements2.size() > 1) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element ele : elements2) {
                        if (StringUtils.isEmpty(ele.getText())) {
                            continue;
                        }
                        list.add(xmlToMap(ele));
                    }
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1则直接递归去
                    map.put(string, xmlToMap(elements2.get(0)));
                }
            }
        }

        return map;
    }

    /**
     * xml转map 带属性
     *
     * @param element
     * @return
     */
    public static Object xmlToMapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<Element> elements = element.elements();
        //没有子节点的情况 key - value
        if (elements.size() == 0) {
            map.put(element.getName(), element.getText());
            if (!element.isRootElement()) {
                return element.getText();
            }
        } else if (elements.size() == 1) {
            //一个子节点的情况，map
            map.put(elements.get(0).getName(),
                    xmlToMapWithAttr(elements.get(0)));
        } else {
            //多个子节点的情况，key-value、map、list 情况据需要考虑
            // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
            // 构造一个map用来去重
            Map<String, Element> tempMap = new LinkedHashMap<String, Element>();
            for (Element ele : elements) {
                tempMap.put(ele.getName(), ele);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = tempMap.get(string).getNamespace();
                List<Element> elements2 = element.elements(new QName(string,
                        namespace));
                // 如果同名的数目大于1则表示要构建list
                if (elements2.size() > 1) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element ele : elements2) {
                        if (StringUtils.isEmpty(ele.getText())) {
                            continue;
                        }
                        list.add(xmlToMapWithAttr(ele));
                    }
                    map.put(string, list);
                } else if (elements2.size() == 1 && "List".equals(elements2.get(0).attributeValue("nodeType"))) {
                    //如果同名的数据只有一个并且属性nodeType为List，则要构建list
                    List<Object> list = new ArrayList<Object>();
                    Element ele = elements2.get(0);
                    if (StringUtils.isEmpty(ele.getText())) {
                        continue;
                    }
                    list.add(xmlToMapWithAttr(ele));
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1则直接递归去
                    map.put(string, xmlToMapWithAttr(elements2.get(0)));
                }
            }
        }
        return map;
    }
}

