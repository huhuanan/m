package m.system.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import m.common.action.Action;
import m.common.action.ActionMeta;
import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.type.FieldType;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.AnnotationUtil;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class DocumentUtil {
	/**
	 *  返回所有Action文档
	 * @return
	 * @throws MException
	 */
	public static List<Document> documentList() throws MException{
		List<Document> list=new ArrayList<Document>();
		Map<String,Class<? extends Action>> actionData=RuntimeData.getActionData();
		for(String key : actionData.keySet()){
			Document doc=getDocument(actionData.get(key));
			if(doc.getMethods().length>0){
				list.add(doc);
			}
		}
		return list;
	}
	private static Document getDocument(Class<? extends Action> clazz) throws MException{
		Document doc=new Document();
		ActionMeta meta=AnnotationUtil.getAnnotation4Class(ActionMeta.class, clazz);
		doc.setClassName(clazz.getName());
		doc.setTitle(meta.title());
		if(StringUtil.isSpace(doc.getTitle())){
			doc.setTitle(meta.name());
		}
		doc.setDescription(meta.description());
		doc.setMethods(getMethods(new StringBuffer("action/").append(meta.name()).toString(),clazz));
		return doc;
	}
	private static DocumentMethod[] getMethods(String actionPath,Class<? extends Action> clazz) throws MException{
		List<DocumentMethod> list=new ArrayList<DocumentMethod>();
		Map<String,DocumentMeta> map=AnnotationUtil.getAnnotationMap4Method(DocumentMeta.class, clazz);
		for(String key : map.keySet()){
			DocumentMeta meta=map.get(key);
			if(null!=meta){
				DocumentMethod method=new DocumentMethod();
				method.setPath(new StringBuffer(actionPath).append("/").append(key).toString());
				method.setTitle(meta.method().title());
				method.setDescription(meta.method().description());
				method.setResult(meta.method().result());
				method.setPermission(meta.method().permission());
				method.setParams(getParams(meta.params(),meta.models(),clazz));
				list.add(method);
			}
		}
		return list.toArray(new DocumentMethod[]{});
	}
	private static DocumentParam[] getParams(DocumentParamMeta[] params,DocumentModelMeta[] models,Class<? extends Action> actionClazz) throws MException{
		List<DocumentParam> list=new ArrayList<DocumentParam>();
		for(DocumentParamMeta meta : params){
			DocumentParam param=new DocumentParam();
			param.setName(meta.name());
			param.setDescription(meta.description());
			param.setType(meta.type());
			param.setLength(meta.length());
			param.setNotnull(meta.notnull());
			list.add(param);
		}
		for(DocumentModelMeta meta : models){
			list.addAll(toParams(meta,actionClazz));
		}
		return list.toArray(new DocumentParam[]{});
	}
	@SuppressWarnings("unchecked")
	private static List<DocumentParam> toParams(DocumentModelMeta meta,Class<? extends Action> actionClazz) throws MException {
		List<DocumentParam> list=new ArrayList<DocumentParam>();
		Class<Model> clazz=null;
		try {
			clazz=(Class<Model>)ClassUtil.getClass(meta.name());
		} catch (ClassNotFoundException e) {
			throw new MException(DocumentUtil.class,"注解DocumentModelMeta有误!"+meta.name());
		}
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		for(String field : meta.fieldNames()){
			if(field.indexOf(".")>-1){
				int i=field.indexOf(".");
				String fn=field.substring(0, i);
				String newField=field.substring(i+1);
				list.add(toParam(new StringBuffer(meta.define()).append(".").append(fn).toString(),newField,meta.notnull(),linkTableMap.get(fn).table(),actionClazz));
			}else{
				list.add(toParam(meta.define(),field,meta.notnull(),clazz,actionClazz));
			}
		}
		return list;
	}
	private static DocumentParam toParam(String define,String field,boolean notnull,Class<? extends Model> clazz,Class<? extends Action> actionClazz) throws MException{
		TableMeta meta=ModelConfig.getTableMeta(clazz);
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		if(field.indexOf(".")>-1){
			int i=field.indexOf(".");
			String fn=field.substring(0, i);
			String newField=field.substring(i+1);
			return toParam(new StringBuffer(define).append(".").append(fn).toString(),newField,notnull,linkTableMap.get(fn).table(),actionClazz);
		}else{
			FieldMeta fm=fieldMap.get(field);
			DocumentParam param=new DocumentParam();
			if(null==fm&&field.equals("oid")){
				param.setName(new StringBuffer(define).append(".oid").toString());
				param.setDescription(new StringBuffer(meta.description()).append(" 主键").toString());
				param.setType(FieldType.STRING);
				param.setLength(20);
				param.setNotnull(notnull);
				return param;
			}else if(null!=fm){
				param.setName(new StringBuffer(define).append(".").append(field).toString());
				param.setDescription(fm.description());
				param.setType(fm.type());
				param.setLength(fm.length());
				param.setNotnull(notnull);
				return param;
			}else{
				throw new MException(DocumentUtil.class,actionClazz.getName()+"注解DocumentModelMeta有误!"+define+"."+field);
			}
		}
	}
}
