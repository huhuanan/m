package m.common.model.document;

public class DocumentModel {
	private String clazz;
	private String name;
	private String description;
	private DocumentField[] fields;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DocumentField[] getFields() {
		return fields;
	}
	public void setFields(DocumentField[] fields) {
		this.fields = fields;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
