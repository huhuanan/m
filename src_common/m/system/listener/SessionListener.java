package m.system.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
	private static int sessionNum=0;
	public void sessionCreated(HttpSessionEvent arg0) {
		sessionNum++;
		System.out.println(arg0.getSession().getId()+"创建,当前:"+sessionNum);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		sessionNum--;
		System.out.println(arg0.getSession().getId()+"销毁,当前:"+sessionNum);
	}

	public static int getSessionNum() {
		return sessionNum;
	}

}
