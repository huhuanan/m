package manage.util.page.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中 
public @interface ActionTableColMeta {

	public enum TableColSort {
		NONE(""),ASC("asc"),DESC("desc");
		private String sort;
		private TableColSort(String sort){
			this.sort=sort;
		}
		public String toString() {
			return this.sort;
		}
	}
	//可选值有：normal（常规列，无需设定）、checkbox（复选框列）、space（空列）、numbers（序号列）  
	public enum TableColType {
		NORMAL(""),HTML("html"),CHECKBOX("selection"),INDEX("index"),
		//status(修改状态的列),color(颜色列)
		STATUS("status"),COLOR("color");
		private String type;
		private TableColType(String type){
			this.type=type;
		}
		public String toString(){
			return this.type;
		}
	}
	//合计类型, sum合计
	public enum TableCountType{
		NONE,SUM;
	}
	TableColType type() default TableColType.NORMAL;
	/**
	 * 字段名称
	 * @return
	 */
	String field();
	/**
	 * 存在的话,替换field的结果
	 * @return
	 */
	String fieldExpression() default "";
	/**
	 * 字段标题
	 * @return
	 */
	String title();
	/**
	 * 组标题 第一个在最顶层
	 * @return
	 */
	String[] groupTitle() default {};
	/**
	 * 列宽度
	 * @return
	 */
	int width() default 90;
	/**
	 * 对齐方式
	 * @return
	 */
	String align() default "";
	/**
	 * true:文本将不换行，超出部分显示为省略号
	 * @return
	 */
	boolean ellipsis() default false;
	/**
	 * 是否排序列
	 * @return
	 */
	boolean sort() default false;//排序
	/**
	 * 默认排序,只能设置一个
	 * @return
	 */
	TableColSort initSort() default TableColSort.NONE;
	/**
	 * 是否固定列 left|right
	 * @return
	 */
	String fixed() default "";//固定
	/** format 先判断
	 * 日期格式化
	 * @return
	 */
	String dateFormat() default "";
	/** format 
	 * 数字格式化
	 * @return
	 */
	String numberFormat() default "";
	/** 设置数字格式化有效
	 * 合计类型
	 * @return
	 */
	TableCountType countType() default TableCountType.NONE;
	/**
	 * 字典类型格式化
	 * @return
	 */
	String dictionaryType() default "";
	/** type=status
	 * 权限字符串
	 * @return
	 */
	String power() default "";
	/**
	 * 值对应的显示
	 * @return
	 */
	TableColData[] colDatas() default {};
	/**
	 * 值对应的css样式                         ------ 需改进
	 * @return
	 */
	TableColStyle[] colStyles() default {};
	/**
	 * 按钮
	 * @return
	 */
	ButtonMeta[] buttons() default {};
	/**
	 * 列链接事件 和按钮相同
	 * @return
	 */
	TableColLink link() default @TableColLink(event = ButtonEvent.OPEN, url = "");
}
