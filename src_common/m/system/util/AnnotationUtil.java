package m.system.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationUtil {
	/**
	 * 获取类的注释
	 * @param entity
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation4Class(Class<T> meta,Class<?> entity){
		T tableMeta=entity.getAnnotation(meta);
		return tableMeta;
	}
	/**
	 * 获取字段的注释
	 * @param entity
	 * @return
	 */
	public static <T extends Annotation> Map<String,T> getAnnotationMap4Field(Class<T> meta,Class<?> entity){
		Map<String,T> map = new LinkedHashMap<String,T>();
        if(entity!=null){
            Field[] fields = ClassUtil.getDeclaredFields(entity);  
            for(Field f : fields){  
                //获取字段中包含fieldMeta的注解  
                T m = f.getAnnotation(meta);  
                if(m!=null){
                    map.put(f.getName(),m);  
                }  
            }
        }  
        return map;  
	}
	/**
	 * 获取方法的注释
	 * @param <T>
	 * @param meta
	 * @param entity
	 * @return
	 */
	public static <T extends Annotation> Map<String,T> getAnnotationMap4Method(Class<T> meta,Class<?> entity){
		Map<String,T> map = new LinkedHashMap<String,T>();
		if(entity!=null){
            Method[] methods = ClassUtil.getDeclaredMethods(entity);
            for(Method m : methods){  
                //获取字段中包含fieldMeta的注解  
                T t = m.getAnnotation(meta);  
                if(t!=null){
                    map.put(m.getName(),t);  
                }  
            }
        }  
        return map;  
	}
	/**
	 * 获取方法的注释
	 * @param <T>
	 * @param meta
	 * @param entity
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation4Method(Class<T> meta,Class<?> entity,String method){
		if(entity!=null){
            Method[] methods = ClassUtil.getDeclaredMethods(entity);
            for(Method m : methods){  
            	if(m.getName().equals(method)){
	                //获取字段中包含fieldMeta的注解  
	                return m.getAnnotation(meta);
            	}
            }
        }  
        return null;  
	}
}
