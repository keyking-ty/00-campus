package com.telit.info.data.business;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "t_market")
public class UsedItem extends GoodsData{
	String title;
	Float price;
}
