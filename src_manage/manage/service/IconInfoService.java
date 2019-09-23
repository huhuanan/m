package manage.service;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.util.FileUtil;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.IconInfo;

public class IconInfoService extends Service {

	public void saveIcon(IconInfo model,File file) throws Exception {
		if(file.length()/1024l>101l) throw new MException(this.getClass(), "图标不能超过100k");
		model.setOid(GenerateID.generatePrimaryKey());
		model.setPath(new StringBuffer(RuntimeData.getFilePath()).append("icon/").append(model.getOid()).append("_").append(model.getName()).toString());

		FileUtil.writeWebFile(model.getPath(), file);
		ModelUpdateUtil.insertModel(model);
	}

	public List<IconInfo> getIconList(QueryPage page) throws SQLException, MException {
		return ModelQueryList.getModelList(IconInfo.class, new String[] {"*"}, page,null);
	}

	public static String getIconPath(String oid) {
		try {
			if(StringUtil.isSpace(oid))return "";
			if(oid.indexOf("\"")==0)oid=oid.substring(1);
			if(oid.lastIndexOf("\"")==oid.length()-1)oid=oid.substring(0,oid.length()-1);
			DataRow dr = DBManager.queryFirstRow("select path from os_icon_info where oid=?",new String[]{oid});
			if(null!=dr){
				return RuntimeData.getStaticDomain()+dr.get(String.class, "path");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
