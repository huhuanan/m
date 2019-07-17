package m.common.service;

import m.common.dao.Dao;
import m.system.RuntimeData;
import m.system.exception.MException;

public class Service {
	public static <T extends Dao> T getDao(Class<? extends T> clazz) throws MException{
		return RuntimeData.getDao(clazz);
	}
	public static Dao getDao() throws MException{
		return RuntimeData.getDao(Dao.class);
	}
	public static <T extends Service> T getService(Class<? extends T> clazz) throws MException{
		return RuntimeData.getService(clazz);
	}
	public static Service getService() throws MException{
		return RuntimeData.getService(Service.class);
	}
}
