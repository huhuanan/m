package manage.util.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import m.system.util.StringUtil;
import manage.model.DictionaryData;

public class DictionaryRadioTag implements Tag {

	////jstl方法----------------------
	private PageContext pageContext;
	private String type;
	private String value;
	private String extend;
	private String showText;
	public void setPageContext(PageContext pageContext) {
		this.pageContext=pageContext;
		value=StringUtil.noSpace(value);
		extend=StringUtil.noSpace(extend);
		showText=StringUtil.noSpace(showText);
	}
	public int doStartTag() throws JspException {
		return Tag.EVAL_BODY_INCLUDE;
	}
	public int doEndTag() throws JspException {
		DictionaryUtil.init();
		StringBuffer sb=new StringBuffer();
		Map<String,DictionaryData> dd=DictionaryUtil.map.get(type);
		if(null!=dd){
			for(String key : dd.keySet()){
				sb.append("<input type=\"radio\" title=\"").append(dd.get(key).getName()).append("\" value=\"").append(key).append("\" ").append(DictionaryUtil.isContain(value, key)?"checked":"").append(" ").append(extend).append(">");
				if("true".equals(showText)) sb.append(dd.get(key).getName());
			}
		}
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
	public String getShowText() {
		return showText;
	}
	public void setShowText(String showText) {
		this.showText = showText;
	}
}
