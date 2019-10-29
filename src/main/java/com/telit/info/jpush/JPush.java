package com.telit.info.jpush;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JPush {
	
	//设置好账号的app_key 和 masterSecret
	@Value("${jpush.regAccount}")
	private static String appKey;
	@Value("${jpush.regAccount}")
	private static String masterSecret;

	//机关推送 >>Android
	public static void jpushAndroid(Map<String, String> parm) {
		//创建JPushClient（极光推送的实例）
		JPushClient jpushClient = new JPushClient(masterSecret,appKey);
		//推送的关键构造一个payload
		PushPayload payload = PushPayload.newBuilder()
							  .setPlatform(Platform.android())//指定安卓平台的用户
							  .setAudience(Audience.all())//项目中的所有用户
							  .setNotification(Notification.android(parm.get("msg"), "这是title", parm))
							  .setOptions(Options.newBuilder().setApnsProduction(false).build())
							  .setMessage(Message.content(parm.get("msg")))
							  .build();
		try {
			PushResult pu = jpushClient.sendPush(payload);
		}catch(APIConnectionException e) {
			e.printStackTrace();
		}catch(APIRequestException e) {
			e.printStackTrace();
		}
	}
	
	//极光推送>>ios
	public static void jpushIOS(Map<String,String> parm) {
		//创建JPushClient（极光推送的实例）
		JPushClient jpushClient = new JPushClient(masterSecret,appKey);
		PushPayload payload = PushPayload.newBuilder()
							  .setPlatform(Platform.ios())//ios平台的用户
							  .setAudience(Audience.all())//所有用户
							  .setNotification(Notification.newBuilder()
									  .addPlatformNotification(IosNotification.newBuilder()
											  .setAlert(parm.get("msg"))
											  .setBadge(+1)
											  .setSound("happy")
											  .addExtras(parm)
											  .build())
									  .build())
							  .setOptions(Options.newBuilder().setApnsProduction(false).build())
							  .setMessage(Message.newBuilder().setMsgContent(parm.get("msg")).addExtras(parm).build())//自定义信息
							  .build();
		try {
			PushResult pu = jpushClient.sendPush(payload);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}    
	}
}
