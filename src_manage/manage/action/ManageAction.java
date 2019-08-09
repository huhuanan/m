package manage.action;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import jxl.write.WriteException;
import m.common.action.Action;
import m.common.action.ActionResult;
import m.common.model.UserModel;
import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.system.RuntimeData;
import m.system.cache.CacheUtil;
import m.system.exception.MException;
import m.system.lang.PageInfo;
import m.system.util.AnnotationUtil;
import m.system.util.GenerateID;
import m.system.util.JSONMessage;
import m.system.util.ObjectUtil;
import m.system.util.StringUtil;
import manage.dao.AdminLoginDao;
import manage.model.AdminGroup;
import manage.model.AdminGroupPower;
import manage.model.AdminLogin;
import manage.service.AdminGroupPowerService;
import manage.util.excel.ExcelObject;
import manage.util.excel.SheetObject;
import manage.util.page.ActionChartUtil;
import manage.util.page.ActionTableUtil;
import manage.util.page.ButtonMetaUtil;
import manage.util.page.FormMetaUtil;
import manage.util.page.QueryMetaUtil;
import manage.util.page.chart.ActionChartMeta;
import manage.util.page.form.ActionFormMeta;
import manage.util.page.table.ActionTableMeta;

public abstract class ManageAction extends Action {
	private String method;
	private String key;
	private String openKey;
	private String openMode;
	private String menuId;
	private String searchText;
	private String searchTimer;// 5,15,60
	private String power;
	private Map<String,String> params=new HashMap<String, String>();
	private QueryPage page;
	private QueryOrder order;
	private Integer pageNo;
	private Integer pageNum;
	public void setPageNo(Integer pageNo){
		this.pageNo=pageNo;
		if(null!=this.pageNum) castQueryPage();
	}
	public void setPageNum(Integer pageNum){
		this.pageNum=pageNum;
		if(null!=this.pageNo) castQueryPage();
	}
	private void castQueryPage(){
		page=new QueryPage((pageNo-1)*pageNum,pageNum);
	}
	public UserModel getSessionLogUser(){
		return getSessionAdmin();
	}
	/**
	 * 获取登录用户信息 返回null说明没有登录
	 * @return
	 */
	public AdminLogin getSessionAdmin() {
		Object oid=getRequest().getSession().getAttribute("login_admin_oid");
		if(null!=oid){
			return sessionAdminMap.get(oid.toString());
		}else{
			String name=null;
			String key=null;
			Cookie[] cs=getRequest().getCookies();
			if(null!=cs){
				for(int i=0;i<cs.length;i++){
					if(cs[i].getName().equals("admin_name")){
						name=cs[i].getValue();
					}else if(cs[i].getName().equals("admin_key")){
						key=cs[i].getValue();
					}
				}
				String adminOid=null;
				if(!StringUtil.isSpace(name)&&!StringUtil.isSpace(key)){
					try {
						adminOid=getDao(AdminLoginDao.class).getUserOid(name, key);
					} catch (Exception e) {}
				}
				if(!StringUtil.isSpace(adminOid)){
					try {
						AdminLogin admin=new AdminLogin();
						admin.setOid(adminOid);
						admin=ModelQueryList.getModel(admin,1);
						admin.setPassword("");
						setSessionAdmin(admin,"");
						getDao(AdminLoginDao.class).updateLastInfo(admin, getIpAddress());
						return admin;
					} catch (Exception e) {}
				}
			}
		}
		return null;
	}
	private static Map<String,AdminLogin> sessionAdminMap=new HashMap<String, AdminLogin>();
	public static AdminLogin getSessionAdmin(String oid){
		return sessionAdminMap.get(oid);
	}
	/**
	 * 设置session
	 * @param admin 登录人信息
	 * @param autoLogin 是否自动登录  Y:是
	 */
	public void setSessionAdmin(AdminLogin admin,String autoLogin){
		if(StringUtil.noSpace(autoLogin).equals("Y")){
			Cookie cookie=new Cookie("admin_name",admin.getUsername());
			cookie.setMaxAge(604800);
			cookie.setPath("/");
			getResponse().addCookie(cookie);
			cookie=new Cookie("admin_key",admin.getPassword());
			cookie.setMaxAge(604800);
			cookie.setPath("/");
			getResponse().addCookie(cookie);
		}
		getRequest().getSession().setAttribute("login_admin_oid", admin.getOid());
		sessionAdminMap.put(admin.getOid(), admin);
	}
	/**
	 * 清除登录信息
	 */
	public void clearSessionAdmin(){
		AdminLogin admin=getSessionAdmin();
		if(null!=admin){
			sessionAdminMap.remove(admin.getOid());
		}
		Cookie cookie=new Cookie("admin_name","");
		cookie.setMaxAge(1);
		cookie.setPath("/");
		getResponse().addCookie(cookie);
		cookie=new Cookie("admin_key","");
		cookie.setMaxAge(1);
		cookie.setPath("/");
		getResponse().addCookie(cookie);
		getRequest().getSession().removeAttribute("login_admin_oid");
		getRequest().getSession().invalidate();
	}
	/**
	 * 重置登录信息 
	 * @param adminOid 登录账号oid
	 */
	public void resetSessionAdmin(String adminOid){
		AdminLogin session=sessionAdminMap.get(adminOid);
		if(null!=session){
			try {
				sessionAdminMap.put(adminOid, ModelQueryList.getModel(session,1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String,Boolean> getAdminOperPower(AdminLogin admin) throws SQLException, MException{
		return RuntimeData.getService(AdminGroupPowerService.class).getPowerMap(admin.getOid());
	}
	public static Map<String,Boolean> getAdminOperPower(AdminGroup adminGroup) throws SQLException, MException{
		return RuntimeData.getService(AdminGroupPowerService.class).getPowerMapByGroup(adminGroup.getOid());
	}
	/**
	 * 获取当前登陆人所在管理员组的操作权限
	 * @return
	 * @throws Exception
	 */
	public Map<String,Boolean> getAdminOperPower() throws Exception{
		AdminLogin admin=getSessionAdmin();
		if(null==admin){
			throw new MException(this.getClass(),"未登录");
		}
		return getAdminOperPower(admin);
	}
	/**
	 * 验证操作权限
	 * @param power 在module.xml配置文件中配置
	 * @throws Exception 
	 */
	public void verifyAdminOperPower(String power) throws Exception{
		Map<String,Boolean> adminGroupPowerMap=getAdminOperPower();
		if(null!=adminGroupPowerMap.get(power)&&adminGroupPowerMap.get(power)){
			
		}else{
			throw new MException(this.getClass(),"操作权限不足");
		}
	}

	/**
	 * 验证登录 未登录则报错
	 * @return
	 * @throws Exception
	 */
	public AdminLogin verifyAdminLogin() throws Exception{
		AdminLogin model=getSessionAdmin();
		if(null!=model){
			return model;
		}else{
			throw new MException(this.getClass(),"未登录");
		}
	}
	/**
	 * 验证操作人是否与登录人一致, 验证失败则报错
	 * @param operUserField 
	 * @throws MException
	 * @throws Exception
	 */
	public void verifyAdminRecord(AdminLogin operUserField) throws MException, Exception{
		if(null!=operUserField&&!StringUtil.isSpace(operUserField.getOid())){
			if(operUserField.getOid().equals(verifyAdminLogin().getOid())){
				//验证通过
			}else{
				throw new MException(this.getClass(),"操作人错误");
			}
		}else{
			throw new MException(this.getClass(),"操作人为空");
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////
	public enum ActionFormPage{
		EDIT("editModel"),VIEW("editModel");
		private String page;
		private ActionFormPage(String page){
			this.page=page;
		}
		@Override
		public String toString() {
			return this.page;
		}
		public String getPage() {
			return page;
		}
		public void setPage(String page) {
			this.page = page;
		}
		
	}
	
	public ActionResult getFormResult(ManageAction action,ActionFormPage actionPage) throws Exception{
		ActionResult result=new ActionResult("manage/actionResult/"+actionPage);
		result.setKey(key);
		StackTraceElement stacks = new Throwable().getStackTrace()[1];
		ActionFormMeta meta=AnnotationUtil.getAnnotation4Method(ActionFormMeta.class, getActionClass(), stacks.getMethodName());
		result.setMap(new HashMap<String,Object>());
		Map<String,Object> map=result.getMap();
		map.put("formTitle", meta.title());
		map.put("formRows", FormMetaUtil.toRows(meta.rows(),getAdminOperPower()));
		map.put("formButtons", FormMetaUtil.toButtons(meta.buttons(),getAdminOperPower()));
		map.put("others", FormMetaUtil.toOthers(meta.others()));
		map.put("action", action);
		map.put("openKey", openKey);
		map.put("openMode", openMode);
		return result;
	}
	public JSONMessage getListDataResult(QueryCondition[] condList){
		JSONMessage result=new JSONMessage();
		try {
			StackTraceElement stacks = new Throwable().getStackTrace()[1];
			ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, getActionClass(), stacks.getMethodName());
			List<QueryCondition> list=QueryMetaUtil.convertQuery(getParams(),meta.querys());//查询条件
			if(null!=condList){
				for(QueryCondition cond : condList){
					list.add(cond);
				}
			}
			QueryCondition scond=QueryMetaUtil.convertSearchQuery(getSearchText(),meta.searchField());//search查询条件
			if(null!=scond) list.add(scond);
			QueryCondition condition=QueryCondition.and(list.toArray(new QueryCondition[]{}));
			PageInfo pageInfo=ActionTableUtil.toPageInfo(meta,getPage(),condition, getOrder());//查询
			List<JSONMessage> data=ActionTableUtil.getDataList(meta,pageInfo.getList());
			if(pageInfo.getCount()>0) {
				JSONMessage count=ActionTableUtil.getCountData(meta,condition);//合计
				if(null!=count) data.add(count);
			}
			result.push("code", 0);
			result.push("msg", "");
			result.push("count", pageInfo.getCount());
			result.push("data", data);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
	public JSONMessage getChartDataResult(QueryCondition[] condList){
		JSONMessage result=new JSONMessage();
		try {
			StackTraceElement stacks = new Throwable().getStackTrace()[1];
			ActionChartMeta meta=AnnotationUtil.getAnnotation4Method(ActionChartMeta.class, getActionClass(), stacks.getMethodName());
			List<QueryCondition> list=QueryMetaUtil.convertQuery(getParams(),meta.querys());//查询条件
			if(null!=condList){
				for(QueryCondition cond : condList){
					list.add(cond);
				}
			}
			QueryCondition scond=QueryMetaUtil.convertSearchQuery(getSearchText(),meta.searchField());//search查询条件
			if(null!=scond) list.add(scond);
			QueryCondition condition=QueryCondition.and(list.toArray(new QueryCondition[]{}));
			//PageInfo pageInfo=ActionTableUtil.toPageInfo(getPage(),condition, getOrder());//查询
			result.push("code", 0);
			result.push("msg", "");
			result.push("data", ActionChartUtil.getDataList(condition));
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
	/**
	 * 转换成sheetObject
	 * @param condList
	 * @return
	 * @throws Exception
	 */
	public SheetObject getExcelSheet(QueryCondition[] condList,String sheetName) throws Exception{
		StackTraceElement stacks = new Throwable().getStackTrace()[1];
		return getExcelSheet(stacks.getMethodName(),condList,sheetName);
	}
	/**
	 * 转换成sheetObject
	 * @param methodName
	 * @param condList
	 * @param sheetName
	 * @return
	 * @throws Exception
	 */
	public SheetObject getExcelSheet(String methodName,QueryCondition[] condList,String sheetName) throws Exception{
		return getExcelSheet(getActionClass(),methodName,condList,sheetName);
	}
	/**
	 * 转换成sheetObject
	 * @param condList
	 * @param methodName
	 * @return
	 * @throws Exception
	 */
	public SheetObject getExcelSheet(Class<? extends ManageAction> actionClass,String methodName,QueryCondition[] condList,String sheetName) throws Exception{
		ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, actionClass, methodName);
		List<QueryCondition> list=QueryMetaUtil.convertQuery(getParams(),meta.querys());//查询条件
		if(null!=condList){
			for(QueryCondition cond : condList){
				list.add(cond);
			}
		}
		QueryCondition scond=QueryMetaUtil.convertSearchQuery(getSearchText(),meta.searchField());//search查询条件
		if(null!=scond) list.add(scond);
		QueryCondition condition=QueryCondition.and(list.toArray(new QueryCondition[]{}));
		PageInfo pageInfo=ActionTableUtil.toPageInfo(meta,null,condition, getOrder());//查询
		return ActionTableUtil.toExcelSheet(meta,ActionTableUtil.getDataList(meta,pageInfo.getList()),sheetName);
	}
	/**
	 * 跳转到导出页面
	 * @param excelObject
	 * @return
	 * @throws WriteException
	 * @throws IOException
	 */
	protected ActionResult toExportExcel(ExcelObject excelObject) throws WriteException, IOException{
		ActionResult result=new ActionResult("manage/actionResult/excel");
		getRequest().setAttribute("fileName", excelObject.getName());
		getRequest().setAttribute("fileObject",excelObject.toExcelFile());
		return result;
	}
	public abstract Class<? extends ManageAction> getActionClass();
	/**
	 * 列表页面 method=数据方法名 用于菜单的页面
	 * @return
	 * @throws MException
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public ActionResult toList() throws Exception{
		ActionResult result=new ActionResult("manage/actionResult/tableList");
		boolean isMenu=!StringUtil.isSpace(menuId);
		result.setKey(key);
		ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, getActionClass(), method);
		result.setMap(new HashMap<String,Object>());
		Map<String,Object> map=result.getMap();
		map.put("params", params);
		map.put("dataUrl", meta.dataUrl());
		if(!isMenu&&meta.tableHeight()!=0){
			map.put("tableHeight",meta.tableHeight());
		}else{
			map.put("tableHeight",0);
		}
		map.put("hiddenQueryList", false);
		if(!StringUtil.isSpace(meta.searchField())){
			map.put("searchHint",meta.searchHint());
		}else if(!QueryMetaUtil.hasNoHiddenQuery(meta.querys())){
			map.put("hiddenQueryList", true);
		}
		map.put("tableCols", ObjectUtil.toString(ActionTableUtil.toList(meta.cols(),getAdminOperPower())));
		map.put("tableQueryList",QueryMetaUtil.toList(meta.querys()));
		ButtonMetaUtil bmUtil=new ButtonMetaUtil();
		map.put("tableButtons",ObjectUtil.toString(bmUtil.toList(meta.buttons(),getAdminOperPower())));
		map.put("openKey", openKey);
		map.put("openMode", openMode);
		return result;
	}
//	public JSONMessage getListFields() {
//
//		JSONMessage result=new JSONMessage();
//		try {
//			boolean isMenu=!StringUtil.isSpace(menuId);
//			ActionTableMeta meta=AnnotationUtil.getAnnotation4Method(ActionTableMeta.class, getActionClass(), method);
//			Map<String,Object> map=new HashMap<String,Object>();
//			map.put("params", params);
//			map.put("dataUrl", meta.dataUrl());
//			if(!isMenu&&meta.tableHeight()!=0){
//				map.put("tableHeight",meta.tableHeight());
//			}else{
//				map.put("tableHeight",0);
//			}
//			map.put("hiddenQueryList", false);
//			if(!StringUtil.isSpace(meta.searchField())){
//				map.put("searchHint",meta.searchHint());
//			}else if(!QueryMetaUtil.hasNoHiddenQuery(meta.querys())){
//				map.put("hiddenQueryList", true);
//			}
//			map.put("tableCols", ObjectUtil.toString(ActionTableUtil.toList(meta.cols(),getAdminOperPower())));
//			map.put("tableQueryList",QueryMetaUtil.toList(meta.querys()));
//			ButtonMetaUtil bmUtil=new ButtonMetaUtil();
//			map.put("tableButtons",bmUtil.toList(meta.buttons(),getAdminOperPower()));
//			map.put("openKey", openKey);
//			map.put("openMode", openMode);
//			result.push("map", map);
//			result.push("code", 0);
//			result.push("msg", "");
//		} catch (Exception e) {
//			result.push("code", 1);
//			result.push("msg", e.getMessage());
//			if(RuntimeData.getDebug()) e.printStackTrace();
//		}
//		return result;
//	}
	public ActionResult toChart() throws Exception{
		ActionResult result=new ActionResult("manage/actionResult/chartList");
		boolean isMenu=!StringUtil.isSpace(menuId);
		String pageKey=GenerateID.tempKey();
		result.setKey(new StringBuffer("page_").append(isMenu?menuId:pageKey).toString());
		ActionChartMeta meta=AnnotationUtil.getAnnotation4Method(ActionChartMeta.class, getActionClass(), method);
		result.setMap(new HashMap<String,Object>());
		Map<String,Object> map=result.getMap();
		map.put("params", params);
		map.put("dataUrl", meta.dataUrl());
		if(!isMenu&&meta.tableHeight()!=0){
			map.put("tableHeight",meta.tableHeight());
		}else{
			map.put("tableHeight",0);
		}
		map.put("hiddenQueryList", false);
		if(!StringUtil.isSpace(meta.searchField())){
			map.put("searchHint",meta.searchHint());
		}else if(!QueryMetaUtil.hasNoHiddenQuery(meta.querys())){
			map.put("hiddenQueryList", true);
		}
		map.put("chartOption", ActionChartUtil.getChartOption(meta));
		map.put("tableQueryList",QueryMetaUtil.toList(meta.querys()));
		ButtonMetaUtil bmUtil=new ButtonMetaUtil();
		map.put("tableButtons",ObjectUtil.toString(bmUtil.toList(meta.buttons(),getAdminOperPower())));
		map.put("openKey", openKey);
		map.put("openMode", openMode);
		return result;
	}

	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public QueryPage getPage() {
		return page;
	}
	public void setPage(QueryPage page) {
		this.page = page;
	}
	public QueryOrder getOrder() {
		return order;
	}
	public void setOrder(QueryOrder order) {
		this.order = order;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getOpenMode() {
		return openMode;
	}
	public void setOpenMode(String openMode) {
		this.openMode = openMode;
	}
	public String getSearchTimer() {
		return searchTimer;
	}
	public void setSearchTimer(String searchTimer) {
		this.searchTimer = searchTimer;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getOpenKey() {
		return openKey;
	}
	public void setOpenKey(String openKey) {
		this.openKey = openKey;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
