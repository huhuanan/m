package goods.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import goods.model.GoodsInfo;
import m.common.model.util.ModelCheckUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.util.GenerateID;
import m.system.util.NumberUtil;
import m.system.util.StringUtil;

public class GoodsInfoService extends Service {

	public String save(GoodsInfo model) throws Exception {
		ModelCheckUtil.check(model);
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			model.setStatus("0");
			model.setSaleNum(0);
			model.setStockNum(0);
			ModelUpdateUtil.insertModel(model);
			return "保存成功";
		}else{
			ModelUpdateUtil.updateModel(model, new String[]{"name","price"});
			return "修改成功";
		}
	}

	public Map<String,Object> getIndexInfo() throws SQLException {
		Map<String,Object> map=new HashMap<String, Object>();
		DataRow dr=DBManager.queryFirstRow("select count(oid) num,sum(stock_num-sale_num) num1 from t_goods_info");
		if(null!=dr) {
			map.put("goodsNum", dr.get("num"));
			map.put("stockNum", dr.get("num1"));
		}else {
			map.put("goodsNum", 0);
			map.put("stockNum", 0);
		}
		dr=DBManager.queryFirstRow("select sum(sale_amount) amount from t_goods_order");
		if(null!=dr) {
			Double d=dr.get(Double.class,"amount");
			if(d>99999999) {
				map.put("saleAmount", NumberUtil.round(d/100000000.0, 2)+"亿元");
			}else if(d>9999){
				map.put("saleAmount", NumberUtil.round(d/10000.0, 2)+"万元");
			}else {
				map.put("saleAmount", NumberUtil.round(d,2));
			}
		}else {
			map.put("saleAmount", 0);
		}
		return map;
	}
	
	
}
