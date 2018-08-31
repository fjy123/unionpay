package com.xiaocunzhe.eSign.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.codec.binary.Base64;

import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.seal.OrganizeTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
import com.timevale.esign.sdk.tech.service.SealService;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.timevale.esign.sdk.tech.v3.service.TemplateSealService;

import cn.hangzhou.demo.exception.DemoException;
/***
 * @Description: 电子签名_印章辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年06月19日
 */
public class SealHelper {

	/***
	 * <ul>
	 * <li>方法名称：创建个人模板印章</li>
	 * <li>方法用途：通过接口调用e签宝印章服务端创建个人客户的模板印章</li>
	 * <li>Demo封装方法：doAddTemplateSeal</li>
	 * <li>SDK接口名称：addTemplateSeal</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @param personTemplateType
	 * @param sealColor
	 * @return
	 * @throws DemoException
	 */
	public static String doAddTemplateSeal(ServiceClient serviceClient, String accountId,
			PersonTemplateType personTemplateType, SealColor sealColor) throws DemoException {

		SealService sealService = serviceClient.sealService();
		AddSealResult addSealResult = sealService.addTemplateSeal(accountId, personTemplateType, sealColor);
		// 个人模板印章SealData
		String personSealData = null;
		if (0 != addSealResult.getErrCode()) {
			String exMsg = MessageFormat.format("创建个人模板印章失败: errCode = {0},msg = {1}",
					String.valueOf(addSealResult.getErrCode()), addSealResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			personSealData = addSealResult.getSealData();
			System.out.println(
					MessageFormat.format("创建个人模板印章成功: sealData = {0}, 可将该SealData保存到贵司数据库以便日后直接使用.", personSealData));
		}
		return personSealData;
	}

	/***
	 * <ul>
	 * <li>方法名称：创建企业模板印章</li>
	 * <li>方法用途：通过接口调用e签宝印章服务端创建企业客户的模板印章</li>
	 * <li>Demo封装方法：doAddTemplateSeal</li>
	 * <li>SDK接口名称：addTemplateSeal</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @param organizeTemplateType
	 * @param sealColor
	 * @param hText
	 * @param qText
	 * @return
	 * @throws DemoException
	 */
	public static String doAddTemplateSeal(ServiceClient serviceClient, String accountId,
			OrganizeTemplateType organizeTemplateType, SealColor sealColor, String hText, String qText)
			throws DemoException {

		SealService sealService = serviceClient.sealService();
		AddSealResult addSealResult = sealService.addTemplateSeal(accountId, organizeTemplateType, sealColor, hText,
				qText);
		// 企业模板印章SealData
		String organizeSealData = null;
		if (0 != addSealResult.getErrCode()) {
			String exMsg = MessageFormat.format("创建企业模板印章失败: errCode = {0},msg = {1}",
					String.valueOf(addSealResult.getErrCode()), addSealResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			organizeSealData = addSealResult.getSealData();
			System.out.println(
					MessageFormat.format("创建企业模板印章成功: sealData = {0}, 可将该SealData保存到贵司数据库以便日后直接使用.", organizeSealData));
		}
		return organizeSealData;
	}

	/***
	 * <ul>
	 * <li>方法名称：创建个人模板印章(本地)</li>
	 * <li>方法用途：通过接口调用SDK内置印章服务创建个人客户的模板印章</li>
	 * <li>Demo封装方法：doCreatePersonalTemplateSeal</li>
	 * <li>SDK接口名称：createPersonalTemplateSeal</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @param personTemplateType
	 * @param sealColor
	 * @return
	 * @throws DemoException
	 */
	public static String doCreatePersonalTemplateSeal(ServiceClient serviceClient,
			PersonTemplateType personTemplateType, String text, SealColor sealColor) throws DemoException {

		TemplateSealService templateSealService = serviceClient.templateSealService();
		AddSealResult addSealResult = templateSealService.createPersonalTemplateSeal(personTemplateType, text,
				sealColor);
		// 个人模板印章SealData
		String personSealData = null;
		if (0 != addSealResult.getErrCode()) {
			String exMsg = MessageFormat.format("本地SDK创建个人模板印章失败: errCode = {0},msg = {1}",
					String.valueOf(addSealResult.getErrCode()), addSealResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			personSealData = addSealResult.getSealData();
			System.out.println(MessageFormat.format("本地SDK创建个人模板印章成功: sealData = {0}, 可将该SealData保存到贵司数据库以便日后直接使用.",
					personSealData));
		}
		return personSealData;
	}

	/***
	 * <ul>
	 * <li>方法名称：创建企业模板印章(本地)</li>
	 * <li>方法用途：通过接口调用SDK内置印章服务创建企业客户的模板印章</li>
	 * <li>Demo封装方法：doCreateOfficialTemplateSeal</li>
	 * <li>SDK接口名称：createOfficialTemplateSeal</li>
	 * </ul>
	 * 
	 * @param serviceClient
	 * @param accountId
	 * @param organizeTemplateType
	 * @param sealColor
	 * @param hText
	 * @param qText
	 * @return
	 * @throws DemoException
	 */
	public static String doCreateOfficialTemplateSeal(ServiceClient serviceClient,
			OrganizeTemplateType organizeTemplateType, String roundText, String hText, String qText,
			SealColor sealColor) throws DemoException {

		TemplateSealService templateSealService = serviceClient.templateSealService();
		AddSealResult addSealResult = templateSealService.createOfficialTemplateSeal(organizeTemplateType, roundText,
				hText, qText, sealColor);
		// 企业模板印章SealData
		String organizeSealData = null;
		if (0 != addSealResult.getErrCode()) {
			String exMsg = MessageFormat.format("本地SDK创建企业模板印章失败: errCode = {0},msg = {1}",
					String.valueOf(addSealResult.getErrCode()), addSealResult.getMsg());
			throw new DemoException(exMsg);
		} else {
			organizeSealData = addSealResult.getSealData();
			System.out.println(MessageFormat.format("本地SDK创建企业模板印章成功: sealData = {0}, 可将该SealData保存到贵司数据库以便日后直接使用.",
					organizeSealData));
		}
		return organizeSealData;
	}

	/***
	 * <ul>
	 * <li>方法名称：通过网络图片URL获取Base64字符串</li>
	 * <li>方法用途：通过Base64将印章图片转换成可作为sealData使用的Base64字符串</li>
	 * <li>Demo封装方法：doGetImageBase64StrFromUrl</li>
	 * <li>SDK接口名称：非SDK提供的接口,此方法为方便Demo演示而封装,仅供参考</li>
	 * </ul>
	 * 
	 * @param imgURL
	 * @return
	 * @throws DemoException
	 */
	public static String doGetImageBase64StrFromUrl(String imgURL) throws DemoException {
		byte[] data = null;
		InputStream inStream = null;
		try {
			// 创建URL
			URL url = new URL(imgURL);
			// 创建链接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			inStream = conn.getInputStream();
			data = new byte[inStream.available()];
			inStream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("通过图片URL获取Base64字符串时发生异常：{0}", e.getMessage());
			throw new DemoException(exMsg);
		} finally {
			try {
				if (null != inStream) {
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				String exMsg = MessageFormat.format("通过图片URL获取Base64字符串关闭InputStream时发生异常：{0}", e.getMessage());
				throw new DemoException(exMsg);
			}
		}

		// 对字节数组Base64编码
		byte[] encodeBase64 = Base64.encodeBase64(data);
		// 返回Base64编码过的字节数组字符串
		String imgageBase64String = new String(encodeBase64);

		if (null == imgageBase64String || imgageBase64String.length() == 0) {
			throw new DemoException("图片Base64字符串为空,请检查造成原因.");
		}
		return imgageBase64String;
	}

	/***
	 * <ul>
	 * <li>方法名称：通过本地图片文件路径获取Base64字符串</li>
	 * <li>方法用途：通过Base64将印章图片转换成可作为sealData使用的Base64字符串</li>
	 * <li>Demo封装方法：doGetImageStrFromPath</li>
	 * <li>SDK接口名称：非SDK提供的接口,此方法为方便Demo演示而封装,仅供参考</li>
	 * </ul>
	 * 
	 * @param imgPath
	 * @return
	 * @throws DemoException
	 */
	public static String doGetImageStrFromPath(String imgPath) throws DemoException {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgPath);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("通过图片文件路径获取Base64字符串时发生异常：{0}", e.getMessage());
			throw new DemoException(exMsg);
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				String exMsg = MessageFormat.format("通过图片URL获取Base64字符串关闭InputStream时发生异常：{0}", e.getMessage());
				throw new DemoException(exMsg);
			}
		}
		// 对字节数组Base64编码
		byte[] encodeBase64 = Base64.encodeBase64(data);
		// 返回Base64编码过的字节数组字符串
		String imgageBase64String = new String(encodeBase64);

		if (null == imgageBase64String || imgageBase64String.length() == 0) {
			throw new DemoException("图片Base64字符串为空,请检查造成原因.");
		}
		return imgageBase64String;
	}

	/***
	 * <ul>
	 * <li>方法名称：印章SealData保存为本地印章图片文件</li>
	 * <li>方法用途：通过Base64解码将印章SealData还原并保存到本地</li>
	 * <li>Demo封装方法：doGetImageStrFromPath</li>
	 * <li>SDK接口名称：非SDK提供的接口,此方法为方便Demo演示而封装,仅供参考</li>
	 * </ul>
	 * 
	 * @param imgBase64String
	 * @param dir
	 * @param fileName
	 * @return
	 * @throws DemoException
	 */
	public static void doSaveBase64AsImageFile(String imgBase64String, String dir, String fileName)
			throws DemoException {
		// 图像数据为空
		if (imgBase64String == null) {
			throw new DemoException("将Base64字符串保存为本地图片文件时发生异常：图片Base64字符串不能为空");
		}

		// Base64解码
		byte[] bytes = Base64.decodeBase64(new String(imgBase64String).getBytes());
		for (int i = 0; i < bytes.length; ++i) {
			if (bytes[i] < 0) {
				// 调整异常数据
				bytes[i] += 256;
			}
		}

		OutputStream out = null;
		try {

			File directory = new File(dir);
			// 判断文件目录是否存在
			if (!directory.exists() && directory.isDirectory()) {
				// 如果不存在则创建目录
				directory.mkdirs();
			}
			// 本地图片文件保存路径
			String imageSaveToFilePath = dir + File.separator + fileName;
			out = new FileOutputStream(imageSaveToFilePath);
			out.write(bytes);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("请检查本地图片文件保存路径是否存在：{0}", e.getMessage());
			throw new DemoException(exMsg);
		} catch (IOException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("图片Base64字符串保存为本地图片文件时发生异常：{0}", e.getMessage());
			throw new DemoException(exMsg);
		} catch (Exception e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("图片Base64字符串保存为本地图片文件时发生异常：{0}", e.getMessage());
			throw new DemoException(exMsg);
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				String exMsg = MessageFormat.format("图片Base64字符串保存为本地图片文件关闭OutputStream时发生异常：{0}", e.getMessage());
				throw new DemoException(exMsg);
			}
		}
	}
}
