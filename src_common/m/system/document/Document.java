package m.system.document;

public class Document {
	private String className;
	private String title;
	private String description;
	private DocumentMethod[] methods;
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
	public DocumentMethod[] getMethods() {
		return methods;
	}
	public void setMethods(DocumentMethod[] methods) {
		this.methods = methods;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
