package m.system.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public final static String YYYY_MM_DD = "yyyy-MM-dd";
	public final static String YYYY_MM = "yyyy-MM";
	public final static String HH_MM_SS = "HH:mm:ss";
	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static String toFormatShow(Date date){
		if(null==date) return "";
		StringBuffer sb=new StringBuffer();
		Calendar newDate=Calendar.getInstance();
		newDate.setTime(new Date());
		Calendar thenDate=Calendar.getInstance();
		thenDate.setTime(date);
		if(newDate.get(Calendar.YEAR)==thenDate.get(Calendar.YEAR)){
			if(newDate.get(Calendar.MONTH)==thenDate.get(Calendar.MONTH)){
				if(newDate.get(Calendar.DATE)==thenDate.get(Calendar.DATE)){
					sb.append("今天");
				}else if(newDate.get(Calendar.DATE)-thenDate.get(Calendar.DATE)==1){
					sb.append("昨天");
				}else if(newDate.get(Calendar.DATE)-thenDate.get(Calendar.DATE)==2){
					sb.append("前天");
				}else{
					sb.append(thenDate.get(Calendar.MONTH)+1).append("月").append(thenDate.get(Calendar.DATE)).append("日");
				}
			}else{
				sb.append(thenDate.get(Calendar.MONTH)+1).append("月").append(thenDate.get(Calendar.DATE)).append("日");
			}
			sb.append(" ").append(format(date,"HH:mm"));
		}else{
			sb.append(thenDate.get(Calendar.YEAR)).append("年").append(thenDate.get(Calendar.MONTH)+1).append("月").append(thenDate.get(Calendar.DATE)).append("日");
		}
		
		return sb.toString();
	}
	public static String toFormatInput(Date date){
		if(null==date) return "";
		return new StringBuffer(format(date,"yyyy-MM-dd")).append("T").append(format(date,"HH:mm")).toString();
	}
	/**
	 * 是否到期,小于当前时间返回true , 大于等于当前时间返回false
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Boolean isExpire(Date date,String pattern){
		if(format(new Date(),pattern).compareTo(format(date,pattern))<=0){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 获取 当前日期yyyy-MM-dd格式的字符串
	 * @return
	 */
	public static String formatNow(){
		return format(new Date());
	}
	/**
	 * 日期格式化－将Date类型的日期格式化为String型 默认yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String format(Date date){
		return format(date,YYYY_MM_DD);
	}
	/**
	 * 日期格式化－将Date类型的日期格式化为String型
	 * @param date 待格式化的日期
	 * @param pattern 时间样式
	 * @return 一个被格式化了的String日期
	 */
	public static String format(Date date, String pattern) {
		if (date == null)
			return "";
		else
			return getFormatter(pattern).format(date);
	}

	/**
	 * 把字符串日期默认转换为yyyy-mm-dd格式的Data对象
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date format(String strDate) {
		return format(strDate, YYYY_MM_DD);
	}
	/**
	 * 把字符串日期默认转换为pattern格式的Data对象
	 * @param strDate
	 * @param pattern
	 * @return
	 */
	public static Date format(String strDate,String pattern){
		Date d = null;
		if (strDate == "")
			return null;
		else
			try {
				d = getFormatter(pattern).parse(strDate);
			} catch (ParseException e) {
				return null;
			}
		return d;
	}
	/**
	 * 获取日期的第一天
	 * @param date
	 * @return
	 */
	public static Date getFirstDay(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		return calendar.getTime();
	}
	/**
	 * 获取当天最早时刻
	 * @param date
	 * @return
	 */
	public static Date getStartDay(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}
	/**
	 * 获取当月最早时刻
	 * @param date
	 * @return
	 */
	public static Date getStartMonth(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}
	/**
	 * 获取当天最晚时刻
	 * @param date
	 * @return
	 */
	public static Date getEndDay(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}
	/**
	 * 获取当月最晚时刻
	 * @param date
	 * @return
	 */
	public static Date getEndMonth(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		calendar.add(Calendar.MONTH,1);
		calendar.add(Calendar.DATE,-1);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}
	/**
	 * 获取日期的最后一天
	 * @param date
	 * @return
	 */
	public static Date getLastDay(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE,1);
		calendar.add(Calendar.MONTH,1);
		calendar.add(Calendar.DATE,-1);
		return calendar.getTime();
	}
	/**
	 * 获取当前日期的月份范围
	 * @return
	 */
	public static Date[] getDateRange(Date date){
		return new Date[]{getFirstDay(date),getLastDay(date)};
	}
	public static Date[] getYearRange(Date date){
		String year=format(date, "yyyy");
		return new Date[]{
				format(year+"-01-01 00:00:00",YYYY_MM_DD_HH_MM_SS),
				format(year+"-12-31 23:59:59",YYYY_MM_DD_HH_MM_SS)
		};
	}
	/**
	 * 获取上个月
	 * @param date
	 * @return
	 */
	public static Date getLastMonth(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,-1);
		return calendar.getTime();
	}
	/**
	 * 获取下个月
	 * @param date
	 * @return
	 */
	public static Date getNextMonth(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,1);
		return calendar.getTime();
	}
	/**
	 * 获取两个时间之间的月差距
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMonthGap(Date date1,Date date2){
		Calendar c1=Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2=Calendar.getInstance();
		c2.setTime(date2);
		return (c1.get(Calendar.YEAR)*12+c1.get(Calendar.MONTH))-(c2.get(Calendar.YEAR)*12+c2.get(Calendar.MONTH));
	}
	/**
	 * 获取 添加的月份后
	 * @param date
	 * @param i 
	 * @return
	 */
	public static Date getAddMonth(Date date,int i){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,i);
		return calendar.getTime();
	}
	/**
	 * 获取 添加的天数后
	 * @param date
	 * @param i 
	 * @return
	 */
	public static Date getAddDate(Date date,int i){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,i);
		return calendar.getTime();
	}
	/**
	 * 是否包含在区间内
	 * @param date
	 * @param start_date
	 * @param end_date
	 * @param date_format
	 * @return
	 */
	public static boolean isContain(Date date,Date start_date,Date end_date,String date_format){
	    if(format(start_date,date_format).compareTo(format(date,date_format))<1
				&&format(end_date,date_format).compareTo(format(date,date_format))>-1){
	    	return true;
	    }else{
	    	return false;
	    }
	}
	/**
	 * 判断当前时间是否包含在区间内
	 * @param start_date
	 * @param end_date
	 * @param date_format
	 * @return
	 */
	public static boolean isContain(Date start_date,Date end_date,String date_format){
		return isContain(new Date(),start_date, end_date, date_format);
	}
	/**
	 * 获取时间的具体值
	 * @param date
	 * @param ci Calendar类里的常量
	 * @return
	 */
	public static int get(Date date,int ci){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(ci);
	}
	/**
	 * 添加时间的具体值
	 * @param date
	 * @param ci Calendar类里的常量
	 * @param num 增量
	 * @return
	 */
	public static Date add(Date date,int ci,int num){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(ci, num);
		return calendar.getTime();
	}
	/**
	 * 设置时间的具体值
	 * @param date
	 * @param ci Calendar类里的常量
	 * @param num 设置量
	 * @return
	 */
	public static Date set(Date date,int ci,int num){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(ci, num);
		return calendar.getTime();
	}
	private static SimpleDateFormat getFormatter(String parttern) {
		return new SimpleDateFormat(parttern);
	}
	public static void main(String[] a ){
		System.out.println(getAddDate(new Date(), 10));
//		System.out.println("2015-06".compareTo("2015-05"));  //1
//		System.out.println("2015-06".compareTo("2015-06"));  //0
//		System.out.println("2015-06".compareTo("2015-07"));  //-1
//		System.out.println(toFormatShow(format("2017-04-16 10:20", "yyyy-MM-dd HH:mm")));
//		System.out.println(toFormatShow(format("2017-04-15 09:10", "yyyy-MM-dd HH:mm")));
//		System.out.println(toFormatShow(format("2017-04-14 19:01", "yyyy-MM-dd HH:mm")));
//		System.out.println(toFormatShow(format("2017-04-10 19:01", "yyyy-MM-dd HH:mm")));
//		System.out.println(toFormatShow(format("2017-03-14 19:01", "yyyy-MM-dd HH:mm")));
//		System.out.println(toFormatShow(format("2016-03-14 19:01", "yyyy-MM-dd HH:mm")));
		System.out.println(format(getEndDay(getLastDay(DateUtil.getAddMonth(new Date(), 24))), "yyyy-MM-dd HH:mm:ss.SSS"));
	}
}
