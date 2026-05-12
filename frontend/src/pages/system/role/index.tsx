import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tree, Modal, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getRoleList, addRole, updateRole, deleteRole, getRoleMenus, assignRoleMenus, getMenuTree } from '@/services/system';
import PermissionBtn from '@/components/PermissionBtn';

export default function RoleList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [menuVisible, setMenuVisible] = useState(false);
  const [currentRoleId, setCurrentRoleId] = useState<number>();
  const [checkedKeys, setCheckedKeys] = useState<number[]>([]);
  const [menuTree, setMenuTree] = useState<any[]>([]);

  const openMenuModal = async (roleId: number) => {
    setCurrentRoleId(roleId);
    const [menus, tree] = await Promise.all([getRoleMenus(roleId), getMenuTree()]);
    setCheckedKeys(menus || []);
    setMenuTree(tree || []);
    setMenuVisible(true);
  };

  const columns: ProColumns[] = [
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode' },
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName' },
    { title: '排序', dataIndex: 'roleSort', key: 'roleSort', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="system:role:edit" type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</PermissionBtn>
          <PermissionBtn permission="system:role:edit" type="link" onClick={() => openMenuModal(record.id)}>分配权限</PermissionBtn>
          <PermissionBtn permission="system:role:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => {
              await deleteRole(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        request={async () => {
          const data = await getRoleList();
          return { data, total: data?.length || 0, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={false}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="system:role:add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>新增角色</PermissionBtn>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑角色' : '新增角色'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        onFinish={async (values) => {
          if (editRecord) {
            await updateRole(editRecord.id, values);
          } else {
            await addRole(values);
          }
          message.success(editRecord ? '修改成功' : '新增成功');
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormText name="roleCode" label="角色编码" rules={[{ required: true }]} />
        <ProFormText name="roleName" label="角色名称" rules={[{ required: true }]} />
        <ProFormDigit name="roleSort" label="排序" />
      </ModalForm>
      <Modal
        title="分配权限"
        open={menuVisible}
        onCancel={() => setMenuVisible(false)}
        onOk={async () => {
          await assignRoleMenus(currentRoleId!, checkedKeys);
          message.success('权限分配成功');
          setMenuVisible(false);
        }}
      >
        <Tree
          checkable
          defaultExpandAll
          fieldNames={{ title: 'menuName', key: 'id' }}
          treeData={menuTree}
          checkedKeys={checkedKeys}
          onCheck={(keys: any) => setCheckedKeys(keys.checked)}
        />
      </Modal>
    </>
  );
}
