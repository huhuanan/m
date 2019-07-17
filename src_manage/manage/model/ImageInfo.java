package manage.model;

import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_image_info",description="图片表")
public class ImageInfo extends Model {

	@FieldMeta(name="image_type",type=FieldType.STRING,length=100,description="图片类型")
	private String imageType;
	@LinkTableMeta(name="image_admin_oid",table=ImageAdmin.class,description="用户表")
	private ImageAdmin imageAdmin;

	@FieldMeta(name="path",type=FieldType.STRING,length=200,description="文件路径")
	private String path;
	@FieldMeta(name="img_path",type=FieldType.STRING,length=200,description="图片路径")
	private String imgPath;
	@FieldMeta(name="thum_path",type=FieldType.STRING,length=200,description="缩略图路径")
	private String thumPath;
	@FieldMeta(name="thum_ratio",type=FieldType.DOUBLE,description="缩略图比例|宽/高")
	private Double thumRatio;
	@FieldMeta(name="thum_width",type=FieldType.DOUBLE,description="缩略宽")
	private Double thumWidth;

	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getThumPath() {
		return thumPath;
	}
	public void setThumPath(String thumPath) {
		this.thumPath = thumPath;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public ImageAdmin getImageAdmin() {
		return imageAdmin;
	}
	public void setImageAdmin(ImageAdmin imageAdmin) {
		this.imageAdmin = imageAdmin;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public Double getThumRatio() {
		return thumRatio;
	}
	public void setThumRatio(Double thumRatio) {
		this.thumRatio = thumRatio;
	}
	public Double getThumWidth() {
		return thumWidth;
	}
	public void setThumWidth(Double thumWidth) {
		this.thumWidth = thumWidth;
	}
	
}
