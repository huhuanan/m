package manage.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.json.JsonReader;
import m.system.util.StringUtil;
import manage.model.SystemInfo;
import manage.service.SystemInfoService;

public class SmsUtil {
	
	private static SmsSingleSender sender=null;
	private static SystemInfo bs=null;
	public static void init(){
		sender=null;
		bs=null;
	}
	private static SystemInfo getSystemInfo() throws MException, Exception{
		if(null==bs){
			bs=RuntimeData.getService(SystemInfoService.class).getUniqueModel();
		}
		return bs;
	}
	private static SmsSingleSender getSender(SystemInfo bs) throws MException, Exception{
		if(null==sender){
			if(StringUtil.isSpace(bs.getSmsAppId())) throw new MException(SmsUtil.class,"未设置短信AppId");
			if(StringUtil.isSpace(bs.getSmsAppKey())) throw new MException(SmsUtil.class,"未设置短信AppKey");
			sender=new SmsSingleSender(Integer.parseInt(bs.getSmsAppId()), bs.getSmsAppKey());
		}
		return sender;
	}
	private static Map<String,String> verifyMap=new HashMap<String,String>();
	private static Map<String,Long> ipLongMap=new HashMap<String,Long>();
	/**
	 * 发送验证短信
	 * @param phone
	 * @throws MException
	 * @throws Exception
	 */
	public static String sendVerify(String phone,String ip) throws MException, Exception{
		if(!StringUtil.isMobileNO(phone)) throw new MException(SmsUtil.class,"手机号错误");
		Long num=ipLongMap.get(ip);
		if(null!=num&&new Date().getTime()-num<60000){
			throw new MException(SmsUtil.class,"获取验证码太快了!");
		}
		Random ne=new Random();
        String code=String.valueOf(ne.nextInt(99999-10000+1)+10000);
		SystemInfo sys=getSystemInfo();
		if(sys.getSmsDebug().equals("Y")){
			verifyMap.put(phone, code);
			ipLongMap.put(ip, new Date().getTime());
			return code;
		}else{
			SmsSingleSender sender=getSender(sys);
			if(StringUtil.isSpace(sys.getSmsVerifyTid())) throw new MException(SmsUtil.class,"未设置短信验证模板id");
			SmsSingleSenderResult result = sender.sendWithParam("86", phone,Integer.parseInt(sys.getSmsVerifyTid()), new String[]{code}, sys.getSmsSign(), "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
			JsonReader json=new JsonReader(result.toString());
			if(RuntimeData.getDebug()) System.out.print(result);
			if(json.get(Integer.class,"result")==0){
	        System.out.println(code);
				verifyMap.put(phone, code);
				ipLongMap.put(ip, new Date().getTime());
			}else{
				throw new MException(SmsUtil.class,json.get(String.class,"errmsg"));
			}
			return "发送成功!";
		}
	}
	/**
	 * 操作成功后清除
	 * @param phone
	 */
	public static void clearVerify(String phone){
		verifyMap.remove(phone);
	}
	/**
	 * 验证短信验证码
	 * @param phone
	 * @param code
	 * @throws MException
	 */
	public static void checkVerify(String phone,String code) throws MException{
		String tt=verifyMap.get(phone);
		if(null==tt){
			throw new MException(SmsUtil.class,"请先发送验证短信");
		}else if(tt.equals(code)){
			//操作成功后调用clearVerify
		}else{
			throw new MException(SmsUtil.class,"短信验证码错误");
		}
	}
}
