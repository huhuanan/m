package goods.model;

import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="t_goods_stock",description="商品库存表")
public class GoodsStock extends Model {

	@LinkTableMeta(name="goods_oid",table=GoodsInfo.class,notnull=true,description="商品")
	private GoodsInfo goods;

	@FieldMeta(name="stock_num",type=FieldType.INT,description="进货数量")
	private Integer stockNum;
	@FieldMeta(name="stock_amount",type=FieldType.DOUBLE,description="进货总价")
	private Double stockAmount;
	
	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;
	
	public GoodsInfo getGoods() {
		return goods;
	}
	public void setGoods(GoodsInfo goods) {
		this.goods = goods;
	}
	public Integer getStockNum() {
		return stockNum;
	}
	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Double getStockAmount() {
		return stockAmount;
	}
	public void setStockAmount(Double stockAmount) {
		this.stockAmount = stockAmount;
	}
	
}
