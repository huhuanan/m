package m.common.dao;

import m.common.model.Model;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.ModelUpdateUtil;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;

public class Dao {

	/**
	 * 查询一个model 通过oid
	 * @param <T>
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public <T extends Model> T getModel(T model) throws Exception{
		return ModelQueryUtil.getModel(model);
	}
	/**
	 * 查询一个model 通过oid
	 * @param <T>
	 * @param model
	 * @param level 查询层级,小于0就不往下查询
	 * @return
	 * @throws Exception
	 */
	public <T extends Model> T getModel(T model,int level) throws Exception{
		return ModelQueryUtil.getModel(model,level);
	}
	/**
	 * 保存model oid如果不存在则生成并插入
	 * @param <T>
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public <T extends Model> T saveModel(T model) throws Exception{
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			ModelUpdateUtil.insertModel(model);
		}else{
			ModelUpdateUtil.updateModel(model);
		}
		return model;
	}
	/**
	 * 插入model
	 * @param <T>
	 * @param model
	 * @throws Exception
	 */
	public <T extends Model> T insertModel(T model) throws Exception{
		ModelUpdateUtil.insertModel(model);
		return model;
	}
	/**
	 * 删除model oid不能为空
	 * @param <T>
	 * @param model
	 * @throws MException
	 */
	public <T extends Model> String deleteModel(T model) throws MException{
		ModelUpdateUtil.deleteModel(model);
		return model.getOid();
	}
}
