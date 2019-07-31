package m.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import m.common.action.Action;
import m.common.action.ActionMeta;
import m.common.model.FieldMeta;
import m.common.model.HostInfo;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.util.ModelUtil;
import m.common.service.HostInfoService;
import m.common.socket.HostSocketUtil;
import m.system.cache.CacheUtil;
import m.system.cache.model.CacheSynch;
import m.system.exception.MException;
import m.system.task.TaskUtil;
import m.system.util.AnnotationUtil;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class SystemInit {
	/**
	 * 设置数据库名称
	 * @param tableSchema
	 */
	public static void setTableSchema(String tableSchema){
		ModelConfig.setTableSchema(tableSchema);
	}
	/**
	 * 初始化数据表结构缓存
	 * @param modelPage
	 */
	@SuppressWarnings("unchecked")
	public static void initModel(String modelPack){
		if(StringUtil.isSpace(modelPack)) return ;
		String[] packs=modelPack.split(",");
		List<Class<Model>> lastInit=new ArrayList<Class<Model>>();
		for(int i=0;i<packs.length;i++){
			String[] modelNames=ClassUtil.getAllQualifiedName4Class(packs[i]);
			for(int j=0;j<modelNames.length;j++){
				String modelName=modelNames[j];
				try {
					Class<?> cc=(Class<?>) Class.forName(modelName);
					if(Model.class.isAssignableFrom(cc)){
						Class<Model> clazz=(Class<Model>)cc;
						TableMeta tm=AnnotationUtil.getAnnotation4Class(TableMeta.class,clazz);
						if(null!=tm){
							if(tm.isView()){
								lastInit.add(clazz);
							}else{
								initModel(clazz,tm);
							}
						}
					}
				} catch (ClassNotFoundException e) { }
			}
		}
		for(Class<Model> clazz : lastInit){
			TableMeta tm=AnnotationUtil.getAnnotation4Class(TableMeta.class,clazz);
			initModel(clazz,tm);
		}
		//特殊表初始化
		initModel(CacheSynch.class,AnnotationUtil.getAnnotation4Class(TableMeta.class,CacheSynch.class));
	}
	private static <T extends Model> void initModel(Class<T> clazz,TableMeta tm){
		if(null!=tm) {
			Map<String,FieldMeta> fieldMap=AnnotationUtil.getAnnotationMap4Field(FieldMeta.class,clazz);
			Map<String,LinkTableMeta> linkTableMap=AnnotationUtil.getAnnotationMap4Field(LinkTableMeta.class,clazz);
			ModelConfig.fillModelInfo(clazz, tm, fieldMap, linkTableMap);
		}
	}
	/**
	 * 初始化数据库表结构
	 */
	public static void initModelTable(){
		boolean isMain=false;
		HostInfo host=RuntimeData.getHostInfo();
		if(null==host||host.getMain()==1) isMain=true;
		if(!isMain) return;
		
		for(Class<? extends Model> clazz : ModelConfig.getTableList()){
			ModelUtil.initModelTable(clazz);
		}
		//表初始化后
		CacheUtil.initSynch();
	}
	public static void initServerGroup(String ip,int port){
		if(!StringUtil.isSpace(ip)){
			System.out.println("主控主机地址："+ip);
			HostSocketUtil.openServer(port);
			HostSocketUtil.openClient(ip, port);
		}else{
			try {
				RuntimeData.getService(HostInfoService.class).setHostInfo(".", new Date(), 0, 0, 1, 1);
			} catch (MException e) {
				e.printStackTrace();
			}
			initModelTable();
			initClassRun();
			taskClassRun();
		}
	}
	private static String initClass;
	public static void setInitClass(String ic){
		initClass=ic;
	}
	public static void initClassRun(){
		if(StringUtil.isSpace(initClass)) return ;
		String[] clazzs=initClass.split(",");
		for(int i=0;i<clazzs.length;i++){
			try {
				ClassUtil.executeMethod(Class.forName(clazzs[i]).newInstance(), "execute");
			} catch (Exception e) {
				System.err.println("初始化错误!"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	private static String taskClass;
	public static void setTaskClass(String tc){
		taskClass=tc;
	}
	public static void taskClassRun(){
		if(StringUtil.isSpace(taskClass)) return ;
		String[] clazzs=taskClass.split("&");
		List<String[]> list=new ArrayList<String[]>();
		for(String clazz : clazzs){
			String[] arr=clazz.split("\\|");
			if(arr.length==2){
				list.add(arr);
			}else{
				System.err.println("定时类配置错误!"+clazz);
			}
		}
		TaskUtil.initTask(list);
	}
	@SuppressWarnings("unchecked")
	public static void initAction(String actionPack){
		if(StringUtil.isSpace(actionPack)) return ;
		String[] packs=actionPack.split(",");
		for(int i=0;i<packs.length;i++){
			String[] modelNames=ClassUtil.getAllQualifiedName4Class(packs[i]);
			for(int j=0;j<modelNames.length;j++){
				String modelName=modelNames[j];
				try {
					Class<?> clazz=Class.forName(modelName);
					if(Action.class.isAssignableFrom(clazz)){
						ActionMeta meta=AnnotationUtil.getAnnotation4Class(ActionMeta.class, clazz);
						if(null!=meta){
							RuntimeData.fillAction(meta.name(), (Class<Action>)clazz);
						}
					}
				} catch (ClassNotFoundException e) { }
			}
		}
	}
}
