package manage.util.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import m.system.util.StringUtil;
import manage.model.DictionaryData;

public class DictionarySelectTag implements Tag {

	////jstl方法----------------------
	private PageContext pageContext;
	private String type;
	private String value;
	private String extend;
	public void setPageContext(PageContext pageContext) {
		this.pageContext=pageContext;
		value=StringUtil.noSpace(value);
		extend=StringUtil.noSpace(extend);
	}
	public int doStartTag() throws JspException {
		StringBuffer sb=new StringBuffer();
		sb.append("<select ").append(extend).append(" >");
		try {
			pageContext.getOut().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Tag.EVAL_BODY_INCLUDE;
	}
	public int doEndTag() throws JspException {
		DictionaryUtil.init();
		StringBuffer sb=new StringBuffer();
		Map<String,DictionaryData> dd=DictionaryUtil.map.get(type);
		if(null!=dd){
			for(String key : dd.keySet()){
				sb.append("<option value=\"").append(key).append("\" ").append(DictionaryUtil.isContain(value, key)?"selected":"").append(">").append(dd.get(key).getName()).append("</option>");
			}
		}
		sb.append("</select>");
		try {
			pageContext.getOut().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Tag.EVAL_PAGE;
	}
	public Tag getParent() {return null;}
	public void release() {}
	public void setParent(Tag arg0) {}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
}
