package manage.service;

import java.io.File;
import java.util.Date;

import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.util.DateUtil;
import m.system.util.FileUtil;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.FileInfo;

public class FileInfoService extends Service {

	/**
	 * 获取文件名称
	 * @param oid
	 * @return
	 */
	public static String getFileName(String oid){
		try {
			if(StringUtil.isSpace(oid))return "";
			if(oid.indexOf("\"")==0)oid=oid.substring(1);
			if(oid.lastIndexOf("\"")==oid.length()-1)oid=oid.substring(0,oid.length()-1);
			DataRow dr = DBManager.queryFirstRow("select name from os_file_info where oid=?",new String[]{oid});
			if(null!=dr){
				return dr.get(String.class, "name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 保存图片
	 * @param model
	 * @param file
	 * @throws Exception
	 */
	public void saveFile(FileInfo model, File file) throws Exception {
		String key=GenerateID.generatePrimaryKey();
		if(null==model.getPath()||model.getPath().equals("/")) {
			model.setPath(new StringBuffer(RuntimeData.getFilePath()).append("file/").append(DateUtil.format(new Date(),"yyyyMM")).append("/").append(DateUtil.format(new Date(),"ddHH")).append("/").toString());
			model.setFilePath(new StringBuffer(model.getPath()).append(key).append("_").append(model.getName()).toString());
		}else {
			model.setFilePath(new StringBuffer(model.getPath()).append(model.getName()).toString());
		}
		model.setOid(key);
		model.setCreateDate(new Date());
		FileUtil.writeWebFile(model.getFilePath(), file);
		ModelUpdateUtil.insertModel(model);
	}
}
