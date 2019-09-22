package manage.action;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.model.DictionaryData;
import manage.service.DictionaryDataService;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ButtonMeta.SuccessMethod;
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
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableMeta;
import manage.util.tag.DictionaryUtil;

@ActionMeta(name="manageDictionaryData")
public class DictionaryDataAction extends StatusAction {

	private DictionaryData model;
	public JSONMessage doSave(){
		setLogContent("保存", "保存字典数据");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(DictionaryDataService.class).save(model);
			DictionaryUtil.refresh();
			result.push("code", 0);
			result.push("msg", "保存成功!");
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public void doDisableCallback() {
		DictionaryUtil.refresh();
	}
	@Override
	public void doRecoveryCallback() {
		DictionaryUtil.refresh();
	}

	@ActionFormMeta(title="字典值",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(field = "model.dictionaryType.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="名称",field="model.name",type=FormFieldType.TEXT,hint="请输入名称")
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="值",field="model.value",type=FormFieldType.TEXT,hint="请输入真实值")
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="排序",field="model.sort",type=FormFieldType.INT,hint="请输入排序号")
			})
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/manageDictionaryData/doSave",success=FormSuccessMethod.DONE_BACK,
				power="manage_system_power")
		}
	)
	public ActionResult toAdd() throws Exception{
		return getFormResult(this,ActionFormPage.EDIT);
	}
	@ActionFormMeta(title="字典值",
			rows={
				@FormRowMeta(minWidth=200,fields={
					@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
					@FormFieldMeta(field = "model.dictionaryType.oid", type = FormFieldType.HIDDEN),
					@FormFieldMeta(title="名称",field="model.name",type=FormFieldType.TEXT,hint="请输入名称")
				}),
				@FormRowMeta(minWidth=200,fields={
					@FormFieldMeta(title="值(不可改)",field="model.value",type=FormFieldType.TEXT,hint="请输入真实值",disabled=true)
				}),
				@FormRowMeta(minWidth=200,fields={
					@FormFieldMeta(title="排序",field="model.sort",type=FormFieldType.INT,hint="请输入排序号")
				})
			},
			buttons={
				@FormButtonMeta(title = "保存", url = "action/manageDictionaryData/doSave",success=FormSuccessMethod.DONE_BACK,
					power="manage_system_power")
			}
		)
		public ActionResult toEdit() throws Exception{
			if(null!=model&&!StringUtil.isSpace(model.getOid())){
				model=ModelQueryUtil.getModel(model);
			}
			return getFormResult(this,ActionFormPage.EDIT);
		}
	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/manageDictionaryData/dictionayDataList",
			modelClass="manage.model.DictionaryData",
		cols = { 
			@ActionTableColMeta(field="oid",type=TableColType.INDEX, title = ""),
			@ActionTableColMeta(field = "name", title = "名称", width=130),
			@ActionTableColMeta(field = "value", title = "值", width=130),
			@ActionTableColMeta(field = "sort", title = "排序号", width=60),
			@ActionTableColMeta(field = "status", title = "状态", width=100,type=TableColType.STATUS,power="manage_system_power",dictionaryType="status",align="center"),
			@ActionTableColMeta(field="oid",title="操作",width=85,buttons={
				@ButtonMeta(title="修改",style=ButtonStyle.NORMAL,event = ButtonEvent.MODAL,modalWidth=450, url = "action/manageDictionaryData/toEdit",
					params={@ParamMeta(name = "model.oid",field="oid")},success=SuccessMethod.REFRESH,
					power="manage_system_power"),
			})
		},
		querys = {
			@QueryMeta(field="dictionaryType.oid",type=QueryType.HIDDEN,name = "字典oid")
		},
		buttons = {
			@ButtonMeta(title="新增",style=ButtonStyle.NORMAL,event = ButtonEvent.MODAL,modalWidth=450, url = "action/manageDictionaryData/toAdd",
				queryParams={@ParamMeta(name = "model.dictionaryType.oid",field="dictionaryType.oid")},success=SuccessMethod.REFRESH,
				power="manage_system_power"),
		}
	)
	public JSONMessage dictionayDataList(){
		return getListDataResult(null);
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
	@Override
	public String getStatusPower() {
		return "manage_system_power";
	}
	public DictionaryData getModel() {
		return model;
	}
	public void setModel(DictionaryData model) {
		this.model = model;
	}

}
