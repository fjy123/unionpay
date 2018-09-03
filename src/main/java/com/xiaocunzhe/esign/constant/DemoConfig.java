package com.xiaocunzhe.esign.constant;

import com.timevale.tech.sdk.constants.AlgorithmType;
import com.timevale.tech.sdk.constants.HttpType;

/***
 * @Description: 电子签名服务（快捷签）常用配置信息类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年05月27日
 */
public class DemoConfig {
	// 项目ID(公共应用ID),正式环境下贵司将拥有独立的应用ID
	public static final String PROJECT_ID = "1111563517";
	// 项目Secret(公共应用Secret),正式环境下贵司将拥有独立的应用Secret
	public static final String PROJECT_SECRET = "95439b0863c241c63a861b87d1e647b7";
	// 协议类型(可选HTTP或HTTPS)
	public static final HttpType HTTP_TYPE = HttpType.HTTP;

	// 模拟环境
	private static final String API_HOST = "http://smlitsm.tsign.cn:8080";

	// 正式环境
	// public static final String API_HOST = "http://openapi.tsign.cn:8080";
	// public static final String API_HOST = "https://openapi.tsign.cn:443";

	/***
	 * 开放平台地址
	 */
	public static final String APISURL = API_HOST + "/tgmonitor/rest/app!getAPIInfo2";

	// 算法类型(可选HMACSHA256或RSA)，但推荐使用HMACSHA256
	public static final AlgorithmType ALGORITHM_TYPE = AlgorithmType.HMACSHA256;
	// e签宝公钥，可以从开放平台获取。若算法类型为RSA，此项必填
	public static final String ESIGN_PUBLIC_KEY = null;
	// 平台私钥，可以从开放平台下载密钥生成工具生成。若算法类型为RSA，此项必填
	public static final String PRIVATE_KEY = null;
}
