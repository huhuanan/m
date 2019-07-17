package m.common.model;

public interface SystemInfoModel {
	public SystemInfoModel getUniqueModel();
	
	public String getStaticDomain();//静态加速域名
	public String getStaticMode();//静态加速模式 N不加速, A域名加速,B主机间加速啊,C全加速
}
