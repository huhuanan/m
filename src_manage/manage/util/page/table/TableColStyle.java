package manage.util.page.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中
public @interface TableColStyle {

	/**
	 * style样式
	 * @return
	 */
	String style();
	/**
	 * value 多个用|分开 包含其中一个则应用样式
	 * @return
	 */
	String value() default "";
}
