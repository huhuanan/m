package manage.util.page.chart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.button.ButtonMeta;
import manage.util.page.query.QueryMeta;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface ActionChartMeta {

	/**
	 * 标题
	 * @return
	 */
	String title() default "";
	/**
	 * 模型类全名
	 * @return
	 */
	String modelClass();
	/**
	 * 数据接口地址
	 * @return
	 */
	String dataUrl();
	/**
	 * table的高度
	 * @return
	 */
	int tableHeight() default 300;
	/**
	 * 混合查询属性
	 * @return
	 */
	String searchField() default "";
	/**
	 * 混合查询提示
	 * @return
	 */
	String searchHint() default "";
	/**
	 * 按钮
	 * @return
	 */
	ButtonMeta[] buttons() default {};
	/**
	 * 查询条件
	 * @return
	 */
	QueryMeta[] querys() default {};
	/**
	 * 数据序列
	 * @return
	 */
	ChartSeries[] series();
	/**
	 * X轴属性
	 * @return
	 */
	ChartXAxis xAxis();
	
}
