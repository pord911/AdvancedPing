package com.tcpping.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class BufferQueueElement {
	private  Map<String, Long> lineList;
	private int msgAcc;
	private int msgNumber;
	private boolean closeQueue = false;

	public BufferQueueElement() {
		lineList = new LinkedHashMap<String, Long>();
	}

	public Map<String, Long> getLineList() {
		return lineList;
	}

	public void addListElement(String line, Long time) {
		lineList.put(line, time);
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

	public boolean isCloseQueue() {
		return closeQueue;
	}

	public void setCloseQueue(boolean closeQueue) {
		this.closeQueue = closeQueue;
	}
}
