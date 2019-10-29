package com.telit.info.data.app;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_pay_tran")
@Data
public class PayTran implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "order_id")
	private Integer orderId;
	@Column(name = "user_id")
    private Integer userId;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "out_trade_no")
    private String outTradeNo;
    @Column(name = "pay_type")
    private String payType;
    @Column(name = "pay_amount")
    private Float payAmount;
    @Column(name = "pay_date")
    private String payDate;
    @Column(name = "pay_sta")
    private String paySta;
    @Column(name = "pay_rem")
    private String payRem;
}