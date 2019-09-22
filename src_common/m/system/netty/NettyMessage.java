package m.system.netty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import m.system.util.JSONMessage;

public class NettyMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8128L;

	private Map<String,Object> jsonMap=new LinkedHashMap<String,Object>();
	public NettyMessage push(String key,Object value){
		jsonMap.put(key, value);
		return this;
	}
	public Object get(String key){
		return jsonMap.get(key);
	}
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,String key){
		Object obj=get(key);
		if(null!=obj) {
			return (T)obj;
		}else {
			return null;
		}
	}
	@Override
	public String toString() {
		return new JSONMessage(jsonMap).toJSONString();
	}
	public Map<String, Object> getJsonMap() {
		return jsonMap;
	}
	public void setJsonMap(Map<String, Object> jsonMap) {
		this.jsonMap = jsonMap;
	}
	
}
