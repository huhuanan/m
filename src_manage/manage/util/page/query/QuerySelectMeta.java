package manage.util.page.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.session.SessionMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中
public @interface QuerySelectMeta {
	String modelClass();
	/**
	 * 显示属性
	 * @return
	 */
	String title();
	/**
	 * sql表达式 //表达式里面的字段用#{}括着
	 * @return
	 */
	String titleExpression() default "";
	String value();
	String sortField() default "";
	SelectConditionMeta[] conditions() default {};
	SessionMeta session() default @SessionMeta();
}
