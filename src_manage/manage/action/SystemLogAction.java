package manage.action;

import m.common.action.ActionMeta;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import manage.service.SystemLogService;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableMeta;

@ActionMeta(name="manageSystemLog")
public class SystemLogAction extends ManageAction {

	public JSONMessage doClear(){
		setLogContent("清除", "清除日志");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			String msg=getService(SystemLogService.class).clear();
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
	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/manageSystemLog/systemLogData",
			modelClass="manage.model.SystemLog",
			searchField="realname,username",searchHint="请输入姓名或者账号",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "realname", title = "姓名", width=80),
			@ActionTableColMeta(field = "username", title = "账号", width=120),
			@ActionTableColMeta(field = "userType", title = "类型", width=80),
			@ActionTableColMeta(field = "operType", title = "操作", width=80),
			@ActionTableColMeta(field = "operIp", title = "IP", width=150),
			@ActionTableColMeta(field = "createDate", title = "时间", width=180,dateFormat="yyyy-MM-dd HH:mm:ss",initSort=TableColSort.DESC),
			@ActionTableColMeta(field = "description", title = "描述", width=250),
			@ActionTableColMeta(field = "operResult", title = "结果", width=90),
			@ActionTableColMeta(field = "resultException", title = "异常", width=130)
		},
		querys = {
			@QueryMeta(field = "realname", name = "姓名", type = QueryType.TEXT, hint="请输入姓名", likeMode=true),
			@QueryMeta(field = "username", name = "账号", type = QueryType.TEXT, hint="请输入账号", likeMode=true),
			@QueryMeta(field = "createDate", name = "时间", type = QueryType.DATE_RANGE, hint="请输入时间", dateFormat="yyyy-MM-dd")
		},
		buttons= {
			@ButtonMeta(title="清除日志", event = ButtonEvent.AJAX, url = "action/manageSystemLog/doClear", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,confirm="确定要清除全部日志吗?",
				power="manage_system_power"
			)
		}
	)
	public JSONMessage systemLogData(){
		return getListDataResult(null);
	}
	
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

}
