package m.system.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtil {
	public static int toInt(double d){
		return new Double(d).intValue();
	}
	public static int toRoundInt(double d) {
		return round(d,0).intValue();
	}
	/**
	 * 格式化数字
	 * @param d
	 * @param parttern
	 * @return
	 */
	public static String format(Object d,String parttern){
		if(null==d){
			return "";
		}else{
			return getFormatter(parttern).format(d);
		}
	}
	private static DecimalFormat getFormatter(String parttern) {
		return new DecimalFormat(parttern);
	}
	/**
	 * 四舍五入, 7位
	 * @param d
	 * @return
	 */
	public static Double round(Double d){
		return round(d,7);
	}
	/**
	 * 四舍五入
	 * @param d
	 * @param n
	 * @return
	 */
	public static Double round(Double d,Integer n){
		if(null==n) n=10;
		if(null==d){
			return d;
		}else{
			return new BigDecimal(d.toString()).setScale(n,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	/**
	 * 向下取整, 10位
	 * @param d
	 * @return
	 */
	public static Double floor(Double d){
		return floor(d,10);
	}
	/**
	 * 向下取整
	 * @param d
	 * @param n
	 * @return
	 */
	public static Double floor(Double d,Integer n){
		if(null==n) n=10;
		if(null==d){
			return d;
		}else{
			return new BigDecimal(d.toString()).setScale(n,BigDecimal.ROUND_FLOOR).doubleValue();
		}
	}
}
