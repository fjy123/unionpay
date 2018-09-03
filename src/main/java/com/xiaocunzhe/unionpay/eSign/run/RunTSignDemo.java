package com.xiaocunzhe.unionpay.eSign.run;

import java.io.File;

import com.timevale.esign.sdk.tech.bean.OrganizeBean;
import com.timevale.esign.sdk.tech.bean.PersonBean;
import com.timevale.esign.sdk.tech.bean.PosBean;
import com.timevale.esign.sdk.tech.bean.SignPDFFileBean;
import com.timevale.esign.sdk.tech.bean.SignPDFStreamBean;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.seal.OrganizeTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
import com.timevale.esign.sdk.tech.impl.constants.LegalAreaType;
import com.timevale.esign.sdk.tech.impl.constants.OrganRegType;
import com.timevale.esign.sdk.tech.impl.constants.SignType;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.xiaocunzhe.unionpay.eSign.constant.DemoConfig;
import com.xiaocunzhe.esign.core.AccountHelper;
import com.xiaocunzhe.esign.core.FileHelper;
import com.xiaocunzhe.esign.core.InitClientHelper;
import com.xiaocunzhe.esign.core.SealHelper;
import com.xiaocunzhe.esign.core.SignHelper;
import com.xiaocunzhe.esign.exception.DemoException;
import com.xiaocunzhe.esign.util.DemoMessage;
import com.xiaocunzhe.esign.util.StringTools;
import com.xiaocunzhe.unionpay.eSign.core.VerifyPDFHelper;


/***
 * @Description: 电子签名_Demo
 * @Version: Ver_2.1
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年05月29日
 */

public class RunTSignDemo {
	
	public static void main(String[] args) {
		// 示例Demo重要提示
		DemoMessage.showImportantMessage();

		try {
			// 客户端初始化,做全局使用,只注册一次即可
			InitClientHelper.doRegistClient();
			// 获取已初始化的客户端以便可以正常调用SDK提供的各种电子签名服务,做全局使用只获取一次即可
			ServiceClient serviceClient = InitClientHelper.doGetServiceClient(DemoConfig.PROJECT_ID);
			long startTime=System.currentTimeMillis();
			// 个人客户账户AccountId,可将该AccountId保存到贵司数据库以便日后直接使用,只创建一次即可
			String personAccountId = addPersonAccount(serviceClient);
			long endTime=System.currentTimeMillis();

			System.out.println("当前程序耗时："+(endTime-startTime)+"ms");
			// 个人客户印章SealData,可将该SealData保存到贵司数据库以便日后直接使用,只创建一次即可
			String personSealData = addPersonTemplateSeal(serviceClient, personAccountId);

			// 企业客户账户AccountId,可将该AccountId保存到贵司数据库以便日后直接使用,只创建一次即可
			String organizeAccountId = addOrganizeAccount(serviceClient);

			// 企业客户印章SealData,可将该SealData保存到贵司数据库以便日后直接使用,只创建一次即可
			String organizeSealData = addOrganizeTemplateSeal(serviceClient, organizeAccountId);

			// 常用场景演示
			int scene = 0;
			switch (scene) {
			case 0:
				System.out.println("----<场景演示：接口调用方(平台方)与其个人客户和企业客户进行合同签署(以PDF文件本地路径方式)>----");
				
				doSign_PlatformWithPersonAndOrganize(serviceClient,personAccountId,personSealData,organizeAccountId,organizeSealData);
				break;
			case 1:
				System.out.println("----<场景演示：个人客户和企业客户进行合同签署(以PDF文件字节流方式)>----");
				
				doSign_PersonWithOrganize(serviceClient,personAccountId,personSealData,organizeAccountId,organizeSealData);
				break;			
			default:
				System.out.println("---- 提示！请选择应用场景...");
				break;
			}
		} catch (DemoException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * <ul>
	 * <li>方法名称：演示接口调用方(平台方)与其个人客户和企业客户进行合同签署</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：演示三方签署</li>
	 * <li>Demo封装方法：doSign_PlatformWithPersonAndOrganize</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param personAccountId
	 * @param personSealData
	 * @param organizeAccountId
	 * @param organizeSealData
	 */
	public static void doSign_PlatformWithPersonAndOrganize(ServiceClient serviceClient,String personAccountId,String personSealData,String organizeAccountId, String organizeSealData){
		try {
			// 当前程序所在文件目录
			String rootFolder = new File("").getAbsolutePath();
			// 待签署PDF文件路径
			String srcPdfPath = rootFolder + File.separator + "pdf" + File.separator + "test.pdf";

			// 文档名称,此文档名称用于在e签宝服务端记录签署日志时用,非签署后PDF文件中的文件名.若为空则取待签署PDF文件中的文件名称
			String signLogFileName = "如合同编号或合同名";
			// 文档编辑密码,如果待签署PDF文件设置了编辑密码时需要填写编辑密码,否则请传入null
			String ownerPWD = null;

			// 接口调用方(平台方)签署后PDF文件路径
			String platformSignedPdfPath = rootFolder + File.separator + "pdf" + File.separator
					+ "Signed_Platform.pdf";
			// 接口调用方(平台方)签署盖章
			doSign_PlatformByPath(serviceClient, srcPdfPath, platformSignedPdfPath, signLogFileName, ownerPWD);

			// 个人客户签署后PDF文件路径
			String personSignedPdfPath = rootFolder + File.separator + "pdf" + File.separator
					+ "Signed_Person.pdf";

			// 个人客户签署盖章
			doSign_PersonByPath(serviceClient, personAccountId, personSealData, platformSignedPdfPath,
					personSignedPdfPath, signLogFileName, ownerPWD);

			// 企业客户签署后PDF文件路径
			String organizeSignedPdfPath = rootFolder + File.separator + "pdf" + File.separator
					+ "Signed_Organize.pdf";

			// 企业客户签署盖章
			doSign_OrganizeByPath(serviceClient, organizeAccountId, organizeSealData, personSignedPdfPath,
					organizeSignedPdfPath, signLogFileName, ownerPWD);
			// 验证一下所以人都签署后的PDF是否文件正常
			VerifyPDFHelper.doLocalVerifyPdf(serviceClient, organizeSignedPdfPath);

		} catch (DemoException e) {
			System.out.println("签署时发生异常:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/***
	 * <ul>
	 * <li>方法名称：演示个人客户和企业客户进行合同签署</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：演示双方签署</li>
	 * <li>Demo封装方法：doSign_PersonWithOrganize</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param personAccountId
	 * @param personSealData
	 * @param organizeAccountId
	 * @param organizeSealData
	 */
	public static void doSign_PersonWithOrganize(ServiceClient serviceClient,String personAccountId,String personSealData,String organizeAccountId, String organizeSealData){
		try {
			// 当前程序所在文件目录
			String rootFolder = new File("").getAbsolutePath();
			
			// 待签署PDF文件路径
			String srcPdfPath = rootFolder + File.separator + "pdf" + File.separator + "test.pdf";					
			// 签署后PDF文件存放目录
			String signedPdfFolder = rootFolder + File.separator + "pdf";
			// 签署后PDF文件名称
			String signedPdfName = "Signed_All.pdf";					
			// 签署后PDF文件保存路径
			String outSignedPdfPath = signedPdfFolder + File.separator + signedPdfName;

			// 文档名称,此文档名称用于在e签宝服务端记录签署日志时用,非签署后PDF文件中的文件名.若为空则取待签署PDF文件中的文件名称
			String signLogFileName = "如合同编号或合同名";
			// 文档编辑密码,如果待签署PDF文件设置了编辑密码时需要填写编辑密码,否则请传入null
			String ownerPWD = null;

			
			// 获取个人客户签署时待签署PDF文件的字节流
			byte[] srcPdfBytes = FileHelper.doGetFileBytes(srcPdfPath);
			
			// 个人客户签署盖章
			FileDigestSignResult personSignResult = doSign_PersonByPDFBytes(serviceClient, personAccountId, personSealData, srcPdfBytes,
					null, signLogFileName, ownerPWD);
			
			// 企业客户签署时的待签署PDF文件字节流,即个人客户签署盖章成功后的PDF文件字节流
			byte[] organizeSrcPdfBytes = personSignResult.getStream();
			
			// 企业客户签署盖章
			FileDigestSignResult organizeSignResult = doSign_OrganizeByPDFBytes(serviceClient, organizeAccountId, organizeSealData, organizeSrcPdfBytes,
					null, signLogFileName, ownerPWD);
			
			// 所有人签署完成后将PDF文件字节流保存为本地PDF文件
			byte[] AllSignedPdfBytes = organizeSignResult.getStream();
			FileHelper.doSaveFileByStream(AllSignedPdfBytes, outSignedPdfPath);

		} catch (DemoException e) {
			System.out.println("签署时发生异常:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/***
	 * <ul>
	 * <li>方法名称：接口调用方(平台方)签署盖章</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：演示接口调用方(平台方)加盖公章</li>
	 * <li>Demo封装方法：doSign_PlatformByPath</li>
	 * </ul>
	 * 
	 * @throws DemoException
	 */
	public static void doSign_PlatformByPath(ServiceClient serviceClient, String srcPdfPath, String outPdfPath,
			String signLogFileName, String ownerPWD) throws DemoException {
		// 设置接口调用方(平台方)签署PDF文档信息
		SignPDFFileBean signPDFFileBean = SignHelper.doSetSignPDFFileBean(srcPdfPath, outPdfPath, signLogFileName,
				ownerPWD);
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Single;

		// 签署页码,单页签署时页码格式为"1";若为多页签署时，支持页码格式"1-3,5,8"
		String page = "1";
		// 签署位置X坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面左端的横向移动距离,单位为px
		float posX = 170F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 714F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 159F;

		// 接口调用方(平台方)的印章,请在www.tsign.cn官网中设置默认印章其sealId值为0
		int sealId = 0;
		// 设置接口调用方(平台方)签章位置信息
		PosBean posBean = SignHelper.doSetXYPosBean(signType, page, posX, posY, widthScaling);

		// 接口调用方(平台方)签署盖章
		SignHelper.doSelfLocalSignPDF(serviceClient, signPDFFileBean, posBean, sealId, signType);
	}

	/***
	 * <ul>
	 * <li>方法名称：个人客户签署盖章</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：演示个人客户加盖公章</li>
	 * <li>Demo封装方法：doSign_PersonByPath</li>
	 * </ul>
	 * 
	 * @throws DemoException
	 */
	public static void doSign_PersonByPath(ServiceClient serviceClient, String accountId, String sealData,
			String srcPdfPath, String outPdfPath, String signLogFileName, String ownerPWD) throws DemoException {
		// 设置个人客户签署PDF文档信息
		SignPDFFileBean signPDFFileBean = SignHelper.doSetSignPDFFileBean(srcPdfPath, outPdfPath, signLogFileName,
				ownerPWD);
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Key;
		// 关键字
		String key = "乙方签名";
		// 签署位置X坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面左端的横向移动距离,单位为px
		float posX = 105F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 5F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 90F;
		// 印章SealData
		String personSealData = sealData;

		// 设置个人客户签章位置信息
		PosBean posBean = SignHelper.doSetKeyPosBean(signType, key, posX, posY, widthScaling);
		// 个人客户签署盖章
		SignHelper.doUserLocalSignPDF(serviceClient, accountId, personSealData, signPDFFileBean, posBean, signType);
	}

	/***
	 * <ul>
	 * <li>方法名称：企业客户签署盖章</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：演示企业客户加盖公章</li>
	 * <li>Demo封装方法：doSign_OrganizeByPath</li>
	 * </ul>
	 * 
	 * @throws DemoException
	 */
	public static void doSign_OrganizeByPath(ServiceClient serviceClient, String accountId, String sealData,
			String srcPdfPath, String outPdfPath, String signLogFileName, String ownerPWD) throws DemoException {
		// 设置企业客户签署PDF文档信息
		SignPDFFileBean signPDFFileBean = SignHelper.doSetSignPDFFileBean(srcPdfPath, outPdfPath, signLogFileName,
				ownerPWD);
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Single;
		// 签署页码,单页签署时页码格式为"1";若为多页签署时，支持页码格式"1-3,5,8"
		String page = "1";
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posX = 120F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 100F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 159F;
		// 印章SealData
		String organizeSealData = sealData;

		// 设置企业客户签章位置信息
		PosBean posBean = SignHelper.doSetXYPosBean(signType, page, posX, posY, widthScaling);

		// 企业客户签署盖章
		SignHelper.doUserLocalSignPDF(serviceClient, accountId, organizeSealData, signPDFFileBean, posBean, signType);
	}

	/***
	 * <ul>
	 * <li>方法名称：接口调用方(平台方)签署盖章</li>
	 * <li>文件方式：PDF文件字节流</li>
	 * <li>方法用途：演示接口调用方(平台方)加盖公章</li>
	 * <li>Demo封装方法：doSign_PlatformByPDFBytes</li>
	 * </ul>
	 * 
	 * @return fileDigestSignResult
	 * @throws DemoException
	 */
	public static FileDigestSignResult doSign_PlatformByPDFBytes(ServiceClient serviceClient, byte[] pdfBytes, String outPdfPath,
			String signLogFileName, String ownerPWD) throws DemoException {

		SignPDFStreamBean signPDFStreamBean = null;

		// 签署后PDF文件本地保存路径,如果希望签署后依然返回PDF文件字节流时请设置该属性为空
		if (StringTools.isNotNull(outPdfPath)) {
			// 设置接口调用方(平台方)签署PDF文档信息,此时签署后不返回PDF文件字节流而是直接按照outPdfPath参数的地址保存签署后的PDF文件
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, outPdfPath, signLogFileName, ownerPWD);
		} else {
			// 设置接口调用方(平台方)签署PDF文档信息,此时签署后直接返回PDF文件字节流,需要接口调用方获取文件字节流后按照实际业务处理(保存为本地文件或传递到其他位置)
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, null, signLogFileName, ownerPWD);
		}
		
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Single;

		// 签署页码,单页签署时页码格式为"1";若为多页签署时，支持页码格式"1-3,5,8"
		String page = "1";
		// 签署位置X坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面左端的横向移动距离,单位为px
		float posX = 170F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 714F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 159F;

		// 接口调用方(平台方)的印章,请在www.tsign.cn官网中设置默认印章其sealId值为0
		int sealId = 0;
		// 设置接口调用方(平台方)签章位置信息
		PosBean posBean = SignHelper.doSetXYPosBean(signType, page, posX, posY, widthScaling);

		// 接口调用方(平台方)签署盖章
		FileDigestSignResult fileDigestSignResult = SignHelper.doSelfLocalSignPDF(serviceClient, signPDFStreamBean, posBean, sealId, signType);
		return fileDigestSignResult;
	}
	
	/***
	 * <ul>
	 * <li>方法名称：个人客户签署盖章</li>
	 * <li>文件方式：PDF文件字节流</li>
	 * <li>方法用途：演示个人客户加盖公章</li>
	 * <li>Demo封装方法：doSign_PersonByPdfBytes</li>
	 * </ul>
	 * 
	 * @return fileDigestSignResult
	 * @throws DemoException
	 */
	public static FileDigestSignResult doSign_PersonByPDFBytes(ServiceClient serviceClient, String accountId, String sealData,
			byte[] pdfBytes, String outPdfPath, String signLogFileName, String ownerPWD) throws DemoException {
		
		SignPDFStreamBean signPDFStreamBean = null;

		// 签署后PDF文件本地保存路径,如果希望签署后依然返回PDF文件字节流时请设置该属性为空
		if (StringTools.isNotNull(outPdfPath)) {
			// 设置个人客户签署PDF文档信息,此时签署后不返回PDF文件字节流而是直接按照outPdfPath参数的地址保存签署后的PDF文件
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, outPdfPath, signLogFileName, ownerPWD);
		} else {
			// 设置个人客户签署PDF文档信息,此时签署后直接返回PDF文件字节流,需要接口调用方获取文件字节流后按照实际业务处理(保存为本地文件或传递到其他位置)
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, null, signLogFileName, ownerPWD);
		}
		
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Key;
		// 关键字
		String key = "乙方签名";
		// 签署位置X坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面左端的横向移动距离,单位为px
		float posX = 105F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 5F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 90F;
		// 印章SealData
		String personSealData = sealData;

		// 设置个人客户签章位置信息
		PosBean posBean = SignHelper.doSetKeyPosBean(signType, key, posX, posY, widthScaling);
		// 个人客户签署盖章
		FileDigestSignResult fileDigestSignResult = SignHelper.doUserLocalSignPDF(serviceClient, accountId, personSealData, signPDFStreamBean, posBean, signType);
		return fileDigestSignResult;
	}
	
	/***
	 * <ul>
	 * <li>方法名称：企业客户签署盖章</li>
	 * <li>文件方式：PDF文件字节流</li>
	 * <li>方法用途：演示企业客户加盖公章</li>
	 * <li>Demo封装方法：doSign_OrganizeByPDFBytes</li>
	 * </ul>
	 * 
	 * @return fileDigestSignResult
	 * @throws DemoException
	 */
	public static FileDigestSignResult doSign_OrganizeByPDFBytes(ServiceClient serviceClient, String accountId, String sealData,
			byte[] pdfBytes, String outPdfPath, String signLogFileName, String ownerPWD) throws DemoException {
		
		SignPDFStreamBean signPDFStreamBean = null;

		// 签署后PDF文件本地保存路径,如果希望签署后依然返回PDF文件字节流时请设置该属性为空
		if (StringTools.isNotNull(outPdfPath)) {
			// 设置企业客户签署PDF文档信息,此时签署后不返回PDF文件字节流而是直接按照outPdfPath参数的地址保存签署后的PDF文件
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, outPdfPath, signLogFileName, ownerPWD);
		} else {
			// 设置企业客户签署PDF文档信息,此时签署后直接返回PDF文件字节流,需要接口调用方获取文件字节流后按照实际业务处理(保存为本地文件或传递到其他位置)
			signPDFStreamBean = SignHelper.doSetSignPDFStreamBean(pdfBytes, null, signLogFileName, ownerPWD);
		}
		// 签章类型,Single-单页签章、Multi-多页签章、Edges-骑缝章、Key-关键字签章
		SignType signType = SignType.Single;
		// 签署页码,单页签署时页码格式为"1";若为多页签署时，支持页码格式"1-3,5,8"
		String page = "1";
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posX = 120F;
		// 签署位置Y坐标,默认值为0,以pdf页面的左下角作为原点,控制距离页面底端的纵向移动距离,单位为px
		float posY = 100F;
		// 印章图片在PDF文件中的等比缩放大小,公章标准大小为4.2厘米即159px
		float widthScaling = 159F;
		// 印章SealData
		String organizeSealData = sealData;

		// 设置企业客户签章位置信息
		PosBean posBean = SignHelper.doSetXYPosBean(signType, page, posX, posY, widthScaling);

		// 企业客户签署盖章
		FileDigestSignResult fileDigestSignResult = SignHelper.doUserLocalSignPDF(serviceClient, accountId, organizeSealData, signPDFStreamBean, posBean, signType);
		return fileDigestSignResult;
	}

	/***
	 * 创建个人客户账户
	 * 
	 * @throws DemoException
	 */
	public static String addPersonAccount(ServiceClient serviceClient) throws DemoException {

		PersonBean personBean = new PersonBean();
		// 姓名
		personBean.setName("欣哲");
		// 证件号码
		personBean.setIdNo("310101199003073412");
		// 用于接收签署验证码的手机号码,可空
		// personBean.setMobile("");

		// 个人归属地：
		// MAINLAND-大陆身份证|HONGKONG-香港居民来往内地通行证|MACAO-澳门居民来往内地通行证|TAIWAN-台湾居民来往大陆通行证
		// PASSPORT-中国护照|FOREIGN-外籍证件|OTHER-其他
		personBean.setPersonArea(LegalAreaType.MAINLAND);

		// 所属公司,可空
		// personBean.setOrgan("XX有限公司");
		// 职位,可空
		// personBean.setTitle("部门经理");

		// 个人客户账户AccountId
		String personAccountId = AccountHelper.doAddAccount(serviceClient, personBean);
		return personAccountId;
	}

	/***
	 * 创建企业客户账户
	 * 
	 * @throws DemoException
	 */
	public static String addOrganizeAccount(ServiceClient serviceClient) throws DemoException {

		OrganizeBean organizeBean = new OrganizeBean();
		// 企业名称
		organizeBean.setName("天之云信息科技有限公司");// 天之云信息科技有限公司为公共使用，请不要随意更改该企业的名称
		// 单位类型，0-普通企业，1-社会团体，2-事业单位，3-民办非企业单位，4-党政及国家机构
		organizeBean.setOrganType(0);
		// 企业注册类型，NORMAL:组织机构代码号，MERGE：多证合一，传递社会信用代码号,REGCODE:企业工商注册码,默认NORMAL
		organizeBean.setRegType(OrganRegType.MERGE);
		// 组织机构代码号、社会信用代码号或工商注册号
		organizeBean.setOrganCode("91360823092907952B");// 52227058XT51M4AL62为公共使用，请不要随意更改该企业的证件号码
		// 用于接收签署验证码的手机号码,可空
		// organizeBean.setMobile("");

		// 公司地址,可空
		// organizeBean.setAddress("杭州城落霞峰7号");
		// 经营范围,可空
		// organizeBean.setScope("");

		// 注册类型,1-代理人注册,2-法人注册,0-缺省注册无需法人或代理人信息
		int userType = 0;
		switch (userType) {
		case 0:
			// 0-缺省注册无需法人或代理人信息
			organizeBean.setUserType(0);
			break;
		case 1:
			// 1-代理人注册
			organizeBean.setUserType(1);
			// 代理人姓名，当注册类型为1时必填
			organizeBean.setAgentName("艾利");
			// 代理人身份证号，当注册类型为1时必填
			organizeBean.setAgentIdNo("220301198705170035");
			break;
		case 2:
			// 2-法人注册
			organizeBean.setUserType(2);
			// 法定代表姓名，当注册类型为2时必填
			organizeBean.setLegalName("天云");
			// 法定代表人归属地,0-大陆，1-香港，2-澳门，3-台湾，4-外籍，默认0
			organizeBean.setLegalArea(0);
			// 法定代表身份证号/护照号，当注册类型为2时必填
			organizeBean.setLegalIdNo("220301198705170019");
			break;
		}

		// // 企业客户账户AccountId
		String organizeAccountId = AccountHelper.doAddAccount(serviceClient, organizeBean);
		return organizeAccountId;
	}

	/***
	 * 创建个人客户模板印章
	 * 
	 * @throws DemoException
	 */
	public static String addPersonTemplateSeal(ServiceClient serviceClient, String accountId) throws DemoException {

		// 印章模板类型,可选SQUARE-正方形印章 | RECTANGLE-矩形印章 | BORDERLESS-无框矩形印章
		PersonTemplateType personTemplateType = PersonTemplateType.RECTANGLE;

		// 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
		SealColor sealColor = SealColor.RED;

		// 个人模板印章SealData
		String personSealData = SealHelper.doAddTemplateSeal(serviceClient, accountId, personTemplateType, sealColor);
		return personSealData;
	}

	/***
	 * 创建企业客户模板印章
	 * 
	 * @throws DemoException
	 */
	public static String addOrganizeTemplateSeal(ServiceClient serviceClient, String accountId) throws DemoException {

		// 印章模板类型,可选STAR-标准公章 | DEDICATED-圆形无五角星章 | OVAL-椭圆形印章
		OrganizeTemplateType organizeTemplateType = OrganizeTemplateType.STAR;

		// 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
		SealColor sealColor = SealColor.RED;

		// hText 生成印章中的横向文内容 如“合同专用章、财务专用章”
		String hText = "合同专用章";

		// qText 生成印章中的下弦文内容 如公章防伪码（一般为13位数字）
		String qText = "9101008613560";

		// 企业模板印章SealData
		String organizeSealData = SealHelper.doAddTemplateSeal(serviceClient, accountId, organizeTemplateType,
				sealColor, hText, qText);
		return organizeSealData;
	}

	/***
	 * 本地SDK创建个人客户模板印章
	 * 
	 * @throws DemoException
	 */
	public static String addLocalPersonTemplateSeal(ServiceClient serviceClient) throws DemoException {

		// 印章模板类型,可选SQUARE-正方形印章 | RECTANGLE-矩形印章 | BORDERLESS-无框矩形印章
		PersonTemplateType personTemplateType = PersonTemplateType.RECTANGLE;

		// 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
		SealColor sealColor = SealColor.RED;

		// 待创建印章的内容文本(个人客户名称)
		String personName = "欣哲";

		// 个人模板印章SealData
		String personSealData = SealHelper.doCreatePersonalTemplateSeal(serviceClient, personTemplateType, personName,
				sealColor);
		return personSealData;
	}

	/***
	 * 本地SDK创建企业客户模板印章
	 * 
	 * @throws DemoException
	 */
	public static String addLocalOrganizeTemplateSeal(ServiceClient serviceClient) throws DemoException {

		// 印章模板类型,可选STAR-标准公章 | DEDICATED-圆形无五角星章 | OVAL-椭圆形印章
		OrganizeTemplateType organizeTemplateType = OrganizeTemplateType.STAR;

		// 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
		SealColor sealColor = SealColor.RED;

		// roundText 生成印章中的上弦文(企业客户名称)
		String roundText = "天之云信息科技有限公司";

		// hText 生成印章中的横向文内容 如“合同专用章、财务专用章”
		String hText = "合同专用章";

		// qText 生成印章中的下弦文内容 如公章防伪码（一般为13位数字）
		String qText = "9101008613560";

		// 企业模板印章SealData
		String organizeSealData = SealHelper.doCreateOfficialTemplateSeal(serviceClient, organizeTemplateType,
				roundText, hText, qText, sealColor);
		return organizeSealData;
	}

//	/***
//	 * 本地PDF模板生成
//	 * 
//	 * @param serviceClient
//	 * @throws DemoException
//	 */
//	public static void doCreateFileFromTemplate(ServiceClient serviceClient) throws DemoException {
//		// 当前程序所在文件目录
//		rootFolder = new File("").getAbsolutePath();
//		// PDF模板文件路径
//		String pdfTemplatePath = rootFolder + File.separator + "pdf" + File.separator + "newPDF1.pdf";
//		// PDF模板文件填充数据后文件存放路径
//		String filledPdfPath = rootFolder + File.separator + "pdf" + File.separator + "filled.pdf";
//		// 文本域填充数据
//		Map<String, Object> txtFields = new LinkedHashMap<String, Object>();
//		txtFields.put("name", "张隆");
//		txtFields.put("iden", "你们2018");
//		// txtFields.put("lesser_Month", "06");
//		// txtFields.put("lesser_Day", "12");
//		// txtFields.put("renter_Name", "欣哲");
//		// txtFields.put("renter_Year", "2018");
//		// txtFields.put("renter_Month", "06");
//		// txtFields.put("renter_Day", "12");
//		// 填充后是否禁用现有文档中的表单域: true-关闭表单域 | false-不关闭表单域
//		// 建议PDF文件填充完毕后关闭表单域
//		Boolean isFlat = true;
//		// 如果PDF模板文件设有密码则需要传入编辑密码口令
//		String pdfEditPWD = null;
//		// 本地PDF模板生成(填充)
//		PDFTemplateHelper.doCreateFileFromTemplate(serviceClient, pdfTemplatePath, filledPdfPath, pdfEditPWD, isFlat,
//				txtFields);
//	}

}
