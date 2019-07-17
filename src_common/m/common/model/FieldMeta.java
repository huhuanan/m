package m.common.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import m.common.model.type.FieldType;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.FIELD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FieldMeta {
	/**
	 * 列名称
	 * @return
	 */
	String name() ;
	/**
	 * 列类型
	 * @return
	 */
	FieldType type() default FieldType.STRING;
	/** String,int,double有效 
	 * 默认  String不能包含单引号,  int和double必须是数字  int是整数
	 * @return
	 */
	String defaultValue() default "";
	/**
	 * 字符串型,长度.
	 * @return
	 */
	int length() default 1;
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
