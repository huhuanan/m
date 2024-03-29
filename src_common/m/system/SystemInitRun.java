package m.system;

import m.common.service.HostInfoService;

public abstract class SystemInitRun {
	/**
	 * 初始化运行方法
	 * @param isMain 是否主控服务器, 如果需要更新sql,建议只在true的情况下执行
	 */
	public abstract void run(boolean isMain);
	
	public void execute(){
		boolean isMain=false;
		//HostInfo host=RuntimeData.getHostInfo();
		if(HostInfoService.isMainHost()) isMain=true;
		run(isMain);
	}
}
