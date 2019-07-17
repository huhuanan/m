package manage.model;

import m.common.model.FieldMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
@TableMeta(name="os_module_info",description="模块信息表")
public class ModuleInfo extends Model {

	@FieldMeta(name="name",type=FieldType.STRING,length=20,description="标题")
	private String name;
	@FieldMeta(name="url_path",type=FieldType.STRING,length=200,description="地址")
	private String urlPath;
	@FieldMeta(name="ico_style",type=FieldType.STRING,length=20,description="图标样式")
	private String icoStyle;
	@FieldMeta(name="sort",type=FieldType.INT,description="排序")
	private Integer sort;
	@FieldMeta(name="is_public",type=FieldType.STRING,length=1,description="是否公共Y/N")
	private String isPublic;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getIcoStyle() {
		return icoStyle;
	}

	public void setIcoStyle(String icoStyle) {
		this.icoStyle = icoStyle;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
}
