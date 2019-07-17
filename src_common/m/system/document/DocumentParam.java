package m.system.document;

import m.common.model.type.FieldType;

public class DocumentParam {
	private String name;
	private String description;
	private FieldType type;
	private Boolean notnull;
	private Integer length;
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
}
