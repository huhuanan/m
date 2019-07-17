package m.common.socket;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import m.common.service.HostInfoService;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.socket.SocketServerEvent;
import m.system.util.ByteUtil;

public class HostSocketServerEvent implements SocketServerEvent {

	public void closeCallback(String ipport) {
		//System.out.println(ipport+"====server_close");
	}

	public void openCallback(String ipport) {
		//System.out.println(ipport+"====server_open");
	}

	public void readCallback(String ipport) {
		//System.out.println(ipport+"====server_read");
	}

	public byte[] readOrReturn(String ipport, byte[] bytes) {
		//if(RuntimeData.getDebug()) System.out.println("接收:"+ByteUtil.toHexString(bytes, " "));
		byte[] newBytes=null;
		try {
			if(!HostSocketUtil.verifyData(bytes)) return null;//校验失败
			if(bytes[0]!=(byte)0x88) return null;
			String ip=ipport.split(":")[0];
			int len=bytes.length;
			byte[] b1=Arrays.copyOfRange(bytes,1,5);//数据
			int num=ByteUtil.toInt(b1);
			byte[] b2=Arrays.copyOfRange(bytes,5,len-1);//数据
			HostInfoService service=RuntimeData.getService(HostInfoService.class);
			if(ip.equals(RuntimeData.getServerIp())){
				service.setHostInfo(ip,new Date(),num,getIncrement(ip),1,1);
				setOtherData(ip,b2);
				newBytes=new byte[]{0x01}; 
			}else{
				service.setHostInfo(ip,new Date(),num,getIncrement(ip),0,0);
				setOtherData(ip,b2);
				newBytes=service.toBytesByHosts(ip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//if(null!=newBytes&&RuntimeData.getDebug())
		//	System.out.println("返回:"+ByteUtil.toHexString(newBytes, " "));
		return newBytes;
	}
	private void setOtherData(String ip,byte[] nb) throws MException{
		HostInfoService service=RuntimeData.getService(HostInfoService.class);
		service.setHostOtherInfo(ip, ByteUtil.toInt(Arrays.copyOfRange(nb, 0, 4)), ByteUtil.toInt(Arrays.copyOfRange(nb, 4, 8)), 
			ByteUtil.toInt(Arrays.copyOfRange(nb, 8, 12)), ByteUtil.toInt(Arrays.copyOfRange(nb, 12, 16)), 
			ByteUtil.toInt(Arrays.copyOfRange(nb, 16, 18)));
	}

	private static Map<String,Integer> ipMap=new HashMap<String,Integer>();
	private synchronized static Integer setIncrement(String ip){
		ipMap.put(ip, ipMap.size()+1);
		return ipMap.get(ip);
	}
	private static int getIncrement(String ip){
		Integer n=ipMap.get(ip);
		if(null==n){
			n=setIncrement(ip);
		}
		return n;
	}
}
