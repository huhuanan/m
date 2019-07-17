package manage.util.page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.Model;
import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;
import m.system.util.AnnotationUtil;
import m.system.util.ClassUtil;
import m.system.util.DateUtil;
import m.system.util.JSONMessage;
import m.system.util.NumberUtil;
import manage.util.page.chart.ActionChartMeta;
import manage.util.page.chart.ChartSeries;
import manage.util.page.chart.ChartSeries.ChartSeriesType;
import manage.util.page.chart.ChartXAxis;
import manage.util.page.chart.ChartXAxis.ChartAxisType;
import manage.util.page.chart.SeriesConditionMeta;

public class ActionChartUtil {
	private static Map<String,ActionChartMeta> chartMetaMap=new HashMap<String, ActionChartMeta>();
	public static ActionChartMeta getActionChartMeta(String key) throws ClassNotFoundException{
		ActionChartMeta meta=chartMetaMap.get(key);
		if(null==meta){
			String[] ss=key.split("\\|");
			meta=AnnotationUtil.getAnnotation4Method(ActionChartMeta.class, ClassUtil.getClass(ss[0]), ss[1]);
			//tableMetaMap.put(key, meta);//缓存
		}
		return meta;
	}
	/**
	 * 获取图表默认选项
	 * @param meta
	 * @return
	 */
	public static JSONMessage getChartOption(ActionChartMeta meta){
		Map<String,Object> title=new HashMap<String,Object>();
		title.put("text", meta.title());
		Map<String,Object> yAxis=new HashMap<String,Object>();
		yAxis.put("type", "value");
		JSONMessage json=new JSONMessage();
		json.push("title", title);
		json.push("yAxis", yAxis);
		return json;
	}
	/**
	 * 返回数据
	 * @param condition
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MException
	 */
	public static JSONMessage getDataList(QueryCondition condition) throws ClassNotFoundException, SQLException, MException {
		StackTraceElement stacks = new Throwable().getStackTrace()[2];
		ActionChartMeta chartMeta=getActionChartMeta(new StringBuffer(stacks.getClassName()).append("|").append(stacks.getMethodName()).toString());
		ChartXAxis xAxisMeta=chartMeta.xAxis();
		String xField=chartMeta.xAxis().field();
		List<String> fieldList=new ArrayList<String>();
		fieldList.add(xField);
		List<String> legendList=new ArrayList<String>();
		List<JSONMessage> series=new ArrayList<JSONMessage>();
		Map<String,Integer> indexMap=new HashMap<String, Integer>();
		Map<String,String> xfMap=new HashMap<String, String>();
		Map<String,SeriesConditionMeta[]> condMap=new HashMap<String,SeriesConditionMeta[]>();
		for(ChartSeries s : chartMeta.series()){
			if(!fieldList.contains(s.field())){
				fieldList.add(s.field());
			}
			for(SeriesConditionMeta cm : s.conditions()){
				if(!fieldList.contains(cm.field())){
					fieldList.add(cm.field());
				}
			}
			JSONMessage jm=new JSONMessage();
			legendList.add(s.name());
			indexMap.put(s.name(),s.index());
			xfMap.put(s.name(),s.field());
			condMap.put(s.name(), s.conditions());
			jm.push("name", s.name());
			if(s.type()==ChartSeriesType.BARLINE) {
				jm.push("type", "line");
				jm.push("step", "middle");
			}else if(s.type()==ChartSeriesType.SMOOTHLINE){
				jm.push("type", "line");
				jm.push("smooth", true);
			}else {
				jm.push("type", s.type());
			}
			if(s.markPoint()) jm.push("markPoint",new HtmlBodyContent("{\"data\":[{\"type\":\"max\",\"name\":\"最大值\"},{\"type\":\"min\",\"name\":\"最小值\"}]}"));
			if(s.markLine()) jm.push("markLine",new HtmlBodyContent("{\"data\":[{\"type\":\"average\",\"name\":\"平均值\"}]}"));
			series.add(jm);
		}
		List<Object> xAxisList=new ArrayList<Object>();
		Map<String,List<Object>> seriesMap=new HashMap<String, List<Object>>();
		List<Model> list=ModelQueryList.getModelList((Class<Model>)ClassUtil.getClass(chartMeta.modelClass()), 
				fieldList.toArray(new String[]{}), 
				null, condition, true,QueryOrder.asc(xField));
		Object x=null;
		for(Model m : list){
			if(xAxisMeta.type()==ChartAxisType.TIME){
				Date d=(Date)ClassUtil.getFieldValue(m, xField);
				if(null==d) continue;
				x=d.getTime(); 
			}else if(xAxisMeta.type()==ChartAxisType.CATEGORY){
				Object d=ClassUtil.getFieldValue(m, xField);
				if(null==d) continue;
				if(d instanceof Date){
					x=DateUtil.format((Date)d,xAxisMeta.dateFormat());
				}else{
					x=d.toString();
				}
			}else if(xAxisMeta.type()==ChartAxisType.VALUE){
				Object d=ClassUtil.getFieldValue(m, xField);
				if(null==d) continue;
				x=d.toString();
			}else{
				throw new MException(ActionChartUtil.class, "ChartAxis类型错误");
			}
			int index=-1;
			if(!xAxisList.contains(x)){
				xAxisList.add(x);
				index=xAxisList.size()-1;
			}
			if(index<0){
				for(int i=xAxisList.size()-1;i>=0;i--){
					if(xAxisList.get(i).equals(x)){
						index=i;
					}
				}
			}
			for(String yf : legendList){
				String fd=xfMap.get(yf);
				SeriesConditionMeta[] conds=condMap.get(yf);
				List<Object> ls=seriesMap.get(yf);
				if(null==ls){
					ls=new ArrayList<Object>();
					seriesMap.put(yf, ls);
				}
				fillList(ls,index);
				Object value=null;
				if(fd.equals("_count")) {
					value=1;
				}else{
					value=ClassUtil.getFieldValue(m, fd);
				}
				Object[] arr=(Object[])ls.get(index);
				if(null!=value){
					boolean flag=true;
					for(SeriesConditionMeta cm : conds){
						if(!cm.other()){
							Object v=ClassUtil.getFieldValue(m, cm.field());
							if(null!=v&&v.toString().equals(cm.value())){
							}else{
								flag=false;
								break;
							}
						}
					}
					arr[0]=x;
					if(flag) arr[1]=NumberUtil.round(Double.parseDouble(arr[1].toString())+Double.parseDouble(value.toString()));
				}
			}
		}
		for(JSONMessage s : series){
			s.push("yAxisIndex", indexMap.get(s.get("name")));
			//s.push("barMaxWidth", "50");
			if(xAxisMeta.type()==ChartAxisType.TIME){
				s.push("data", seriesMap.get(s.get("name")));
			}else{
				List<Object> dd=new ArrayList<Object>();
				List<Object> objs=seriesMap.get(s.get("name"));
				if(null!=objs) {
					for(Object obj : objs){
						dd.add(((Object[])obj)[1]);
					}
				}
				s.push("data", dd);
			}
		}
		JSONMessage json=getChartOption(chartMeta);
		json.push("m_type", xAxisMeta.type());
		if(xAxisMeta.dataZoom()) json.push("dataZoom", new HtmlBodyContent("[{\"type\":\"inside\"},{\"type\":\"slider\"}]"));
		json.push("grid", new HtmlBodyContent("{\"left\":\"10px\",\"right\":\"15px\",\"bottom\":\""+(xAxisMeta.dataZoom()?40:10)+"px\",\"containLabel\":true}"));
		JSONMessage legend=new JSONMessage();
		legend.push("data", legendList);
		json.push("legend", legend);
		JSONMessage xAxis=new JSONMessage();
		xAxis.push("type", chartMeta.xAxis().type());
		xAxis.push("data", xAxisList);
		json.push("xAxis", xAxis);
		json.push("yAxis", new HtmlBodyContent("[{\"type\":\"value\"},{\"type\":\"value\",\"splitLine\":{\"show\":false}}]"));
		json.push("tooltip", new HtmlBodyContent("{\"trigger\":\"axis\",\"axisPointer\":{\"type\":\"cross\"}}"));
		json.push("toolbox", new HtmlBodyContent("{\"show\":true,\"feature\":{\"saveAsImage\":{\"show\":true}}}"));
		json.push("series", series);
		return json;
	}
	private static void fillList(List<Object> list,int len){
		for(int i=list.size();i<len+1;i++){
			list.add(new Object[]{"",0.0});
		}
	}
	
}
