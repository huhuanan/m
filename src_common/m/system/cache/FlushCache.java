package m.system.cache;

/**
 * 同步缓存属性
 *
 */
public interface FlushCache extends IFluchCache {
	/**
	 * 获取缓存模型
	 * @param key
	 * @return
	 */
	public <T extends FlushCache> T getCacheModel(String key) throws Exception;
	
}
