package com.xiaocunzhe.unionpay.config;

import com.timevale.esign.sdk.tech.bean.result.Result;
import com.timevale.esign.sdk.tech.v3.client.ServiceClientManager;
import com.timevale.tech.sdk.bean.HttpConnectionConfig;
import com.timevale.tech.sdk.bean.ProjectConfig;
import com.timevale.tech.sdk.bean.SignatureConfig;
import com.timevale.tech.sdk.constants.AlgorithmType;
import com.xiaocunzhe.esign.constant.DemoConfig;
import com.xiaocunzhe.esign.exception.DemoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.text.MessageFormat;

@Order(1)
public class StartupRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

	@Override
	public void run(String... args) throws Exception {
		logger.info("客户端初始化");
		ProjectConfig proCfg = new ProjectConfig();
		// 项目ID(应用ID)
		proCfg.setProjectId(DemoConfig.PROJECT_ID);
		// 项目Secret(应用Secret)
		proCfg.setProjectSecret(DemoConfig.PROJECT_SECRET);
		// 开放平台地址
		proCfg.setItsmApiUrl(DemoConfig.APISURL);

		HttpConnectionConfig httpConCfg = new HttpConnectionConfig();
		// 协议类型
		httpConCfg.setHttpType(DemoConfig.HTTP_TYPE);
		// 请求失败重试次数，默认5次
		httpConCfg.setRetry(5);
		// 代理服务IP地址
		// httpConCfg.setProxyIp(null);
		// 代理服务端口
		// httpConCfg.setProxyPort(0);
		// 代理服务器用户名
		// httpConCfg.setUsername(null);
		// 代理服务器用户密码
		// httpConCfg.setPassword(null);

		SignatureConfig signCfg = new SignatureConfig();
		// 算法类型
		signCfg.setAlgorithm(DemoConfig.ALGORITHM_TYPE);
		// 如果算法类型为RSA,则需要设置e签宝公钥和平台私钥
		if (AlgorithmType.RSA == DemoConfig.ALGORITHM_TYPE) {
			System.out.println("RSA");
			// e签宝公钥，可以从开放平台获取。若算法类型为RSA，此项必填
			signCfg.setEsignPublicKey(DemoConfig.ESIGN_PUBLIC_KEY);
			// 平台私钥，可以从开放平台下载密钥生成工具生成。若算法类型为RSA，此项必填
			signCfg.setPrivateKey(DemoConfig.PRIVATE_KEY);
		}
		// 注册客户端
		Result result = ServiceClientManager.registClient(proCfg, httpConCfg, signCfg);
		if (0 != result.getErrCode()) {
			String exMsg = MessageFormat.format("注册[{0}]的客户端失败: errCode = {1},msg = {2}", DemoConfig.PROJECT_ID,
					String.valueOf(result.getErrCode()), result.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println(MessageFormat.format("注册[{0}]的客户端成功: errCode = {1},msg = {2}", DemoConfig.PROJECT_ID,
					String.valueOf(result.getErrCode()), result.getMsg()));
		}
	}
//	@Override
//	public void run(String... args) throws Exception {
//		 logger.info("startup runner");
//		 //银联加载配置
//		 SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
//	}

}
