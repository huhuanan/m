package manage.util.page.chart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface ChartSeries {

	public enum ChartSeriesType {
		LINE("line"),BAR("bar"),BARLINE("bar_line"),SMOOTHLINE("smooth_line");
		private String type;
		private ChartSeriesType(String type){
			this.type=type;
		}
		@Override
		public String toString() {
			return this.type;
		}
	}
	/**
	 * 数据名称
	 * @return
	 */
	String name();
	/**
	 * 字段名称
	 * @return
	 */
	String field();
	/**
	 * 图表类型
	 * @return
	 */
	ChartSeriesType type() default ChartSeriesType.LINE;
	/**
	 * yAxisIndex  0 or 1
	 * @return
	 */
	int index() default 0;
	/**
	 * 查询条件
	 * @return
	 */
	SeriesConditionMeta[] conditions() default {};
	/**
	 * 显示最大值和最小值
	 * @return
	 */
	boolean markPoint() default false;
	/**
	 * 显示平均值
	 * @return
	 */
	boolean markLine() default false;
}
