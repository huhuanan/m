package m.system.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import m.system.SystemSessionTask;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class SessionListener implements HttpSessionListener,HttpSessionAttributeListener {
	private static List<SystemSessionTask> tasks=null;
	public static void init(String taskClass) {
		if(StringUtil.isSpace(taskClass)) return ;
		tasks=new ArrayList<SystemSessionTask>();
		String[] clazzs=taskClass.split(",");
		for(int i=0;i<clazzs.length;i++){
			try {
				tasks.add(ClassUtil.newInstance(clazzs[i].trim()));
			} catch (Exception e) {
				System.err.println("sessionTask初始化错误!"+e.getMessage());
				e.printStackTrace();
			}
		}
	};
	
	public void sessionCreated(HttpSessionEvent e) {
		if(null!=tasks) {
			for(SystemSessionTask task : tasks) {
				task.created(e);
			}
		}
	}

	public void sessionDestroyed(HttpSessionEvent e) {
		if(null!=tasks) {
			for(SystemSessionTask task : tasks) {
				task.destroyed(e);
			}
		}
	}

	public void attributeAdded(HttpSessionBindingEvent e) {
		if(null!=tasks) {
			for(SystemSessionTask task : tasks) {
				task.added(e.getName(),e.getValue());
			}
		}
	}
	public void attributeRemoved(HttpSessionBindingEvent e) {
		if(null!=tasks) {
			for(SystemSessionTask task : tasks) {
				task.removed(e.getName(),e.getValue());
			}
		}
	}
	public void attributeReplaced(HttpSessionBindingEvent e) {
		if(null!=tasks) {
			for(SystemSessionTask task : tasks) {
				task.removed(e.getName(), e.getValue());
				task.added(e.getName(), e.getSession().getAttribute(e.getName()));
			}
		}
		  
	}

}
