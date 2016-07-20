package com.hkmotors.service.impl;

import java.util.Map;

import com.hkmotors.service.IInitService;

public class InitService implements IInitService {

	private Map<String, String> propertyMap;

	@Override
	public void init() {

	}

	@Override
	public String getProperty(String propertyName) {
		return propertyMap.get(propertyName);
	}

	@Override
	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

}
