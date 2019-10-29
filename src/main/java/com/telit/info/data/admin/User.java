package com.telit.info.data.admin;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_user")
@Data
public class User implements IntegerKey{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String bz;
    private String password;
    @Column(name = "true_name")
    private String trueName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "icon_type")
    private int iconType;
    @Column(name = "icon_name")
    private String iconName;
    @Column(name = "icon_url")
    private String iconUrl;
    @Transient
    private String roles;
    @Transient
    private String oldPassword;
    
	public void copy(User user) {
		this.trueName = user.trueName;
		this.password = user.password;
		this.iconName = user.iconName;
		this.iconType = user.iconType;
		this.iconUrl  = user.iconUrl;
		this.userName = user.userName;
	}
}