package manage.model;

import m.common.model.FieldMeta;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_icon_info",description="图标表")
public class IconInfo extends StatusModel {

	@FieldMeta(name="path",type=FieldType.STRING,length=200,description="文件路径")
	private String path;
	@FieldMeta(name="name",type=FieldType.STRING,length=200,description="文件名称")
	private String name;

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
