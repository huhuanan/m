package m.system.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import m.common.action.Action;
import m.system.RuntimeData;
import m.system.exception.MException;

public class ActionUtil {
	public static Map<String,File> fillInFileItemStream(Action action,HttpServletRequest request) throws MException{
		try{
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			Map<String,File> fileMap=new HashMap<String, File>();
			String fPath=new StringBuffer(RuntimeData.getFilePath()).append("tmp/").toString();
			String wPath=RuntimeData.getWebPath();
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    String name = item.getFieldName();
				String fName=new StringBuffer(GenerateID.tempKey()).append("_").append(item.getName()).toString();
			    File f=FileUtil.getFile(wPath+fPath+fName);
			    FileUtil.writeFile(f.getPath(), item.openStream());
			    fileMap.put(name, f);
			}
			action.setFileMap(fileMap);
			return fileMap;
		} catch (FileUploadException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Object getInAttribute(Object action,String paramName) throws ClassNotFoundException, MException{
		String[] paramNames=paramName.split("\\.");
		if(paramNames.length>1&&(paramName.indexOf("[")==-1||paramName.indexOf("[")>paramName.indexOf("."))){
			Object obj=getQuoteAttribute(action, paramNames[0]);
			if(null!=obj){
				return getInAttribute(obj, paramName.substring(paramName.indexOf(".")+1));
			}else{
				return null;
			}
		}else{
			Object obj=getValueAttribute(action, paramName);
			if(obj instanceof Double){
				return new BigDecimal((Double)obj).toPlainString();
			}else{
				String str=ObjectUtil.toString(obj);
				return "null".equals(str)?"\"\"":str;
			}
		}
	}
	public static Object getInAttributeByArray(Object action,String paramName) throws ClassNotFoundException, MException{
		String str=getInAttribute(action,paramName).toString();
		if(!StringUtil.isSpace(str)){
			if(str.indexOf("\"")==0) str=str.substring(1);
			if(str.lastIndexOf("\"")==str.length()-1) str=str.substring(0,str.length()-1);
			String[] arr=str.split(",");
			StringBuffer sb=new StringBuffer();
			for(int i=0,len=arr.length;i<len;i++){
				if(i!=0)sb.append(",");
				sb.append("\"").append(arr[i]).append("\"");
			}
			return sb.toString();
		}
		return "";
	}
	public static void fillInAttribute(Object action,String paramName,String paramValue) throws MException, ClassNotFoundException{
		String[] paramNames=paramName.split("\\.");
		if(paramNames.length>1&&(paramName.indexOf("[")==-1||paramName.indexOf("[")>paramName.indexOf("."))){
			Object obj=getQuoteAttribute(action, paramNames[0]);
			if(null!=obj){
				fillInAttribute(obj, paramName.substring(paramName.indexOf(".")+1), paramValue);
			}
		}else if(paramName.length()-2!=-1&&paramName.indexOf("[]")==paramName.length()-2){
			setArrayAttribute(action, paramName.substring(0, paramName.length()-2), paramValue);
		}else{
			setValueAttribute(action, paramName, paramValue);
		}
	}
	@SuppressWarnings("unchecked")
	private static Object getQuoteAttribute(Object action,String paramName) throws MException, ClassNotFoundException{
		int n=paramName.indexOf("[");
		if(n>=0){
			String key=paramName.substring(n+1, paramName.indexOf("]"));
			Object[] object=getObjectAttribute(action, paramName.substring(0, paramName.indexOf("[")));
			if(null!=object){
				try{
					int intkey=Integer.parseInt(key);
					List<Object> list=(List<Object>)object[1];
					supportList(list,intkey);
					if(list.get(intkey)==null){
						list.set(intkey, ClassUtil.newInstanceGenerics((Field)object[0], 0));
					}
					return list.get(intkey);
				}catch(NumberFormatException e){
					Map<String,Object> map=(Map<String,Object>)object[1];
					if(key.indexOf("'")==0&&key.lastIndexOf("'")==key.length()-1){
						key=key.substring(1,key.length()-2);
					}
					if(map.get(key)==null){
						map.put(key, ClassUtil.newInstanceGenerics((Field)object[0], 1));
					}
					return map.get(key);
				}
			}
		}else{
			Object[] object=getObjectAttribute(action, paramName);
			if(null!=object){
				return object[1];
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private static Object[] getObjectAttribute(Object action,String paramName) throws MException, ClassNotFoundException{
		Field field=ClassUtil.getDeclaredField(action.getClass(),paramName);
		if(null!=field){
			Object obj=ClassUtil.getFieldValue(action, field.getName());
			if(null==obj){
				if(field.getType().equals(List.class)){
					obj=new ArrayList();
				}else if(field.getType().equals(Map.class)){
					obj=new HashMap();
				}else{
					obj=ClassUtil.newInstance(field.getType());
				}
				ClassUtil.setFieldValue(action, field.getName(), obj);
			}
			return new Object[]{field,obj};
		}else{
			return null;
		}
	}
	private static void setArrayAttribute(Object action,String paramName,Object paramValue) throws MException{
		Field field=ClassUtil.getDeclaredField(action.getClass(),paramName);
		if(null!=field){
			Class<?> clazz=field.getType().getComponentType();
			String[] arr=paramValue.toString().split(",");
			Object obj=Array.newInstance(field.getType().getComponentType(), arr.length);
			for(int i=0;i<arr.length;i++){
				Array.set(obj, i, ObjectUtil.convert(clazz, arr[i]));
			}
			ClassUtil.setFieldValue(action, paramName, obj);
		}
	}
	@SuppressWarnings("unchecked")
	private static void setValueAttribute(Object action,String paramName,Object paramValue) throws MException, ClassNotFoundException{
		int n=paramName.indexOf("[");
		if(n>=0){
			Object[] object=getObjectAttribute(action, paramName.substring(0, paramName.indexOf("[")));
			if(null!=object){
				setMapAttribute(object[1], paramName.substring(n), paramValue);
			}
		}else{
			ClassUtil.setFieldValue(action, paramName, paramValue);
		}
	}
	@SuppressWarnings("unchecked")
	private static Object getValueAttribute(Object action,String paramName) throws MException, ClassNotFoundException{
		int n=paramName.indexOf("[");
		if(n>=0){
			Object[] object=getObjectAttribute(action, paramName.substring(0, paramName.indexOf("[")));
			if(null!=object){
				return getMapAttribute(object[1], paramName.substring(n));
			}else{
				return null;
			}
		}else{
			return ClassUtil.getFieldValue(action, paramName);
		}
	}
	private static Object setMapAttribute(Object map,String paramName,Object paramValue){
		String key=paramName.substring(paramName.indexOf("[")+1, paramName.indexOf("]"));
		String fn=paramName.substring(paramName.indexOf("]")+1);
		if(null!=map){
			try{
				int intkey=Integer.parseInt(key);
				List<Object> list=(List<Object>)map;
				supportList(list,intkey);
				if((!StringUtil.isSpace(fn))&&fn.indexOf("[")==0){
					Object obj=list.get(intkey);
					if(obj==null){
						list.set(intkey,setMapAttribute(getEmptyMap(fn.substring(fn.indexOf("[")+1, fn.indexOf("]"))),fn,paramValue));
					}
					setMapAttribute(obj,fn,paramValue);
				}else{
					list.set(intkey, paramValue);
				}
				return list;
			}catch(NumberFormatException e){
				Map<String,Object> m=(Map<String,Object>)map;
				if(key.indexOf("'")==0&&key.lastIndexOf("'")==key.length()-1){
					key=key.substring(1,key.length()-1);
				}
				if((!StringUtil.isSpace(fn))&&fn.indexOf("[")==0){
					Object obj=m.get(key);
					if(obj==null){
						m.put(key,setMapAttribute(getEmptyMap(fn.substring(fn.indexOf("[")+1, fn.indexOf("]"))),fn,paramValue));
					}
					setMapAttribute(obj,fn,paramValue);
				}else{
					m.put(key, paramValue);
				}
				return m;
			}
		}else{
			return null;
		}
	}
	private static Object getMapAttribute(Object map,String paramName){
		String key=paramName.substring(paramName.indexOf("[")+1, paramName.indexOf("]"));
		String fn=paramName.substring(paramName.indexOf("]")+1);
		if(null!=map){
			try{
				int intkey=Integer.parseInt(key);
				List<Object> list=(List<Object>)map;
				supportList(list,intkey);
				if((!StringUtil.isSpace(fn))&&fn.indexOf("[")==0){
					Object obj=list.get(intkey);
					if(obj==null){
						return null;
					}else{
						return getMapAttribute(obj,fn);
					}
				}else{
					return list.get(intkey);
				}
			}catch(NumberFormatException e){
				Map<String,Object> m=(Map<String,Object>)map;
				if(key.indexOf("'")==0&&key.lastIndexOf("'")==key.length()-1){
					key=key.substring(1,key.length()-1);
				}
				if((!StringUtil.isSpace(fn))&&fn.indexOf("[")==0){
					Object obj=m.get(key);
					if(obj==null){
						return null;
					}else{
						return getMapAttribute(obj,fn);
					}
				}else{
					return m.get(key);
				}
			}
		}else{
			return null;
		}
	}
	private static Object getEmptyMap(String key){
		try{
			Integer.parseInt(key);
			List<Object> list=new ArrayList<Object>();
			return list;
		}catch(NumberFormatException e){
			Map<String,Object> m=new HashMap<String,Object>();
			return m;
		}
	}
	private static void supportList(List<?> list,int n){
		if(list.size()>n){
		}else{
			for(int i=list.size();i<=n;i++){
				list.add(null);
			}
		}
	}
	
	
	public static String getRequestBody(javax.servlet.http.HttpServletRequest request) throws UnsupportedEncodingException, IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));
		StringBuffer sb = new StringBuffer("");
		String temp;
		while ((temp = br.readLine()) != null) { 
			sb.append(temp);
		}
		br.close();
		return sb.toString();
	}
}
