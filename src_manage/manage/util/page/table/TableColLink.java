package manage.util.page.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.button.ParamMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中
public @interface TableColLink {


	/**
	 * 权限 Module.xml里设置的属性*
	 * @return
	 */
	String power() default "";
	/** INLINE
	 * 按钮操作方式 
	 * @return
	 */
	ButtonEvent event();
	/**
	 * 操作前确认
	 * @return
	 */
	String confirm() default "";
	/**
	 * 操作url
	 * @return
	 */
	String url();
	/** ActionTable
	 * 操作是否带上查询参数
	 * @return
	 */
	boolean useQueryParams() default false;
	/**
	 * 操作url的参数
	 * @return
	 */
	ParamMeta[] params() default{};
	/**
	 * 对应查询列表的参数
	 */
	ParamMeta[] queryParams() default{};
	/** INLINE 可用
	 * 判断隐藏的字段
	 * @return
	 */
	String hiddenField() default "";
	/** INLINE 可用
	 * 判断隐藏的值,多个用逗号分开
	 * @return
	 */
	String hiddenValues() default "";
	/** INLINE 可用
	 * 判断显示的字段
	 * @return
	 */
	String showField() default "";
	/** INLINE 可用
	 * 判断显示的值,多个用逗号分开
	 * @return
	 */
	String showValues() default "";
	/**
	 * 操作结束，成功后执行方式
	 * @return
	 */
	SuccessMethod success() default SuccessMethod.NONE;
	/**
	 * 操作为modal时, modal的宽
	 * @return
	 */
	int modalWidth() default 750;
}
