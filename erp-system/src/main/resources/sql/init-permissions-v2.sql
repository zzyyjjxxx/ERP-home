-- Get max ID to start from
-- Adding ALL missing button permissions for all modules

INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, perms, sort, status) VALUES
-- Supplier (parent=101)
(1210,101,'新增供应商','B','purchase:supplier:add',1,1),
(1211,101,'编辑供应商','B','purchase:supplier:edit',2,1),
(1212,101,'删除供应商','B','purchase:supplier:delete',3,1),
-- Customer (parent=201)
(1220,201,'新增客户','B','sales:customer:add',1,1),
(1221,201,'编辑客户','B','sales:customer:edit',2,1),
(1222,201,'删除客户','B','sales:customer:delete',3,1),
-- Warehouse (parent=301)
(1230,301,'新增仓库','B','inventory:warehouse:add',1,1),
(1231,301,'编辑仓库','B','inventory:warehouse:edit',2,1),
(1232,301,'删除仓库','B','inventory:warehouse:delete',3,1),
-- Product (parent=302)
(1240,302,'新增商品','B','inventory:product:add',1,1),
(1241,302,'编辑商品','B','inventory:product:edit',2,1),
(1242,302,'删除商品','B','inventory:product:delete',3,1),
-- Subject (parent=401)
(1250,401,'新增科目','B','finance:subject:add',1,1),
(1251,401,'编辑科目','B','finance:subject:edit',2,1),
(1252,401,'删除科目','B','finance:subject:delete',3,1),
-- Category (needs menu first)
(1253,300,'分类管理','C','inventory:category:list',0,1),
(1254,1253,'新增分类','B','inventory:category:add',1,1),
(1255,1253,'编辑分类','B','inventory:category:edit',2,1),
(1256,1253,'删除分类','B','inventory:category:delete',3,1),
-- Unit (needs menu first)
(1257,300,'单位管理','C','inventory:unit:list',0,1),
(1258,1257,'新增单位','B','inventory:unit:add',1,1),
(1259,1257,'编辑单位','B','inventory:unit:edit',2,1),
(1260,1257,'删除单位','B','inventory:unit:delete',3,1);

-- Grant all new permissions to admin role
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 1210 AND 1260;
