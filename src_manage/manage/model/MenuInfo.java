package manage.model;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
@TableMeta(name="os_menu_info",description="菜单信息表")
public class MenuInfo extends Model {

	@FieldMeta(name="name",type=FieldType.STRING,length=20,description="标题")
	private String name;
	@FieldMeta(name="url_path",type=FieldType.STRING,length=200,description="地址")
	private String urlPath;
	@FieldMeta(name="sort",type=FieldType.INT,description="排序")
	private Integer sort;
	@FieldMeta(name="description",type=FieldType.STRING,length=200,description="描述")
	private String description;
	@FieldMeta(name="ico_style",type=FieldType.STRING,length=20,description="图标样式")
	private String icoStyle;
	@LinkTableMeta(name="parent_menu_oid",table=MenuInfo.class,description="所属菜单")
	private MenuInfo parentMenu;
	@LinkTableMeta(name="module_oid",table=ModuleInfo.class,description="所属模块")
	private ModuleInfo moduleInfo;
	@FieldMeta(name="todo_class",type=FieldType.STRING,length=100,description="待办实现类")
	private String todoClass;
	@FieldMeta(name="is_public",type=FieldType.STRING,length=1,description="是否公共Y/N")
	private String isPublic;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcoStyle() {
		return icoStyle;
	}
	public void setIcoStyle(String icoStyle) {
		this.icoStyle = icoStyle;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}
	public void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
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
	public MenuInfo getParentMenu() {
		return parentMenu;
	}
	public void setParentMenu(MenuInfo parentMenu) {
		this.parentMenu = parentMenu;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTodoClass() {
		return todoClass;
	}
	public void setTodoClass(String todoClass) {
		this.todoClass = todoClass;
	}
}
