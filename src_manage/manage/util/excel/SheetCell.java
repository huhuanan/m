package manage.util.excel;

import jxl.format.Alignment;
import jxl.format.Colour;

public class SheetCell {
	private String content;
	private Alignment align;
	private Integer width;
	private Colour background;
	
	/**
	 * 
	 * @param content
	 * @param width
	 */
	public SheetCell(String content,Integer width){
		this(content,width,Alignment.LEFT);
	}
	/**
	 * 
	 * @param content
	 * @param width
	 * @param align
	 */
	public SheetCell(String content,Integer width,Alignment align){
		this.content=content;
		this.width=width;
		this.align=align;
		this.background=Colour.WHITE;
	}
	
	/**
	 * 
	 * @param content
	 * @param width
	 * @param align
	 * @return
	 */
	public static SheetCell headCell(String content,int width,Alignment align){
		SheetCell cell=new SheetCell(content,width,align);
		cell.setBackground(Colour.GRAY_25);
		return cell;
	}
	
	protected String getContent() {
		return content;
	}
	protected void setContent(String content) {
		this.content = content;
	}
	protected Alignment getAlign() {
		return align;
	}
	protected void setAlign(Alignment align) {
		this.align = align;
	}
	protected Integer getWidth() {
		return width;
	}
	protected void setWidth(Integer width) {
		this.width = width;
	}
	protected Colour getBackground() {
		return background;
	}
	protected void setBackground(Colour background) {
		this.background = background;
	}
}
