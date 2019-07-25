package goods.model;

import m.common.model.FieldMeta;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
import manage.model.StatusModel;
@TableMeta(name="t_goods_info",description="商品信息表")
public class GoodsInfo extends StatusModel {

	@FieldMeta(name="name",type=FieldType.STRING,length=100,notnull=true,description="商品名称")
	private String name;

	@FieldMeta(name="price",type=FieldType.DOUBLE,description="单价")
	private Double price;
	
	@FieldMeta(name="stock_num",type=FieldType.INT,description="库存数量")
	private Integer stockNum;
	@FieldMeta(name="sale_num",type=FieldType.INT,description="销售数量")
	private Integer saleNum;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getStockNum() {
		return stockNum;
	}
	public void setStockNum(Integer stockNum) {
		this.stockNum = stockNum;
	}
	public Integer getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}
	
	
}
