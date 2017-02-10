package net.wendal.nutzbook.common.page;
/**
 * @author 科技㊣²º¹³
 * 2014年2月3日 下午4:48:45
 * http://www.rekoe.com
 * QQ:5382211
 */
public interface Paginable {

	/**
	 * 总记录数
	 * 
	 * @return
	 */
	public int getTotalCount();

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public int getTotalPage();

	/**
	 * 每页记录数
	 * 
	 * @return
	 */
	public int getPageSize();

	/**
	 * 当前页号
	 * 
	 * @return
	 */
	public int getPageNo();

	/**
	 * 是否第一页
	 * 
	 * @return
	 */
	public boolean isFirstPage();

	/**
	 * 是否最后一页
	 * 
	 * @return
	 */
	public boolean isLastPage();

	/**
	 * 返回下页的页号
	 */
	public int getNextPage();

	/**
	 * 返回上页的页号
	 */
	public int getPrePage();

}
