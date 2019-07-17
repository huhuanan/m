package m.common.model.util;

public class QueryPage {
	private int index;
	private int num;
	/**
	 * 翻页参数
	 */
	public QueryPage(){
		this.index=0;
		this.num=10;
	}
	/**
	 * 翻页参数
	 * @param index 开始位置
	 * @param num 显示条数
	 */
	public QueryPage(int index,int num){
		this.index=index;
		this.num=num;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
