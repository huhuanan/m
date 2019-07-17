package m.system.cache;

import java.util.List;

import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.system.cache.model.CacheSynch;
import m.system.exception.MException;

public class CacheUtil {
	
	public static <T extends FlushCache> T get(Class<T> clazz,String key){
		return CacheMap.instance(clazz).get(key);
	}
	public static <T extends FlushCache2> T get(Class<T> clazz,String key1,String key2){
		return CacheMap2.instance(clazz).get(key1,key2);
	}
	public static <T extends FlushCacheList> List<T> getList(Class<T> clazz,String key){
		return CacheMapList.instance(clazz).getList(key);
	}

	public static <T extends IFluchCache> void clear(Class<T> clazz) {
		if(FlushCache.class.isAssignableFrom(clazz)) {
			CacheMap.instance((Class<FlushCache>)clazz).clear();
		}
		if(FlushCache2.class.isAssignableFrom(clazz)) {
			CacheMap2.instance((Class<FlushCache2>)clazz).clear();
		}
		if(FlushCacheList.class.isAssignableFrom(clazz)) {
			CacheMapList.instance((Class<FlushCacheList>)clazz).clear();
		}
	}
	public static <T extends IFluchCache> void clear(Class<T> clazz,String key1) {
		if(FlushCache.class.isAssignableFrom(clazz)) {
			CacheMap.instance((Class<FlushCache>)clazz).clear(key1);
		}
		if(FlushCache2.class.isAssignableFrom(clazz)) {
			CacheMap2.instance((Class<FlushCache2>)clazz).clear(key1);
		}
		if(FlushCacheList.class.isAssignableFrom(clazz)) {
			CacheMapList.instance((Class<FlushCacheList>)clazz).clear(key1);
		}
	}
	public static <T extends FlushCache2> void clear(Class<T> clazz,String key1,String key2) {
		CacheMap2.instance((Class<FlushCache2>)clazz).clear(key1,key2);
	}
	/**
	 * 清除全部同步状态  启动的时候调用
	 * @throws Exception
	 */
	public static void initSynch() {
		CacheSynch cs=new CacheSynch();
		cs.setSynchStatus(0);
		try {
			ModelUpdateUtil.update(cs, new String[] {"synchStatus"}, QueryCondition.and(new QueryCondition[] {
				QueryCondition.eq("synchStatus",1)
			}));
			System.out.println("初始化同步缓存成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 执行同步缓存
	 * @param key
	 * @throws Exception
	 */
	public static String executeSynch(String key) throws Exception {
		return executeSynch(key,10);
	}
	public static String executeSynch(String key,int n) throws Exception {
		if(n<0) throw new MException(CacheUtil.class, "操作超时,请重试!");
		CacheSynch m=get(CacheSynch.class,key);
		if(null==m) {
			clear(CacheSynch.class,key);
			CacheSynch cs=new CacheSynch();
			cs.setOid(key);
			cs.setSynchStatus(0);//未执行
			int ins=ModelUpdateUtil.insertModel(cs);
			if(ins<1) {//未插入成功
				Thread.sleep(100);
				executeSynch(key,n-1);
			}
			m=get(CacheSynch.class,key);
		}
		m.setSynchStatus(1);
		int num=ModelUpdateUtil.update(m, new String[] {"synchStatus"}, QueryCondition.and(new QueryCondition[] {
			QueryCondition.eq("oid", key),QueryCondition.eq("synchStatus", 0)
		}));
		if(num<1) {//未更新成功
			Thread.sleep(100);
			executeSynch(key,n-1);
		}
		return key;
	}
	/**
	 * 重置同步缓存
	 * @param key
	 * @throws Exception
	 */
	public static void releaseSynch(String key) {
		CacheSynch m=get(CacheSynch.class,key);
		m.setSynchStatus(0);
		try {
			ModelUpdateUtil.updateModel(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
