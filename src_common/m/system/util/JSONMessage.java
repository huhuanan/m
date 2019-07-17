package m.system.util;

import java.util.LinkedHashMap;
import java.util.Map;

import m.system.exception.MException;

public class JSONMessage {
	public JSONMessage(){
		jsonMap=new LinkedHashMap<String,Object>();
	}
	public JSONMessage(Map<String,Object> map){
		jsonMap=map;
	}
	private Map<String,Object> jsonMap;
	
	public JSONMessage push(String key,Object value){
		jsonMap.put(key, value);
		return this;
	}
	public Object get(String key){
		return jsonMap.get(key);
	}
	/**
	 * 转换对象为字符串的展现形式.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String toJSONString(){
		StringBuffer sb=new StringBuffer();
		for(String key : jsonMap.keySet()){
			sb.append(",\"").append(key).append("\":");
			Object value=jsonMap.get(key);
			try {
				sb.append(ObjectUtil.toString(value));
			} catch (MException e) {
				sb.append("null");
			}
		}
		return new StringBuffer("{").append(sb.length()>0?sb.substring(1):"").append("}").toString();
	}
	@Override
	public String toString() {
		return this.toJSONString();
	}
	public static void main(String[] a){
		JSONMessage jj=new JSONMessage();
		jj.push("22", "22");
		JSONMessage json=new JSONMessage();
		json.push("aa", "123");
		json.push("bb", 19.2);
		json.push("cc", new Object[]{"11",new String[]{"123","124"}});
		json.push("jj", jj);
		System.out.println(json.toJSONString());
	}
}
