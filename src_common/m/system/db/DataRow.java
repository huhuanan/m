package m.system.db;

import java.math.BigDecimal;
import java.util.Map;

import m.system.RuntimeData;

public class DataRow {
	private Map<String,Object> map;
	protected DataRow(Map<String,Object> map){
		this.map=map;
	}
	/**
	 * 获取值 并转换
	 * @param clazz 值的类型
	 * @param name field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,String name){
		Object value=get(name);
		if(null==value) return null;
		else if(clazz.isInstance(value)) return (T) map.get(name);
		else if(value instanceof BigDecimal) {
			//数字类型
			if(clazz.equals(Integer.class)) return (T) new Integer(((BigDecimal)value).intValue());
			else if(clazz.equals(Double.class)) return (T) new Double(((BigDecimal)value).doubleValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((BigDecimal)value).floatValue());
			else if(clazz.equals(Long.class)) return (T) new Long(((BigDecimal)value).longValue());
			else if(clazz.equals(Short.class)) return (T) new Short(((BigDecimal)value).shortValue());
		}else if(value instanceof Long) {
			//long
			if(clazz.equals(Integer.class)) return (T) new Integer(((Long)value).intValue());
			else if(clazz.equals(Double.class)) return (T) new Double(((Long)value).doubleValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((Long)value).floatValue());
			else if(clazz.equals(Short.class)) return (T) new Short(((Long)value).shortValue());
		}else if(value instanceof Double) {
			//double
			if(clazz.equals(Integer.class)) return (T) new Integer(((Double)value).intValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((Double)value).floatValue());
			else if(clazz.equals(Long.class)) return (T) new Long(((Double)value).longValue());
			else if(clazz.equals(Short.class)) return (T) new Short(((Double)value).shortValue());
		}else if(value instanceof Float) {
			//float
			if(clazz.equals(Integer.class)) return (T) new Integer(((Float)value).intValue());
			else if(clazz.equals(Double.class)) return (T) new Double(((Float)value).doubleValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((Float)value).floatValue());
			else if(clazz.equals(Short.class)) return (T) new Short(((Float)value).shortValue());
		}else if(value instanceof Short) {
			//short
			if(clazz.equals(Integer.class)) return (T) new Integer(((Short)value).intValue());
			else if(clazz.equals(Double.class)) return (T) new Double(((Short)value).doubleValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((Short)value).floatValue());
			else if(clazz.equals(Long.class)) return (T) new Long(((Short)value).longValue());
		}else if(value instanceof Integer) {
			//int
			if(clazz.equals(Double.class)) return (T) new Double(((Integer)value).doubleValue());
			else if(clazz.equals(Float.class)) return (T) new Float(((Integer)value).floatValue());
			else if(clazz.equals(Long.class)) return (T) new Long(((Integer)value).longValue());
			else if(clazz.equals(Short.class)) return (T) new Short(((Integer)value).shortValue());
		}
		if(RuntimeData.getDebug()) System.out.println("类型不匹配");
		return (T) map.get(name);
	}
	/**
	 * 获取值 返回Object,需自己转换
	 * @param name
	 * @return
	 */
	public Object get(String name){
		return map.get(name);
	}
	public int size(){
		return map.size();
	}
}
