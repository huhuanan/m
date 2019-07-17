package m.system.db;

import java.util.Map;

public class DataRow {
	private Map<String,Object> map;
	protected DataRow(Map<String,Object> map){
		this.map=map;
	}
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,String name){
		return (T) map.get(name);
	}
	public Object get(String name){
		return map.get(name);
	}
	public int size(){
		return map.size();
	}
}
