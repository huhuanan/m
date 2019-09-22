package m.system.cache;

import java.util.HashMap;
import java.util.Map;

import m.system.util.ClassUtil;

public class CacheMap<T extends FlushCache> {
	private static Map<Class<?>, CacheMap<?>> cacheMap=new HashMap<Class<?>,CacheMap<?>>();
	@SuppressWarnings("unchecked")
	protected static <T extends FlushCache> CacheMap<T> instance(Class<T> clazz){
		CacheMap<T> cache=(CacheMap<T>) cacheMap.get(clazz);
		if(null==cache) {
			synchronized(CacheMap.class) {
				cacheMap.put(clazz, new CacheMap<T>(clazz));
				cache=(CacheMap<T>) cacheMap.get(clazz);
			}
		}
		return cache;
	}
	
	private Class<T> clazz;
	private Map<String,T> data;
	private CacheMap(Class<T> claz) {
		clazz=claz;
		data=new HashMap<String,T>();
	}
	

	protected Class<T> getClazz() {
		return clazz;
	}
	protected void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}
	/**
	 * 获取缓存数据
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	protected T get(String key) {
		T t=this.data.get(key);
		if(null==t) {
			synchronized(CacheMap.class) {
				try {
					t=(T) ((T)ClassUtil.newInstance(clazz)).getCacheModel(key);
					System.out.println(new StringBuffer("加载缓存成功:").append(clazz).append(", key=").append(key));
					this.data.put(key, t);
				} catch (Exception e) {
					System.out.println(new StringBuffer("加载缓存异常:").append(clazz).append(", key=").append(key).append(", error:").append(e.getMessage()));
					e.printStackTrace();
				}
			}
		}
		return t;
	}
	/**
	 * 清除缓存
	 * @param key
	 */
	protected void clear(String key) {
		this.data.remove(key);
		System.out.println("清除缓存成功:"+key);
	}
	protected void clear() {
		this.data=new HashMap<String,T>();
	}

}
