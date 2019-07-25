package goods.dao;

import java.sql.SQLException;

import m.common.dao.Dao;
import m.system.db.DBManager;

public class GoodsInfoDao extends Dao {
	/**
	 * 更新商品库存数量
	 * @param goodsOid
	 * @throws SQLException
	 */
	public void updateStockNum(String goodsOid) throws SQLException {
		DBManager.executeUpdate("update t_goods_info set stock_num=(select sum(stock_num) from t_goods_stock where goods_oid=?) where oid=?",
			new Object[] {goodsOid,goodsOid});
	}
	/**
	 * 更新商品销量
	 * @param goodsOid
	 * @throws SQLException
	 */
	public void updateSaleNum(String goodsOid) throws SQLException {
		DBManager.executeUpdate("update t_goods_info set sale_num=(select sum(sale_num) from t_goods_order where goods_oid=?) where oid=?",
			new Object[] {goodsOid,goodsOid});
	}
}
