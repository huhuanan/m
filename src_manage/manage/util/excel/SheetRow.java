package manage.util.excel;


public class SheetRow {
	private Integer height;
	private SheetCell[] cells;

	/**
	 * 
	 * @param cells
	 */
	public SheetRow(SheetCell[] cells){
		this(cells,null);
	}
	/**
	 * 
	 * @param cells
	 * @param height
	 */
	public SheetRow(SheetCell[] cells,Integer height){
		this.cells=cells;
		this.height=height;
	}


	protected SheetCell[] getCells() {
		return cells;
	}


	protected void setCells(SheetCell[] cells) {
		this.cells = cells;
	}
	protected Integer getHeight() {
		return height;
	}
	protected void setHeight(Integer height) {
		this.height = height;
	}
}
