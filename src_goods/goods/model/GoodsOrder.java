package goods.model;

import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="t_goods_order",description="商品订单表")
public class GoodsOrder extends Model {

	@LinkTableMeta(name="goods_oid",table=GoodsInfo.class,notnull=true,description="商品")
	private GoodsInfo goods;

	@FieldMeta(name="price",type=FieldType.DOUBLE,description="销售单价")
	private Double price;
	@FieldMeta(name="sale_num",type=FieldType.INT,description="销售数量")
	private Integer saleNum;
	@FieldMeta(name="sale_amount",type=FieldType.DOUBLE,description="销售金额")
	private Double saleAmount;
	
	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;
	
	public GoodsInfo getGoods() {
		return goods;
	}
	public void setGoods(GoodsInfo goods) {
		this.goods = goods;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Double getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
	}
	
	
}
