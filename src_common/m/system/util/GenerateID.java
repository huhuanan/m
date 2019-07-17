package m.system.util;

import java.text.DateFormat;
import java.util.Date;

import m.common.service.HostInfoService;
import m.system.RuntimeData;
import m.system.exception.MException;
/**
 * 调用方法,  获得20位长度的主键
 *  GenerateID.generatePrimaryKey()
 * @author admin
 *
 */
public class GenerateID {
	private final static char[] chars=new char[]{'0','1','2','3','4','5','6','7','8','9',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static DateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static long lastTime=0l;
	private static long index=36;
	private static long pi=36;
	private static long no=0;
	private static void init(){
		long time=new Date().getTime();
		if(time!=lastTime){
			lastTime=time;
			index=36;
			pi=36;
			no=0;
		}
	}
	private static HostInfoService service;
	private static char getUnionKey(){
		if(null==service){
			try {
				service=RuntimeData.getService(HostInfoService.class);
			} catch (MException e) {
				return '0';
			}
		}
		return chars[service.getCurrentOid()];
	}
	/**
	 * 获得主键 数据库唯一键
	 * @return
	 */
	public synchronized static String generatePrimaryKey(){
		init();
		index=index+1;
		StringBuffer sb=new StringBuffer();
		toKey(sb,index);
		sb.insert(0,getUnionKey());
		toKey(sb,lastTime-913543421221l);
        return sb.insert(0,"I").toString();
	}
	/**
	 * 多服务器不唯一,数据库唯一主键不可使用
	 * @return
	 */
	public synchronized static String tempKey(){
		init();
		pi=pi+1;
		StringBuffer sb=new StringBuffer();
		toKey(sb,pi);
		toKey(sb,lastTime-913543421221l);
	    return sb.insert(0,"P").toString();
	}
	/**
	 * 单号 20位
	 * @return
	 */
	public synchronized static String generatePrimaryNo(){
		init();
		no=no+1;
		StringBuffer sb=new StringBuffer(format.format(new Date()));
		sb.append(getUnionKey());
		if(no<10){
			sb.append("0").append(no);
		}else if(no<100){
			sb.append(no);
		}else{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
			return generatePrimaryNo();
		}
		return sb.toString();
	}
	private static void toKey(StringBuffer sb,long time){
		sb.insert(0,chars[(int) (time%chars.length)]);
		if(time/chars.length>0){
			toKey(sb,time/chars.length);
		}
	}
	public static void main(String[] s){
		long d=new Date().getTime();
//		generatePrimaryKeys(1000000);
		
		for(int i=0;i<10000;i++){
			System.out.println(tempKey());
		}
		System.out.println(new Date().getTime()-d);
//		Arrays.sort(chars);
//		for(char str : chars) {  
//		    System.out.println(str);  
//		}  
	}
}
