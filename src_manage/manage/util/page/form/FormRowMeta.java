package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FormRowMeta {
	/**
	 * 表单行上端分割线
	 * @return
	 */
	boolean splitLine() default false;
	/**
	 * 提示
	 * @return
	 */
	FormAlertMeta alert() default @FormAlertMeta(title="");
	/**
	 * html标签段
	 * @return
	 */
	FormViewUIMeta[] viewui() default {};
	/**
	 * 分割线上的文字
	 * @return
	 */
	String title() default "";
	/**
	 * 表单tabs  true代表一个tab页的开始,
	 * @return
	 */
	boolean tabs() default false;
	/**
	 * tabs结束行   true代表tabs组件结束, 如果全部都是false,tab到最后结束
	 * @return
	 */
	boolean endTabs() default false;
	/**
	 * tab的标题
	 * @return
	 */
	String tabTitle() default "";
	/**
	 * 最小宽度
	 * @return
	 */
	int minWidth() default 400;
	/**
	 * 右侧空白距离
	 * @return
	 */
	int marginRight() default 0;
	/**
	 * 表单行里的字段
	 * @return
	 */
	FormFieldMeta[] fields() default{};
	/**
	 * 其它信息展示
	 * @return
	 */
	FormOtherMeta[] others() default {};
}
