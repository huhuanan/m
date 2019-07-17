package manage.util.excel;


public class SheetObject {
	private String name;
	private SheetRow[] rows;
	/**
	 * 
	 * @param rows
	 * @param name
	 */
	public SheetObject(SheetRow[] rows,String name){
		this.rows=rows;
		this.name=name;
	}
	
	protected String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected SheetRow[] getRows() {
		return rows;
	}
	protected void setRows(SheetRow[] rows) {
		this.rows = rows;
	}
}
