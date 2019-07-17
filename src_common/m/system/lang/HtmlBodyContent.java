package m.system.lang;

public class HtmlBodyContent {
	private String htmlBody;
	public HtmlBodyContent(String htmlBody){
		this.htmlBody=htmlBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	public String toString(){
		return this.htmlBody;
	}
}
