package com.telit.info.data.app;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_order")
@Data
public class Order implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "order_num")
    private String orderNum;
	@Column(name = "order_type")
    private String orderType;
	@Column(name = "key_id")
    private Integer keyId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "create_date")
    private String createDate;
    @Column(name = "order_sta")
    private String orderSta;
    private String content;
    @Column(name = "ori_price")
    private String oriPrice;
    @Column(name = "pay_price")
    private String payPrice;
}