package manage.service;

import m.common.model.util.ModelQueryUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import manage.model.SystemInfo;
import manage.util.SmsUtil;

public class SystemInfoService extends Service {
	private static SystemInfo systemInfo=null;
	/** 获取静态
	 * 获取缓存系统信息,可能为空 
	 * @return
	 */
	public static SystemInfo getSystemInfo() {
		if(null==systemInfo){
			try {
				RuntimeData.getService(SystemInfoService.class).getUniqueModel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return systemInfo;
	}
	/**
	 * 获取唯一的后台设置
	 * @return
	 * @throws Exception
	 */
	public SystemInfo getUniqueModel() throws Exception{
		if(null==systemInfo){
			DataRow dr=DBManager.queryFirstRow("SELECT oid FROM os_system_info");
			SystemInfo ps=new SystemInfo();
			if(null==dr){
				ps.setOid("1");
				ps.setTitleType("N");
				ps.setBackgroundTitle("后台管理");
				ps.setDomainName("");
				ps.setSmsDebug("N");
				ps.setStaticDomain("");
				ps.setStaticMode("N");
				ModelUpdateUtil.insertModel(ps);
			}else{
				ps.setOid(dr.get(String.class,"oid"));
			}
			systemInfo=ModelQueryUtil.getModel(ps,1);
		}
		return systemInfo;
	}
	public String save(SystemInfo model) throws Exception {
		ModelUpdateUtil.updateModel(model);
		ImageLinkService.addOnlyImageLink(model.getOid(),"后台背景图片", model.getBackgroundImage());//添加业务对应的唯一图片
		systemInfo=null;
		getUniqueModel();
		SmsUtil.init();
		return "保存成功,请刷新页面";
	}
}
