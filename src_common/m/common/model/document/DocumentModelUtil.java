package m.common.model.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.type.FieldType;
import m.system.exception.MException;

public class DocumentModelUtil {
	public static List<DocumentModel> documentList() throws MException{
		List<DocumentModel> dmlist=new ArrayList<DocumentModel>();
		List<Class<? extends Model>> list=ModelConfig.getTableList();
		for(Class<? extends Model> cl : list){
			dmlist.add(getDocumentModel(cl));
		}
		return dmlist;
	}
	public static DocumentModel getDocumentModel(Class<? extends Model> clazz){
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		DocumentModel model=new DocumentModel();
		model.setClazz(clazz.getName());
		model.setName(tableMeta.name());
		model.setDescription(tableMeta.description());
		model.setFields(getDocumentFields(clazz));
		return model;
	}
	public static DocumentField[] getDocumentFields(Class<? extends Model> clazz){
		List<DocumentField> list=new ArrayList<DocumentField>();
		DocumentField documentField=new DocumentField();
		documentField.setField("oid");
		documentField.setName("oid");
		documentField.setDescription("oid主键");
		documentField.setType(FieldType.STRING);
		documentField.setLength(20);
		documentField.setNotnull(true);
		list.add(documentField);
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		for(String field : fieldMap.keySet()){
			FieldMeta fieldMeta=fieldMap.get(field);
			DocumentField df=new DocumentField();
			df.setField(field);
			df.setName(fieldMeta.name());
			df.setDescription(fieldMeta.description());
			df.setType(fieldMeta.type());
			df.setLength(fieldMeta.length());
			df.setNotnull(fieldMeta.notnull());
			list.add(df);
		}
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		for(String field : linkTableMap.keySet()){
			LinkTableMeta linkMeta=linkTableMap.get(field);
			DocumentField df=new DocumentField();
			df.setField(field);
			df.setName(linkMeta.name());
			df.setDescription(linkMeta.description());
			df.setType(FieldType.STRING);
			df.setLength(20);
			df.setNotnull(false);
			df.setLinkName(ModelConfig.getTableMeta(linkMeta.table()).name());
			df.setLinkClazz(linkMeta.table().getName());
			list.add(df);
		}
		return list.toArray(new DocumentField[]{});
	}
}
