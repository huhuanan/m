package manage.model;

import m.common.model.FieldMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_dictionary_type",description="字典类型表")
public class DictionaryType extends Model {
	@FieldMeta(name="name",type=FieldType.STRING,length=20,notnull=true,description="类型名")
	private String name;
	@FieldMeta(name="type",type=FieldType.STRING,length=20,notnull=true,description="类型")
	private String type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
