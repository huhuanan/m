package m.system.cache;

/**
 * 同步缓存属性
 *
 */
public interface FlushCache2 extends IFluchCache {
	/**
	 * 获取缓存模型
	 * @param key
	 * @return
	 */
	public <T extends FlushCache2> T getCacheModel(String key,String key2) throws Exception;
	
}
