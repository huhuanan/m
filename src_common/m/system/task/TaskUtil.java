package m.system.task;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import m.system.SystemTaskRun;
import m.system.util.ClassUtil;

public class TaskUtil {
	private static Scheduler scheduler;
	public static void initTask(List<String[]> list){
		if(list.size()==0) return ;
		try {
			SchedulerFactory sFactory = new StdSchedulerFactory();
			scheduler = sFactory.getScheduler();
			for(String[] strs : list){
				System.out.println(strs[0]+"--"+strs[1]);
				scheduler.scheduleJob(JobBuilder.newJob(ClassUtil.getClass(SystemTaskRun.class,strs[0])).build(),
					TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(strs[1])).build());
			}
			scheduler.start();
			System.out.println("定时任务已初始化完毕!");
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static void closeTask() {
		try {
			scheduler.shutdown(true);
			System.out.println("定时任务已关闭!");
		} catch (SchedulerException e) {
			System.out.println("定时任务关闭失败!");
			e.printStackTrace();
		}
	}
	
}
