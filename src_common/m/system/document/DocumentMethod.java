package m.system.document;

public class DocumentMethod {
	private String title;
	private String description;
	private String result;
	private String path;
	private Boolean permission;
	private DocumentParam[] params;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Boolean getPermission() {
		return permission;
	}
	public void setPermission(Boolean permission) {
		this.permission = permission;
	}
	public DocumentParam[] getParams() {
		return params;
	}
	public void setParams(DocumentParam[] params) {
		this.params = params;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
