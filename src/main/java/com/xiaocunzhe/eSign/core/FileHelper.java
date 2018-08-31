package com.xiaocunzhe.eSign.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

/***
 * @Description: 电子签名_文件辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年06月19日
 */
public class FileHelper {

	/***
	 * <ul>
	 * <li>方法名称：获取文件的字节流</li>
	 * <li>方法用途：用于获取PDF文件的字节流</li>
	 * <li>Demo封装方法：doGetFileBytes</li>
	 * <li>SDK接口名称：非SDK提供的接口,此方法为方便Demo演示而封装,仅供参考</li>
	 * </ul>
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] doGetFileBytes(String filePath)  {

		File file = new File(filePath);
		FileInputStream fis = null;
		byte[] buffer = null;
		try {
			fis = new FileInputStream(file);
			buffer = new byte[(int) file.length()];
			fis.read(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("获取文件的字节流时发生异常：{0}", e.getMessage());
			throw new RuntimeException(exMsg);
		} catch (IOException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("获取文件的字节流时IO操作发生异常：{0}", e.getMessage());
			throw new RuntimeException(exMsg);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					String exMsg = MessageFormat.format("关闭FileInputStream时发生异常：{0}", e.getMessage());
					throw new RuntimeException(exMsg);
				}
			}
		}
		return buffer;
	}

	/***
	 * <ul>
	 * <li>方法名称：文件字节流保存为本地文件</li>
	 * <li>方法用途：用于将PDF字节流保存为本地文件</li>
	 * <li>Demo封装方法：doSaveFileByStream</li>
	 * <li>SDK接口名称：非SDK提供的接口,此方法为方便Demo演示而封装,仅供参考</li>
	 * </ul>
	 * 
	 * @param bytes PDF 字节流
	 * @param outSignedPdfPath 签署后PDF文件保存路径
	 */
	public static void doSaveFileByStream(byte[] bytes, String outSignedPdfPath)  {

		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			File outSignedPdfFile = new File(outSignedPdfPath);
			String directoryStr = outSignedPdfFile.getParent();
			File directory = new File(directoryStr);
			// 判断文件目录是否存在
			if (!directory.exists() && directory.isDirectory()) {
				directory.mkdirs();
			}			
			fos = new FileOutputStream(outSignedPdfFile);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("文件字节流保存为本地文件时发生异常：{0}", e.getMessage());
			throw new RuntimeException(exMsg);
		} catch (IOException e) {
			e.printStackTrace();
			String exMsg = MessageFormat.format("文件字节流保存为本地文件时IO操作发生异常：{0}", e.getMessage());
			throw new RuntimeException(exMsg);
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
					String exMsg = MessageFormat.format("关闭BufferedOutputStream时发生异常：{0}", e.getMessage());
					throw new RuntimeException(exMsg);
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					String exMsg = MessageFormat.format("关闭FileOutputStream时发生异常：{0}", e.getMessage());
					throw new RuntimeException(exMsg);
				}
			}
		}
	}	
}
