package goods.service;

import goods.dao.GoodsInfoDao;
import goods.model.GoodsInfo;
import goods.model.GoodsOrder;
import m.common.model.util.ModelCheckUtil;
import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.NumberUtil;
import m.system.util.StringUtil;

public class GoodsOrderService extends Service {

	public String save(GoodsOrder model) throws Exception {
		ModelCheckUtil.check(model);
		if(null==model.getPrice()||model.getPrice()<=0) {
			throw new MException(this.getClass(), "请输入正确的销售价格");
		}else if(null==model.getSaleNum()||model.getSaleNum()<=0) {
			throw new MException(this.getClass(), "请输入正确的数量");
		}
		model.setSaleAmount(NumberUtil.round(model.getSaleNum()*model.getPrice()));
		TransactionManager tm=new TransactionManager();
		try {
			tm.begin(model.getGoods().getOid());
			if(StringUtil.isSpace(model.getOid())){
				model.setOid(GenerateID.generatePrimaryKey());
				ModelUpdateUtil.insertModel(model);
			}else{
				throw new MException(this.getClass(), "不允许修改");
			}
			//更新商品库存
			getDao(GoodsInfoDao.class).updateSaleNum(model.getGoods().getOid());
			GoodsInfo goods=ModelQueryList.getModel(model.getGoods(), new String[] {"stockNum","saleNum"});
			if(goods.getStockNum()-goods.getSaleNum()<0) {
				throw new MException(this.getClass(), "库存不足");
			}
			tm.commit();
		}catch(Exception e) {
			tm.rollback();
			throw e;
		}
		return "保存成功";
	}
	
}
