package m.system.util;

public class ByteUtil {

	private static char[] hex={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	/**
	 * 转会byte为String
	 * @param b
	 * @return
	 */
	public static String toHexString(byte b) {
		return new StringBuffer("").append(hex[(b >> 4) & 0x0f]).append(hex[b & 0x0f]).toString();
	}
	/**
	 * 链接byte转换的String串
	 * @param bs
	 * @param split
	 * @return
	 */
	public static String toHexString(byte[] bs,String split){
		StringBuffer sb=new StringBuffer();
		for(int i=0,len=bs.length;i<len;i++){
			if(i!=0) sb.append(split);
			sb.append(toHexString(bs[i]));
		}
		return sb.toString();
	}
	/**
	 * 补0
	 * @param str
	 * @param len
	 * @return
	 */
	private static String zeroize(String str,int len){
		StringBuffer sb=new StringBuffer(str);
		for(int i=sb.length();i<len;i++){
			sb.insert(0, "0");
		}
		return sb.toString();
	}
	/**
	 * byte转换成2进制
	 * @param b
	 * @return
	 */
	public static String toBitString(byte b){
		String bit=Integer.toString(toInt(b),2);
		return zeroize(bit,8);
	}
	/**
	 * byte数组转换成string
	 * @param bs
	 * @param split
	 * @return
	 */
	public static String toBitString(byte[] bs,String split){
		StringBuffer sb=new StringBuffer();
		for(int i=0,len=bs.length;i<len;i++){
			if(i!=0) sb.append(split);
			sb.append(toBitString(bs[i]));
		}
		return sb.toString();
	}
	/**
	 * byte转换成int
	 * @param b
	 * @return
	 */
	public static int toInt(byte b) {  
	    return b & 0xFF;  
	}
	/**
	 * byte数组转换成int
	 * @param bs
	 * @return
	 */
	public static int toInt(byte[] bs){
		int n=0;
		int len=bs.length;
		if(len>=1) n=toInt(bs[len-1]);
		if(len>=2) n|=bs[len-2]<<8&0xff00;
		if(len>=3) n|=bs[len-3]<<24>>>8;
		if(len>=4) n|=bs[len-4]<<24;
		return n;
	}
	/**
	 * byte数组转换成int链接字符串
	 * @param bs
	 * @param split
	 * @return
	 */
	public static String toIntJoin(byte[] bs,String split){
		StringBuffer sb=new StringBuffer();
		for(int i=0,len=bs.length;i<len;i++){
			if(i!=0) sb.append(split);
			sb.append(toInt(bs[i]));
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param b
	 * @param len
	 * @return
	 */
	public static String toIntString(byte b,int len){
		int n=toInt(b);
		return zeroize(String.valueOf(n),len);
	}
	/**
	 * 
	 * @param bs
	 * @param len
	 * @return
	 */
	public static String toIntString(byte[] bs,int len){
		int n=toInt(bs);
		return zeroize(String.valueOf(n),len);
	}
	/**
	 * 转换两位字符串为byte
	 * @param hax
	 * @return
	 */
	public static byte toByte(String hex){
		return (byte) Integer.parseInt(hex, 16);
	}
	/**
	 * 转换两位字符串数组为byte数组
	 * @param haxs
	 * @return
	 */
	public static byte[] toByte(String[] hexs){
		byte[] bs=new byte[hexs.length];
		for(int i=0,len=hexs.length;i<len;i++){
			bs[i]=toByte(hexs[i]);
		}
		return bs;
	}
	/**
	 * int转换成固定长度的byte数组
	 * @param n
	 * @param len
	 * @return
	 */
	public static byte[] toBytes(int n,int len){
		byte[] bs=new byte[len];
		for(int i=0;i<len;i++){
			bs[i]=(byte) (n>>((len-1-i)*8));
		}
		return bs;
	}
	public static void main(String[] a){
//		System.out.println(toInt(new byte[]{(byte) 0xFF}));
//		System.out.println(toInt(new byte[]{(byte) 0x70,(byte) 0xff,(byte) 0xff,(byte) 0xfe}));
//		System.out.println((0x1B<<8)+0x58); //1B 58 = 7000
//		System.out.println(Integer.toHexString(Integer.MAX_VALUE));
//		System.out.println(toInt(toByte("FF")));
//		System.out.println(toHexString(toBytes(12343,2)," "));
//		System.out.println(toBitString((byte) 0x99));
//		System.out.println(toBitString(new byte[]{(byte) 0x99,0x23}," "));
//		System.out.println(toIntString(new byte[]{0x01,0x01},4));
//		for(int i=0;i<521;i++){
//			System.out.println(toHexString((byte) i));
//		
//		System.out.println(toInt((byte)0xff));
//		System.out.println(toHexString(toBytes(7000, 2), ""));
//		System.out.println(toInt(new byte[]{(byte) 0x1b,(byte) 0x58}));
//		System.out.println(toInt(toBytes(2147483647, 4)));
//		System.out.println(toHexString(toBytes(2147483647, 4), " "));
//		System.out.println(Integer.MAX_VALUE);
	}
}
