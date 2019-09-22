package m.system.cache;

import java.util.List;

import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.netty.HostNettyUtil;
import m.system.cache.model.CacheSynch;
import m.system.exception.MException;
import m.system.netty.NettyClient;
import m.system.netty.NettyMessage;
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
		clear(clazz,true);
	}
	public static <T extends IFluchCache> void clear(Class<T> clazz,boolean synch) {
		if(FlushCache.class.isAssignableFrom(clazz)) {
			CacheMap.instance((Class<FlushCache>)clazz).clear();
		}
		if(FlushCache2.class.isAssignableFrom(clazz)) {
			CacheMap2.instance((Class<FlushCache2>)clazz).clear();
		}
		if(FlushCacheList.class.isAssignableFrom(clazz)) {
			CacheMapList.instance((Class<FlushCacheList>)clazz).clear();
		}
		if(synch) sendNettySynchCache(clazz,null,null);
	}
	public static <T extends IFluchCache> void clear(Class<T> clazz,String key1) {
		clear(clazz,key1,true);
	}
	public static <T extends IFluchCache> void clear(Class<T> clazz,String key1,boolean synch) {
		if(FlushCache.class.isAssignableFrom(clazz)) {
			CacheMap.instance((Class<FlushCache>)clazz).clear(key1);
		}
		if(FlushCache2.class.isAssignableFrom(clazz)) {
			CacheMap2.instance((Class<FlushCache2>)clazz).clear(key1);
		}
		if(FlushCacheList.class.isAssignableFrom(clazz)) {
			CacheMapList.instance((Class<FlushCacheList>)clazz).clear(key1);
		}
		if(synch) sendNettySynchCache(clazz,key1,null);
	}
	public static <T extends FlushCache2> void clear(Class<T> clazz,String key1,String key2) {
		clear(clazz,key1,key2,true);
	}
	public static <T extends FlushCache2> void clear(Class<T> clazz,String key1,String key2,boolean synch) {
		CacheMap2.instance((Class<FlushCache2>)clazz).clear(key1,key2);
		if(synch) sendNettySynchCache(clazz,key1,key2);
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
	/**
	 * 发送同步缓存消息 清除缓存
	 * @param clazz
	 * @param key1
	 * @param key2
	 */
	private static <T extends IFluchCache> void sendNettySynchCache(Class<T> clazz,String key1,String key2) {
		NettyMessage msg=new NettyMessage();
		msg.push("cache_clazz", clazz)
			.push("cache_key1", key1)
			.push("cache_key2", key2);
		NettyClient client=HostNettyUtil.getClient();
		if(null!=client) {//客户端，发送到服务端转发
			client.send(msg);
		}
		if(null!=HostNettyUtil.getServer()) {//服务端，直接发送所有客户端
			forwardNettySynchCache(msg);
		}
	}
	/**
	 * 转发同步缓存消息， 服务端执行
	 * @param msg  消息中包含cache_clazz，才转发
	 */
	public static void forwardNettySynchCache(NettyMessage msg) {
		if(null!=msg.get("cache_clazz")) {
			HostNettyUtil.getServer().sendAll(msg);
		}
	}
	/**
	 * 执行同步缓存消息，客户端执行
	 * @param msg  消息中包含cache_clazz，才处理
	 */
	public static void doNettySynchCache(NettyMessage msg) {
		if(null!=msg.get("cache_clazz")) {
			String key1=msg.get(String.class, "key1");
			String key2=msg.get(String.class, "key2");
			if(!StringUtil.isSpace(key2)) {
				clear((Class<FlushCache2>)msg.get("cache_clazz"), key1, key2, false);
			}else if(!StringUtil.isSpace(key1)) {
				clear((Class<IFluchCache>)msg.get("cache_clazz"), key1, false);
			}else{
				clear((Class<IFluchCache>)msg.get("cache_clazz"), false);
			}
		}
	}
}
