package m.common.action;

import java.util.List;
import java.util.Map;

import m.common.model.Model;
import m.system.lang.PageInfo;

public class ActionResult {
	public ActionResult(String page){
		this.page=page;
	}
	public ActionResult(String page,String param){
		this.page=page;
		this.param=param;
	}
	public ActionResult(String page,Model model){
		this.page=page;
		this.model=model;
	}
	public ActionResult(String page,List<? extends Model> list){
		this.page=page;
		this.list=list;
	}
	private String param;
	private String key;//页面唯一标识,如果不设置,则自动设置
	private String page;
	private String htmlBody;
	private Model model;
	private PageInfo pageInfo;
	private List<? extends Model> list;
	private List<? extends Object> array;
	private Map<String,Object> map;
	private Object power;

	public List<? extends Object> getArray() {
		return array;
	}
	public void setArray(List<? extends Object> array) {
		this.array = array;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<? extends Model> getList() {
		return list;
	}
	public void setList(List<? extends Model> list) {
		this.list = list;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Object getPower() {
		return power;
	}
	public void setPower(Object power) {
		this.power = power;
	}
	public String getHtmlBody() {
		return htmlBody;
	}
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}
