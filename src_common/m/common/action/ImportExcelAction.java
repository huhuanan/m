package m.common.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import m.system.exception.MException;
import m.system.util.DateUtil;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;

public abstract class ImportExcelAction extends Action {
	/**
	 * 处理上传文件的数据
	 * @param data 数据
	 * @return 处理成功消息
	 * @throws Exception 错误信息
	 */
	protected abstract String processImportData(List<String[]> data) throws MException;

	/**
	 * action 导入方法
	 * @return
	 */
	public JSONMessage importExcel(){
		JSONMessage message=new JSONMessage();

		try {
			List<String[]> list=new ArrayList<String[]>();
			Map<String,File> fileMap=getFileMap();
			for(String key : fileMap.keySet()){
				Workbook rwb = Workbook.getWorkbook(fileMap.get(key));
				Sheet sheet = rwb.getSheet(0);
				for (int i = 1; i < sheet.getRows(); i++) {
					String[] strs=new String[sheet.getColumns()];
					for (int j = 0; j < sheet.getColumns(); j++) {
						Cell cell = sheet.getCell(j, i);
						if(CellType.DATE.equals(cell.getType())){
							DateCell dc = (DateCell)cell; 
							java.util.Date date = dc.getDate();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
							strs[j]=sdf.format(date);
						}else if(CellType.NUMBER.equals(cell.getType())){
							NumberCell nc = (NumberCell)cell;
							strs[j]=String.valueOf(nc.getValue());
						}else{
							strs[j]=cell.getContents().trim();
						}
					}
					list.add(strs);
				}
			}
			if(list.size()<1) throw new Exception("Excel文件中没有数据!");
			message.push("message", processImportData(list));
			message.push("status",1);
		} catch (BiffException e) {
			message.push("message", "文件错误!"+e.getMessage());
			message.push("status",0);
		} catch (IOException e) {
			message.push("message", "文件操作失败!"+e.getMessage());
			message.push("status",0);
		} catch (Exception e) {
			//e.printStackTrace();
			message.push("message", e.getMessage());
			message.push("status",0);
		}
		//System.out.println(message.toJSONString());
		return message;
	}
	/**
	 * 转换double类型
	 * @param str 
	 * @param row 行号
	 * @param errorMessage 错误消息容易
	 * @return
	 */
	protected Double parseDouble(String str,int row,StringBuffer errorMessage) {
		if(StringUtil.isSpace(str)){
			return null;
		}
		try{
			return Double.parseDouble(str);
		}catch(NumberFormatException e){
			errorMessage.append("\r\n第"+row+"行double格式化出错!"+e.getMessage());
		}
		return null;
	}
	/**
	 * 转换Integer类型
	 * @param str 
	 * @param row 行号
	 * @param errorMessage 错误消息容易
	 * @return
	 */
	protected Integer parseInteger(String str,int row,StringBuffer errorMessage) {
		if(StringUtil.isSpace(str)){
			return null;
		}
		try{
			return Integer.parseInt(str);
		}catch(NumberFormatException e){
			errorMessage.append("\r\n第"+row+"行int格式化出错!"+e.getMessage());
		}
		return null;
	}
	/**
	 * 转换Date类型
	 * @param str 
	 * @param pattern 时间格式 
	 * @param row 行号
	 * @param errorMessage 错误消息容易
	 * @return
	 */
	protected Date parseDate(String str,String pattern,int row,StringBuffer errorMessage) {
		if(StringUtil.isSpace(str)){
			return null;
		}
		try{
			return DateUtil.format(str, pattern);
		}catch(NumberFormatException e){
			errorMessage.append("\r\n第"+row+"行date格式化出错!"+e.getMessage());
		}
		return null;
	}
}
