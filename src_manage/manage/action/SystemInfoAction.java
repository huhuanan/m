package manage.action;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import manage.model.SystemInfo;
import manage.service.SystemInfoService;
import manage.util.page.form.ActionFormMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormSuccessMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormOtherMeta;
import manage.util.page.form.FormRowMeta;
import manage.util.page.query.SelectDataMeta;

@ActionMeta(name="manageSystemInfo")
public class SystemInfoAction extends ManageAction {
	private SystemInfo model;
	
	public JSONMessage doSave(){
		setLogContent("保存", "保存系统信息");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			String msg=getService(SystemInfoService.class).save(model);
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
	
	@ActionFormMeta(title="系统信息",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="后台标题", field = "model.backgroundTitle", type = FormFieldType.TEXT,span=14,hint="请输入后台标题"),
				@FormFieldMeta(field = "model.titleImage.oid", type = FormFieldType.IMAGE,imageType="titleImage",hideTitle=true,span=6),
				@FormFieldMeta(field = "model.titleType", type = FormFieldType.SELECT,hideTitle=true,span=4,
					querySelectDatas={@SelectDataMeta(title = "文本", value = "N"),@SelectDataMeta(title = "图片", value = "Y")})
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="网站域名",field="model.domainName",type=FormFieldType.TEXT,hint="请输入网站域名  以http://或https://开头")
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="静态域名",field="model.staticDomain",type=FormFieldType.TEXT,span=18,hint="请输入静态加速域名  以http://或https://开头 以/结尾"),
				@FormFieldMeta(title="静态加速模式",field = "model.staticMode", type=FormFieldType.SELECT,span=6,hint="请选择静态加速模式",
					querySelectDatas= {@SelectDataMeta(title = "不加速", value = "N"),@SelectDataMeta(title = "静态域名加速", value = "A"),
						@SelectDataMeta(title = "主机间加速", value = "B"),@SelectDataMeta(title = "全加速", value = "C")}
				)
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="短信AppId",field="model.smsAppId",type=FormFieldType.TEXT,span=8,hint="请输入短信AppId"),
				@FormFieldMeta(title="短信AppKey",titleWidth=130,field="model.smsAppKey",type=FormFieldType.TEXT,span=16,hint="请输入短信AppKey")
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="短信签名",field="model.smsSign",type=FormFieldType.TEXT,span=8,hint="请输入短信签名"),
				@FormFieldMeta(title="短信验证模板id",titleWidth=130,field="model.smsVerifyTid",type=FormFieldType.TEXT,span=10,hint="请输入短信验证模板id"),
				@FormFieldMeta(field = "model.smsDebug", type = FormFieldType.RADIO,hideTitle=true,span=6,
				querySelectDatas={@SelectDataMeta(title = "正常", value = "N"),@SelectDataMeta(title = "调试", value = "Y")})
			}),
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/manageSystemInfo/doSave",success=FormSuccessMethod.NONE,power="manage_system_power")
		},
		others={
			@FormOtherMeta(title = "服务器列表", url = "action/manageHostInfo/toList")
		}
	)
	public ActionResult toEdit() throws Exception{
		model=getService(SystemInfoService.class).getUniqueModel();
		return getFormResult(this,ActionFormPage.EDIT);
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public SystemInfo getModel() {
		return model;
	}

	public void setModel(SystemInfo model) {
		this.model = model;
	}


}
