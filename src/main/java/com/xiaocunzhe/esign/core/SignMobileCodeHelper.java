package com.xiaocunzhe.esign.core;

import java.text.MessageFormat;

import com.timevale.esign.sdk.tech.bean.result.Result;
import com.timevale.esign.sdk.tech.service.MobileService;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.xiaocunzhe.esign.exception.DemoException;


/***
 * @Description: 电子签名_签署短信验证码发送辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年06月17日
 */
public class SignMobileCodeHelper {

	/***
	 * <ul>
	 * <li>方法名称：发送签署短信验证码</li>
	 * <li>发送到：短信验证码将发送到创建账户时所填写的手机号</li>
	 * <li>方法用途：发送签署时使用的短信验证码</li>
	 * <li>Demo封装方法：doUserLocalSignPDF</li>
	 * <li>SDK接口名称:sendSignMobileCode</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @throws DemoException
	 */
	public static void doSendSignMobileCode(ServiceClient serviceClient, String accountId) throws DemoException {
		MobileService mobileService = serviceClient.mobileService();
		Result result = mobileService.sendSignMobileCode(accountId);

		if (0 != result.getErrCode()) {
			String exMsg = MessageFormat.format("发送签署短信验证码失败: errCode = {0},msg = {1}",
					String.valueOf(result.getErrCode()), result.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("发送签署短信验证码成功,请创建账户时所填写的手机号中查找验证码且在30分钟内完成签署");
		}
	}

	/***
	 * <ul>
	 * <li>方法名称：指定手机发送签署短信验证码</li>
	 * <li>发送到：指定的手机号</li>
	 * <li>方法用途：发送签署时使用的短信验证码</li>
	 * <li>Demo封装方法：doSendSignMobileCode3rd</li>
	 * <li>SDK接口名称:sendSignMobileCode3rd</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @throws DemoException
	 */
	public static void doSendSignMobileCode3rd(ServiceClient serviceClient, String accountId, String mobile)
			throws DemoException {
		MobileService mobileService = serviceClient.mobileService();
		Result result = mobileService.sendSignMobileCode3rd(accountId, mobile);

		if (0 != result.getErrCode()) {
			String exMsg = MessageFormat.format("指定手机发送签署短信验证码失败: errCode = {0},msg = {1}",
					String.valueOf(result.getErrCode()), result.getMsg());
			throw new DemoException(exMsg);
		} else {
			MessageFormat.format("指定手机发送签署短信验证码成功, 请手机号[{0}]中查找验证码且在30分钟内完成签署", mobile);
		}
	}
}
