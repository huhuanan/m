package m.system.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSet {
	private List<Object> list;
	protected DataSet(List<Object> list){
		this.list=list;
	}
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,int i,String name){
		return (T) ((Map<String,Object>)list.get(i)).get(name);
	}
	@SuppressWarnings("unchecked")
	public Object get(int i,String name){
		return ((Map<String,Object>)list.get(i)).get(name);
	}
	public int size(){
		return list.size();
	}
	@SuppressWarnings("unchecked")
	public List<DataRow> rows(){
		List<DataRow> rowlist=new ArrayList<DataRow>();
		for(Object map : list){
			rowlist.add(new DataRow((Map<String,Object>)map));
		}
		return rowlist;
	}
	public List<Object> toArray(){
		return this.list;
	}
}
