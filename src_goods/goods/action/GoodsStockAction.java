package goods.action;

import goods.model.GoodsStock;
import goods.service.GoodsStockService;
import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.action.ManageAction;
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

@ActionMeta(name="goodsGoodsStock")
public class GoodsStockAction extends ManageAction {
	private GoodsStock model;

	/**
	 * 保存
	 * @return
	 */
	public JSONMessage doSave(){
		setLogContent("保存", "保存商品信息");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("goods_manager_power");
			String msg=getService(GoodsStockService.class).save(model);
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
						conditions= {@SelectConditionMeta(field = "status",value="0")})
				),
				@FormFieldMeta(title="创建时间",field="model.createDate",type=FormFieldType.DATE,span=12),
			}),
			@FormRowMeta(fields={
				@FormFieldMeta(title="数量",field="model.stockNum",type=FormFieldType.INT,numberRange="0~",span=12),
				@FormFieldMeta(title="金额",field="model.stockAmount",type=FormFieldType.DOUBLE,decimalCount=2,numberRange="0~",span=12),
			}),
		},
		buttons={
			@FormButtonMeta(title = "保存", url = "action/goodsGoodsStock/doSave",success=FormSuccessMethod.DONE_BACK)
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
	@ActionTableMeta(dataUrl = "action/goodsGoodsStock/goodsStockData",
			modelClass="goods.model.GoodsStock",
		cols = { 
			@ActionTableColMeta(field = "oid", title = "",type=TableColType.INDEX),
			@ActionTableColMeta(field = "goods.name", title = "商品名称", width=130,sort=true),
			@ActionTableColMeta(field = "createDate", title = "创建时间", width=130,sort=true,initSort=TableColSort.DESC,dateFormat="yyyy-MM-dd",align="center"),
			@ActionTableColMeta(field = "stockNum", title = "数量", width=130,sort=true,numberFormat="#,##0",align="right",countType=TableCountType.SUM),
			@ActionTableColMeta(field = "stockAmount", title = "金额", width=130,sort=true,numberFormat="#,##0.00",align="right",countType=TableCountType.SUM),
		},
		querys = {
			@QueryMeta(field = "goods.oid", name = "oid", type = QueryType.HIDDEN),
			@QueryMeta(field = "goods.name", name = "名称", type = QueryType.TEXT, hint="请输入名称", likeMode=true),
			@QueryMeta(field = "createDate", name = "创建时间", type = QueryType.DATE_RANGE)
		},
		buttons = {
			@ButtonMeta(title="新增", event = ButtonEvent.MODAL,modalWidth=700,  url = "action/goodsGoodsStock/toEdit", 
				success=SuccessMethod.REFRESH,style=ButtonStyle.NORMAL,
				power="goods_manager_power"
			)
		}
	)
	public JSONMessage goodsStockData(){
		return getListDataResult(null);
	}
	
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public GoodsStock getModel() {
		return model;
	}


	public void setModel(GoodsStock model) {
		this.model = model;
	}


}
