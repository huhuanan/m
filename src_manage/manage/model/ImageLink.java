package manage.model;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_image_link",description="图片链接表")
public class ImageLink extends Model {

	@FieldMeta(name="business_oid",type=FieldType.STRING,length=20,description="业务主键")
	private String businessOid;
	@FieldMeta(name="business",type=FieldType.STRING,length=100,description="业务描述")
	private String business;
	@LinkTableMeta(name="image_admin_oid",table=ImageAdmin.class,description="用户表")
	private ImageAdmin imageAdmin;
	@LinkTableMeta(name="image_info_oid",table=ImageInfo.class,description="图片表")
	private ImageInfo imageInfo;
	public String getBusinessOid() {
		return businessOid;
	}
	public void setBusinessOid(String businessOid) {
		this.businessOid = businessOid;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public ImageInfo getImageInfo() {
		return imageInfo;
	}
	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}
	public ImageAdmin getImageAdmin() {
		return imageAdmin;
	}
	public void setImageAdmin(ImageAdmin imageAdmin) {
		this.imageAdmin = imageAdmin;
	}
}
