package manage.model;

import m.common.model.FieldMeta;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_admin_group",description="用户组表")
public class AdminGroup extends StatusModel {

	@FieldMeta(name="name",type=FieldType.STRING,length=20,description="名称")
	private String name;
	@FieldMeta(name="description",type=FieldType.STRING,length=1000,description="描述")
	private String description;
	@FieldMeta(name="type",type=FieldType.STRING,length=1,defaultValue="A",description="类型|A:组,B:角色")
	private String type;
	@FieldMeta(name="sort",type=FieldType.INT,description="排序")
	private Integer sort;
	public Integer getSort() {
		return sort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
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
	
}
