package m.common.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.FIELD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface LinkTableMeta {
	/**
	 * 列名称
	 * @return
	 */
	String name();
	/**
	 * 列类型
	 * @return
	 */
	Class<? extends Model> table() ;
	/**
	 * 列描述
	 * @return
	 */
	String description() default "";
	/**
	 * 是否不能为空 默认为可以为空
	 * @return
	 */
	boolean notnull() default false;
}
