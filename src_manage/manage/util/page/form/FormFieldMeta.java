package manage.util.page.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.query.LinkFieldMeta;
import manage.util.page.query.QuerySelectMeta;
import manage.util.page.query.SelectDataMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface FormFieldMeta {
	public enum FormFieldType {
		HIDDEN,//隐藏域
		TEXT,//普通输入框
		PASSWORD,//密码框
		TEXTAREA,//文本域
		SELECT,//选择框
		CHECKBOX,//多选框
		RADIO,//单选框
		STEPS,//步骤, 不可修改
		DATE,//日期选择器
		DATETIME,//时间选择器
		INT,//整型
		DOUBLE,//
		IMAGE,//图片
		FILE,//文件
		EDITER,//富文本编辑器
		COLOR,//颜色
		MAP,//地图坐标
		BUTTON//按钮
	}
	/**
	 * 标题
	 * @return
	 */
	String title() default "";
	/**
	 * 提示消息
	 * @return
	 */
	String message() default "";
	/**
	 * 是否隐藏标题
	 * @return
	 */
	boolean hideTitle() default false;
	/**
	 * 标题宽度
	 * @return
	 */
	int titleWidth() default 100;
	/**
	 * 字段名
	 * @return
	 */
	String field();
	/**
	 * 是否必填
	 * @return
	 */
	boolean required() default false;
	/**
	 * 输入框类型
	 * @return
	 */
	FormFieldType type();
	/**
	 * 除隐藏域以外, 默认为可编辑
	 * @return
	 */
	boolean disabled() default false;
	/**
	 * 输入框宽度    1~24
	 * @return
	 */
	int span() default 24;
	/** IMAGE
	 * 高度 默认32
	 * @return
	 */
	int height() default 30;
	/**
	 * TEXTAREA 文本域高度行数
	 * @return
	 */
	int rows() default 3;
	/**
	 * 提示
	 * @return 
	 */
	String hint() default "";
	/** SELECT CHECKBOX RADIO STEPS
	 * select的查询  优先
	 * @return
	 */
	QuerySelectMeta querySelect() default @QuerySelectMeta(modelClass = "", title = "", value = "");
	/** SELECT CHECKBOX RADIO STEPS
	 * 字典类型 其次
	 * @return
	 */
	String dictType() default "";
	/** SELECT CHECKBOX RADIO STEPS
	 * select的数据  最终
	 * @return
	 */
	SelectDataMeta[] querySelectDatas() default {};
	/** DATE
	 * 时间格式
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd";
	/** DOUBLE
	 * double 小数位数
	 * @return
	 */
	int decimalCount() default 2;
	/** INT DOUBLE
	 * 数字范围   例如 -2.1~99.99
	 * @return
	 */
	String numberRange() default "";
	/** HIDDEN 和 TEXTAREA 不支持
	 * 输入后清除的form字段 执行对于字段的其他字段填充
	 * @return
	 */
	String clearField() default "";
	/** 只有 SELECT CHECKBOX RADIO STEPS 支持
	 * 其他字段填充,用其他字段结合起来填充
	 * @return
	 */
	LinkFieldMeta linkField() default @LinkFieldMeta();
	/** IMAGE
	 * 图片类型 用于标识业务类型
	 * @return
	 */
	String imageType() default "image";
	/**
	 * 对应属性为空则隐藏
	 * @return
	 */
	String nullHidden() default "";
	/** 只有 IMAGE
	 * 缩略图宽高比例
	 * @return
	 */
	double thumRatio() default 1.0;
	/** 只有 IMAGE
	 * 缩略图宽
	 * @return
	 */
	double thumWidth() default 500.0;
	/** FILE
	 *  文件类型 
	 * @return
	 */
	String fileType() default "A";
	/** FILE
	 *  文件路径  指定路径要以/结束  , 根目录为空字符串
	 * @return
	 */
	String filePath() default "/";
	/** 只有 BUTTON
	 *  操作按钮
	 * @return
	 */
	FormButtonMeta[] buttons() default {};
}
