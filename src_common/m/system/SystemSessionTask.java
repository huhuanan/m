package m.system;

import javax.servlet.http.HttpSessionEvent;

public abstract class SystemSessionTask {
	public abstract void created(HttpSessionEvent e);
	public abstract void destroyed(HttpSessionEvent e);
	public abstract void added(String key,Object value);
	public abstract void removed(String key,Object value);
	
	
	private static int sessionNum=0;
	public static void addSessionNum(int n) {
		sessionNum+=n;
	}
	public static int getSessionNum() {
		return sessionNum;
	}
	private static int loginNum=0;
	public static void addLoginNum(int n) {
		loginNum+=n;
	}
	public static int getLoginNum() {
		return loginNum;
	}
}
