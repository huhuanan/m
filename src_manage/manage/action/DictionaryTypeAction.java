package manage.action;

import m.common.action.ActionMeta;
import m.common.model.type.FieldType;
import m.system.RuntimeData;
import m.system.document.DocumentMeta;
import m.system.document.DocumentMethodMeta;
import m.system.document.DocumentParamMeta;
import m.system.util.JSONMessage;
import manage.model.DictionaryType;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ParamMeta;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableMeta;
import manage.util.tag.DictionaryUtil;

@ActionMeta(name="manageDictionaryType",title="系统-数据字典")
public class DictionaryTypeAction extends ManageAction {
	
	private DictionaryType model;

	@DocumentMeta(
		method=@DocumentMethodMeta(title="获取数据字典",description=""),
		params={@DocumentParamMeta(name="model.type",description="数据字典类型",type=FieldType.STRING,length=20,notnull=true)
		}
	)
	public JSONMessage getDict(){
		JSONMessage result=new JSONMessage();
		try {
			result.push("list", DictionaryUtil.get(model.getType()));
			result.push("code", 0);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/manageDictionaryType/dictionayTypeList",
			modelClass="manage.model.DictionaryType",
		cols = { 
			@ActionTableColMeta(field="oid",type=TableColType.INDEX, title = ""),
			@ActionTableColMeta(field = "name", title = "名称", width=200,sort=true,initSort=TableColSort.ASC),
			@ActionTableColMeta(field = "type", title = "类型", width=200,sort=true),
			@ActionTableColMeta(field="oid",title="操作",width=85,buttons={
				@ButtonMeta(title="字典数据",event = ButtonEvent.MODAL,modalWidth=600, url = "action/manageDictionaryData/toList?method=dictionayDataList",
					params={@ParamMeta(field="oid",name="params[dictionaryType.oid]")},style=ButtonStyle.NONE)
			}),
		},
		buttons = {
			@ButtonMeta(title="图标管理",event = ButtonEvent.MODAL,modalWidth=840, url = "page/manage/image/iconManageList.html?oper=manage",
				power="manage_system_power",style=ButtonStyle.NONE)
		}
	)
	public JSONMessage dictionayTypeList(){
		return getListDataResult(null);
	}
	
	public DictionaryType getModel() {
		return model;
	}
	public void setModel(DictionaryType model) {
		this.model = model;
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
}
