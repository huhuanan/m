package m.system.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheHost implements Serializable,IFluchCache {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7435331921613292156L;
	
	private static Map<String, CacheHost> cacheMap=new HashMap<String,CacheHost>();

	protected static CacheHost instance(String key){
		CacheHost cache=cacheMap.get(key);
		if(null==cache) {
			synchronized(CacheMap.class) {
				cache=cacheMap.get(key);
				if(null==cache) {
					cacheMap.put(key, new CacheHost(key));
					cache=cacheMap.get(key);
				}
			}
		}
		return cache;
	}
	/**
	 * 获取超时的缓存
	 * @param timeout 超时时长, 毫秒
	 * @return
	 */
	public static String[] getTimeoutCaches(long timeout) {
		List<String> ls=new ArrayList<String>();
		Date d=new Date();
		for(String key : cacheMap.keySet()) {
			if(d.getTime()-cacheMap.get(key).time.getTime()>timeout) {
				ls.add(key);
			}
		}
		return ls.toArray(new String[] {});
	}
	
	private String key;
	private Object obj;
	private Date time;
	private boolean ready=true;
	private int readyNum=0;
	
	private CacheHost(String key) {
		this.key=key;
		obj=null;
	}
	
	protected void push(Object obj) {
		this.obj=obj;
		this.time=new Date();
	}
	protected Object get() {
		if(this.ready) {
			this.time=new Date();
			return this.obj;
		}else {
			if(this.readyNum>60) {
				this.ready=true;
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) { }
			this.readyNum++;
			return get();
		}
	}
	protected void clear() {
		cacheMap.remove(this.key);
	}
	protected void setReady(boolean b) {
		this.ready=b;
	}
	
}
