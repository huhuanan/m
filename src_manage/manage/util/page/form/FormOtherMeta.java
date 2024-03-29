package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.query.LinkFieldMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FormOtherMeta {
	/**
	 * 标题
	 * @return
	 */
	String title();
	/**
	 * 其它信息地址
	 * @return
	 */
	String url();
	/** 
	 * 其他字段填充,用其他字段结合起来连接url
	 * @return
	 */
	LinkFieldMeta linkField() default @LinkFieldMeta();
}
