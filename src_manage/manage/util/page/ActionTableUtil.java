package manage.util.page;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.format.Alignment;
import jxl.write.WriteException;
import m.common.model.Model;
import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.model.util.QueryParameter;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;
import m.system.lang.PageInfo;
import m.system.util.ClassUtil;
import m.system.util.DateUtil;
import m.system.util.JSONMessage;
import m.system.util.NumberUtil;
import m.system.util.ObjectUtil;
import m.system.util.StringUtil;
import manage.util.excel.SheetCell;
import manage.util.excel.SheetObject;
import manage.util.excel.SheetRow;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.DropButtonMeta;
import manage.util.page.button.ParamMeta;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableColMeta.TableColSort;
import manage.util.page.table.ActionTableColMeta.TableColType;
import manage.util.page.table.ActionTableColMeta.TableCountType;
import manage.util.page.table.ActionTableMeta;
import manage.util.page.table.TableColData;
import manage.util.page.table.TableColStyle;
import manage.util.tag.DictionaryUtil;

public class ActionTableUtil {
	
	private static String getFieldName(ActionTableColMeta cm){
		String field="";
		if(!StringUtil.isSpace(cm.dictionaryType())){
			field=new StringBuffer(cm.field().replaceAll("\\.", "_")).append("-").append(cm.dictionaryType()).toString();
		}else if(cm.colDatas().length>0){
			field=new StringBuffer(cm.field().replaceAll("\\.", "_")).append("-d").toString();
		}else{
			field=cm.field().replaceAll("\\.", "_");
		}
		return field;
	}
	
	/**
	 * @param cm
	 * @return
	 */
	public static List<Map<String,Object>> toList(ActionTableColMeta[] cms,Map<String,Boolean> powerMap){
		List<Map<String,Object>> cols=new ArrayList<Map<String,Object>>();
		ButtonMetaUtil bmUtil=new ButtonMetaUtil();
		for(ActionTableColMeta cm : cms){
			if(StringUtil.isSpace(cm.power())||null!=powerMap.get(cm.power())&&powerMap.get(cm.power())){
				Map<String,Object> col=new HashMap<String,Object>();
				col.put("key", getFieldName(cm));
				col.put("title", cm.title());
				col.put("groupTitle", cm.groupTitle());
				col.put("width", cm.width());
				col.put("align", cm.align());
				col.put("fixed", cm.fixed());
				col.put("ellipsis", cm.ellipsis());
				if(cm.colStyles().length>0){
					col.put("type", TableColType.HTML);
				}else if(cm.type()==TableColType.IMAGE) {
					col.put("render", new HtmlBodyContent(new StringBuffer("(h, params)=>{return this.imageRender(h,params,'").append(col.get("key")).append("');}").toString()));
					col.put("type", TableColType.NORMAL);
					col.put("width", 80);
					col.put("align", "center");
				}else if(cm.type()==TableColType.STATUS){
					col.put("type", TableColType.NORMAL);
					col.put("render", new HtmlBodyContent(new StringBuffer("(h, params)=>{return this.statusRender(h,params,'").append(cm.field()).append("');}").toString()));
					col.put("width", 80);
					col.put("align", "center");
				}else if(cm.type()==TableColType.COLOR){
					col.put("type", TableColType.NORMAL);
					col.put("render", new HtmlBodyContent(new StringBuffer("(h, params)=>{return this.colorRender(h,params,'").append(cm.field()).append("');}").toString()));
					col.put("width", 50);
					col.put("align", "center");
				}else if(cm.type()==TableColType.CHECKBOX||cm.type()==TableColType.INDEX){
					col.put("type", cm.type());
					col.put("width", 50);
					col.put("fixed", "left");
					col.put("align", "center");
				}else{
					col.put("render", new HtmlBodyContent(new StringBuffer("(h, params)=>{return this.colRender(h,params,'").append(col.get("key")).append("');}").toString()));
					col.put("type", cm.type());
					List<Map<String,Object>> btnList=bmUtil.toList(cm.buttons(), powerMap);
					List<Map<String,Object>> dropBtnList=bmUtil.toList(cm.dropButtons(), powerMap);
					if(btnList.size()>0||dropBtnList.size()>0){
						col.put("align", "center");
						col.put("buttons",btnList);
						col.put("dropButtons", dropBtnList);
					}
				}
				if(!StringUtil.isSpace(cm.link().url())) {
					Map<String,Object> link=bmUtil.toPamams(cm.link(),powerMap);
					if(null!=link) {
						col.put("link",link);
					}
				}
				if(cm.sort()){
					col.put("sortable", "custom");
					if(cm.initSort()!=TableColSort.NONE){
						col.put("sortType", cm.initSort());
					}
				}
				cols.add(col);
			}
		}
		return toNewList(cols);
	}
	private static List<Map<String,Object>> toNewList(List<Map<String,Object>> cols){
		List<Map<String,Object>> newCols=new ArrayList<Map<String,Object>>();
		String groupTitle=null;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : cols) {
			String[] gt=(String[])map.get("groupTitle");
			if(gt.length==0) {
				if(null!=groupTitle&&list.size()>0) {
					newCols.add(toNewMap(groupTitle, list));
					list=new ArrayList<Map<String,Object>>();
				}
				newCols.add(map);
				groupTitle=null;
			}else if(null==groupTitle){
				groupTitle=gt[0];
				list.add(map);
			}else if(groupTitle.equals(gt[0])) {
				list.add(map);
			}else {
				if(null!=groupTitle&&list.size()>0) {
					newCols.add(toNewMap(groupTitle, list));
					list=new ArrayList<Map<String,Object>>();
				}
				groupTitle=gt[0];
				list.add(map);
			}
		}
		if(null!=groupTitle&&list.size()>0) {
			newCols.add(toNewMap(groupTitle, list));
			list=new ArrayList<Map<String,Object>>();
		}
		return newCols;
	}
	private static Map<String,Object> toNewMap(String groupTitle,List<Map<String,Object>> cols){
		if(cols.size()==1 && ((String[])cols.get(0).get("groupTitle")).length==0) {
			return cols.get(0);
		}else {
			for(Map<String,Object> col : cols) {
				String[] gt=(String[])col.get("groupTitle");
				col.put("groupTitle", Arrays.copyOfRange(gt, 1, gt.length));
			}
			Map<String,Object> col=new HashMap<String, Object>();
			col.put("align", cols.size()==1?cols.get(0).get("align"):"center");
			col.put("title", groupTitle);
			col.put("children", toNewList(cols));
			return col;
		}
	}
	/**
	 * 获取初始化属性
	 * @param cms
	 * @return
	 */
	public static String[] getInitSort(ActionTableColMeta[] cms){
		for(ActionTableColMeta cm : cms){
			if(cm.initSort()!=TableColSort.NONE){
				return new String[]{getFieldName(cm),cm.initSort().toString()};
			}
		}
		return new String[]{"",""};
	}
	@SuppressWarnings("unchecked")
	private static ModelQueryList getModelQueryList(ActionTableMeta tableMeta,QueryPage page,QueryCondition condition,QueryOrder... orders) throws ClassNotFoundException{
		return ModelQueryList.instance((Class<Model>)ClassUtil.getClass(tableMeta.modelClass()), 
				getFieldNames(tableMeta.cols(),tableMeta.buttons(),tableMeta.dropButtons()), 
				page, condition,getFieldExps(tableMeta.cols()), orders);
	}
	/**
	 * 查询数据结果 必须由带有ActionTableColMeta注释的Action直接调用
	 * @param <T>
	 * @param <A> 
	 * @param tableClass 查询的模型类
	 * @param page 分页
	 * @param condition 条件
	 * @param orders 排序
	 * @return
	 * @throws MException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static PageInfo toPageInfo(ActionTableMeta tableMeta,QueryPage page,QueryCondition condition,QueryOrder... orders) throws ClassNotFoundException, SQLException, MException{
		List<QueryOrder> os=new ArrayList<QueryOrder>();
		for(QueryOrder order : orders) {//处理转换的排序列
			if(null!=order) order.setName(order.getName().replaceAll("_", "."));
			if(null!=order) os.add(order);
		}
		for(String o : tableMeta.orders()) {
			String[] arr=o.trim().split(" ");
			if(arr.length>1&&arr[1].toLowerCase().equals("desc")) os.add(QueryOrder.desc(arr[0]));
			else os.add(QueryOrder.asc(arr[0]));
		}
		return ModelQueryList.getModelPageInfo(getModelQueryList(tableMeta, page, condition, os.toArray(new QueryOrder[] {})));
	}
	private static String[] getFieldNames(ActionTableColMeta[] cols,ButtonMeta[] buttons,DropButtonMeta[] dropButtons){
		Set<String> fs=new HashSet<String>();
		for(ActionTableColMeta col : cols){
			fs.add(col.field());
			for(ParamMeta pm : col.link().params()) {
				if(!StringUtil.isSpace(pm.field())) {
					fs.add(pm.field());
				}
			}
			for(ButtonMeta btn : col.buttons()){
				if(!StringUtil.isSpace(btn.hiddenField())) fs.add(btn.hiddenField());
				if(!StringUtil.isSpace(btn.showField())) fs.add(btn.showField());
				for(ParamMeta b : btn.params()){
					if(!StringUtil.isSpace(b.field())) fs.add(b.field());
				}
			}
			for(DropButtonMeta dbm : col.dropButtons()) {
				if(!StringUtil.isSpace(dbm.hiddenField())) fs.add(dbm.hiddenField());
				if(!StringUtil.isSpace(dbm.hiddenField())) fs.add(dbm.hiddenField());
				for(ButtonMeta btn : dbm.buttons()){
					if(!StringUtil.isSpace(btn.hiddenField())) fs.add(btn.hiddenField());
					if(!StringUtil.isSpace(btn.showField())) fs.add(btn.showField());
					for(ParamMeta b : btn.params()){
						if(!StringUtil.isSpace(b.field())) fs.add(b.field());
					}
				}
			}
		}
		for(ButtonMeta btn : buttons){
			if(!StringUtil.isSpace(btn.hiddenField())) fs.add(btn.hiddenField());
			if(!StringUtil.isSpace(btn.showField())) fs.add(btn.showField());
			for(ParamMeta b : btn.params()){
				if(!StringUtil.isSpace(b.field())) fs.add(b.field());
			}
		}
		for(DropButtonMeta dbm : dropButtons) {
			if(!StringUtil.isSpace(dbm.hiddenField())) fs.add(dbm.hiddenField());
			if(!StringUtil.isSpace(dbm.hiddenField())) fs.add(dbm.hiddenField());
			for(ButtonMeta btn : dbm.buttons()){
				if(!StringUtil.isSpace(btn.hiddenField())) fs.add(btn.hiddenField());
				if(!StringUtil.isSpace(btn.showField())) fs.add(btn.showField());
				for(ParamMeta b : btn.params()){
					if(!StringUtil.isSpace(b.field())) fs.add(b.field());
				}
			}
		}
		return fs.toArray(new String[]{});
	}
	public static Map<String,String> getFieldExps(ActionTableColMeta[] cols){
		Map<String,String> map=new HashMap<String, String>();
		for(ActionTableColMeta col : cols){
			map.put(col.field(),col.fieldExpression());
		}
		return map;
	}
	/**
	 * 将数据转换长前台table数据
	 * @param <T>
	 * @param list
	 * @return
	 * @throws MException
	 * @throws ClassNotFoundException
	 */
	public static <T extends Model> List<JSONMessage> getDataList(ActionTableMeta tableMeta,List<T> list) throws MException, ClassNotFoundException{
		ActionTableColMeta[] cols=tableMeta.cols();
		ButtonMeta[] buttons=tableMeta.buttons();
		DropButtonMeta[] dropButtons=tableMeta.dropButtons();
		List<JSONMessage> data=new ArrayList<JSONMessage>();
		List<String> spanFieldList=new ArrayList<String>();
		for(int t=0,len=list.size();t<len;t++){
			T model=list.get(t);
			JSONMessage d=new JSONMessage();
			for(int i=0;i<cols.length;i++){
				ActionTableColMeta m=cols[i];
				String fn=getFieldName(m);
				if(t==0&&i>=tableMeta.rowspanIndex()&&i<tableMeta.rowspanIndex()+tableMeta.rowspanNum()) {
					spanFieldList.add(fn);
				}
				Object value=ClassUtil.getFieldValue(model, m.field());
				Object v=null;
				if(!StringUtil.isSpace(m.dateFormat())){
					v=DateUtil.format((Date)value,m.dateFormat());
				}else if(!StringUtil.isSpace(m.numberFormat())){
					v=NumberUtil.format(value,m.numberFormat());
				}else if(!StringUtil.isSpace(m.dictionaryType())){
					d.push(m.field().replaceAll("\\.", "_"), value);
					v=DictionaryUtil.getName(m.dictionaryType(),value);
				}else if(m.colDatas().length>0){
					d.push(m.field().replaceAll("\\.", "_"), value);
					v=getDisplayValue(m.colDatas(),value);
				}else{
					v=value;
				}
				if(m.colStyles().length>0){
					for(TableColStyle cs : m.colStyles()){
						String vv=(String) ObjectUtil.convert(String.class,value);
						String[] cv=cs.value().split("|");
						for(String c : cv) {
							if(vv.indexOf(c)>=0) {
								v=new StringBuffer("<span style=\"").append(cs.style()).append("\">").append(v).append("</span>");
								break;
							}
						}
					}
				}
				d.push(fn,v);
				for(ParamMeta b : m.link().params()) {
					if(!StringUtil.isSpace(b.field())&&null==d.get(b.field().replaceAll("\\.", "_"))){
						d.push(b.field().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, b.field()));
					}
				}
				for(ButtonMeta btn : m.buttons()){
					if(!StringUtil.isSpace(btn.hiddenField())&&null==d.get(btn.hiddenField().replaceAll("\\.", "_"))){
						d.push(btn.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.hiddenField()));
					}
					if(!StringUtil.isSpace(btn.showField())&&null==d.get(btn.showField().replaceAll("\\.", "_"))){
						d.push(btn.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.showField()));
					}
					for(ParamMeta b : btn.params()){
						if(!StringUtil.isSpace(b.field())&&null==d.get(b.field().replaceAll("\\.", "_"))){
							d.push(b.field().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, b.field()));
						}
					}
				}
				for(DropButtonMeta dbm : m.dropButtons()) {
					if(!StringUtil.isSpace(dbm.hiddenField())&&null==d.get(dbm.hiddenField().replaceAll("\\.", "_"))){
						d.push(dbm.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, dbm.hiddenField()));
					}
					if(!StringUtil.isSpace(dbm.showField())&&null==d.get(dbm.showField().replaceAll("\\.", "_"))){
						d.push(dbm.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, dbm.showField()));
					}
					for(ButtonMeta btn : dbm.buttons()){
						if(!StringUtil.isSpace(btn.hiddenField())&&null==d.get(btn.hiddenField().replaceAll("\\.", "_"))){
							d.push(btn.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.hiddenField()));
						}
						if(!StringUtil.isSpace(btn.showField())&&null==d.get(btn.showField().replaceAll("\\.", "_"))){
							d.push(btn.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.showField()));
						}
						for(ParamMeta b : btn.params()){
							if(!StringUtil.isSpace(b.field())&&null==d.get(b.field().replaceAll("\\.", "_"))){
								d.push(b.field().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, b.field()));
							}
						}
					}
				}
			}
			for(ButtonMeta btn : buttons){
				if(!StringUtil.isSpace(btn.hiddenField())&&null==d.get(btn.hiddenField().replaceAll("\\.", "_"))){
					d.push(btn.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.hiddenField()));
				}
				if(!StringUtil.isSpace(btn.showField())&&null==d.get(btn.showField().replaceAll("\\.", "_"))){
					d.push(btn.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.showField()));
				}
				for(ParamMeta b : btn.params()){
					if(!StringUtil.isSpace(b.field())&&null==d.get(b.field().replaceAll("\\.", "_"))){
						d.push(b.field().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, b.field()));
					}
				}
			}
			for(DropButtonMeta dbm : dropButtons) {
				if(!StringUtil.isSpace(dbm.hiddenField())&&null==d.get(dbm.hiddenField().replaceAll("\\.", "_"))){
					d.push(dbm.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, dbm.hiddenField()));
				}
				if(!StringUtil.isSpace(dbm.showField())&&null==d.get(dbm.showField().replaceAll("\\.", "_"))){
					d.push(dbm.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, dbm.showField()));
				}
				for(ButtonMeta btn : dbm.buttons()){
					if(!StringUtil.isSpace(btn.hiddenField())&&null==d.get(btn.hiddenField().replaceAll("\\.", "_"))){
						d.push(btn.hiddenField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.hiddenField()));
					}
					if(!StringUtil.isSpace(btn.showField())&&null==d.get(btn.showField().replaceAll("\\.", "_"))){
						d.push(btn.showField().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, btn.showField()));
					}
					for(ParamMeta b : btn.params()){
						if(!StringUtil.isSpace(b.field())&&null==d.get(b.field().replaceAll("\\.", "_"))){
							d.push(b.field().replaceAll("\\.", "_"), ClassUtil.getFieldValue(model, b.field()));
						}
					}
				}
			}
			data.add(d);
		}
		fillSpan(spanFieldList,data);
		return data;
	}
	public static void fillSpan(List<String> spanFieldList,List<JSONMessage> data) throws MException {
		if(spanFieldList.size()==0) return;
		List<Boolean> lastIsSpanList=null;
		List<Boolean> isSpanList=null;
		String lastValue=null;
		int lastIndex=-1;
		int num=0;
		for(String f : spanFieldList) {
			isSpanList=new ArrayList<Boolean>();
			int len=data.size();
			for(int i=0;i<len;i++) {
				JSONMessage d=data.get(i);
				if(null!=lastValue&&lastValue.equals(ObjectUtil.toString(d.get(f)))
						&&(null==lastIsSpanList||lastIsSpanList.get(i))){
					d.push("_rowspan_num."+f, 0);
					isSpanList.add(true);
					num++;
				}else {
					if(null!=lastValue) {
						data.get(lastIndex).push("_rowspan_num."+f, num);
						isSpanList.add(false);
					}else {
						isSpanList.add(true);
					}
					lastValue=ObjectUtil.toString(d.get(f));
					lastIndex=i;
					num=1;
				}
			}
			if(null!=lastValue) {
				data.get(lastIndex).push("_rowspan_num."+f, num);
			}
			lastValue=null;
			lastIndex=-1;
			lastIsSpanList=isSpanList;
		}
	}
	public static JSONMessage getCountData(ActionTableMeta tableMeta,QueryCondition condition) throws Exception{
		ModelQueryList query=getModelQueryList(tableMeta,null,condition,null);
		QueryParameter qp=query.getQueryParameter();

		Map<String,String> sumMap=new HashMap<String,String>();
		Map<String,String> formatMap=new HashMap<String, String>();
		for(ActionTableColMeta col : tableMeta.cols()){
			if(StringUtil.isSpace(col.dateFormat())&&!StringUtil.isSpace(col.numberFormat())&&col.countType()==TableCountType.SUM){
				String key=getFieldName(col);
				sumMap.put(key,query.getAlias4Field(col.field()));
				formatMap.put(key, col.numberFormat());
			}
		}
		if(sumMap.size()>0){
			StringBuffer sql=new StringBuffer("SELECT '' nofield");
			for(String key : sumMap.keySet()){
				sql.append(",sum(a.").append(sumMap.get(key)).append(") ").append(key);
			}
			sql.append(" FROM (").append(qp.getSql()).append(") a");
			DataRow dr=DBManager.queryFirstRow(sql.toString(),qp.getValueList().toArray(new Object[]{}));
			if(null!=dr){
				JSONMessage json=new JSONMessage();
				for(String key : sumMap.keySet()){
					json.push(key, NumberUtil.format(dr.get(key),formatMap.get(key)));
				}
				json.push("_count_row", true);
				json.push("_disabled", true);
				return json;
			}
		}
		return null;
	}
	private static Object getDisplayValue(TableColData[] cds,Object value){
		if(null==value) return "";
		String v=value.toString();
		TableColData od=null;
		for(TableColData cd : cds){
			if(cd.other()){
				od=cd;
			}
			if(v.equals(cd.value())){
				v=cd.title();
			}
		}
		if(null!=od&&v.equals(value.toString())){
			v=od.title();
		}
		return v;
	}
	/**
	 * 返回excel的sheet
	 * @param tableMeta
	 * @param data
	 * @param sheetName
	 * @return
	 * @throws MException
	 * @throws WriteException
	 * @throws IOException
	 */
	public static SheetObject toExcelSheet(ActionTableMeta tableMeta,List<JSONMessage> data,String sheetName) throws MException, WriteException, IOException{
		List<SheetRow> rows=new ArrayList<SheetRow>();
		rows.add(toHeadCell(tableMeta.cols()));
		rows.addAll(toBodyCell(tableMeta.cols(),data));
		return new SheetObject(rows.toArray(new SheetRow[]{}),sheetName);
	}
	/**
	 * 填充头信息
	 * @param sheet
	 * @param fields
	 * @throws WriteException
	 */
	private static SheetRow toHeadCell(ActionTableColMeta[] fields) throws WriteException{
		List<SheetCell> cells=new ArrayList<SheetCell>();
		for(int i=0;i<fields.length;i++){
			ActionTableColMeta field=fields[i];
			if(field.type()!=TableColType.NORMAL) continue;
			cells.add(SheetCell.headCell(field.title(),field.width(),toAlignment(field.align())));
		}
		return new SheetRow(cells.toArray(new SheetCell[]{}),20);
	}
	private static Alignment toAlignment(String align){
		Alignment a=Alignment.LEFT;
		if("center".equals(align)){
			a=Alignment.CENTRE;
		}else if("right".equals(align)){
			a=Alignment.RIGHT;
		}
		return a;
	}
	/**
	 * 填充内容
	 * @param sheet
	 * @param fields
	 * @param data
	 * @throws WriteException 
	 * @throws WriteException
	 */
	private static List<SheetRow> toBodyCell(ActionTableColMeta[] fields,List<JSONMessage> data) throws WriteException {
		List<SheetRow> rows=new ArrayList<SheetRow>();
		for(int i=0;i<data.size();i++){
			List<SheetCell> cells=new ArrayList<SheetCell>();
			for(int j=0;j<fields.length;j++){
				ActionTableColMeta field=fields[j];
				if(field.type()!=TableColType.NORMAL) continue;
				String content="";
				if(null!=data.get(i).get(getFieldName(field))){
					content=data.get(i).get(getFieldName(field)).toString();
				}
				cells.add(new SheetCell(content,field.width(),toAlignment(field.align())));
			}
			rows.add(new SheetRow(cells.toArray(new SheetCell[]{})));
		}
		return rows;
	}

}
