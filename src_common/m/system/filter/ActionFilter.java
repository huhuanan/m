package m.system.filter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;

import m.common.action.Action;
import m.common.action.ActionResult;
import m.common.dao.Dao;
import m.common.model.LogModel;
import m.common.model.Model;
import m.system.RuntimeData;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.url.UrlMarker;
import m.system.util.ActionUtil;
import m.system.util.ArrayUtil;
import m.system.util.ClassUtil;
import m.system.util.DateUtil;
import m.system.util.FileUtil;
import m.system.util.GenerateID;
import m.system.util.JSONMessage;
import m.system.util.ObjectUtil;
import m.system.util.StringUtil;

public class ActionFilter implements Filter {

	public void destroy() {
	}
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	/**
	 * 执行url请求的动作,
	 * 并填充FinalFilte所需的跳转和返回参数
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Date startDate=new Date();
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		String servletPath=req.getServletPath();
		if(UrlMarker.isDisabledUrl(servletPath)) {
			if(RuntimeData.getDebug()) {
				System.out.print("Disabled url:");
				System.out.println(servletPath);	
			}
			return;
		}
		if(RuntimeData.getDebug()){
			System.out.println("-----------------------------------------------------------");
			System.out.print(DateUtil.format(startDate, DateUtil.YYYY_MM_DD_HH_MM_SS));
			System.out.print("\t>>>>>\t\t");
			System.out.print("url:");
			System.out.println(servletPath);
		}
		try {
			TransactionManager.initConnection();
			executeFilter(req,res,chain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TransactionManager.closeConnection();
		if(RuntimeData.getDebug()){
			Date endDate=new Date();
			long time=endDate.getTime()-startDate.getTime();
			System.out.print(DateUtil.format(endDate, DateUtil.YYYY_MM_DD_HH_MM_SS));
			System.out.print("\t<<<<<\t 耗时:");
			System.out.print(time);
			if(time>1000) {
				System.out.print(" 耗时超1秒 ");
			}
			System.out.print("\turl:");
			System.out.println(servletPath);
		}
	}
	@SuppressWarnings("unchecked")
	private void executeFilter(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws IOException, ServletException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=UTF-8");
		String servletPath=req.getServletPath();
		String serverName=req.getServerName();
		String referer = req.getHeader("referer");
		String authorization = req.getHeader("Authorization");
		Boolean isEnableUrl=(Boolean)req.getAttribute("isEnableUrl");
		if(null!=isEnableUrl&&isEnableUrl || UrlMarker.isEnableUrl(serverName,servletPath,referer,authorization)){
			req.setAttribute("isEnableUrl", true);
			if(RuntimeData.getDebug()) {
				System.out.print("   referer:");
				System.out.println(referer);
				System.out.print("\t Authorization:");
				System.out.println(authorization);
			}
		}else {
			if(RuntimeData.getDebug()) {
				System.out.println("Disabled url!");
				System.out.print("\t referer:");
				System.out.println(referer);
				System.out.print("\t Authorization:");
				System.out.println(authorization);
			}
			return;
		}
		Object result=null;
		String redirectPath=UrlMarker.getRedirectUrl(serverName,servletPath);
		if(null!=redirectPath){
			if(redirectPath.indexOf("#")>=0){
				if(RuntimeData.getDebug()) {
					System.out.print("redirect:");
					System.out.print(req.getContextPath());
					System.out.println(redirectPath);
				}
				res.sendRedirect(new StringBuffer(req.getContextPath()).append(redirectPath).toString());
			}else{
				if(RuntimeData.getDebug()) {
					System.out.print("dispatcher:");
					System.out.println(redirectPath);
				}
				req.getRequestDispatcher(redirectPath).forward(req, res);
			}
			return;
		}
		if(servletPath.length()>1){
			String[] urlParts=servletPath.substring(1).split("/");
			if("action".equals(urlParts[0])&&urlParts.length==3){
				Class<Action> clazz=(Class<Action>) RuntimeData.getAction(urlParts[1]);
				if(null==clazz){
					new MException(this.getClass(),"Did not find the corresponding Action!").record();
				}
				LogModel logModel=null;
				Action action=null;
				Map<String,String> logDataMap=new HashMap<String, String>();
				try {
					if("application/json".equals(req.getHeader("Content-Type"))){
						String requestBody=ActionUtil.getRequestBody(req);
						if(RuntimeData.getDebug()){
							System.out.print("requestBody:");System.out.println(requestBody);
						}
						action=JSONObject.parseObject(requestBody,clazz);
						action.setRequestBody(requestBody);
					}else{
						action=ClassUtil.newInstance(clazz);
					}
					action.setRequest(req);
					action.setResponse(res);
					action.setAuthorization(authorization);
					for(Object paramName : req.getParameterMap().keySet()){
						String paramValue=ArrayUtil.connection((Object[]) req.getParameterMap().get(paramName),",");
						try{
							logDataMap.put(paramName.toString(), paramValue.length()>1000?paramValue.substring(0, 1000):paramValue);
							ActionUtil.fillInAttribute(action,paramName.toString(),paramValue);
							if(RuntimeData.getDebug()) System.out.println(new StringBuffer("\t").append(paramName).append("\t\t").append(paramValue));
						}catch(MException ex){
							if(RuntimeData.getDebug()) System.out.println(new StringBuffer("Parameters are not populated!\tparamName:").append(paramName).append("\tparamValue:").append(paramValue));
						}
					}
					Map<String,File> fileMap=ActionUtil.fillInFileItemStream(action,req);
					result=ClassUtil.executeMethod(action, urlParts[2]);
					if(null!=fileMap){//删除上传的文件
						for(String key : fileMap.keySet()){
							FileUtil.deleteFile(fileMap.get(key).getPath());
						}
					}
					logModel=action.getLogModel();
					if(null!=logModel){
						logModel.setOperUrl(servletPath);
						logModel.setOperData(ObjectUtil.toString(logDataMap));
						try {
							RuntimeData.getDao(Dao.class).saveModel((Model)logModel);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					//System.out.println(result);
				} catch (Exception e) {
					if(RuntimeData.getDebug()) e.printStackTrace();
					System.out.println("--------------");
					System.out.println(new StringBuffer("error:").append(e.getMessage()).append("\turl:").append(servletPath));
					if(null!=logModel){
						try {
							logModel.setOperUrl(servletPath);
							logModel.setOperData(ObjectUtil.toString(logDataMap));
							logModel.setOperResult("请求异常");
							logModel.setResultException(e.getMessage());
							RuntimeData.getDao(Dao.class).saveModel((Model)logModel);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					if(e instanceof InvocationTargetException){
						req.setAttribute("error", ((InvocationTargetException)e).getTargetException().getMessage());
					}else{
						req.setAttribute("error", new StringBuffer("请求失败!").append(e.getMessage()).toString());
					}
					//打印错误消息到前台
					req.getRequestDispatcher("/error500.jsp").forward(req, res);//这句执行会报错. 为了跳转到500页面.
					return;
				}  
			}else if("page".equals(urlParts[0])){
				if(servletPath.lastIndexOf(".")>=0&&!servletPath.substring(servletPath.lastIndexOf(".")+1).equals("jsp")){
					String path=new StringBuffer(RuntimeData.getClassPath()).append("page")
						.append(servletPath.substring(urlParts[0].length()+1)).toString();
					outFile(req,res,path);
					return;
				}else{
					result=new ActionResult(servletPath.substring(urlParts[0].length()+1));
					if(RuntimeData.getDebug()){
						for(Object paramName : req.getParameterMap().keySet()){
							String paramValue=((String[])req.getParameterMap().get(paramName))[0];
							System.out.println(new StringBuffer("\t").append(paramName).append("\t\t").append(paramValue));
						}
					}
				}
			}else if("qrcode".equals(urlParts[0])){
				String text=req.getParameter("txt");
				try {
					res.setContentType("image/png");
					BufferedImage image=FileUtil.toQRCode(text, 300);
					ImageIO.write(image, "PNG", res.getOutputStream());
					return;
				} catch (WriterException e) {
					req.setAttribute("error", new StringBuffer("二维码生成失败!").append(e.getMessage()).toString());
					//打印错误消息到前台
					req.getRequestDispatcher("/error500.jsp").forward(req, res);//这句执行会报错. 为了跳转到500页面.
					return;
				}
			}
		}
		if(null==result){
			chain.doFilter(req, res);
		}else{
			if(result instanceof ActionResult){
				ActionResult actionResult=(ActionResult)result;
				if(!StringUtil.isSpace(actionResult.getPage())){
					if(RuntimeData.getDebug()) System.out.println(new StringBuffer("return page:").append(actionResult.getPage()));
					req.setAttribute("list", actionResult.getList());
					req.setAttribute("model", actionResult.getModel());
					req.setAttribute("array", actionResult.getArray());
					req.setAttribute("map", actionResult.getMap());
					req.setAttribute("power", actionResult.getPower());
					req.setAttribute("htmlBody", actionResult.getHtmlBody());
					req.setAttribute("pageInfo", actionResult.getPageInfo());
					req.setAttribute("key", null!=actionResult.getKey()?actionResult.getKey():GenerateID.tempKey());
					String page=new StringBuffer("/WEB-INF/classes/page/").append(actionResult.getPage()).append(".jsp?")
						.append(StringUtil.noSpace(actionResult.getParam())).toString();
					req.getRequestDispatcher(page).forward(req, res);
				}
			}else{
				String outString="";
				try {
					outString=ObjectUtil.toString(result);
				} catch (MException e) {
					JSONMessage message=new JSONMessage();
					message.push("msg",new StringBuffer("输出错误!").append(e.getMessage()).toString());
					outString=message.toJSONString();
				}
				if(RuntimeData.getDebug()) System.out.println(new StringBuffer("return Object:").append(outString));
				if(!StringUtil.isSpace(outString)) res.getWriter().print(outString);
			}
		}
	}
	private void outFile(HttpServletRequest request,HttpServletResponse response,String path) throws IOException{
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		ServletOutputStream sos = response.getOutputStream();
		sos.write(content);
		sos.flush();
		sos.close();
	}
}
