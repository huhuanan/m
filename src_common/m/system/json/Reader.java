package m.system.json;

import java.util.Map;

import m.system.exception.MException;
import m.system.util.GenerateID;

abstract class Reader {

	static String putSubMap(String jsonStr,Map<String,Reader> subMap) throws MException{
		String js=jsonStr.replaceAll("\\\\\"", "<quote>");
		char[] chars=js.toCharArray();
		char left_type=0,right_type=0;
		int start=-1,end=-1,left=0,right=0;
		boolean flag=true;
		for(int i=0;i<chars.length;i++){
			char ch=chars[i];
			if(ch=='"'){
				flag=!flag;
			}
			if(flag){
				if(start==-1&&(ch=='{'||ch=='[')){
					if(ch=='{'){
						left_type='{';
						right_type='}';
					}else if(ch=='['){
						left_type='[';
						right_type=']';
					}
					start=i;
				}
				if(left_type==ch){
					left++;
				}
				if(right_type==ch){
					right++;
				}
				if(left==right&&(ch=='}'||ch==']')){
					end=i;
					break;
				}
			}
		}
		if(start==-1&&end==-1){
			return jsonStr;
		}else{
			String key=new StringBuffer("sub").append(GenerateID.tempKey()).toString();
			String content=js.substring(start, end+1).replaceAll("<quote>", "\\\\\"");
			if(left_type=='{'){
				subMap.put(key, new JsonReader(content));
			}else if(left_type=='['){
				subMap.put(key, new ArrayReader(content));
			}
			return putSubMap(jsonStr.replace(content, key),subMap);
		}
	}
}
