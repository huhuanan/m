package manage.util.page.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface QueryMeta {
	public enum QueryType {
		HIDDEN,TEXT,SELECT,INT_RANGE,DOUBLE_RANGE,DATE_RANGE
	}
	/**
	 * 对应字段名
	 * @return
	 */
	String field();
	/**
	 * 参数中文名称
	 * @return
	 */
	String name();
	/**
	 * 条件默认值
	 * @return
	 */
	String value() default "";
	/** TEXT,SELECT
	 * 是否使用模糊比较方式 
	 * @return
	 */
	boolean likeMode() default false;
	/**
	 * 查询条件类型
	 * @return
	 */
	QueryType type();
	/**
	 * 宽度
	 * @return
	 */
	int width() default 200;
	/**
	 * 提示
	 * @return
	 */
	String hint() default "";
	/** SELECT
	 * select的查询  优先
	 * @return
	 */
	QuerySelectMeta querySelect() default @QuerySelectMeta(modelClass = "", title = "", value = "");
	/** SELECT CHECKBOX RADIO
	 * 字典类型 其次
	 * @return
	 */
	String dictType() default "";
	/** SELECT
	 * select的数据  --
	 * @return
	 */
	SelectDataMeta[] querySelectDatas() default {};
	/** SELECT
	 * 多属性值 ,号分割
	 * @return
	 */
	boolean muchValue() default false;
	/** HIDDEN 不支持
	 * 输入后清除的form字段 执行对于字段的其他字段填充
	 * @return
	 */
	String clearField() default "";
	/** 只有 SELECT CHECKBOX RADIO 支持
	 * 其他字段填充,用其他字段结合起来填充
	 * @return
	 */
	LinkFieldMeta linkField() default @LinkFieldMeta();
	/** DATE_RANGE
	 * 时间格式
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd";
}
