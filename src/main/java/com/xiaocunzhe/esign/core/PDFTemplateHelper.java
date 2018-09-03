package com.xiaocunzhe.esign.core;

import java.text.MessageFormat;
import java.util.Map;

import com.timevale.esign.sdk.tech.bean.SignPDFFileBean;
import com.timevale.esign.sdk.tech.bean.SignPDFStreamBean;
import com.timevale.esign.sdk.tech.bean.result.FileCreateFromTemplateResult;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.timevale.esign.sdk.tech.v3.service.PdfDocumentService;
import com.xiaocunzhe.esign.exception.DemoException;
import com.xiaocunzhe.esign.util.StringTools;


/***
 * @Description: PDF模板填充辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018/06/02
 */
public class PDFTemplateHelper {

	/***
	 * <ul>
	 * <li>方法名称：本地PDF模板生成</li>
	 * <li>文件方式：本地文件路径</li>
	 * <li>方法用途：根据本地PDF模板文件路径填充数据生成待签署PDF文件</li>
	 * <li>Demo封装方法：doCreateFileFromTemplate</li>
	 * <li>SDK接口名称:createFileFromTemplate</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param srcPdfPath
	 * @param outPdfPath
	 * @param pdfEditPWD
	 * @param isFlat
	 * @param txtFields
	 * @throws DemoException
	 */
	public static void doCreateFileFromTemplate(ServiceClient serviceClient, String srcPdfPath, String outPdfPath,
			String pdfEditPWD, Boolean isFlat, Map<String, Object> txtFields) throws DemoException {
		// 设置模板生成接口中输入输出文件路径封装对象SignPDFFileBean属性
		SignPDFFileBean signPDFFileBean = new SignPDFFileBean();
		// 待填充PDF模板文件路径
		signPDFFileBean.setSrcPdfFile(srcPdfPath);
		// 填充后PDF文件保存路径
		signPDFFileBean.setDstPdfFile(outPdfPath);
		// 如果PDF文件设有密码则需要传入编辑密码口令
		if (StringTools.isNotNull(pdfEditPWD)) {
			// 待填充PDF模板文件编辑密码
			signPDFFileBean.setOwnerPassword(pdfEditPWD);
		}

		try {
			PdfDocumentService pdfDocumentService = serviceClient.pdfDocumentService();
			FileCreateFromTemplateResult pdfFillResult = pdfDocumentService.createFileFromTemplate(signPDFFileBean,
					isFlat, txtFields);
			if (0 != pdfFillResult.getErrCode()) {
				String exMsg = MessageFormat.format("本地PDF模板生成失败: errCode = {0},msg = {1}",
						String.valueOf(pdfFillResult.getErrCode()), pdfFillResult.getMsg());
				throw new DemoException(exMsg);
			} else {
				System.out
						.println(MessageFormat.format("本地PDF模板生成成功,填充后PDF文件保存路径: {0}", pdfFillResult.getDstPdfFile()));
			}
		} catch (NullPointerException ex) {
			String exMsg = "客户端serviceClient为空,请检查客户端serviceClient是否成功获取.";
			throw new DemoException(exMsg);
		}
	}

	/***
	 * <ul>
	 * <li>方法名称：本地PDF模板生成(文件流)</li>
	 * <li>文件方式：文件字节流数组</li>
	 * <li>方法用途：根据PDF模板文件字节流数组填充数据生成待签署PDF文件</li>
	 * <li>Demo封装方法：doCreateFileFromTemplate</li>
	 * <li>SDK接口名称:createFileFromTemplate</li> 
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param pdfBytes
	 * @param outPdfPath
	 * @param pdfEditPWD
	 * @param isFlat
	 * @param txtFields
	 * @throws DemoException
	 */
	public static void doCreateFileFromTemplateA(ServiceClient serviceClient, byte[] pdfBytes, String outPdfPath,
			String pdfEditPWD, Boolean isFlat, Map<String, Object> txtFields) throws DemoException {
		// 设置模板生成接口中输入输出文件路径封装对象SignPDFFileBean属性
		SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
		// 待填充PDF模板文件字节流数组
		signPDFStreamBean.setStream(pdfBytes);
		// 填充后PDF文件保存路径
		signPDFStreamBean.setDstPdfFile(outPdfPath);
		// 如果PDF文件设有密码则需要传入编辑密码口令
		if (StringTools.isNotNull(pdfEditPWD)) {
			// 待填充PDF模板文件编辑密码
			signPDFStreamBean.setOwnerPassword(pdfEditPWD);
		}
		PdfDocumentService pdfDocumentService = serviceClient.pdfDocumentService();
		FileCreateFromTemplateResult pdfFillResult = pdfDocumentService.createFileFromTemplate(signPDFStreamBean,
				isFlat, txtFields);
		if (0 != pdfFillResult.getErrCode()) {
			String exMsg = MessageFormat.format("本地PDF模板生成(文件流)失败: errCode = {0},msg = {1}",
					String.valueOf(pdfFillResult.getErrCode()), pdfFillResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			System.out
					.println(MessageFormat.format("本地PDF模板生成(文件流)成功,填充后PDF文件保存路径: {0}", pdfFillResult.getDstPdfFile()));
		}
	}
}
