package manage.action;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.QueryCondition;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.model.AdminGroup;
import manage.service.AdminGroupService;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.button.DropButtonMeta;
import manage.util.page.button.ParamMeta;
import manage.util.page.form.ActionFormMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormSuccessMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormRowMeta;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableMeta;

@ActionMeta(name="manageAdminGroup")
public class AdminGroupAction extends StatusAction {
	public AdminGroup model;
	

	/**
	 * 保存
	 * @return
	 */
	public JSONMessage doSave(){
		setLogContent("保存", "保存管理员组(角色)信息");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			String msg=getService(AdminGroupService.class).save(model);
			result.push("model.oid", model.getOid());
			result.push("code", 0);
			result.push("msg", msg);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			setLogError(e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
//	/**
//	 * 删除
//	 * @return
//	 */
//	public JSONMessage doDelete(){
//		JSONMessage result=new JSONMessage();
//		try {
//			verifyAdminOperPower("manage_system_power");
//			getService(AdminGroupService.class).delete(model);
//			result.push("code", 0);
//			result.push("msg", "删除成功");
//		} catch (Exception e) {
//			result.push("code", 1);
//			result.push("msg", e.getMessage());
//			e.printStackTrace();
//		}
//		return result;
//	}
	@ActionFormMeta(title="管理员组信息",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(field = "model.type", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="名称",field="model.name",type=FormFieldType.TEXT,hint="请输入名称",span=16),
				@FormFieldMeta(title="排序",titleWidth=80,field="model.sort",type=FormFieldType.INT,hint="请输入排序",span=8)
			}),
			@FormRowMeta(fields={@FormFieldMeta(title="描述", field = "model.description", type = FormFieldType.TEXTAREA,rows=5,hint="请输入描述")})
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/manageAdminGroup/doSave",success=FormSuccessMethod.DONE_BACK)
		}
	)
	public ActionResult toEditGroup() throws Exception{
		if(null!=model&&!StringUtil.isSpace(model.getOid())){
			model=ModelQueryUtil.getModel(model);
		}
		return getFormResult(this,ActionFormPage.EDIT);
	}
	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/manageAdminGroup/adminGroupData",
			modelClass="manage.model.AdminGroup",
			searchField="name,description",searchHint="请输入名称或者描述",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "name", title = "名称", width=130,sort=true,initSort=TableColSort.DESC),
			@ActionTableColMeta(field = "description", title = "描述", width=200),
			@ActionTableColMeta(field = "sort", title = "排序", width=100,align="left"),
			@ActionTableColMeta(field = "status", title = "状态",type=TableColType.STATUS,power="manage_system_power",dictionaryType="status",align="center"),
			@ActionTableColMeta(field = "oid",title="操作",width=150,align="center",buttons={
				@ButtonMeta(title="修改", event = ButtonEvent.MODAL,modalWidth=700, url = "action/manageAdminGroup/toEditGroup",
					params={@ParamMeta(name = "model.oid", field="oid")},success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
					power="manage_system_power"
				),
//				@ButtonMeta(title="菜单权限", event = ButtonEvent.MODAL,modalWidth=800,  url = "action/manageGroupMenuLink/setGroupMenuPage", 
//					params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,
//					power="manage_system_power"
//				),
//				@ButtonMeta(title="菜单", event = ButtonEvent.MODAL,modalWidth=800,  url = "page/manage/groupMenuLink/setGroupMenuPage.html", 
//					params={@ParamMeta(name = "adminGroupOid", field="oid")}, style=ButtonStyle.NONE,
//					power="manage_system_power"
//				),
//				@ButtonMeta(title="权限", event = ButtonEvent.MODAL,modalWidth=350,  url = "action/manageAdminGroupPower/setAdminGroupPowerPage", 
//					params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,success=SuccessMethod.MUST_REFRESH,
//					power="manage_system_power"
//				),
			},dropButtons= {
				@DropButtonMeta(title = "权限",buttons = { 
					@ButtonMeta(title="菜单权限", event = ButtonEvent.MODAL,modalWidth=800,  url = "page/manage/groupMenuLink/setGroupMenuPage.html", 
						params={@ParamMeta(name = "adminGroupOid", field="oid")}, style=ButtonStyle.NONE,
						power="manage_system_power"
					),
					@ButtonMeta(title="操作权限", event = ButtonEvent.MODAL,modalWidth=350,  url = "action/manageAdminGroupPower/setAdminGroupPowerPage", 
						params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,success=SuccessMethod.MUST_REFRESH,
						power="manage_system_power"
					),
				})
			})
		},
		querys = {
			@QueryMeta(field = "name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "description", name = "描述", type = QueryType.TEXT, hint="请输入描述", likeMode=true)
		},
		buttons = {
			@ButtonMeta(title="新增", event = ButtonEvent.MODAL,modalWidth=700,  url = "action/manageAdminGroup/toEditGroup?model.type=A", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
				power="manage_system_power"
			)
		}
	)
	public JSONMessage adminGroupData(){
		return getListDataResult(new QueryCondition[] {QueryCondition.eq("type", "A")});
	}

	@ActionFormMeta(title="角色信息",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(field = "model.type", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="名称",field="model.name",type=FormFieldType.TEXT,hint="请输入名称",span=16),
				@FormFieldMeta(title="排序",titleWidth=80,field="model.sort",type=FormFieldType.INT,hint="请输入排序",span=8)
			}),
			@FormRowMeta(fields={@FormFieldMeta(title="描述", field = "model.description", type = FormFieldType.TEXTAREA,rows=5,hint="请输入描述")})
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/manageAdminGroup/doSave",success=FormSuccessMethod.DONE_BACK)
		}
	)
	public ActionResult toEditRole() throws Exception{
		if(null!=model&&!StringUtil.isSpace(model.getOid())){
			model=ModelQueryUtil.getModel(model);
		}
		return getFormResult(this,ActionFormPage.EDIT);
	}
	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/manageAdminGroup/adminRoleData",
			modelClass="manage.model.AdminGroup",
			searchField="name,description",searchHint="请输入名称或者描述",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "name", title = "名称", width=130,sort=true,initSort=TableColSort.DESC),
			@ActionTableColMeta(field = "description", title = "描述", width=200),
			@ActionTableColMeta(field = "sort", title = "排序", width=100,align="left"),
			@ActionTableColMeta(field = "status", title = "状态",type=TableColType.STATUS,power="manage_system_power",dictionaryType="status",align="center"),
			@ActionTableColMeta(field = "oid",title="操作",width=250,align="center",buttons={
				@ButtonMeta(title="修改", event = ButtonEvent.MODAL,modalWidth=700, url = "action/manageAdminGroup/toEditRole",
					params={@ParamMeta(name = "model.oid", field="oid")},success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
					power="manage_system_power"
				),
//				@ButtonMeta(title="菜单权限", event = ButtonEvent.MODAL,modalWidth=800,  url = "action/manageGroupMenuLink/setGroupMenuPage", 
//					params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,
//					power="manage_system_power"
//				),
//				@ButtonMeta(title="菜单", event = ButtonEvent.MODAL,modalWidth=800,  url = "page/manage/groupMenuLink/setGroupMenuPage.html", 
//					params={@ParamMeta(name = "adminGroupOid", field="oid")}, style=ButtonStyle.NONE,
//					power="manage_system_power"
//				),
//				@ButtonMeta(title="权限", event = ButtonEvent.MODAL,modalWidth=350,  url = "action/manageAdminGroupPower/setAdminGroupPowerPage", 
//					params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,success=SuccessMethod.MUST_REFRESH,
//					power="manage_system_power"
//				),
				@ButtonMeta(title="关联用户", event = ButtonEvent.MODAL,modalWidth=350,  url = "page/manage/adminGroupLink/setAdminGroupLinkPage.html", 
					params={@ParamMeta(name = "adminGroupOid", field="oid")}, style=ButtonStyle.NONE,
					power="manage_system_power"
				),
			},dropButtons= {
				@DropButtonMeta(title = "权限",buttons = { 
					@ButtonMeta(title="菜单权限", event = ButtonEvent.MODAL,modalWidth=800,  url = "page/manage/groupMenuLink/setGroupMenuPage.html", 
						params={@ParamMeta(name = "adminGroupOid", field="oid")}, style=ButtonStyle.NONE,
						power="manage_system_power"
					),
					@ButtonMeta(title="操作权限", event = ButtonEvent.MODAL,modalWidth=350,  url = "action/manageAdminGroupPower/setAdminGroupPowerPage", 
						params={@ParamMeta(name = "model.adminGroup.oid", field="oid")}, style=ButtonStyle.NONE,success=SuccessMethod.MUST_REFRESH,
						power="manage_system_power"
					),
				})
			})
		},
		querys = {
			@QueryMeta(field = "name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "description", name = "描述", type = QueryType.TEXT, hint="请输入描述", likeMode=true)
		},
		buttons = {
			@ButtonMeta(title="新增", event = ButtonEvent.MODAL,modalWidth=700,  url = "action/manageAdminGroup/toEditRole?model.type=B", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
				power="manage_system_power"
			)
		}
	)
	public JSONMessage adminRoleData(){
		return getListDataResult(new QueryCondition[] {QueryCondition.eq("type", "B")});
	}
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
	@Override
	public String getStatusPower() {
		return "manage_system_power";
	}

	public AdminGroup getModel() {
		return model;
	}

	public void setModel(AdminGroup model) {
		this.model = model;
	}

}
