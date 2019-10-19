package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中
public @interface FormAlertMeta {
	public enum AlertType{
		INFO("info"),//
		SUCCESS("success"),//成功
		WARM("warning"),//提醒
		ERROR("error");//警告
		private String type;
		private AlertType(String type){
			this.type=type;
		}
		@Override
		public String toString() {
			return this.type;
		}
	}
	/**
	 * 类型 
	 * @return
	 */
	AlertType type() default AlertType.INFO;
	/**
	 * 自定义图标 
	 * @return
	 */
	String icon() default "";
	/**
	 * 标题  支持变量 #{变量名}
	 * @return
	 */
	String title();
	/**
	 * 内容  支持变量 #{变量名}
	 * @return
	 */
	String desc() default "";
}
