package m.system.cache;

import java.util.HashMap;
import java.util.Map;

import m.system.util.ClassUtil;

public class CacheMap2<T extends FlushCache2> {
	private static Map<Class<?>, CacheMap2<?>> cacheMap=new HashMap<Class<?>,CacheMap2<?>>();
	@SuppressWarnings("unchecked")
	protected static <T extends FlushCache2> CacheMap2<T> instance(Class<T> clazz){
		CacheMap2<T> cache=(CacheMap2<T>) cacheMap.get(clazz);
		if(null==cache) {
			synchronized(CacheMap2.class) {
				cache=(CacheMap2<T>) cacheMap.get(clazz);
				if(null==cache) {
					cacheMap.put(clazz, new CacheMap2<T>(clazz));
					cache=(CacheMap2<T>) cacheMap.get(clazz);
				}
			}
		}
		return cache;
	}
	
	private Class<T> clazz;
	private Map<String,T> data;
	private CacheMap2(Class<T> claz) {
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
	protected T get(String key,String key2) {
		T t=this.data.get(key+key2);
		if(null==t) {
			synchronized(CacheMap2.class) {
				t=this.data.get(key+key2);
				if(null==t) {
					try {
						t=(T) ((T)ClassUtil.newInstance(clazz)).getCacheModel(key,key2);
						System.out.println(new StringBuffer("加载缓存成功:").append(clazz).append(", key=").append(key+key2));
						this.data.put(key+key2, t);
					} catch (Exception e) {
						System.out.println(new StringBuffer("加载缓存异常:").append(clazz).append(", key=").append(key+key2).append(", error:").append(e.getMessage()));
						e.printStackTrace();
					}
				}
			}
		}
		return t;
	}
	/**
	 * 清除缓存
	 * @param key
	 */
	protected void clear(String key,String key2) {
		this.data.remove(key+key2);
		System.out.println("清除缓存成功:"+key+key2);
	}
	protected void clear(String key) {
		for(String k : this.data.keySet()) {
			if(k.indexOf(key)==0) {
				this.data.remove(k);
			}
		}
		System.out.println("清除缓存成功:"+key+"*****");
	}
	protected void clear() {
		this.data=new HashMap<String,T>();
	}

}
