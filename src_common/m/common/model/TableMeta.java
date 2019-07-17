package m.common.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.TYPE})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface TableMeta {
	/**
	 * 表名称
	 * @return
	 */
	String name();
	/**
	 * 表描述
	 * @return
	 */
	String description() default "";
	/**
	 * 是否是视图
	 * @return
	 */
	boolean isView() default false;
	/**
	 * 视图的sql语句
	 * @return
	 */
	String viewSql() default "";
}
