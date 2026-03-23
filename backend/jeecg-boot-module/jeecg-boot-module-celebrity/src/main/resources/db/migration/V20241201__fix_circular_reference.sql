-- 修复分类数据循环引用问题的SQL脚本
-- 该脚本用于检测和修复kol_category、kol_tag_category、kol_attribute表中的循环引用问题

-- 1. 检测kol_category表中的循环引用
SELECT 
    c1.id AS node_id,
    c1.category_name AS node_name,
    c1.parent_id AS parent_id,
    c2.id AS parent_node_id,
    c2.category_name AS parent_node_name
FROM kol_category c1
JOIN kol_category c2 ON c1.parent_id = c2.id
WHERE c1.id = c2.parent_id
AND c1.id != '0'
AND c1.parent_id != '0';

-- 2. 检测kol_tag_category表中的循环引用
SELECT 
    c1.id AS node_id,
    c1.category_name AS node_name,
    c1.parent_id AS parent_id,
    c2.id AS parent_node_id,
    c2.category_name AS parent_node_name
FROM kol_tag_category c1
JOIN kol_tag_category c2 ON c1.parent_id = c2.id
WHERE c1.id = c2.parent_id
AND c1.id != '0'
AND c1.parent_id != '0';

-- 3. 检测kol_attribute表中的循环引用
SELECT 
    c1.id AS node_id,
    c1.attribute_name AS node_name,
    c1.parent_id AS parent_id,
    c2.id AS parent_node_id,
    c2.attribute_name AS parent_node_name
FROM kol_attribute c1
JOIN kol_attribute c2 ON c1.parent_id = c2.id
WHERE c1.id = c2.parent_id
AND c1.id != '0'
AND c1.parent_id != '0';

-- 4. 修复常见的循环引用问题（将有问题的节点设置为根节点）
-- 注意：请根据实际检测结果调整以下UPDATE语句

-- 修复kol_category循环引用（示例）
-- UPDATE kol_category SET parent_id = '0' WHERE id IN (
--     SELECT node_id FROM (
--         SELECT c1.id AS node_id
--         FROM kol_category c1
--         JOIN kol_category c2 ON c1.parent_id = c2.id
--         WHERE c1.id = c2.parent_id
--         AND c1.id != '0'
--         AND c1.parent_id != '0'
--     ) AS temp
-- );

-- 修复kol_tag_category循环引用（示例）
-- UPDATE kol_tag_category SET parent_id = '0' WHERE id IN (
--     SELECT node_id FROM (
--         SELECT c1.id AS node_id
--         FROM kol_tag_category c1
--         JOIN kol_tag_category c2 ON c1.parent_id = c2.id
--         WHERE c1.id = c2.parent_id
--         AND c1.id != '0'
--         AND c1.parent_id != '0'
--     ) AS temp
-- );

-- 修复kol_attribute循环引用（示例）
-- UPDATE kol_attribute SET parent_id = '0' WHERE id IN (
--     SELECT node_id FROM (
--         SELECT c1.id AS node_id
--         FROM kol_attribute c1
--         JOIN kol_attribute c2 ON c1.parent_id = c2.id
--         WHERE c1.id = c2.parent_id
--         AND c1.id != '0'
--         AND c1.parent_id != '0'
--     ) AS temp
-- );

-- 5. 验证修复结果
-- 重新运行第1-3步的查询，确认没有循环引用

-- 6. 建议添加约束防止未来出现循环引用
-- ALTER TABLE kol_category ADD CONSTRAINT chk_no_self_reference 
-- CHECK (id != parent_id);

-- ALTER TABLE kol_tag_category ADD CONSTRAINT chk_no_self_reference 
-- CHECK (id != parent_id);

-- ALTER TABLE kol_attribute ADD CONSTRAINT chk_no_self_reference 
-- CHECK (id != parent_id);