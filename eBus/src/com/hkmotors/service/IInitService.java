package com.hkmotors.service;

import java.util.Map;

/**
 * 系统启动初始化服务
 * 
 */
public interface IInitService {

	public void init();

	/**
	 * 得到property指定名称的配置属性
	 * 
	 * @param propertyName
	 *            属性名称
	 * @return
	 */
	public String getProperty(String propertyName);

	/**
	 * 设置各URL
	 * 
	 * @param propertyMap
	 */
	public void setPropertyMap(Map<String, String> propertyMap);
}
