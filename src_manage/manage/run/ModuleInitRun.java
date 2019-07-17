package manage.run;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import m.common.model.util.ModelUpdateUtil;
import m.system.SystemInitRun;
import m.system.db.DBManager;
import m.system.db.TransactionManager;
import manage.model.MenuInfo;
import manage.model.ModuleInfo;


public class ModuleInitRun extends SystemInitRun {
	private static String adminPage;
	private static List<String[]> powerList;
	public void run(boolean isMain) {
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/module.xml");
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		TransactionManager tm=new TransactionManager();
		try {
			tm.begin();
			if(isMain) clearModuleMenu();//清除现有菜单,重新插入
			dombuilder = domfac.newDocumentBuilder();
			Document doc=dombuilder.parse(is);
			Element root=doc.getDocumentElement();
			adminPage=getNodeContent(root.getAttributes().getNamedItem("adminPage"));
			NodeList moduleList=root.getChildNodes();
			if(moduleList!=null){
				for(int i=0;i<moduleList.getLength();i++){
					Node module=moduleList.item(i);
					if(null==module.getAttributes()) continue;
					if("power".equals(module.getNodeName())){
						initPowerAttribute(module);
					}else if("module".equals(module.getNodeName())){
						if(!isMain) continue;
						ModuleInfo mi=new ModuleInfo();
						mi.setOid(getNodeContent(module.getAttributes().getNamedItem("oid")));
						//mi=ModelQueryUtil.getModel(mi);
						mi.setName(getNodeContent(module.getAttributes().getNamedItem("name")));
						mi.setSort(Integer.parseInt(getNodeContent(module.getAttributes().getNamedItem("sort"))));
						mi.setUrlPath(getNodeContent(module.getAttributes().getNamedItem("urlPath")));
						mi.setIsPublic(getNodeContent(module.getAttributes().getNamedItem("isPublic")));
						mi.setIcoStyle(getNodeContent(module.getAttributes().getNamedItem("icoStyle")));
						//if(StringUtil.isSpace(mi.getOid())){
						//	mi.setOid(module.getAttributes().getNamedItem("oid")));
							ModelUpdateUtil.insertModel(mi);
						//}else{
						//	ModelUpdateUtil.updateModel(mi);
						//}
						updateMenu(module,mi,null);
					}
				}
			}
			tm.commit();
		} catch (Exception e) {
			tm.rollback();
			//throw new MException(ModuleInitRun.class,"初始化模块菜单出错!"+e.getMessage());
			System.out.println("初始化模块菜单出错!");
			e.printStackTrace();
		}
	}
	private void clearModuleMenu() throws SQLException{
		DBManager.executeUpdate("TRUNCATE TABLE os_module_info");
		DBManager.executeUpdate("TRUNCATE TABLE os_menu_info");
	}
	private void updateMenu(Node module,ModuleInfo moduleObj,MenuInfo menuObj) throws Exception{
		NodeList menuList=module.getChildNodes();
		if(menuList!=null){
			for(int i=0;i<menuList.getLength();i++){
				Node menu=menuList.item(i);
				if(null==menu.getAttributes()) continue;
				MenuInfo mi=new MenuInfo();
				mi.setOid(getNodeContent(menu.getAttributes().getNamedItem("oid")));
				//mi=ModelQueryUtil.getModel(mi);
				mi.setName(getNodeContent(menu.getAttributes().getNamedItem("name")));
				mi.setSort(Integer.parseInt(getNodeContent(menu.getAttributes().getNamedItem("sort"))));
				mi.setUrlPath(getNodeContent(menu.getAttributes().getNamedItem("urlPath")));
				mi.setIsPublic(getNodeContent(menu.getAttributes().getNamedItem("isPublic")));
				mi.setIcoStyle(getNodeContent(menu.getAttributes().getNamedItem("icoStyle")));
				mi.setDescription(getNodeContent(menu.getAttributes().getNamedItem("description")));
				mi.setTodoClass(getNodeContent(menu.getAttributes().getNamedItem("todoClass")));
				mi.setModuleInfo(moduleObj);
				if(null!=menuObj){
					mi.setParentMenu(menuObj);
				}
				//if(StringUtil.isSpace(mi.getOid())){
				//	mi.setOid(menu.getAttributes().getNamedItem("oid")));
					ModelUpdateUtil.insertModel(mi);
				//}else{
				//	ModelUpdateUtil.updateModel(mi);
				//}
				updateMenu(menu,moduleObj,mi);
			}
		}
	}
	private String getNodeContent(Node node){
		if(null==node){
			return "";
		}else{
			return node.getTextContent();
		}
	}
	private void initPowerAttribute(Node module){
		powerList=new ArrayList<String[]>();
		NodeList menuList=module.getChildNodes();
		if(menuList!=null){
			for(int i=0;i<menuList.getLength();i++){
				Node operation=menuList.item(i);
				if(null==operation.getAttributes()) continue;
				powerList.add(new String[]{getNodeContent(operation.getAttributes().getNamedItem("name")), getNodeContent(operation.getAttributes().getNamedItem("description"))});
			}
		}
	}
	public static List<String[]> getPowerList() {
		return powerList;
	}
	public static String getAdminPage() {
		return adminPage;
	}
}
