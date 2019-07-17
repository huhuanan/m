package m.system.util;

import java.util.ArrayList;
import java.util.List;

import m.system.exception.MException;

public class ArrayUtil {

	/**
	 * 数组连接成字符串
	 * @param arrays 要连接的字符串数组
	 * @param split 中间的分隔符
	 * @return
	 */
	public static String connection(Object[] arrays,String split){
		StringBuffer stringBuffer=new StringBuffer();
		for(int i=0;i<arrays.length;i++){
			if(i!=0) stringBuffer.append(split);
			stringBuffer.append(arrays[i]);
		}
		return stringBuffer.toString();
	}
	/**
	 * 组合两个数组
	 * @param arr
	 * @param s
	 * @return
	 */
	public static String[] toArray(String[] arr,String... s){
		List<String> list=new ArrayList<String>();
		for(String o : arr) {
			list.add(o);
		}
		for(String o : s) {
			list.add(o);
		}
		return list.toArray(new String[] {});
	}
	/**
	 * 数组中是否包含对象
	 * @param arrays
	 * @param obj
	 * @return
	 */
	public static boolean isContain(Object[] arrays,Object obj){
		for(int i=0;i<arrays.length;i++){
			if(obj.equals(arrays[i])){
				return true;
			}
		}
		return false;
	}
	/**
	 * 数组中移出对象
	 * @param arrays
	 * @param obj
	 * @return
	 */
	public static Object[] removeObject(Object[] arrays,Object obj){
		List<Object> list=new ArrayList<Object>();
		for(Object o : arrays){
			if(!obj.equals(o)){
				list.add(o);
			}
		}
		return list.toArray(new Object[]{});
	}
	/**
	 * 转换对象为字符串的展现形式.
	 * @param list
	 * @return
	 */
	public static String toString(List<Object> list){
		return toString(list.toArray(new Object[]{}));
	}
	/**
	 * 转换对象为字符串的展现形式.
	 * @param objs
	 * @return
	 */
	public static String toString(Object[] objs){
		StringBuffer sb=new StringBuffer();
		for(Object value : objs){
			sb.append(",");
			try {
				sb.append(ObjectUtil.toString(value));
			} catch (MException e) {
				sb.append("null");
			}
		}
		return new StringBuffer("[").append(sb.length()>0?sb.substring(1):"").append("]").toString();
	}
	public static void main(String[] args) {
		Object[] a=new Object[]{1,"1",2,"3"};
		a=ArrayUtil.removeObject(a, 2);
		System.out.println(ArrayUtil.connection(a, ","));
	}
}
