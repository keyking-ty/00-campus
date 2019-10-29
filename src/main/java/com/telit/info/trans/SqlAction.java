package com.telit.info.trans;


public interface SqlAction <T>{
	T doAction(SqlMap data);
}
