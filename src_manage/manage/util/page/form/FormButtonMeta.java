package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.button.ButtonMeta.ButtonStyle;
import manage.util.page.button.ParamMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FormButtonMeta {

	public enum FormSuccessMethod{
		REFRESH_OTHER,//刷新其它信息
		NONE,//不执行
		DONE_BACK,//返回并执行回调
		BACK//返回不执行
	}
	public enum FormButtonMethod {
		FORM_SUBMIT,//form提交
		PARAMS_SUBMIT//params提交
	}
	public enum FormButtonEvent {
		AJAX,//ajax请求 {code:0,msg:""} code=0为成功
		MODAL//弹出窗口
	}
	/** --
	 * 按钮名称
	 * @return
	 */
	String title();
	/**
	 * 权限 Module.xml里设置的属性
	 * @return
	 */
	String power() default "";
	/**
	 * 按钮icon
	 * @return
	 */
	String icon() default "";
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
	/**
	 * 操作url的参数
	 * @return
	 */
	ParamMeta[] params() default{};
	/**
	 * 提交方式
	 * @return
	 */
	FormButtonMethod method() default FormButtonMethod.FORM_SUBMIT;
	/**
	 * 打开方式
	 * @return
	 */
	FormButtonEvent event() default FormButtonEvent.AJAX;
	/**
	 * 操作为modal时, modal的宽
	 * @return
	 */
	int modalWidth() default 750;
	/**
	 * 操作结束，成功后执行方式
	 * @return
	 */
	FormSuccessMethod success() default FormSuccessMethod.NONE;
	/**
	 * 按钮样式
	 * @return
	 */
	ButtonStyle style() default ButtonStyle.NORMAL;
	/** 
	 * 判断操作的字段 不设置就代表不判断
	 * @return
	 */
	String operField() default "";
	/** 
	 * 判断操作的值,多个用逗号分开
	 * @return
	 */
	String operValues() default "";
}
