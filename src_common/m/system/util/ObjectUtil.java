package m.system.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import m.common.model.Model;
import m.common.model.util.ModelUtil;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;

public class ObjectUtil {
	public static Object convert(Class<?> clazz,Object object){
		if(null==object){
			return null;
		}else if(object.getClass().equals(clazz)){
			return object;
		}else if(String.class.equals(clazz)){
			return object.toString();
		}else if(Byte.class.equals(clazz)){
			return Byte.parseByte(object.toString());
		}else if(clazz.equals(byte.class)){
			return Byte.parseByte(object.toString());
		}else if(Short.class.equals(clazz)){
			return Short.parseShort(object.toString());
		}else if(clazz.equals(short.class)){
			return Short.parseShort(object.toString());
		}else if(Integer.class.equals(clazz)){
			return Integer.parseInt(object.toString());
		}else if(clazz.equals(int.class)){
			return Integer.parseInt(object.toString());
		}else if(Long.class.equals(clazz)){
			return Long.parseLong(object.toString());
		}else if(clazz.equals(long.class)){
			return Long.parseLong(object.toString());
		}else if(Float.class.equals(clazz)){
			return Float.parseFloat(object.toString());
		}else if(clazz.equals(float.class)){
			return Float.parseFloat(object.toString());
		}else if(Double.class.equals(clazz)){
			return Double.parseDouble(object.toString());
		}else if(clazz.equals(double.class)){
			return Double.parseDouble(object.toString());
		}else if(Boolean.class.equals(clazz)){
			return Boolean.parseBoolean(object.toString());
		}else if(clazz.equals(boolean.class)){
			return Boolean.parseBoolean(object.toString());
		}else if(Date.class.equals(clazz)||java.sql.Date.class.equals(clazz)){
			String string=object.toString().replace('T', ' ').replace(',', ' ');
			return DateUtil.format(string, "yyyy-MM-dd HH:mm:ss.SSS".substring(0,string.length()));
		}else{
			return object;
		}
	}
	/**
	 * 转换对象为字符串的展现形式.
	 * @param value
	 * @return
	 * @throws MException 
	 */
	@SuppressWarnings("unchecked")
	public static String toString(Object value) throws MException{
		if(null==value){
			return "null";
		}else if(value instanceof HtmlBodyContent){
			return value.toString();
		}else if(value instanceof Number || value instanceof Boolean){
			return value.toString();
		}else if(value instanceof Date){
			return new StringBuffer().append("\"").append(DateUtil.format((Date)value,DateUtil.YYYY_MM_DD_HH_MM_SS)).append("\"").toString();
		}else if(value instanceof Object[]){
			return ArrayUtil.toString((Object[])value);
		}else if(value instanceof List){
			return ArrayUtil.toString((List<Object>)value);
		}else if(value instanceof JSONMessage){
			return ((JSONMessage)value).toJSONString();
		}else if(value instanceof Map){
			return new JSONMessage((Map<String, Object>) value).toJSONString();
		}else if(value instanceof Model){
			return ModelUtil.toJSONMessage("",(Model)value).toJSONString();
		}else{
			return new StringBuffer().append("\"").append(StringUtil.conver2JS(value.toString())).append("\"").toString();
		}
	}
	public static void main(String[] a) throws MException{
		//convert(String.class,1);
		System.out.println(toString(new String[]{"111",""}));
	}
}
