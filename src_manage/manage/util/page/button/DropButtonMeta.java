package manage.util.page.button;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.button.ButtonMeta.ButtonStyle;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface DropButtonMeta {

	/** --
	 * 按钮名称*
	 * @return
	 */
	String title();
	/**
	 * 按钮icon
	 * @return
	 */
	String icon() default "";
	/**
	 * 权限 Module.xml里设置的属性*
	 * @return
	 */
	String power() default "";
	/**
	 * 按钮样式*
	 * @return
	 */
	ButtonStyle style() default ButtonStyle.DEFAULT;
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
	 * 操作按钮
	 * @return
	 */
	ButtonMeta[] buttons();
}
