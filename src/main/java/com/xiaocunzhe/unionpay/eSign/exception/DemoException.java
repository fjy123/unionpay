package com.xiaocunzhe.esign.exception;

public class DemoException extends Exception {

	private static final long serialVersionUID = -6941301122172734237L;
	private int errCode;
	private String message;

	/**
	 * 构造一个基本异常.
	 *
	 * @param message
	 *            信息描述
	 */
	public DemoException(String message) {
		this.setMessage(message);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorCode
	 *            错误编码
	 * @param message
	 *            信息描述
	 */
	public DemoException(int errCode, String message) {
		this.setErrCode(errCode);
		this.setMessage(message);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorCode
	 *            错误编码
	 */
	public DemoException(int errCode) {
		this.setErrCode(errCode);
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
