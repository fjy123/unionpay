package com.xiaocunzhe.esign.util;

public class StringTools {
	/***
	 * 功能说明：字符串为空判断
	 * 
	 * @param str
	 * @return true:字符串为空 | false:字符串不为空
	 */
	public static boolean isNull(String str) {
		if (null == str || str.length() == 0) {
			return true;
		}
		return false;
	}

	/***
	 * 功能说明：字符串不为空判断
	 * 
	 * @param str
	 * @return true:字符串不为空 | false:字符串为空
	 */
	public static boolean isNotNull(String str) {
		if (null == str || str.length() == 0) {
			return false;
		}
		return true;
	}
	
	/***
	 * 功能说明：Object对象为空判断
	 * 
	 * @param str
	 * @return true:Object对象为空 | false:Object对象不为空
	 */
	public static boolean isNull(Object obj) {
		if (null == obj) {
			return true;
		}
		return false;
	}
	
	/***
	 * 功能说明：Object对象不为空判断
	 * 
	 * @param str
	 * @return true:Object对象不为空 | false:Object对象为空
	 */
	public static boolean isNotNull(Object obj) {
		if (null == obj) {
			return false;
		}
		return true;
	}
}
