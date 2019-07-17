package m.system.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import m.system.exception.MException;




public class ClassUtil {
	public static void main(String[] a){
//		a=getAllQualifiedName4Class("m.base.model");
//		for(int i=0;i<a.length;i++){
//			System.out.println(a[i]);
//		}
	}
	private static final String TYPE_NAME_PREFIX = "class ";

	public static String getClassName(Type type) {
		if (type==null) {
			return null;
		}
		String className = type.toString();
		if (className.startsWith(TYPE_NAME_PREFIX)) {
			className = className.substring(TYPE_NAME_PREFIX.length());
		}
		return className;
	}

	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getClass(Class<T> clazz,String className) throws ClassNotFoundException {
		return (Class<T>)Class.forName(className);
	}
	public static Class<?> getClass(Type type) throws ClassNotFoundException {
		String className = getClassName(type);
		if (StringUtil.isSpace(className)) {
			return null;
		}
		return Class.forName(className);
	}
	public static Object newInstanceGenerics(Field field,int index) throws ClassNotFoundException, MException{
		Type mapMainType = field.getGenericType();   
		if (mapMainType instanceof ParameterizedType) {   
			ParameterizedType parameterizedType = (ParameterizedType)mapMainType;
			Type[] types = parameterizedType.getActualTypeArguments();
			return newInstance(getClass(types[index]));
		}
		return null;
	}
	/**
	 * 实例化对象
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws MException
	 */
	public static <T> T newInstance(Class<T> clazz) throws MException{
		try {
			return (T)clazz.getConstructor(new Class[]{}).newInstance(new Object[]{});
		} catch (Exception e) {
			throw new MException(ClassUtil.class,"类实例化失败!"+e.getMessage());
		} 
	}
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) throws MException{
		try {
			Class<T> clazz=(Class<T>) Class.forName(className);
			return newInstance(clazz);
		} catch (Exception e) {
			throw new MException(ClassUtil.class,"类实例化失败!"+e.getMessage());
		} 
	}
	/**
	 * 获取属性字段, 没有的话找父类
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getDeclaredField(Class<?> clazz,String fieldName){
		do{
			try{
				return clazz.getDeclaredField(fieldName);
			}catch(Exception e){
				clazz=clazz.getSuperclass();
			}
		}while(clazz!=Object.class);
		return null;
	}
	/**
	 * 获取所有属性字段, 包括父类的
	 * @param clazz
	 * @return
	 */
	public static Field[] getDeclaredFields(Class<?> clazz){
		List<Field> fields=new ArrayList<Field>();
		do{
			fields.addAll(0,Arrays.asList(clazz.getDeclaredFields()));
			clazz=clazz.getSuperclass();
		}while(clazz!=Object.class);
		return fields.toArray(new Field[]{});
	}
	public static Method[] getDeclaredMethods(Class<?> clazz){
		List<Method> methods=new ArrayList<Method>();
		do{
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			clazz=clazz.getSuperclass();
		}while(clazz!=Object.class);
		return methods.toArray(new Method[]{});
	}
	/**
	 * 
	 * @param packeageName
	 * @return
	 */
	public static String[] getAllQualifiedName4Class(String packeageName){
		File file=new File(Thread.currentThread().getContextClassLoader().getResource(packeageName.replaceAll("\\.", "/")).getPath());
		if(file.isDirectory()){
			String[] names=file.list();
			for(int i=0;i<names.length;i++){
				names[i]=packeageName+"."+names[i].substring(0, names[i].lastIndexOf("."));
			}
			return names;
		}
		return new String[]{};
	}
	/**
	 * 获取对象的get方法返回值
	 * @param obj 
	 * @param fieldName
	 * @return
	 * @throws MException
	 */
	public static Object getFieldValue(Object obj,String fieldName) throws MException {
		String[] fns=fieldName.split("\\.");
		if(fns.length==1){
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			String getName = "get" + stringLetter + fieldName.substring(1);
			Method getMethod;
			try {
				getMethod = obj.getClass().getMethod(getName, new Class[] {});
				return getMethod.invoke(obj, new Object[] {});
			} catch (Exception e) {
				//e.printStackTrace();
				throw new MException(ClassUtil.class,"获取get方法返回值失败!"+e.getMessage());
			}
		}else if(fns.length>1){
			Object object=getFieldValue(obj, fns[0]);
			if(null!=object){
				return getFieldValue(object, fieldName.substring(fieldName.indexOf(".")+1));
			}else{
				return null;
			}
		}else{
			throw new MException(ClassUtil.class,"获取get方法时,fieldName参数错误!");
		}
	}
	/**
	 * 填充对象set方法值
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws MException
	 */
	public static void setFieldValue(Object obj,String fieldName,Object value) throws MException{
		String stringLetter = fieldName.substring(0, 1).toUpperCase();
		String setName = "set" + stringLetter + fieldName.substring(1);
		try {
			Class<?> clazz=getDeclaredField(obj.getClass(),fieldName).getType();
			Method setMethod = obj.getClass().getMethod(setName, new Class[] { clazz });
			setMethod.invoke(obj, new Object[] { ObjectUtil.convert(clazz,value) });
		} catch (Exception e) {
			//e.printStackTrace();
			throw new MException(ClassUtil.class,"填充"+setName+"方法失败!");
		}
	}
	/**
	 * 对象执行方法
	 * @param obj
	 * @param method_name
	 * @return
	 * @throws MException
	 */
	public static Object executeMethod(Object obj,String method_name) throws Exception{
		return executeMethod(obj, method_name, null, null);
	}
	/**
	 * 对象执行方法
	 * @param obj
	 * @param method_name
	 * @param paramTypes
	 * @param paramValues
	 * @return
	 * @throws Exception 
	 */
	public static Object executeMethod(Object obj,String method_name,Class<?>[] paramTypes,Object[] paramValues) throws Exception{
		Method setMethod = obj.getClass().getMethod(method_name,paramTypes);
		return setMethod.invoke(obj, paramValues);
	}
//	public static <T> T fillInAttribute(Class<T> clazz,String fieldName,HttpServletRequest request) throws MException{
//		try {
//			T returnObject=(T)clazz.getConstructor(new Class[]{}).newInstance(new Object[]{});
//			if(returnObject instanceof String){
//				
//			}
//			
//			return returnObject;
//		} catch (Exception e) {
//			throw new MException(ClassUtil.class,"填充属性失败!"+e.getMessage());
//		} 
//	}
}
