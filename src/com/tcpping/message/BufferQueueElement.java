package com.tcpping.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class BufferQueueElement {
	private Map<Long, String> lineList;
	private int msgAcc;
	private int msgNumber;

	public BufferQueueElement() {
		lineList = new LinkedHashMap<Long, String>();
	}

	public Map<Long, String> getLineList() {
		return lineList;
	}

	public void addListElement(Long time, String line) {
		lineList.put(time, line);
	}

	public int getMsgAcc() {
		return msgAcc;
	}

	public void setMsgAcc(int msgAcc) {
		this.msgAcc = msgAcc;
	}

	public int getMsgNumber() {
		return msgNumber;
	}

	public void setMsgNumber(int msgNumber) {
		this.msgNumber = msgNumber;
	}
}
