package m.system.json;

import java.util.HashMap;
import java.util.Map;

import m.system.exception.MException;

public class JsonReader extends Reader {
	private String jsonStr;
	private Map<String,Reader> subMap;
	private Map<String,Object> valueMap;
	public JsonReader(String jsonStr) throws MException{
		jsonStr=jsonStr.trim();
		//System.out.println(jsonStr);
		if(jsonStr.indexOf("{")==0&&jsonStr.lastIndexOf("}")==jsonStr.length()-1){
			valueMap=new HashMap<String,Object>();
			subMap=new HashMap<String,Reader>();
			this.jsonStr=putSubMap(jsonStr.substring(1, jsonStr.length()-1),subMap);
		}else{
			throw new MException(this.getClass(),"JSON格式错误!"+jsonStr);
		}
	}
	public void append(JsonReader reader){
		this.jsonStr=new StringBuffer(this.jsonStr).append(",").append(reader.jsonStr).toString();
		this.subMap.putAll(reader.subMap);
	}
	public Object get(String key){
		if(null==valueMap.get(key)){
			Object obj=null;
			String[] strs=this.jsonStr.split(new StringBuffer("\\s*\"{0,0}").append(key).append("\"{0,0}\\s*:+?\\s*").toString());
			if(strs.length!=2){
				strs=this.jsonStr.split(new StringBuffer("\\s*\"{1,1}").append(key).append("\"{1,1}\\s*:+?\\s*").toString());
			}
			if(strs.length>1){
				String str="";
				String[] ss=null;
				boolean isStr=true;
				if(strs[1].charAt(0)=='"'){
					ss=strs[1].split("\"\\s*,\\s*\"{0,1}[^\"]+\"{0,1}\\s*:+?\\s*");
					str=ss[0].substring(1).trim();
					if(ss.length==1){
						str=str.substring(0, str.length()-1);
					}
				}else{
					str=strs[1].split(",\\s*\"{0,1}[^\"]+\"{0,1}\\s*:+?\\s*")[0].trim();
					isStr=false;
				}
				if(null!=subMap.get(str)){
					obj=subMap.get(str);
				}else if(isStr){
					obj=str.replaceAll("\\\\\"", "\"");
				}else{
					try{
						if("".equals(str)||"undefined".equals(str)||"null".equals(str)){
						}else if(str.indexOf(".")!=-1){
							obj=Double.parseDouble(str);
						}else{
							obj=Integer.parseInt(str);
						}
					}catch(Exception e){
						obj=str;
					}
				}
			}
			valueMap.put(key, obj);
			return obj;
		}else{
			return valueMap.get(key);
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,String key){
		return (T)get(key);
	}
	
	@Override
	public String toString() {
		String str=this.jsonStr;
		for(String key : this.subMap.keySet()){
			str=str.replace(key, this.subMap.get(key).toString());
		}
		return new StringBuffer("{").append(str).append("}").toString();
	}
	public static void main(String[] a) throws Exception{
//		String json="{\"source_type\":1,\"source\":\"<a href=\"http://app.weibo.com/t/feed/4J5QJ9\" rel=\"nofollow\">小米手机5</a>\",\"favorited\":false,\"truncated\":false,\"in_reply_to_status_id\":\"\",\"in_reply_to_user_id\":\"\",\"in_reply_to_screen_name\":\"\",\"pic_urls\":[],\"geo\":null,\"annotations\":[{\"shooting\":1,\"place\":{\"lon\":113.62322,\"poiid\":\"8008641010200000000\",\"title\":\"中原区\",\"type\":\"checkin\",\"lat\":34.75463},\"client_mblogid\":\"7b1e1bef-089c-42ef-8055-50d786534528\"},{\"mapi_request\":true}],\"reposts_count\":0,\"comments_count\":0,\"attitudes_count\":0,\"isLongText\":false,\"mlevel\":0,\"visible\":{\"type\":0,\"list_id\":0},\"biz_ids\":[100101],\"biz_feature\":0,\"page_type\":40,\"hasActionTypeCard\":0,\"darwin_tags\":[],\"hot_weibo_tags\":[],\"text_tag_tips\":[],\"userType\":0,\"positive_recom_flag\":0,\"gif_ids\":\"\",\"is_show_bulletin\":2}";
//		json=json.trim();
//		if(json.indexOf("{")==0&&json.lastIndexOf("}")==json.length()-1){
//			json=json.substring(1, json.length()-1);
//
//			System.out.println(json);
//			String[] strs=json.split("\\s*\"{0,1}"+"id"+"\"{0,1}\\s*:+?");
//			if(strs.length>1){
//				for(String str:strs){
//					System.out.println(str);
//				}
//			}
//		}
		JsonReader json=new JsonReader("{\"data\":[{\"content\":\"这是STEAM的规矩！\",\"hashId\":\"A9A6ABA505CC2C59C5ADB5433F9F6A0C\",\"unixtime\":1503632630,\"url1\":\"\",\"url\":\"http://juheimg.oss-cn-hangzhou.aliyuncs.com/joke/201704/24/A9A6ABA505CC2C59C5ADB5433F9F6A0C.png\\\"\"}]}");
		System.out.println(json.get(ArrayReader.class,"data").get(JsonReader.class,0).get("url"));
//		JsonReader json=new JsonReader(new HttpRequestUtil().doGet("http://127.0.0.1:8080/MStudio/pay.jsp"));
//		System.out.println(json.get(String.class,"OrderDetailOids"));
//		System.out.println(jsonReader);  小王是在公司10楼人事部门工作,一个月前,被调到9楼行政部门去了......今天,小王同学电话到人事部门找他
//		System.out.println(jsonReader.get(ArrayReader.class,"name"));
	}
}
