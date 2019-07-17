package manage.model;

import m.common.model.FieldMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
@TableMeta(name="v_image_admin",description="图片管理者",isView=true)
public class ImageAdmin extends Model {
	@FieldMeta(name="token",type=FieldType.STRING,length=100,description="token")
	private String token;
	@FieldMeta(name="name",type=FieldType.STRING,length=100,description="名称")
	private String name;
	@FieldMeta(name="type",type=FieldType.STRING,length=20,description="类型")
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
