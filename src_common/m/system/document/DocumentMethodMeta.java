package m.system.document;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface DocumentMethodMeta {
	/**
	 * 名称
	 */
	String title() default "";
	/**
	 * 描述
	 */
	String description() default "";
	/**
	 * 返回内容格式html格式
	 */
	String result() default "未定义";
	/**
	 * 权限
	 */
	boolean permission() default false;
}
