package m.common.model.document;

import m.common.model.type.FieldType;

public class DocumentField {
	private String field;
	private String name;
	private String description;
	private FieldType type;
	private Integer length;
	private Boolean notnull;
	private String linkName;
	private String linkClazz;
	public String getLinkClazz() {
		return linkClazz;
	}
	public void setLinkClazz(String linkClazz) {
		this.linkClazz = linkClazz;
	}
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
	public FieldType getType() {
		return type;
	}
	public void setType(FieldType type) {
		this.type = type;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Boolean getNotnull() {
		return notnull;
	}
	public void setNotnull(Boolean notnull) {
		this.notnull = notnull;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
}
