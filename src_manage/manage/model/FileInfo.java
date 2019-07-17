package manage.model;

import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_file_info",description="文件表")
public class FileInfo extends Model {

	@FieldMeta(name="type",type=FieldType.STRING,length=1,description="类型|A普通文件,X保密文件")
	private String type;
	@LinkTableMeta(name="image_admin_oid",table=ImageAdmin.class,description="用户表")
	private ImageAdmin imageAdmin;

	@FieldMeta(name="name",type=FieldType.STRING,length=100,description="文件名称")
	private String name;
	@FieldMeta(name="path",type=FieldType.STRING,length=200,description="文件所在文件夹路径")
	private String path;
	@FieldMeta(name="file_path",type=FieldType.STRING,length=300,description="文件完整路径")
	private String filePath;

	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;

	@FieldMeta(name="allow_down",type=FieldType.STRING,length=1,description="允许下载|Y允许,N不允许")
	private String allowDown;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ImageAdmin getImageAdmin() {
		return imageAdmin;
	}

	public void setImageAdmin(ImageAdmin imageAdmin) {
		this.imageAdmin = imageAdmin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getAllowDown() {
		return allowDown;
	}

	public void setAllowDown(String allowDown) {
		this.allowDown = allowDown;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
