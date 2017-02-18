package com.javaping.message;

import java.util.LinkedList;

/**
 * Object for storing message information.
 * These values are extracted on message receival.
 * The object stores a map of <Message, ArrivalTime>
 * @author pord911
 *
 */
public class BufferQueueElement<T> {
	private LinkedList<T> element;
	private boolean streamClosed = false;

	public BufferQueueElement() {
		element = new LinkedList<T>();
	}

	public boolean isStreamClosed() {
		return streamClosed;
	}

	public void setStreamClosed(boolean streamClosed) {
		this.streamClosed = streamClosed;
	}

	public int getListLength() {
		return element.size();
	}

	public LinkedList<T> getMessageList() {
		return element;
	}

	public void setMessageElement(T e) {
		element.add(e);
	}
}
