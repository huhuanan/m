package goods.action;

import java.util.Date;
import java.util.List;

import goods.model.GoodsInfo;
import goods.model.GoodsOrder;
import goods.model.GoodsStock;
import goods.service.GoodsInfoService;
import goods.service.GoodsOrderService;
import goods.service.GoodsStockService;
import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.QueryCondition;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.listener.InitListener;
import m.system.util.AnnotationUtil;
import m.system.util.DateUtil;
import m.system.util.JSONMessage;
import m.system.util.NumberUtil;
import m.system.util.StringUtil;
import manage.action.ManageAction;
import manage.action.StatusAction;
import manage.util.excel.ExcelObject;
import manage.util.page.QueryMetaUtil;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.button.ParamMeta;
import manage.util.page.form.ActionFormMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormSuccessMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormOtherMeta;
import manage.util.page.form.FormRowMeta;
import manage.util.page.query.LinkFieldMeta;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableColMeta.TableCountType;
import manage.util.page.table.ActionTableMeta;

@ActionMeta(name="goodsGoodsInfo")
public class GoodsInfoAction extends StatusAction {
	private GoodsInfo model;
	public JSONMessage getIndexInfo() {
		JSONMessage result=new JSONMessage();
		try {
			result.push("map", getService(GoodsInfoService.class).getIndexInfo());
			result.push("code", 0);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
		}
		return result;
	}
	////////////////////////添加测试数据 start
	public static void main(String[] a) throws MException, Exception {
		InitListener.initDBConfig();
		Date start=DateUtil.format("2018-01-01", "yyyy-MM-dd");
		Date end=DateUtil.format("2019-06-60", "yyyy-MM-dd");
		GoodsInfo goods;
		for(int i=0;i<1000;i++) {
			goods=new GoodsInfo();
			goods.setName("测试商品名称"+i);
			goods.setPrice(new Double(i+1));
			RuntimeData.getService(GoodsInfoService.class).save(goods);
			GoodsStock stock;
			for(int n=0;n<100;n++) {
				stock=new GoodsStock();
				stock.setGoods(goods);
				stock.setCreateDate(randomDate(start,end));
				stock.setStockNum(new Double(100.0+Math.random()*100.0).intValue());
				stock.setStockAmount(NumberUtil.round(stock.getStockNum()*goods.getPrice()));
				RuntimeData.getService(GoodsStockService.class).save(stock);
			}
			GoodsOrder order;
			for(int m=0;m<100;m++) {
				order=new GoodsOrder();
				order.setGoods(goods);
				order.setCreateDate(randomDate(start,end));
				order.setSaleNum(new Double(10.0+Math.random()*50.0).intValue());
				order.setPrice(NumberUtil.round(goods.getPrice()+Math.random()*1000.0,2));
				RuntimeData.getService(GoodsOrderService.class).save(order);
			}
		}
		
	}
	public static Date randomDate(Date start,Date end) {
		long date = random(start.getTime(),end.getTime());
		return new Date(date);
	}
	private static long random(long begin,long end){
		long rtn = begin + (long)(Math.random() * (end - begin));
		if(rtn == begin || rtn == end){
			return random(begin,end);
		}
		return rtn;
	}
	//////////////////////添加测试数据 end
	
	/**
	 * 保存
	 * @return
	 */
	public JSONMessage doSave(){
		setLogContent("保存", "保存商品信息");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("goods_manager_power");
			String msg=getService(GoodsInfoService.class).save(model);
			result.push("model.oid", model.getOid());
			result.push("code", 0);
			result.push("msg", msg);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			setLogError(e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}

	@ActionFormMeta(title="商品信息",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="名称",field="model.name",type=FormFieldType.TEXT,hint="请输入名称",span=12),
				@FormFieldMeta(title="单价",field="model.price",type=FormFieldType.DOUBLE,numberRange="0~",decimalCount=2,hint="请输入单价",span=12),
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="库存",field="model.stockNum",type=FormFieldType.INT,span=12,disabled=true),
				@FormFieldMeta(title="销量",field="model.saleNum",type=FormFieldType.INT,span=12,disabled=true),
			})
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/goodsGoodsInfo/doSave",success=FormSuccessMethod.DONE_BACK)
		},
		others= {
			@FormOtherMeta(title="商品库存",url="action/goodsGoodsStock/toList?method=goodsStockData",
				linkField=@LinkFieldMeta(field="params[goods.oid]",valueField="model.oid"))
		}
	)
	public ActionResult toEdit() throws Exception{
		if(null!=model&&!StringUtil.isSpace(model.getOid())){
			model=ModelQueryUtil.getModel(model);
		}
		return getFormResult(this,ActionFormPage.EDIT);
	}
	/**
	 * 查询列表
	 * @return
	 */
	@ActionTableMeta(dataUrl = "action/goodsGoodsInfo/goodsInfoData",
			modelClass="goods.model.GoodsInfo",tableHeight=500,
			searchField="name",searchHint="请输入名称",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "status", title = "状态",type=TableColType.STATUS,power="goods_manager_power",dictionaryType="status",align="center"),
			@ActionTableColMeta(field = "name", title = "名称", width=130,sort=true,initSort=TableColSort.DESC),
			@ActionTableColMeta(field = "price", title = "单价", width=130,sort=true,numberFormat="0.00",align="right"),
			@ActionTableColMeta(field = "stockNum", title = "库存", width=130,sort=true,numberFormat="#,##0",align="right",countType=TableCountType.SUM),
			@ActionTableColMeta(field = "saleNum", title = "销量", width=130,sort=true,numberFormat="#,##0",align="right",countType=TableCountType.SUM),
			@ActionTableColMeta(field = "oid",title="操作",width=120,align="center",power="goods_manager_power",buttons={
				@ButtonMeta(title="修改", event = ButtonEvent.MODAL,modalWidth=700, url = "action/goodsGoodsInfo/toEdit",
					params={@ParamMeta(name = "model.oid", field="oid")},success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
					power="goods_manager_power"
				),
			})
		},
		querys = {
			@QueryMeta(field = "name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "price", name = "单价", type = QueryType.DOUBLE_RANGE)
		},
		buttons = {
			@ButtonMeta(title="新增", event = ButtonEvent.MODAL,modalWidth=700,  url = "action/goodsGoodsInfo/toEdit", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
				power="goods_manager_power"
			)
		}
	)
	public JSONMessage goodsInfoData(){
		return getListDataResult(null);
	}
	public ActionResult toExcel() throws Exception{
		ExcelObject eo=new ExcelObject("订单列表");
		
		ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, getActionClass(), "goodsOrderData");
		List<QueryCondition> list=QueryMetaUtil.convertQuery(getParams(),meta.querys());//查询条件
		eo.addSheet(super.getExcelSheet(GoodsOrderAction.class,"goodsOrderData",list.toArray(new QueryCondition[] {}),"订单列表"));
		return toExportExcel(eo);
	}
	
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
	@Override
	public String getStatusPower() {
		return "goods_manager_power";
	}


	public GoodsInfo getModel() {
		return model;
	}


	public void setModel(GoodsInfo model) {
		this.model = model;
	}


}
