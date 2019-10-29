package com.telit.info.data;

public class FileData {
	String name;
	byte[] data;
	int pos = 0;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void add(byte[] more,int len) {
		if (data == null) {
			data = new byte[more.length];
		}
		if (pos + len > data.length) {
			byte[] newData = new byte[pos + len];
			System.arraycopy(data,0,newData,0,pos);
			data = newData;
		}
		System.arraycopy(more,0,data,pos,len);
		pos += len;
	}
}
