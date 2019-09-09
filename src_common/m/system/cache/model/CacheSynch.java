package m.system.cache.model;

import m.common.model.FieldMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
import m.common.model.util.ModelQueryUtil;
import m.system.cache.FlushCache;
@TableMeta(name="z_cache_synch",description="缓存同步表")
public class CacheSynch extends Model implements FlushCache {

	@FieldMeta(name="synch_status",type=FieldType.INT,description="同步状态 0未执行,1执行中")
	private Integer synchStatus;

	public Integer getSynchStatus() {
		return synchStatus;
	}

	public void setSynchStatus(Integer synchStatus) {
		this.synchStatus = synchStatus;
	}

	public CacheSynch getCacheModel(String key) throws Exception {
		this.setOid(key);
		return ModelQueryUtil.getModel(this);
	}
	
}
