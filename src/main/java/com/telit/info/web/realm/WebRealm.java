package com.telit.info.web.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.telit.info.data.admin.Menu;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.trans.service.DataService;
import com.telit.info.web.NetUitl;

public class WebRealm extends AuthorizingRealm {
	@Resource
	private DataService dataService;
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userName = SecurityUtils.getSubject().getPrincipal().toString();
		//根据用户名查询出用户记录
		User user = NetUitl.getDataByConditions(dataService,User.class,userName,"userName");
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		List<Role> roleList = NetUitl.selectRolesByUserId(dataService,"IN",user.getId());
		Set<String> roles = new HashSet<String>();
		if (roleList.size()> 0 ){
			for (Role role : roleList){
				roles.add(role.getName());
				//根据角色id查询所有资源
				List<Menu> menuList = NetUitl.selectMenusByRoleId(dataService,role.getId());
				for (Menu menu : menuList){
					info.addStringPermission(menu.getName());//添加权限
				}
			}
		}
		info.setRoles(roles);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String userName = token.getPrincipal().toString();
		User user = NetUitl.getDataByConditions(dataService,User.class,userName,"userName");
		if (user!=null){
			AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUserName(),user.getPassword(),"xxx");
			return authcInfo;
		}else{
			return null;
		}
	}
}
