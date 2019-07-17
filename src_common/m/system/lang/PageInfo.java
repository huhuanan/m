package m.system.lang;

import java.util.List;

import m.common.model.Model;

public class PageInfo {

	private List<? extends Model> list;
	private Integer count;
	private Integer num;
	private Integer index;
	private Integer pageCount;//总共几页
	private Integer pageIndex;//当前第几页
	/**
	 * 查询包含分页信息的list
	 * @param list 结果list
	 * @param count 总记录数
	 * @param index 开始记录数
	 * @param num 每页显示
	 */
	public PageInfo(List<? extends Model> list,Integer count,Integer index,Integer num){
		this.list=list;
		this.count=count;
		this.index=index;
		this.num=num;
		this.pageCount=this.count/this.num-(this.count%this.num!=0?0:1)+1;
		this.pageIndex=this.index/this.num+1;
	}
	public List<? extends Model> getList() {
		return list;
	}
	public void setList(List<? extends Model> list) {
		this.list = list;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
}
