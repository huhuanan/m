package manage.util.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.ModelQueryList;
import m.system.cache.CacheUtil;
import m.system.util.ArrayUtil;
import manage.model.DictionaryData;
import manage.model.DictionaryType;

public class DictionaryUtil {
	/**
	 * value 是否包含 dictvalue
	 * @param value
	 * @param dictvalue
	 * @return
	 */
	public static boolean isContain(String value,String dictvalue){
		return new StringBuffer(",").append(value).append(",").toString().indexOf(new StringBuffer(",").append(dictvalue).append(",").toString())>=0;
	}
	//----------------dictType   dataValue   dataName
	protected static Map<String,Map<String,DictionaryData>> map=null;
	/**
	 * 初始化字典
	 */
	protected static void init(){
		if(null==map){
			map=new HashMap<String, Map<String,DictionaryData>>();
			try {
				List<DictionaryType> list=ModelQueryList.getModelList(DictionaryType.class, 
					new String[]{"oid","type"}, 
					null, 
					null
				);
				for(DictionaryType dt : list){
					Map<String,DictionaryData> dmap=new LinkedHashMap<String, DictionaryData>();
					List<DictionaryData> dlist=CacheUtil.getList(DictionaryData.class,dt.getType());//获取缓存
					for(DictionaryData dd : dlist){
						dmap.put(dd.getValue(), dd);
					}
					map.put(dt.getType(), dmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	/**
	 * 从新初始化
	 */
	public static void refresh(){
		if(null!=map) {
			for(String key : map.keySet()) {
				CacheUtil.clear(DictionaryData.class,key);//清除缓存
			}
			map=null;
			init();
		}
	}
	/**
	 * 获取字典值
	 * @param dictType 字典类型
	 * @param dataName 字典名称
	 * @return
	 */
	public static String getValue(String dictType,String dataName){
		init();
		Map<String,DictionaryData> dd=map.get(dictType);
		if(null!=dd){
			for(String key : dd.keySet()){
				if(dd.get(key).getValue().equals(dataName)){
					return key;
				}
			}
		}
		return "";
	}
	/**
	 * 获取字典名称
	 * @param dictType 字典类型
	 * @param dataValue 字典值
	 * @return
	 */
	public static String getName(String dictType,String dataValue){
		if(null==dataValue){
			return "";
		}else{
			return getName(dictType,dataValue.split(","));
		}
	}
	public static String getName(String dictType,Object dataValue){
		if(null==dataValue){
			return "";
		}else{
			return getName(dictType, dataValue.toString());
		}
	}
	private static String getName(String dictType,String[] arr){
		init();
		List<String> list=new ArrayList<String>();
		DictionaryData n=null;
		Map<String,DictionaryData> dd=map.get(dictType);
		if(null==dd) return "";
		for(String s : arr){
			n=dd.get(s);
			if(null!=n){
				list.add(n.getName());
			}
		}
		if(list.size()>0){
			return ArrayUtil.connection(list.toArray(new String[]{}), ",");
		}else{
			return "";
		}
	}
	/**
	 * 获取对应字典下的所有字典值.
	 * @param dictType
	 * @return
	 */
	public static List<DictionaryData> get(String dictType){
		init();
		Map<String,DictionaryData> dd=map.get(dictType);
		List<DictionaryData> list=new ArrayList<DictionaryData>();
		if(null!=dd){
			for(String key : dd.keySet()){
				list.add(dd.get(key));
			}
		}
		return list;
	}
	
	
	
}
