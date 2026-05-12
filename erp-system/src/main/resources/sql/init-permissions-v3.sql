-- Add ALL missing permissions
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, perms, sort, status) VALUES
-- Missing supplier buttons
(1301,101,'新增供应商','B','purchase:supplier:add',1,1),
(1302,101,'编辑供应商','B','purchase:supplier:edit',2,1),
(1303,101,'删除供应商','B','purchase:supplier:delete',3,1),
-- Missing customer buttons
(1311,201,'新增客户','B','sales:customer:add',1,1),
(1312,201,'编辑客户','B','sales:customer:edit',2,1),
(1313,201,'删除客户','B','sales:customer:delete',3,1),
-- Missing warehouse buttons
(1321,301,'新增仓库','B','inventory:warehouse:add',1,1),
(1322,301,'编辑仓库','B','inventory:warehouse:edit',2,1),
(1323,301,'删除仓库','B','inventory:warehouse:delete',3,1),
-- Missing product buttons
(1331,302,'新增商品','B','inventory:product:add',1,1),
(1332,302,'编辑商品','B','inventory:product:edit',2,1),
(1333,302,'删除商品','B','inventory:product:delete',3,1),
-- Missing category (no menu yet, add menu+buttons)
(1341,300,'分类管理','C','inventory:category:list',0,1),
(1342,1341,'新增分类','B','inventory:category:add',1,1),
(1343,1341,'编辑分类','B','inventory:category:edit',2,1),
(1344,1341,'删除分类','B','inventory:category:delete',3,1),
-- Missing unit (no menu yet, add menu+buttons)
(1351,300,'单位管理','C','inventory:unit:list',0,1),
(1352,1351,'新增单位','B','inventory:unit:add',1,1),
(1353,1351,'编辑单位','B','inventory:unit:edit',2,1),
(1354,1351,'删除单位','B','inventory:unit:delete',3,1),
-- Missing subject buttons
(1361,401,'新增科目','B','finance:subject:add',1,1),
(1362,401,'编辑科目','B','finance:subject:edit',2,1),
(1363,401,'删除科目','B','finance:subject:delete',3,1),
-- Missing production audit buttons
(1371,505,'审核工单','B','production:workorder:audit',3,1),
(1372,509,'审核计划','B','production:planning:audit',3,1),
-- Missing stock flow view
(1381,303,'库存流水','C','inventory:stock:flow',0,1);

-- Grant to admin
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 1301 AND 1381;
