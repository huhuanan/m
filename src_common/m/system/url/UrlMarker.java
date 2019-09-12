package m.system.url;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class UrlMarker {
	private static List<RedirectUrl> ruList;
	private static Map<String,String> redirectMap;
	private static List<String> enableList;
	private static List<String> refererList;
	private static List<String> authorizationList;
	private static void initXML(){
		if(null==redirectMap){
			synchronized (UrlMarker.class) {
				if(null==redirectMap){
					ruList=new ArrayList<RedirectUrl>();
					redirectMap=new HashMap<String,String>();
					enableList=new ArrayList<String>();
					refererList=new ArrayList<String>();
					authorizationList=new ArrayList<String>();
					InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/urlmarker.xml");
					DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
					DocumentBuilder dombuilder;
					try {
						String domainClass=RuntimeData.getDomainClass();
						if(!StringUtil.isSpace(domainClass)){
							for(String clazz : domainClass.split(",")){
								ruList.add((RedirectUrl) ClassUtil.newInstance(clazz));
							}
						}
						dombuilder = domfac.newDocumentBuilder();
						Document doc=dombuilder.parse(is);
						Element root=doc.getDocumentElement();
						NodeList urlType=root.getChildNodes();
						if(urlType!=null){
							for(int i=0;i<urlType.getLength();i++){
								Node utype=urlType.item(i);
								if("redirect"==utype.getNodeName()){
									fillMap(utype,redirectMap,"tag");
								}
								if("enable"==utype.getNodeName()){
									fillList(utype,enableList);
									//enableList.add("/action/");
									//enableList.add("/page/");
									//enableList.add("/WEB-INF/classes/page/");
								}
								if("referer"==utype.getNodeName()){
									fillList(utype,refererList);
									refererList.add("127.0.0.1");
								}
								if("authorization"==utype.getNodeName()){
									fillList(utype,authorizationList);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	private static void fillMap(Node utype,Map<String,String> map,String key){
		NodeList urls=utype.getChildNodes();
		if(urls!=null){
			for(int i=0;i<urls.getLength();i++){
				Node url=urls.item(i);
				if(null==url.getAttributes()) continue;
				Node attr=url.getAttributes().getNamedItem(key);
				if(null!=attr){
					map.put(attr.getTextContent(), url.getTextContent());
				}
			}
		}
	}
	private static void fillList(Node utype,List<String> list){
		NodeList urls=utype.getChildNodes();
		if(urls!=null){
			for(int i=0;i<urls.getLength();i++){
				Node url=urls.item(i);
				if(null!=url.getNodeName()&&"tag".equals(url.getNodeName())) {
					list.add(url.getTextContent());
				}
			}
		}
	}
	public static boolean isDisabledUrl(String servletPath) {
		if(servletPath.endsWith(".php")) return true;//.php结尾的自动禁止访问
		return false;
	}
	/**
	 * 判断是否允许的url
	 * @param servletPath
	 * @return
	 */
	public static boolean isEnableUrl(String serverName,String servletPath,String referer,String authorization){
		initXML();
		if(!StringUtil.isSpace(authorization)) {
			for(String key : authorizationList) {
				if(authorization.indexOf(key)==0) {
					return true; //如果授权前缀正确, 则不验证url 但后续action还需要自行验证.
				}
			}
		}
		if(null!=referer) {//referer和serverName相同时直接过
			referer=referer.substring(referer.indexOf("//")+2);
			referer=referer.substring(0, referer.indexOf("/"));
			if(referer.indexOf(":")>=0) referer=referer.substring(0, referer.indexOf(":"));
			if(referer.equals(serverName)) return true;
		}else if(servletPath.indexOf("/")==servletPath.lastIndexOf("/")){//跟目录不限制
			return true;
		}
		boolean b=false;
		//判断路径是否允许访问
		if(servletPath.indexOf(RuntimeData.getFilePath())==1){//文件路径
			if(RuntimeData.accessFilePath()) {
				return true;
			}else {
				b=true;
			}
		}else{
			for(String key : enableList){//允许列表
				if(servletPath.indexOf(key)==0){
					b=true; break;
				}
			}
			if(!b) {
				for(String key : redirectMap.keySet()){//自定义跳转列表
					if(servletPath.indexOf(key)==0||servletPath.indexOf(redirectMap.get(key))==0){
						b=true; break;
					}
				}
			}
		}
		if(b) {//判断主机是否可以访问
			b=false;
			for(String key : refererList){//允许列表
				if(null==referer&&serverName.indexOf(key)==0
						||null!=referer&&referer.indexOf(key)==0){
					return true;
				}
			}
			b=isReferer(referer);//通过域名实现类检查
		}
		return b;
	}
	private static boolean isReferer(String referer) {
		boolean b=false;
		for(RedirectUrl ru : ruList){
			b=ru.isReferer(referer);
			if(b) break;
		}
		return b;
	}
	/**
	 * 域名实现类重定向
	 * @param serverName
	 * @param servletPath
	 * @return
	 */
	public static String getRedirectUrl(String serverName, String servletPath){
		if(servletPath.equals("/index.jsp")){
			try {
				String url=toRedirectUrl4Domain(serverName);
				if(null!=url){
					return url;
				}
			} catch (MException e) {
				e.printStackTrace();
			}
		}
		String redirectPath=toRedirectUrl(servletPath);
		if(null!=redirectPath){
			return redirectPath;
		}
		return null;
	}
	private static String toRedirectUrl(String path){
		initXML();
		for(String tag : redirectMap.keySet()){
			if(path.indexOf(tag)==0&&path.lastIndexOf("/")<3&&path.indexOf(".")==-1){
				char[] as=path.toCharArray();
				for(char a : as){
					if(a>256){
						String s=new String(new char[]{a});
						try {
							path=path.replaceAll(s,java.net.URLEncoder.encode(s,"UTF-8"));
						} catch (UnsupportedEncodingException e) {}
					}
				}
				return new StringBuffer(redirectMap.get(tag)).append(path.substring(path.indexOf(tag)+tag.length())).toString();
			}
		}
		return null;
	}
	private static String toRedirectUrl4Domain(String domain) throws MException{
		String url=null;
		for(RedirectUrl ru : ruList){
			url=ru.getRedirectUrl(domain);
			if(null!=url) break;
		}
		return url;
	}
}
