package m.system.json;

import java.util.HashMap;
import java.util.Map;

import m.system.exception.MException;

public class ArrayReader extends Reader {
	private String arrayStr;
	private Map<String,Reader> subMap;
	private Map<Integer,Object> valueMap;
	public ArrayReader(String arrayStr) throws MException{
		arrayStr=arrayStr.trim();
		if(arrayStr.indexOf("[")==0&&arrayStr.lastIndexOf("]")==arrayStr.length()-1){
			valueMap=new HashMap<Integer,Object>();
			subMap=new HashMap<String,Reader>();
			this.arrayStr=putSubMap(arrayStr.substring(1, arrayStr.length()-1),subMap);
		}else{
			throw new MException(this.getClass(),"ARRAY格式错误!"+arrayStr);
		}
	}

	public void append(ArrayReader reader){
		this.arrayStr=new StringBuffer(this.arrayStr).append(",").append(reader.arrayStr).toString();
		this.subMap.putAll(reader.subMap);
	}
	private void pushValueMap(int index,int start,int end){
		Object obj=null;
		if(start==0&&end==0){
		}else{
			String str;
			if(end>=start){
				str=this.arrayStr.substring(start, end);
			}else{
				str=this.arrayStr.substring(start);
			}
			str=str.trim();
			if(null!=subMap.get(str)){
				obj=subMap.get(str);
			}else if("".equals(str)||"undefined".equals(str)||"null".equals(str)){
				obj="";
			}else if(str.indexOf("\"")==0&&str.lastIndexOf("\"")==str.length()-1){
				obj=str.substring(1, str.length()-1).replaceAll("\\\\\"", "\"");
			}else{
				try{
					if(str.indexOf(".")!=-1){
						obj=Double.parseDouble(str);
					}else{
						obj=Integer.parseInt(str);
					}
				}catch(Exception e){
					obj=str;
				}
			}
		}
		if(null!=obj)
			valueMap.put(index, obj);
	}
	public Object get(int index){
		if(index<0) return null;
		if(null==valueMap.get(index)&&valueMap.size()==0){
			char[] chars=this.arrayStr.toCharArray();
			int num=0,start=0,end=0,left=0,right=0;
			for(int i=0;i<chars.length;i++){
				char ch=chars[i];
				if(ch==','&&left==right){
					end=i;
					pushValueMap(num,start,end);
					num++;
					start=i+1;
				}
				if((ch=='"'&&left==right)||ch=='{'||ch=='['){
					left++;
				}else if((ch=='"'&&left!=right)||ch=='}'||ch==']'){
					right++;
				}
			}
			if(start==0&&end==0&&chars.length!=0){
				end=chars.length;
				pushValueMap(num,start,end);
			}else if(start>end){
				end=chars.length;
				pushValueMap(num,start,end);
			}
		}
		Object obj=valueMap.get(index);
		if(null!=obj&&null!=subMap.get(obj.toString())){
			return subMap.get(obj.toString());
		}
		return obj;
	}
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> clazz,int index){
		return (T)get(index);
	}
	public int size(){
		get(0);
		return valueMap.size();
	}
	
	@Override
	public String toString() {
		String str=this.arrayStr;
		for(String key : this.subMap.keySet()){
			str=str.replace(key, this.subMap.get(key).toString());
		}
		return new StringBuffer("[").append(str).append("]").toString();
	}
}
