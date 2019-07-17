package manage.service;

import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.exception.MException;
import m.system.util.StringUtil;
import manage.model.StatusModel;

public class StatusService extends Service {

	/**
	 *停用	 */
	public void doDisable(StatusModel model) throws MException {
		if(StringUtil.isSpace(model.getOid())){
			throw new MException(this.getClass(), "没有要停用的记录!");
		}else if(model.getOid().length()<=2){
			throw new MException(this.getClass(), "初始化资源不能停用!");
		}
		model.setStatus("9");
		ModelUpdateUtil.updateModel(model,new String[]{"status"});
	}
	/**
	 * 恢复  */
	public void doRecovery(StatusModel model) throws MException {
		if(StringUtil.isSpace(model.getOid())){
			throw new MException(this.getClass(), "没有要恢复的记录!");
		}
		model.setStatus("0");
		ModelUpdateUtil.updateModel(model,new String[]{"status"});
	}
}
