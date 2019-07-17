package manage.util.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelObject {
	private String name;
	private List<SheetObject> sheets;
	public ExcelObject(String name){
		this.name=name;
		this.sheets=new ArrayList<SheetObject>();
	}
	public ExcelObject addSheet(SheetObject sheet){
		this.sheets.add(sheet);
		return this;
	}
	
	
	public File toExcelFile() throws WriteException, IOException{
		File f=new File(File.createTempFile("export_excel_",".tmp").getAbsolutePath());
		System.out.println("导出("+this.name+")所创建的临时文件:"+f.getAbsolutePath());
		WritableWorkbook book=Workbook.createWorkbook(f);
		for(int i=0;i<this.sheets.size();i++){
			SheetObject sh=this.sheets.get(i);
			WritableSheet sheet=book.createSheet(sh.getName(),i);
			fillBodyCell(sheet,sh);	//填充内容
		}
		book.write();
		book.close();
		f.deleteOnExit();
		return null==f?null:f;
	}

	/**
	 * 填充内容
	 * @param sheet
	 * @param fields
	 * @param data
	 * @throws WriteException 
	 * @throws WriteException
	 */
	private static void fillBodyCell(WritableSheet sheet,SheetObject sh) throws WriteException {
		for(int i=0;i<sh.getRows().length;i++){
			SheetRow row=sh.getRows()[i];
			if(null!=row.getHeight()){
				CellView cc=new CellView();
				cc.setSize(row.getHeight()*35);
				sheet.setRowView(i, cc);
			}
			for(int j=0;j<row.getCells().length;j++){
				SheetCell cell=row.getCells()[j];
				WritableFont font=new WritableFont(WritableFont.TIMES,11);
				WritableCellFormat format=new WritableCellFormat(font);
				format.setWrap(true);
				format.setAlignment(cell.getAlign());
				format.setVerticalAlignment(VerticalAlignment.CENTRE);
				format.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK);
				format.setBackground(cell.getBackground());
				Label label=new Label(j,i,cell.getContent(),format);
				sheet.addCell(label);
				if(null!=cell.getWidth()){
					CellView cv=new CellView();
					cv.setSize(cell.getWidth()*35);
					sheet.setColumnView(j, cv);
				}
			}
		}
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<SheetObject> getSheets() {
		return sheets;
	}


	public void setSheets(List<SheetObject> sheets) {
		this.sheets = sheets;
	}
}
