package com.telit.info.actions;

public interface SelectAction<S,T>{
	T doAction (S data);
}
