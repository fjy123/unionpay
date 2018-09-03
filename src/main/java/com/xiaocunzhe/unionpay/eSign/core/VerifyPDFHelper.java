package com.xiaocunzhe.unionpay.eSign.core;

import java.text.MessageFormat;
import java.util.List;

import com.timevale.esign.sdk.tech.bean.SignBean;
import com.timevale.esign.sdk.tech.bean.SignatureBean;
import com.timevale.esign.sdk.tech.bean.result.VerifyPdfResult;
import com.timevale.esign.sdk.tech.service.SignService;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.xiaocunzhe.esign.exception.DemoException;


/***
 * @Description: 签署后PDF文件验签辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年06月25日
 */
public class VerifyPDFHelper {
	
	/***
	 * <ul>
	 * <li>方法名称：PDF文档验签</li>
	 * <li>方法用途：在PDF签署后或签署后的PDF复制到其他地方时可以通过此接口验证一下签署后的PDF文档是否正常</li>
	 * <li>Demo封装方法：doLocalVerifyPdf</li>
	 * <li>SDK中的方法:localVerifyPdf</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param signedPdfPath
	 * @throws DemoException 
	 */
	public static void doLocalVerifyPdf(ServiceClient serviceClient, String signedPdfPath) throws DemoException {
		SignService signService = serviceClient.signService();		
		VerifyPdfResult verifyPdfResult = signService.localVerifyPdf(signedPdfPath);
		
		if (0 != verifyPdfResult.getErrCode()) {
			String exMsg = MessageFormat.format("PDF文档验签时出现异常: errCode = {0},msg = {1}",
					String.valueOf(verifyPdfResult.getErrCode()), verifyPdfResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			// 获取PDF文件中所有签名信息
			List<SignBean> signatures = verifyPdfResult.getSignatures();
			
			for(int i = 0; i < signatures.size(); i++){
				
				SignBean signBean = signatures.get(i);
				// 签名使用的印章名称
				String sealName = signBean.getSealName();
				// 签署人证书名称
				String signer = signBean.getCert().getCn();
				// 签署人证书序列号
				String certSN = signBean.getCert().getSn();
				// 证书发布者名称
				String issuerCN = signBean.getCert().getIssuerCN();
				// 签署人证书有效期开始时间
				String startDate = signBean.getCert().getStartDate();
				// 签署人证书有效期结束时间
				String endDate = signBean.getCert().getEndDate();
				
				SignatureBean signatureBean = signBean.getSignature();
				// 该PDF中签名的验证结果:true成功,false失败
				boolean validate = signatureBean.isValidate();
				// 文档签署时间
				String signDate = signatureBean.getSignDate();
				// 签名数据来源
				String timeFrom = signatureBean.getTimeFrom();		
				
				String message = MessageFormat.format("签名使用的印章名称:{0},签署人证书名称:{1},签署人证书序列号:{2},证书发布者名称:{3},签署人证书有效期开始时间:{4},签署人证书有效期结束时间:{5},该PDF中签名的验证结果:{6},文档签署时间:{7},签名数据来源:{8}",
						sealName,signer,certSN,issuerCN,startDate,endDate,validate,signDate,timeFrom);
				System.out.println(message);
			}
		}
		
	}

}
