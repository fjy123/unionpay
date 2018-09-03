package com.xiaocunzhe.esign.util;

public class DemoMessage {
	/***
	 * 示例Demo重要提示
	 */
	public static void showImportantMessage() {
		System.err.println("1、本Demo是用来阐述SDK中接口的基本使用方法,仅针对大众场景,供接口调用方(平台方)接入参考.特殊情况还请接口调用方(平台方)自行调整,确保符合接口调用方(平台方)实际业务需求;");
		System.err.println("2、本Demo中定义接口调用方(平台方)为在e签宝官网注册开发者账号并集成本SDK且拥有直接调用SDK中接口权限的一方;");
		System.err.println("3、平台自身摘要签署:表示接口调用方(平台方)自己公司需要在PDF文件中盖上企业公章;");
		System.err.println("4、平台用户摘要签署:表示接口调用方(平台方)的个人客户或企业客户需要在PDF文件中盖上个人印章或企业公章;");
		System.err.println("5、本SDK默认信任接口调用方(平台方)传入的数据都是真实有效准确的,故此接口调用方(平台方)需要保证各项数据的真实有效性和准确性;");
		System.err.println("6、接口调用方(平台方)需要妥善保管每次调用签署类接口时返回SignServiceId(签署记录ID)和签署后的PDF文件;");
		System.err.println("7、正式上线前需要提前2-4天联系e签宝工作人员进行接口权限和套餐次数的配置,请提前与e签宝工作人员进行沟通上线事宜;");
		System.err.println("8、其他事项请结合贵司实际业务需求和e签宝的接口文档进行联调,如有疑问请随时与e签宝工作人员进行交流确认;");
		System.err.println("- - - - - - - - - - # # # # - - - - - - - - - - ");
	}
}
