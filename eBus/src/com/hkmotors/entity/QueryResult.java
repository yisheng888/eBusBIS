/**
 * 
 */
package com.hkmotors.entity;

import java.util.List;

/**
 * 
 * @company yeagiaro
 * @author george.zhao
 * @date 2015-1-8 下午4:28:07
 *
 * @param <T>
 */
public class QueryResult<T> {
	private List<T> list;
	private int total;

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	
	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public QueryResult(List<T> list, int total) {
		this.list = list;
		this.total = total;
	}
}
