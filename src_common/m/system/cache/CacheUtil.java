package m.system.cache;

import java.util.List;

import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.netty.HostNettyUtil;
import m.system.cache.model.CacheSynch;
import m.system.db.SqlBuffer;
import m.system.exception.MException;
import m.system.netty.NettyClient;
import m.system.netty.NettyMessage;
import m.system.util.StringUtil;

public class CacheUtil {
	public static Object get(String key) {
		Object obj=CacheHost.instance(key).get();
		if(null==obj) {
			sendNettyGetCache(key);
			return CacheHost.instance(key).get();
		}
		return obj;
	}
	public static void push(String key,Object obj) {
		CacheHost.instance(key).push(obj);
		sendNettyPushCache(key,obj);
	}
	public static void clear(String key) {
		clear(key,true);
	}
	public static void clear(String key,boolean synch) {
		CacheHost.instance(key).clear();
		if(synch) sendNettyClearCache(CacheHost.class,key,null);
	}
	
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
		if(synch) sendNettyClearCache(clazz,null,null);
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
		if(synch) sendNettyClearCache(clazz,key1,null);
	}
	public static <T extends FlushCache2> void clear(Class<T> clazz,String key1,String key2) {
		clear(clazz,key1,key2,true);
	}
	public static <T extends FlushCache2> void clear(Class<T> clazz,String key1,String key2,boolean synch) {
		CacheMap2.instance((Class<FlushCache2>)clazz).clear(key1,key2);
		if(synch) sendNettyClearCache(clazz,key1,key2);
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
		return executeSynch(key,20);
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
//		int num=ModelUpdateUtil.update(m, new String[] {"synchStatus"}, QueryCondition.and(new QueryCondition[] {
//			QueryCondition.eq("oid", key),QueryCondition.eq("synchStatus", 0)
//		}));
		try {
			int num=new SqlBuffer(1).append("update z_cache_synch set synch_status=1 where oid=? and synch_status=0", key).executeUpdate();
			if(num<1) {//未更新成功
				throw new MException(CacheUtil.class, "被占用,更新失败!");
			}
		} catch (Exception e) {
			Thread.sleep(15);
			return executeSynch(key,n-1);
		}
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
	private static <T extends IFluchCache> void sendNettyClearCache(Class<T> clazz,String key1,String key2) {
		NettyMessage msg=new NettyMessage();
		msg.push("cache_clear_clazz", clazz)
			.push("cache_key1", key1)
			.push("cache_key2", key2);
		NettyClient client=HostNettyUtil.getClient();
		if(null!=client) {//客户端，发送到服务端转发
			client.send(msg);
		}
		if(null!=HostNettyUtil.getServer()) {//服务端，直接发送所有客户端
			readNettyServerMessage(null,msg);
		}
	}
	/**
	 * 发送填充缓存消息 客户端执行
	 * @param key
	 * @param obj
	 */
	private static void sendNettyPushCache(String key,Object obj) {
		NettyClient client=HostNettyUtil.getClient();
		if(null!=client) {//客户端，发送到服务端
			NettyMessage msg=new NettyMessage();
			msg.push("cache_host_push_key", key)
				.push("cache_host_obj", obj);
			client.send(msg);
		}
	}
	/**
	 * 发送获取缓存消息 客户端执行
	 * @param key
	 */
	private static void sendNettyGetCache(String key) {
		NettyClient client=HostNettyUtil.getClient();
		if(null!=client) {//客户端，发送到服务端
			CacheHost.instance(key).setReady(false);
			NettyMessage msg=new NettyMessage();
			msg.push("cache_host_get_key", key);
			client.send(msg);
		}
	}
	/**
	 * 服务器端执行
	 * @param msg
	 */
	public static void readNettyServerMessage(String ipport, NettyMessage msg) {
		//转发同步缓存消息， 服务端执行
		if(null!=msg.get("cache_clear_clazz")) {
			HostNettyUtil.getServer().sendAll(msg);
			readNettyClientMessage(ipport,msg);//执行清除
		}
		//回复填充缓存消息 服务端执行
		if(null!=msg.get("cache_host_push_key")) {
			String key=msg.get(String.class, "cache_host_push_key");
			Object obj=msg.get("cache_host_obj");
			CacheHost.instance(key).push(obj);
			CacheHost.instance(key).setReady(true);
		}
		//回复获取缓存消息 服务端执行
		if(null!=msg.get("cache_host_get_key")) {
			String key=msg.get(String.class, "cache_host_get_key");
			NettyMessage rm=new NettyMessage();
			rm.push("cache_host_get_key", key)
				.push("cache_host_obj", CacheHost.instance(key).get());
			if(!StringUtil.isSpace(ipport)) {
				HostNettyUtil.getServer().send(ipport, rm);
			}
		}
	}
	/**
	 * 客户端执行
	 * @param ipport
	 * @param msg
	 */
	public static void readNettyClientMessage(String ipport, NettyMessage msg) {
		//执行同步缓存消息，客户端执行
		if(null!=msg.get("cache_clear_clazz")) {
			Object clazz=msg.get("cache_clear_clazz");
			String key1=msg.get(String.class, "cache_key1");
			String key2=msg.get(String.class, "cache_key2");
			if(clazz instanceof CacheHost) {
				clear(key1);
			}else if(!StringUtil.isSpace(key2)) {
				clear((Class<FlushCache2>)clazz, key1, key2, false);
			}else if(!StringUtil.isSpace(key1)) {
				clear((Class<IFluchCache>)clazz, key1, false);
			}else{
				clear((Class<IFluchCache>)clazz, false);
			}
		}
		//接受缓存消息  客户端执行
		if(null!=msg.get("cache_host_get_key")) {
			String key=msg.get(String.class, "cache_host_get_key");
			Object obj=msg.get("cache_host_obj");
			CacheHost.instance(key).push(obj);
			CacheHost.instance(key).setReady(true);
		}
	}
}
