package com.telit.info.data.business;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryUserMeal implements Serializable {
    List<Integer> schoolId;
    List<String> operator;
    String merge;
}
