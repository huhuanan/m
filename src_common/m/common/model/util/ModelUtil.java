package m.common.model.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.type.FieldType;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataSet;
import m.system.exception.MException;
import m.system.lang.HtmlBodyContent;
import m.system.util.ClassUtil;
import m.system.util.FileUtil;
import m.system.util.GenerateID;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;

public class ModelUtil {
	public static <T extends Model> void initModelTable(Class<T> clazz){
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		
		String tableSchema=ModelConfig.getTableSchema();
		if(!StringUtil.isSpace(tableSchema)){
			try {
				if(tableMeta.isView()){//视图表
					creatView(tableMeta);
				}else{
					if(!isHasTable(tableSchema,tableMeta.name())){
						creatTable(clazz);
					}else{
						for(String key : fieldMap.keySet()){
							FieldMeta field=fieldMap.get(key);
							if(!isHasField(tableSchema, tableMeta.name(), field.name())){
								addField2Table(tableSchema,tableMeta.name(),field.name(),getDatabaseType(field),field.description(),field.notnull(),false);
							}
						}
						for(String key : linkTableMap.keySet()){
							LinkTableMeta field=linkTableMap.get(key);
							if(!isHasField(tableSchema, tableMeta.name(), field.name())){
								addField2Table(tableSchema,tableMeta.name(),field.name(),"VARCHAR(20)",field.description(),field.notnull(),true);
							}
						}
					}
				}
			} catch (MException e) {
				e.record();
			}
		}
	}
	/**
	 * 创建或更新视图
	 * @param tableMeta
	 * @throws MException 
	 * @throws SQLException
	 */
	private static void creatView(TableMeta tableMeta) throws MException{
		StringBuffer sql=new StringBuffer();
		String viewSql;
		if(StringUtil.isSpace(tableMeta.viewSql())){
			viewSql=getViewSql(tableMeta.name());
		}else{
			viewSql=tableMeta.viewSql();
		}
		if(StringUtil.isSpace(viewSql)){
			throw new MException(ModelUtil.class,"没有找到"+tableMeta.name()+"的视图!");
		}
		sql.append("CREATE OR REPLACE VIEW ").append(tableMeta.name()).append(" AS ").append(viewSql);
		try {
			DBManager.executeUpdate(sql.toString());
		} catch (SQLException e) {
			throw new MException(ModelUtil.class,"创建视图时SQL出错!"+e.getMessage());
		}
	}
	private static String getViewSql(String tableName) throws MException{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/dbviewsql.xml");
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		try {
			dombuilder = domfac.newDocumentBuilder();
			Document doc=dombuilder.parse(is);
			Element root=doc.getDocumentElement();
			NodeList sqlList=root.getChildNodes();
			if(sqlList!=null){
				for(int i=0;i<sqlList.getLength();i++){
					Node sql=sqlList.item(i);
					if(null==sql.getAttributes()) continue;
					Node attr=sql.getAttributes().getNamedItem("tableName");
					if(null!=attr&&tableName.equals(attr.getTextContent())){
						return sql.getTextContent();
					}
				}
			}
		} catch (Exception e) {
			throw new MException(ModelUtil.class,"获取视图SQL出错!"+e.getMessage());
		}
		return null;
	}
	private static boolean isHasTable(String tableSchema,String tableName) throws MException {
		try {
			DataSet ds=DBManager.executeQuery("select count(*) num from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA=? and TABLE_NAME=?",new String[]{tableSchema,tableName});
			if(ds.size()>0&&ds.get(Long.class, 0, "num")>0){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			throw new MException(ModelUtil.class,"查询是否存在表时SQL出错!"+e.getMessage());
		}
	}
	private static <T extends Model> void creatTable(Class<T> clazz) throws MException{
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		List<String> indexs=new ArrayList<String>();
		StringBuffer sql=new StringBuffer(100);
		sql.append("CREATE TABLE ").append(ModelConfig.getTableSchema()).append(".").append(tableMeta.name()).append(" (");
		sql.append(" oid VARCHAR(50) NOT NULL COMMENT 'oid主键', ");
		for(String key : fieldMap.keySet()){
			FieldMeta field=fieldMap.get(key);
			sql.append(field.name()).append(" ").append(getDatabaseType(field)).append(field.notnull()?" NOT NULL":" NULL").append("  COMMENT '").append(field.description()).append("',");
		}
		for(String key : linkTableMap.keySet()){
			LinkTableMeta field=linkTableMap.get(key);
			indexs.add(field.name());
			sql.append(field.name()).append(" VARCHAR(50) ").append(field.notnull()?" NOT NULL":" NULL").append(" COMMENT '").append(field.description()).append("',");
		}
		sql.append("PRIMARY KEY (oid) ");
		for(String ind : indexs){
			sql.append(",INDEX ind_").append(ind).append(" (").append(ind).append(" ASC)");
		}
		sql.append(" ) ").append("COMMENT='").append(tableMeta.description()).append("' CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci");
		try {
			DBManager.executeUpdate(sql.toString());
			executeInitSql(tableMeta.name());
		} catch (SQLException e) {
			throw new MException(ModelUtil.class,"创建数据库时SQL出错!"+e.getMessage());
		}
	}
	private static void executeInitSql(String tableName) throws MException{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/dbinitsql.xml");
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		try {
			dombuilder = domfac.newDocumentBuilder();
			Document doc=dombuilder.parse(is);
			Element root=doc.getDocumentElement();
			NodeList sqlList=root.getChildNodes();
			if(sqlList!=null){
				for(int i=0;i<sqlList.getLength();i++){
					Node sql=sqlList.item(i);
					if(null==sql.getAttributes()) continue;
					Node attr=sql.getAttributes().getNamedItem("tableName");
					if(null!=attr&&tableName.equals(attr.getTextContent())){
						String[] sqls=sql.getTextContent().split(";");
						DBManager.batchUpdate(sqls);
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new MException(ModelUtil.class,"初始化数据出错!"+e.getMessage());
		}
	}
	private static boolean isHasField(String tableSchema,String tableName,String fieldName) throws MException{
		try {
			DataSet ds=DBManager.executeQuery(new StringBuffer("DESCRIBE ").append(tableSchema).append(".").append(tableName).append(" ").append(fieldName).toString());
			if(ds.size()>0){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			throw new MException(ModelUtil.class,"查询是否存在字段时SQL出错!"+e.getMessage());
		}
	}
	private static void addField2Table(String tableSchema,String tableName,String fieldName,String type,String description,boolean notnull,boolean index) throws MException{
		StringBuffer sql=new StringBuffer("ALTER TABLE ").append(tableSchema).append(".").append(tableName)
			.append(" ADD COLUMN ").append(fieldName).append(" ").append(type).append(notnull?" NOT NULL":" NULL").append(" COMMENT '").append(description).append("'");
		if(index){
			sql.append(",ADD INDEX ind_").append(fieldName).append(" (").append(fieldName).append(" ASC)");
		}
		try {
			DBManager.executeUpdate(sql.toString());
		} catch (SQLException e) {
			throw new MException(ModelUtil.class,"添加字段时SQL出错!"+e.getMessage());
		}
	}
	private static String getDatabaseType(FieldMeta field){
		boolean isDef=false;
		if(!StringUtil.isSpace(field.defaultValue())) isDef=true;
		StringBuffer sb=new StringBuffer();
		if(FieldType.STRING.equals(field.type())){
			sb.append("VARCHAR(").append(field.length()).append(")");
			if(isDef) sb.append(" default '").append(field.defaultValue()).append("'");
		}else if(FieldType.INT.equals(field.type())){
			sb.append("INT");
			if(isDef) sb.append(" default ").append(field.defaultValue());
		}else if(FieldType.DOUBLE.equals(field.type())){
			sb.append("DOUBLE");
			if(isDef) sb.append(" default ").append(field.defaultValue());
		}else if(FieldType.DATE.equals(field.type())){
			return "DATETIME";
		}else{
			return "CHAR(1)";
		}
		return sb.toString();
	}
	/**
	 * 转换对象为jsonMessage
	 * @param <T>
	 * @param a
	 * @param model
	 * @return
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> JSONMessage toJSONMessage(String a,T model) throws MException{
		String prefix=StringUtil.isSpace(a)?"":(a+".");
		JSONMessage message=new JSONMessage();
		Class<T> clazz=(Class<T>) model.getClass();
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		if(null!=model.getOid()){
			message.push(prefix+"oid", model.getOid());
		}else {
			message.push(prefix+"oid", null);
		}
		if(null!=fieldMap) {
			for(String key : fieldMap.keySet()){
				Object obj=ClassUtil.getFieldValue(model, key);
				if(null!=obj){
					message.push(prefix+key,obj);
				}else {
					message.push(prefix+key,null);
				}
			}
		}
		if(null!=linkTableMap) {
			for(String key : linkTableMap.keySet()){
				Model bean=(Model) ClassUtil.getFieldValue(model, key);
				if(null!=bean){
					message.push(prefix+key, toJSONMessage("", bean));
				}else {
					message.push(prefix+key, new HtmlBodyContent("{}"));
				}
			}
		}
		return message;
	}
	public static <T extends Model> File serialize(T model) throws IOException{
		File file=null;
		FileOutputStream fos=null;
		ObjectOutputStream oos=null;
		try {
			String path=new StringBuffer(RuntimeData.getWebPath()).append(RuntimeData.getFilePath()).append("serialize/").append(GenerateID.tempKey()).append(".txt").toString();
			file=FileUtil.getFile(path);
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(model);
			oos.flush();
		} finally {
			if(null!=oos) oos.close();
			if(null!=fos) fos.close();
		}
		return file;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Model> T deserialize(File file) throws IOException, ClassNotFoundException{
		T model=null;
		FileInputStream fis=null;
		ObjectInputStream ois=null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			model=(T)ois.readObject();
		} finally {
			if(null!=fis) fis.close();
			if(null!=ois) ois.close();
		}
		return model;
	}
}
