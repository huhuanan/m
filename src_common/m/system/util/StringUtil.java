package m.system.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isSpace(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}
	/**
	 * 返回不为空的字符串, 去首尾空格
	 * @param str
	 * @return
	 */
	public static String noSpace(String str) {
		if (isSpace(str))
			return "";
		else
			return str.trim();
	}
	/**
	 * 转化 String类型, 
	 *   包括 “"” “\r\n” “\”
	 * @param value
	 * @return
	 */
	public static String conver2JS(String value){
		if(null==value){
			return null;
		}else{
			return  value.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r","\\\\r").replaceAll("\n", "\\\\n");
		}
	}
	/**
	 * 转换成MD5
	 * @param password
	 * @return
	 */
	public static String toMD5(String str) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	private final static int[] li_SecPosValue = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };  
	private final static String[] lc_FirstLetter = { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "W", "X", "Y", "Z" }; 
	public static String toFirstLetter(String str) throws UnsupportedEncodingException{
		StringBuffer sb=new StringBuffer();
		for(char c: str.toCharArray()){
			String chinese =new String(new String(new char[]{c}).getBytes("GBK"),"iso8859-1");
			if(chinese .length()>1){
				int li_SectorCode = (int) chinese.charAt(0); // 汉字区码  
				int li_PositionCode = (int) chinese.charAt(1); // 汉字位码  
				li_SectorCode = li_SectorCode - 160;  
				li_PositionCode = li_PositionCode - 160;  
				int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码  
				if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {  
					for (int i = 0; i < 23; i++) {  
						if (li_SecPosCode >= li_SecPosValue[i] && li_SecPosCode < li_SecPosValue[i + 1]) {  
							chinese = lc_FirstLetter[i];  
							break;  
						}  
					}  
				} else {  // 非汉字字符,如图形符号或ASCII码  
					chinese = new String(chinese.getBytes("iso8859-1"),"GBK");  
					chinese = chinese.substring(0, 1);  
				}
			}
			sb.append(chinese);
		}
		return sb.toString();
	}
	/**　　
	 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 * 联通：130、131、132、152、155、156、185、186
	 * 电信：133、153、180、189、（1349卫通）
	 * */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	public static void main(String[] a) throws UnsupportedEncodingException{
//		String s="是否23的能d就看d上";
//		System.out.println(s);
//		System.out.println(toFirstLetter(s));
		System.out.println(isMobileNO("13344556677"));
	}
}
