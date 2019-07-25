package goods.service;

import goods.dao.GoodsInfoDao;
import goods.model.GoodsStock;
import m.common.model.util.ModelCheckUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;

public class GoodsStockService extends Service {

	public String save(GoodsStock model) throws Exception {
		ModelCheckUtil.check(model);
		TransactionManager tm=new TransactionManager();
		try {
			tm.begin();
			if(StringUtil.isSpace(model.getOid())){
				model.setOid(GenerateID.generatePrimaryKey());
				ModelUpdateUtil.insertModel(model);
			}else{
				throw new MException(this.getClass(), "不允许修改");
			}
			//更新商品库存
			getDao(GoodsInfoDao.class).updateStockNum(model.getGoods().getOid());
			tm.commit();
		}catch(Exception e) {
			tm.rollback();
			throw e;
		}
		return "保存成功";
	}
	
}
