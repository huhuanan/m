package manage.util.page.chart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface ChartXAxis {

	public enum ChartAxisType {//目前只有CATEGORY可用
		VALUE("value"),CATEGORY("category"),TIME("time");
		private String type;
		private ChartAxisType(String type){
			this.type=type;
		}
		@Override
		public String toString() {
			return this.type;
		}
	}
	/**
	 * 查询出来所有的数值作为x
	 * @return
	 */
	String field() default "";
	/**
	 * 坐标轴类型
	 * @return
	 */
	ChartAxisType type() default ChartAxisType.CATEGORY;
	/**
	 * type为time时 有效
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd";
	/**
	 * 数据区间放大
	 * @return
	 */
	boolean dataZoom() default false;
}
