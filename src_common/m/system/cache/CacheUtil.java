package m.system.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.system.cache.model.CacheSynch;
import m.system.exception.MException;
import m.system.util.StringUtil;

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
	 * @param key  返回的key需要传到CacheUtil.releaseSynch重置
	 * @throws Exception
	 */
	public static String executeSynch(String key) throws Exception {
		return executeSynch(key,30);
	}
	public static String executeSynch(String key,int n) throws Exception {
		if(n<0) throw new MException(CacheUtil.class, "操作超时,请重试!");
		CacheSynch m=get(CacheSynch.class,key);
		if(null==m) {
			clear(CacheSynch.class,key);
			insertSynch(key);
			return executeSynch(key,n-1);
		}
		m.setSynchStatus(1);
		int num=ModelUpdateUtil.update(m, new String[] {"synchStatus"}, QueryCondition.and(new QueryCondition[] {
			QueryCondition.eq("oid", key),QueryCondition.eq("synchStatus", 0)
		}));
		if(num<1) {//未更新成功
			Thread.sleep(15);
			return executeSynch(key,n-1);
		}
		System.out.println(num);
		return key;
	}
	private static void insertSynch(String key) {
		CacheSynch cs=new CacheSynch();
		cs.setOid(key);
		cs.setSynchStatus(0);//未执行
		try {
			ModelUpdateUtil.insertModel(cs);
		} catch (MException e) {
			System.out.println(e.getMessage());
		}
	}
	/**
	 * 重置同步缓存
	 * @param key  CacheUtil.executeSynch执行结果
	 * @throws Exception
	 */
	public static void releaseSynch(String key) {
		if(StringUtil.isSpace(key)) return;
		CacheSynch m=get(CacheSynch.class,key);
		m.setSynchStatus(0);
		try {
			ModelUpdateUtil.updateModel(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
