package com.liu.msg;

import org.apache.commons.lang.StringUtils;

public enum DataType {
	HEAD("h"),
	START("s"),
	REGIST("r"),
	EVENT("e"),
	MESSAGE("m"),
	CLOSE("c"),
	NULL("");
	private final String symbal;
	DataType(String symbal) {
		this.symbal = symbal;
	}
	public String getValue() {
		return symbal;
	}
	public static DataType valueOfSymbal(String symbal) {
		if (StringUtils.isBlank(symbal)) { 
			return DataType.NULL;
		} else if (symbal.equals("h")) {
			return DataType.HEAD;
		} else if (symbal.equals("s")) {
			return DataType.START;
		} else if (symbal.equals("r")) {
			return DataType.REGIST;
		} else if (symbal.equals("e")) {
			return DataType.EVENT;
		} else if (symbal.equals("m")) {
			return DataType.MESSAGE;
		} else if (symbal.equals("c")) {
			return DataType.CLOSE;
		}
		return DataType.NULL;
	}
}
