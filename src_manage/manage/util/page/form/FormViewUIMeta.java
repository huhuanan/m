package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FormViewUIMeta {
	/**
	 * html代码  支持属性 #{属性名}
	 * @return
	 */
	String template();
	/**
	 * 引用的属性字段
	 * @return
	 */
	String[] fields() default{};
}
