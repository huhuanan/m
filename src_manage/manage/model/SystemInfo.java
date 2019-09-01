package manage.model;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.SystemInfoModel;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
import manage.service.SystemInfoService;
@TableMeta(name="os_system_info",description="系统信息表")
public class SystemInfo extends Model implements SystemInfoModel {
	public SystemInfoModel getUniqueModel() {
		return SystemInfoService.getSystemInfo();
	}
	@FieldMeta(name="domain_name",type=FieldType.STRING,length=200,description="域名")
	private String domainName;
	@FieldMeta(name="static_domain",type=FieldType.STRING,length=200,description="静态域名地址")
	private String staticDomain;
	@FieldMeta(name="static_mode",type=FieldType.STRING,length=1,defaultValue="N",description="静态加速模式|N不加速, A域名加速,B主机间加速啊,C全加速")
	private String staticMode;
	@FieldMeta(name="background_title",type=FieldType.STRING,length=50,description="后台标题")
	private String backgroundTitle;
	@FieldMeta(name="title_type",type=FieldType.STRING,length=1,description="后台显示类型|N标题,Y图片")
	private String titleType;
	@LinkTableMeta(name="title_image_oid",table=ImageInfo.class,description="标题图片")
	private ImageInfo titleImage;
	@FieldMeta(name="bg_type",type=FieldType.STRING,length=1,defaultValue="A",description="背景显示|N不使用背景,Y使用背景,A仅登录背景,B仅界面背景")
	private String backgroundType;
	@LinkTableMeta(name="bg_image_oid",table=ImageInfo.class,description="背景图片")
	private ImageInfo backgroundImage;
	@FieldMeta(name="sms_app_id",type=FieldType.STRING,length=50,description="短信appid")
	private String smsAppId;
	@FieldMeta(name="sms_app_key",type=FieldType.STRING,length=50,description="短信appkey")
	private String smsAppKey;
	@FieldMeta(name="sms_sign",type=FieldType.STRING,length=10,description="短信签名")
	private String smsSign;
	@FieldMeta(name="sms_verify_tid",type=FieldType.STRING,length=10,description="短信验证模板id")
	private String smsVerifyTid;
	@FieldMeta(name="sms_debug",type=FieldType.STRING,length=1,description="后台显示类型|N不调试 发送短信,Y调试 不发送并显示验证码")
	private String smsDebug;
	
	public String getSmsAppId() {
		return smsAppId;
	}
	public String getStaticDomain() {
		return staticDomain;
	}
	public void setStaticDomain(String staticDomain) {
		this.staticDomain = staticDomain;
	}
	public String getBackgroundType() {
		return backgroundType;
	}
	public void setBackgroundType(String backgroundType) {
		this.backgroundType = backgroundType;
	}
	public ImageInfo getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(ImageInfo backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public String getStaticMode() {
		return staticMode;
	}
	public void setStaticMode(String staticMode) {
		this.staticMode = staticMode;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public void setSmsAppId(String smsAppId) {
		this.smsAppId = smsAppId;
	}
	public String getSmsAppKey() {
		return smsAppKey;
	}
	public void setSmsAppKey(String smsAppKey) {
		this.smsAppKey = smsAppKey;
	}
	public String getSmsSign() {
		return smsSign;
	}
	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	public String getSmsVerifyTid() {
		return smsVerifyTid;
	}
	public void setSmsVerifyTid(String smsVerifyTid) {
		this.smsVerifyTid = smsVerifyTid;
	}
	public String getBackgroundTitle() {
		return backgroundTitle;
	}
	public void setBackgroundTitle(String backgroundTitle) {
		this.backgroundTitle = backgroundTitle;
	}
	public String getTitleType() {
		return titleType;
	}
	public void setTitleType(String titleType) {
		this.titleType = titleType;
	}
	public String getSmsDebug() {
		return smsDebug;
	}
	public void setSmsDebug(String smsDebug) {
		this.smsDebug = smsDebug;
	}
	public ImageInfo getTitleImage() {
		return titleImage;
	}
	public void setTitleImage(ImageInfo titleImage) {
		this.titleImage = titleImage;
	}
}
