package m.system;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import m.common.model.HostInfo;
import m.system.db.TransactionManager;
import m.system.exception.MException;

public abstract class SystemTaskRun implements Job {
	/**
	 * 定时运行方法
	 * @param isMain 是否主控服务器, 如果需要更新sql,建议只在true的情况下执行
	 */
	public abstract void run(boolean isMain);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			TransactionManager.initConnection();
			boolean isMain=false;
			HostInfo host=RuntimeData.getHostInfo();
			if(null==host||host.getMain()==1) isMain=true;
			run(isMain);
		} catch (MException e) {
			e.printStackTrace();
		}
		TransactionManager.closeConnection();
	}
}
