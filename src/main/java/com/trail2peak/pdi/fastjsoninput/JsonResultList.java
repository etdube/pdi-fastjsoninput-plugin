package com.trail2peak.pdi.fastjsoninput;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;

public class JsonResultList {

	private List<Object> l;
	private boolean nullValue;

	public JsonResultList() throws KettleException {
		this.l = new ArrayList<Object>();
		setNull(false);
	}

	public JsonResultList(List<Object> l) throws KettleException {
		this.l = l;
		setNull(false);
	}

	public void setNull(boolean value) {
		this.nullValue = value;
	}

	public boolean isNull() {
		return this.nullValue;
	}

	public List<Object> getObjectList() {
		return this.l;
	}

	public void add(String value) {
		this.l.add(value);
	}

	public int size() {
		return this.l.size();
	}

}
