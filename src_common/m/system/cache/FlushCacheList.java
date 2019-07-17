package m.system.cache;

import java.util.List;

/**
 * 同步缓存属性
 *
 */
public interface FlushCacheList extends IFluchCache {
	/**
	 * 获取缓存列表  调用方法 CacheMapList.instance(DictionaryType.class).getList("all");  //DictionaryType.class 需要实现FlushCache
	 * @param key
	 * @return
	 */
	public <T extends FlushCacheList> List<T> getCacheList(String key) throws Exception;
	
}
