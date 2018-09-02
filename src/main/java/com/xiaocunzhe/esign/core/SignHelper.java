package com.xiaocunzhe.esign.core;

import java.text.MessageFormat;
import java.util.List;

import com.timevale.esign.sdk.tech.bean.PosBean;
import com.timevale.esign.sdk.tech.bean.SignPDFFileBean;
import com.timevale.esign.sdk.tech.bean.SignPDFStreamBean;
import com.timevale.esign.sdk.tech.bean.SignParamBean;
import com.timevale.esign.sdk.tech.bean.result.CodeMultiSignResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.impl.constants.SignType;
import com.timevale.esign.sdk.tech.service.SelfSignService;
import com.timevale.esign.sdk.tech.service.UserSignService;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.xiaocunzhe.esign.exception.DemoException;
import com.xiaocunzhe.esign.util.StringTools;


/***
 * @Description: 电子签名_签署辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年06月19日
 */
public class SignHelper {

	/***
	 * <ul>
	 * <li>方法名称：设置签章位置信息</li>
	 * <li>印章位置定位类型：XY坐标定位</li>
	 * <li>方法用途：设置签章位置为通过XY坐标方式定位</li>
	 * <li>Demo封装方法：doSetXYPosBean</li>
	 * <li>SDK中类:PosBean</li>
	 * </ul>
	 * 
	 * @param page
	 * @param x
	 * @param y
	 * @param scaling
	 */
	public static PosBean doSetXYPosBean(SignType signType, String page, float x, float y, float scaling) {
		PosBean posBean = new PosBean();
		// 签署类型
		if (SignType.Key == signType) {
			// 定位类型,SignType.Key时PosType值为1否则均为0
			posBean.setPosType(1);
		} else {
			// 定位类型,SignType.Key时PosType值为1否则均为0
			posBean.setPosType(0);
		}
		// 签署页码,单页签署时页码格式为"1";若为多页签署时，支持页码格式"1-3,5,8"
		posBean.setPosPage(page);
		// 签署位置X坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面左端的横向移动距离,单位为px
		posBean.setPosX(x);
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		posBean.setPosY(y);
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		posBean.setWidth(scaling);
		return posBean;
	}

	/***
	 * <ul>
	 * <li>方法名称：设置签章位置信息</li>
	 * <li>印章位置定位类型：关键字定位</li>
	 * <li>方法用途：设置签章位置为通过检索关键字方式定位</li>
	 * <li>Demo封装方法：doSetKeyPosBean</li>
	 * <li>SDK中类:PosBean</li>
	 * </ul>
	 * 
	 * @param key
	 * @param x
	 * @param y
	 * @param scaling
	 */
	public static PosBean doSetKeyPosBean(SignType signType, String key, float x, float y, float scaling) {
		PosBean posBean = new PosBean();
		// 签署类型
		if (SignType.Key == signType) {
			// 定位类型,SignType.Key时PosType值为1否则均为0
			posBean.setPosType(1);
		} else {
			// 定位类型,SignType.Key时PosType值为1否则均为0
			posBean.setPosType(0);
		}
		// 关键字,仅限关键字签章时有效,签署时将从PDF中所有页中检索此关键字
		posBean.setKey(key);
		// 签署位置X坐标,默认值为0,以关键字左端位置为原点进行偏移,控制距离关键字左端的横向移动距离,单位为px
		posBean.setPosX(x);
		// 签署位置Y坐标,默认值为0,以关键字左端位置为原点进行偏移,控制距离关键字底端的纵向移动距离,单位为px
		posBean.setPosY(y);
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		posBean.setWidth(scaling);
		return posBean;
	}

	/***
	 * <ul>
	 * <li>方法名称：设置签署PDF文档信息</li>
	 * <li>文件方式：本地PDF文件路径</li>
	 * <li>方法用途：设置签署PDF文档信息</li>
	 * <li>Demo封装方法：doSetSignPDFFileBean</li>
	 * <li>SDK中类:SignPDFFileBean</li>
	 * </ul>
	 * 
	 * @param srcPdfPath
	 * @param outPdfPath
	 * @param signLogFileName
	 * @param ownerPWD
	 */
	public static SignPDFFileBean doSetSignPDFFileBean(String srcPdfPath, String outPdfPath, String signLogFileName,
			String ownerPWD) {
		SignPDFFileBean signPDFFileBean = new SignPDFFileBean();
		// 待签署PDF文件本地路径
		signPDFFileBean.setSrcPdfFile(srcPdfPath);
		// 签署后PDF文件本地保存路径
		signPDFFileBean.setDstPdfFile(outPdfPath);

		if (StringTools.isNotNull(signLogFileName)) {
			// 文档名称,此文档名称用于在e签宝服务端记录签署日志时用,非签署后PDF文件中的文件名.若为空则取待签署PDF文件中的文件名称
			signPDFFileBean.setFileName(signLogFileName);
		}

		if (StringTools.isNotNull(ownerPWD)) {
			// 文档编辑密码,如果待签署PDF文件设置了编辑密码时需要填写编辑密码,否则请传入null
			signPDFFileBean.setOwnerPassword(ownerPWD);
		}

		return signPDFFileBean;
	}

	/***
	 * <ul>
	 * <li>方法名称：设置签署PDF文档信息</li>
	 * <li>文件方式：本地PDF文件路径</li>
	 * <li>方法用途：设置签署PDF文档信息</li>
	 * <li>Demo封装方法：doSetSignPDFStreamBean</li>
	 * <li>SDK中类:SignPDFStreamBean</li>
	 * </ul>
	 * 
	 * @param pdfBytes
	 * @param outPdfPath
	 * @param signLogFileName
	 * @param ownerPWD
	 */
	public static SignPDFStreamBean doSetSignPDFStreamBean(byte[] pdfBytes, String outPdfPath, String signLogFileName,
			String ownerPWD) {
		SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();

		// 待签署PDF文件字节流
		signPDFStreamBean.setStream(pdfBytes);

		if (StringTools.isNotNull(outPdfPath)) {
			// 签署后PDF文件本地保存路径,如果希望签署后依然返回PDF文件字节流时请设置该属性为空
			signPDFStreamBean.setDstPdfFile(outPdfPath);
		}

		if (StringTools.isNotNull(signLogFileName)) {
			// 文档名称,此文档名称用于在e签宝服务端记录签署日志时用,非签署后PDF文件中的文件名.若为空则取待签署PDF文件中的文件名称
			signPDFStreamBean.setFileName(signLogFileName);
		}

		if (StringTools.isNotNull(ownerPWD)) {
			// 文档编辑密码,如果待签署PDF文件设置了编辑密码时需要填写编辑密码,否则请传入null
			signPDFStreamBean.setOwnerPassword(ownerPWD);
		}

		return signPDFStreamBean;
	}

	/***
	 * <ul>
	 * <li>方法名称：平台自身PDF摘要签署（印章标识）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：将接口调用方(平台方)自己的公章盖到本地PDF文件中</li>
	 * <li>印章SealData：SDK接口通过sealId从e签宝服务端获取接口调用方(平台方)在e签宝官网创建的印章</li>
	 * <li>Demo封装方法：doSelfLocalSignPDF</li>
	 * <li>SDK接口名称:SelfSignService的localSignPdf</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealId
	 * @param signType
	 * @throws DemoException
	 */
	public static void doSelfLocalSignPDF(ServiceClient serviceClient, SignPDFFileBean file, PosBean posBean,
			int sealId, SignType signType) throws DemoException {

		SelfSignService selfSignService = serviceClient.selfSignService();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(file, posBean, sealId, signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)自身签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)自身签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)自身签署成功后的PDF文件存放路径: " + fileDigestSignResult.getDstFilePath() + ", 请妥善保管签署后的文件.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台自身PDF摘要签署（文件流&印章标识）</li>
	 * <li>文件方式：PDF字节流</li>
	 * <li>方法用途：将接口调用方(平台方)自己的公章盖到本地PDF文件中</li>
	 * <li>印章SealData：SDK接口通过sealId从e签宝服务端获取接口调用方(平台方)在e签宝官网创建的印章</li>
	 * <li>Demo封装方法：doSelfLocalSignPDF</li>
	 * <li>SDK接口名称:SelfSignService的localSignPdf</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param stream
	 * @param posBean
	 * @param sealId
	 * @param signType
	 * @throws DemoException
	 */
	public static FileDigestSignResult doSelfLocalSignPDF(ServiceClient serviceClient, SignPDFStreamBean stream, PosBean posBean,
			int sealId, SignType signType) throws DemoException {

		SelfSignService selfSignService = serviceClient.selfSignService();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(stream, posBean, sealId, signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)自身签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)自身签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)自身签署成功, 请妥善保管签署后的文件字节流");
		}
		return fileDigestSignResult;

	}

	/***
	 * <ul>
	 * <li>方法名称：平台自身PDF摘要签署（印章图片）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：将接口调用方(平台方)自己的公章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doSelfLocalSignPDF</li>
	 * <li>SDK接口名称:SelfSignService的localSignPdf</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doSelfLocalSignPDF(ServiceClient serviceClient, SignPDFFileBean file, PosBean posBean,
			String sealData, SignType signType) throws DemoException {

		SelfSignService selfSignService = serviceClient.selfSignService();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(file, posBean, sealData, signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)自身签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)自身签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)自身签署成功后的PDF文件存放路径: " + fileDigestSignResult.getDstFilePath() + ", 请妥善保管签署后的文件.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台自身PDF摘要签署（文件流&印章图片）</li>
	 * <li>文件方式：PDF字节流</li>
	 * <li>方法用途：将接口调用方(平台方)自己的公章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doSelfLocalSignPDF</li>
	 * <li>SDK接口名称:SelfSignService的localSignPdf</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param stream
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doSelfLocalSignPDF(ServiceClient serviceClient, SignPDFStreamBean stream, PosBean posBean,
			String sealData, SignType signType) throws DemoException {

		SelfSignService selfSignService = serviceClient.selfSignService();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(stream, posBean, sealData, signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)自身签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)自身签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)自身签署成功, 请妥善保管签署后的文件字节流");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSignPDF(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFFileBean file, PosBean posBean, SignType signType) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSignPDF(accountId, sealData, file, posBean,
				signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)的客户签署成功后的PDF文件存放路径: " + fileDigestSignResult.getDstFilePath() + ", 请妥善保管签署后的文件.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署</li>
	 * <li>文件方式：PDF字节流</li>
	 * <li>方法用途：将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param stream
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static FileDigestSignResult doUserLocalSignPDF(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFStreamBean stream, PosBean posBean, SignType signType) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSignPDF(accountId, sealData, stream, posBean,
				signType);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)的客户签署成功, 请妥善保管签署后的文件字节流.");
		}
		return fileDigestSignResult;

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（短信验证）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：使用签署短信验证码的方式将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSafeSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeSignPDF(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFFileBean file, PosBean posBean, SignType signType, String code) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSafeSignPDF(accountId, sealData, file, posBean,
				signType, code);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户短信验证码方式签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户短信验证码方式签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)的客户短信验证码方式签署成功后的PDF文件存放路径: " + fileDigestSignResult.getDstFilePath() + ", 请妥善保管签署后的文件.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（短信验证）</li>
	 * <li>文件方式：PDF字节流</li>
	 * <li>方法用途：使用签署短信验证码的方式将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSafeSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeSignPDF(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFStreamBean stream, PosBean posBean, SignType signType, String code) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSafeSignPDF(accountId, sealData, stream,
				posBean, signType, code);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户短信验证码方式签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户短信验证码方式签署成功: SignServiceId = " + fileDigestSignResult.getSignServiceId()
					+ ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println(
					"接口调用方(平台方)的客户短信验证码方式签署成功, 请妥善保管签署后的文件字节流.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（指定手机短信验证）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：使用签署短信验证码的方式将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeSignPDF3rd</li>
	 * <li>SDK接口名称:UserSignService的localSafeSignPDF3rd</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeSignPDF3rd(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFFileBean file, PosBean posBean, SignType signType, String mobile, String code) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSafeSignPDF3rd(accountId, sealData, file,
				posBean, signType, mobile, code);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户指定手机号短信验证码方式签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户指定手机号短信验证码方式签署成功: SignServiceId = "
					+ fileDigestSignResult.getSignServiceId() + ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println("接口调用方(平台方)的客户指定手机号短信验证码方式签署成功后的PDF文件存放路径: " + fileDigestSignResult.getDstFilePath()
					+ ", 请妥善保管签署后的文件.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（指定手机短信验证）</li>
	 * <li>文件方式：PDF字节流</li>
	 * <li>方法用途：使用签署短信验证码的方式将接口调用方(平台方)的个人客户或企业客户的印章盖到本地PDF文件中</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeSignPDF3rd</li>
	 * <li>SDK接口名称:UserSignService的localSafeSignPDF3rd</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeSignPDF3rd(ServiceClient serviceClient, String accountId, String sealData,
			SignPDFStreamBean stream, PosBean posBean, SignType signType, String mobile, String code)
					throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		FileDigestSignResult fileDigestSignResult = userSignService.localSafeSignPDF3rd(accountId, sealData, stream,
				posBean, signType, mobile, code);

		if (0 != fileDigestSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户指定手机号短信验证码方式签署失败: errCode = {0},msg = {1}",
					String.valueOf(fileDigestSignResult.getErrCode()), fileDigestSignResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户指定手机号短信验证码方式签署成功: SignServiceId = "
					+ fileDigestSignResult.getSignServiceId() + ", 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.");
			System.out.println("接口调用方(平台方)的客户指定手机号短信验证码方式签署成功, 请妥善保管签署后的文件字节流.");
		}

	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（短信验证批量签署）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：当同一个平台用户需要输入一次签署短信验证码时同时向多份本地PDF文件加盖印章时可调用此接口</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeMultiSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSafeMultiSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeMultiSignPDF(ServiceClient serviceClient, String accountId,
			List<SignParamBean> signParams, String sealData, String code) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		CodeMultiSignResult codeMultiSignResult = userSignService.localSafeMultiSignPDF(accountId, signParams, sealData,
				code);

		if (0 != codeMultiSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户批量签署失败: errCode = {0},msg = {1}",
					String.valueOf(codeMultiSignResult.getErrCode()), codeMultiSignResult.getMsg());
			// 签署失败列表
			List<FileDigestSignResult> failList = codeMultiSignResult.getFailList();
			if (null != failList) {
				FileDigestSignResult fileDigestSignResult = null;
				for (int i = 0; i < failList.size(); i++) {
					fileDigestSignResult = failList.get(i);
					System.out.println("待签署文件路径: " + fileDigestSignResult.getFilePath() + "签署失败");
				}
			}
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户批量签署成功");
			// 签署成功列表
			List<FileDigestSignResult> successList = codeMultiSignResult.getSuccessList();
			if (null != successList) {
				FileDigestSignResult fileDigestSignResult = null;
				for (int i = 0; i < successList.size(); i++) {
					fileDigestSignResult = successList.get(i);
					// 签署后PDF文件路径
					String signedPdfPath = fileDigestSignResult.getDstFilePath();
					// 签署后记录ID
					String signServiceId = fileDigestSignResult.getDstFilePath();
					System.out.println(MessageFormat.format(
							"签署成功后的PDF文件存放路径: {0}, SignServiceId = {1}, 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.",
							signedPdfPath, signServiceId));
				}
			}
		}
	}

	/***
	 * <ul>
	 * <li>方法名称：平台用户PDF摘要签署（指定手机短信验证批量签署）</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：当同一个平台用户需要输入一次签署短信验证码时同时向多份本地PDF文件加盖印章时可调用此接口</li>
	 * <li>印章SealData：可通过SDK提供的印章服务接口获取SealData或接口调用方(平台方)自行将印章图片处理成Base64字符串
	 * </li>
	 * <li>Demo封装方法：doUserLocalSafeMultiSignPDF</li>
	 * <li>SDK接口名称:UserSignService的localSafeMultiSignPDF</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param file
	 * @param posBean
	 * @param sealData
	 * @param signType
	 * @throws DemoException
	 */
	public static void doUserLocalSafeMultiSignPDF(ServiceClient serviceClient, String accountId,
			List<SignParamBean> signParams, String sealData, String mobile, String code) throws DemoException {
		UserSignService userSignService = serviceClient.userSignService();
		CodeMultiSignResult codeMultiSignResult = userSignService.localSafeMultiSignPDF3rd(accountId, signParams,
				sealData, mobile, code);

		if (0 != codeMultiSignResult.getErrCode()) {
			String exMsg = MessageFormat.format("接口调用方(平台方)的客户批量签署失败: errCode = {0},msg = {1}",
					String.valueOf(codeMultiSignResult.getErrCode()), codeMultiSignResult.getMsg());
			// 签署失败列表
			List<FileDigestSignResult> failList = codeMultiSignResult.getFailList();
			if (null != failList) {
				FileDigestSignResult fileDigestSignResult = null;
				for (int i = 0; i < failList.size(); i++) {
					fileDigestSignResult = failList.get(i);
					System.out.println("待签署文件路径: " + fileDigestSignResult.getFilePath() + "签署失败");
				}
			}
			throw new DemoException(exMsg);
		} else {
			System.out.println("接口调用方(平台方)的客户批量签署成功");
			// 签署成功列表
			List<FileDigestSignResult> successList = codeMultiSignResult.getSuccessList();
			if (null != successList) {
				FileDigestSignResult fileDigestSignResult = null;
				for (int i = 0; i < successList.size(); i++) {
					fileDigestSignResult = successList.get(i);
					// 签署后PDF文件路径
					String signedPdfPath = fileDigestSignResult.getDstFilePath();
					// 签署后记录ID
					String signServiceId = fileDigestSignResult.getDstFilePath();
					System.out.println(MessageFormat.format(
							"签署成功后的PDF文件存放路径: {0}, SignServiceId = {1}, 请妥善保管签署记录ID(SignServiceId)以便日后场景式存证使用.",
							signedPdfPath, signServiceId));
				}
			}
		}
	}
}
