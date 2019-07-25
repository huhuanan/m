package goods.action;

import java.util.Date;
import java.util.List;

import goods.model.GoodsOrder;
import goods.service.GoodsOrderService;
import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.QueryCondition;
import m.system.RuntimeData;
import m.system.util.AnnotationUtil;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.action.ManageAction;
import manage.util.excel.ExcelObject;
import manage.util.page.QueryMetaUtil;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.button.ParamMeta;
import manage.util.page.chart.ActionChartMeta;
import manage.util.page.chart.ChartSeries;
import manage.util.page.chart.ChartSeries.ChartSeriesType;
import manage.util.page.chart.ChartXAxis;
import manage.util.page.chart.ChartXAxis.ChartAxisType;
import manage.util.page.form.ActionFormMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormSuccessMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormRowMeta;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.query.QuerySelectMeta;
import manage.util.page.query.SelectConditionMeta;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableColMeta.TableCountType;
import manage.util.page.table.ActionTableMeta;

@ActionMeta(name="goodsGoodsOrder")
public class GoodsOrderAction extends ManageAction {
	private GoodsOrder model;

	/**
	 * 保存
	 * @return
	 */
	public JSONMessage doSave(){
		setLogContent("保存", "保存商品信息");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("goods_manager_power");
			String msg=getService(GoodsOrderService.class).save(model);
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

	@ActionFormMeta(title="商品入库",
		rows={
			@FormRowMeta(fields={
				@FormFieldMeta(field = "model.oid", type = FormFieldType.HIDDEN),
				@FormFieldMeta(title="名称",field="model.goods.oid",type=FormFieldType.SELECT,hint="请选择商品",span=12,
					querySelect=@QuerySelectMeta(modelClass = "goods.model.GoodsInfo", title = "name", value = "oid",
						titleExpression="concat(#{name},'(',#{price},'元)')",
						conditions= {@SelectConditionMeta(field = "status",value="0")})
				),
				@FormFieldMeta(title="创建时间",field="model.createDate",type=FormFieldType.DATE,span=12),
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="单价",field="model.price",type=FormFieldType.DOUBLE,decimalCount=2,numberRange="0~",span=12),
				@FormFieldMeta(title="数量",field="model.saleNum",type=FormFieldType.INT,numberRange="0~",span=12),
			}),
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/goodsGoodsOrder/doSave",success=FormSuccessMethod.DONE_BACK)
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
	@ActionTableMeta(dataUrl = "action/goodsGoodsOrder/goodsOrderData",
			modelClass="goods.model.GoodsOrder",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "goods.name", title = "商品名称", width=130,sort=true),
			@ActionTableColMeta(field = "createDate", title = "创建时间", width=130,sort=true,initSort=TableColSort.DESC,dateFormat="yyyy-MM-dd",align="center"),
			@ActionTableColMeta(field = "price", title = "单价", width=130,sort=true,numberFormat="#,##0.00",align="right"),
			@ActionTableColMeta(field = "saleNum", title = "数量", width=130,sort=true,numberFormat="#,##0",align="right",countType=TableCountType.SUM),
			@ActionTableColMeta(field = "saleAmount", title = "金额", width=130,sort=true,numberFormat="#,##0.00",align="right",countType=TableCountType.SUM),
		},
		querys = {
			@QueryMeta(field = "goods.oid", name = "oid", type = QueryType.HIDDEN),
			@QueryMeta(field = "goods.name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "createDate", name = "创建时间", type = QueryType.DATE_RANGE)
		},
		buttons = {
			@ButtonMeta(title="新增", event = ButtonEvent.MODAL,modalWidth=700,  url = "action/goodsGoodsOrder/toEdit", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
				power="goods_manager_power"
			),
			@ButtonMeta(title="报表",style=ButtonStyle.DEFAULT, event = ButtonEvent.MODAL,modalWidth=1000, url = "action/goodsGoodsOrder/toChart?method=goodsOrderChart", 
				queryParams={
					@ParamMeta(name="params[goods.name]",field="goods.name"),
					@ParamMeta(name="params[createDatedown]",field="createDatedown"),
					@ParamMeta(name="params[createDateup]",field="createDateup"),
				}
			),
			@ButtonMeta(title="导出",style=ButtonStyle.DEFAULT, event = ButtonEvent.OPEN, url = "action/goodsGoodsOrder/toExcel", 
			queryParams={
				@ParamMeta(name="params[goods.name]",field="goods.name"),
				@ParamMeta(name="params[createDatedown]",field="createDatedown"),
				@ParamMeta(name="params[createDateup]",field="createDateup"),
			}
		),
		}
	)
	public JSONMessage goodsOrderData(){
		return getListDataResult(null);
	}
	public ActionResult toExcel() throws Exception{
		ExcelObject eo=new ExcelObject("订单列表");
		ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, getActionClass(), "goodsOrderData");
		List<QueryCondition> list=QueryMetaUtil.convertQuery(getParams(),meta.querys());//查询条件
		eo.addSheet(super.getExcelSheet(GoodsOrderAction.class,"goodsOrderData",list.toArray(new QueryCondition[] {}),"订单列表"));
		return toExportExcel(eo);
	}

	@ActionChartMeta(dataUrl="action/goodsGoodsOrder/goodsOrderChart",tableHeight=400,
			modelClass="goods.model.GoodsOrder",
		series = { 
			@ChartSeries(field = "saleNum", name = "数量",markPoint=true,type=ChartSeriesType.BAR),
			@ChartSeries(field = "saleAmount", name = "金额",index=1),
		},
		xAxis=@ChartXAxis(field="createDate",type=ChartAxisType.CATEGORY,dateFormat="yyyy-MM",dataZoom=true),
		querys={
			@QueryMeta(field = "goods.name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "createDate", name = "创建时间", type = QueryType.DATE_RANGE)
		}
	)
	public JSONMessage goodsOrderChart() throws Exception {
		return getChartDataResult(null);
	}
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public GoodsOrder getModel() {
		return model;
	}


	public void setModel(GoodsOrder model) {
		this.model = model;
	}


}
