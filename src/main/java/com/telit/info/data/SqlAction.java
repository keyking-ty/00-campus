package com.telit.info.data;

import java.util.Map;

public interface SqlAction <T>{
	T doAction(Map<String,Object> data);
}
