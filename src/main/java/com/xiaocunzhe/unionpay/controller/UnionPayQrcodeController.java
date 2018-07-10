package com.xiaocunzhe.unionpay.controller;

import com.jpay.unionpay.AcpService;
import com.jpay.unionpay.LogUtil;
import com.jpay.unionpay.SDKConfig;
import com.jpay.unionpay.SDKConstants;
import com.xiaocunzhe.util.AcpServiceCustomer;
import com.xiaocunzhe.util.Base64Util;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.xiaocunzhe.unionpay.controller.UnionPayController.getAllRequestParam;

/**
 * 主扫支付类
 */
@Controller
@RequestMapping("/unionpayQrcode")
public class UnionPayQrcodeController {
    private static final Logger logger = LoggerFactory.getLogger(UnionPayQrcodeController.class);

    private static final String merId = "777290058110097";
    private static final String issCode = "90880020";

    /**
     * 主扫--查询订单信息
     */

    @ApiOperation(value = "查询订单信息接口", notes = "查询订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "qrCode", value = "二维码链接", required = true, paramType = "form", dataType = "String")
            /**@ApiImplicitParam( name="dictType",value="字典类型",required=true,paramType="form",dataType="String"),
             @ApiImplicitParam( name="dictName",value="字典名称",required=true,paramType="form",dataType="String"),
             @ApiImplicitParam( name="state",value="字典状态",required=true,paramType="form",dataType="String"),*/
    })
    @RequestMapping(value = "/queryOrderInfo", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> queryOrderInfo(@ApiIgnore String qrCode) {
        Map<String, String> result = new HashMap<>();
        //获取到的二维码qrCode信息
        //String qrCode = req.getParameter("qrCode");
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0120000903");
        contentData.put("issCode", issCode);
        contentData.put("qrCode", qrCode);

        Map<String, String> reqData = AcpService.signByCertInfo(contentData, "D:/certs/acp_test_sign_inst.pfx", "000000", DemoBase.encoding);         //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
        Map<String, String> rspData = AcpServiceCustomer.post(reqData, requestUrl, DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                String payeeInfoStr = rspData.get("payeeInfo");
                if (("00").equals(respCode)) {
                    if (payeeInfoStr != null) {
                        //TODO 直接将base64解密即可内容如下
                        /**
                         * {id=777290058110048&merCatCode=5812&name=测试虚拟商户777290058110048&termId=01080209}
                         */
                        String decode = Base64Util.decode(payeeInfoStr);
                        rspData.put("payeeInfoData", decode);
                    }
                } else {
                    //其他应答码为失败请排查原因
                    //TODO
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return ResponseEntity.ok(rspData);
    }
//    /**
//     * 查询营销信息在查询订单之后调用  这一步已经支付
//     * @param req
//     * @param resp
//     */
//
//    @ApiOperation(value="查询营销信息", notes="查询营销信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(
//                    name="txnNo",value="交易序列号",required=true,paramType="form",dataType="String"),
//            @ApiImplicitParam(
//            name="txnAmt",value="交易金额",required=true,paramType="form",dataType="String"),
//             @ApiImplicitParam(
//             name="dictName",value="字典名称",required=true,paramType="form",dataType="String"),
//             @ApiImplicitParam(
//             name="state",value="字典状态",required=true,paramType="form",dataType="String")
//    })
//    @RequestMapping(value="/queryCouponInfo", method=RequestMethod.POST)
//    @PostMapping("/queryCouponInfo")
//    public void queryCouponInfo(@ApiIgnore String txnNo,@ApiIgnore String txnAmt,) throws IOException {
//        //调用查询订单后收到的交易序列号
//        /**
//         * 查询订单之后的应答用户信息
//         */
//        //用户支付凭证卡号token
//        String accNo = req.getParameter("accNo");
//        String name = req.getParameter("name");
//        String mobile = req.getParameter("mobile");
//        String acctClass = req.getParameter("acctClass");
//        String cardAttr = req.getParameter("cardAttr");
//
//        /**
//         * 收款方信息
//         */
//        String name_payeeInfo = req.getParameter("name_payeeInfo");
//        String merCatCode = req.getParameter("merCatCode");
//        String id = req.getParameter("id");
//
//        /**
//         * 组装请求报文
//         */
//        Map<String, String> contentData = new HashMap<String, String>();
//
//        contentData.put("version", "1.0.0");
//        contentData.put("reqType", "0180000903");
//        contentData.put("issCode", issCode);
//        contentData.put("txnNo", txnNo);
//        contentData.put("txnAmt", txnAmt);
//        contentData.put("currencyCode", "156");
//
//        Map<String, String> payerInfoMap = new HashMap<String, String>();
//        payerInfoMap.put("accNo", accNo);
//        payerInfoMap.put("name", name);
//        payerInfoMap.put("mobile", mobile);//目前不校验
//        payerInfoMap.put("acctClass", acctClass);
//        payerInfoMap.put("cardAttr", cardAttr);//01 – 借记卡 02 – 贷记卡（含准贷记卡）
//
//        Map<String, String> payeeInfoMap = new HashMap<String, String>();
//        payeeInfoMap.put("name",name_payeeInfo);
//        payeeInfoMap.put("id", id);
//        payeeInfoMap.put("merCatCode", merCatCode);//目前不校验
//        payeeInfoMap.put("termId", "49000002");
//
//        //riskInfo必送，详细参考规范说明
//        contentData.put("riskInfo", AcpService.base64Encode("{deviceID=123456999&deviceType=1&mobile=13525677809&accountIdHash=00000002&sourceIP=111.13.100.91}", DemoBase.encoding));
//
//        //敏感信息不加密使用DemoBase.getPayerInfo方法
//        //付款方信息
//        contentData.put("payerInfo", DemoBase.getPayerInfo(payerInfoMap,"UTF-8"));
//        //收款方信息
//        contentData.put("payeeInfo", DemoBase.getPayeeInfo(payeeInfoMap,"UTF-8"));
//
//        //如果对机构号issCode 配置了敏感信息加密，必须1.送encryptCertId 2.对payerInfo，payeeInfo的值整体加密
//        //目前二维码系统要求所有接入均采用加密方式，使用正式机构号测试的时候参考如下方法上送
//        //contentData.put("payerInfo", DemoBase.getPayerInfoWithEncrpyt(payerInfoMap,"UTF-8"));
//        //contentData.put("payeeInfo", DemoBase.getPayeeInfoWithEncrpyt(payeeInfoMap,"UTF-8"));
//        //contentData.put("encryptCertId", AcpService.getEncryptCertId());
//
//        Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
//
//        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
//        Map<String, String> rspData = AcpServiceCustomer.post(reqData,requestUrl,DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
//
//        if(!rspData.isEmpty()){
//            if(AcpService.validate(rspData, DemoBase.encoding)){
//                LogUtil.writeLog("验证签名成功");
//                String respCode = rspData.get("respCode") ;
//                if(("00").equals(respCode)){
//
//                }else{
//                    //其他应答码为失败请排查原因
//                    //TODO
//                }
//            }else{
//                LogUtil.writeErrorLog("验证签名失败");
//                //TODO 检查验证签名失败的原因
//            }
//        }else{
//            //未返回正确的http状态
//            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
//        }
//        String reqMessage = DemoBase.genHtmlResult(reqData);
//        String rspMessage = DemoBase.genHtmlResult(rspData);
//        resp.getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
//    }


    @PostMapping("/toPay")
    @ApiOperation("支付")
    @ApiImplicitParams({@ApiImplicitParam(name = "txnNo", value = "交易序列号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "accNo", value = "支付token", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "acctClass", value = "账户类型", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cardAttr", value = "卡属性", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "txnAmt", value = "交易金额", required = true, dataType = "String", paramType = "query")})
    public ResponseEntity<Map<String, String>> toPay(@ApiIgnore String txnNo, @ApiIgnore String txnAmt, @ApiIgnore String accNo, @ApiIgnore String mobile, @ApiIgnore String acctClass, @ApiIgnore String cardAttr) throws IOException {

        String backUrl = "http://fangjingyao.tunnel.qydev.com/unionpayQrcode/payResultResponse";
        /**
         * 组装请求报文
         */
        Map<String, String> contentData = new HashMap<String, String>();

        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0130000903");
        contentData.put("issCode", issCode);
        contentData.put("txnNo", txnNo);
        contentData.put("txnAmt", txnAmt);
        contentData.put("currencyCode", "156");

        //riskInfo必送，详细参考规范说明
        contentData.put("riskInfo", AcpService.base64Encode("{deviceID=123456999&deviceType=1&mobile=13525677809&accountIdHash=00000002&sourceIP=111.13.100.91}", DemoBase.encoding));

        /**
         * 优惠信息暂时不上送
         * if(null!=origTxnAmt && !"".equals(origTxnAmt))
         contentData.put("origTxnAmt", origTxnAmt);

         if(null !=couponInfo && !"".equals(couponInfo))
         contentData.put("couponInfo", couponInfo);*/

        Map<String, String> payerInfoMap = new HashMap<String, String>();
        payerInfoMap.put("accNo", accNo);
        /**
         * 姓名暂时不上送
         * if(null!=name && !"".equals(name)) payerInfoMap.put("name", name);
         */

        payerInfoMap.put("mobile", mobile);//手机号必送
        payerInfoMap.put("acctClass", acctClass);
        payerInfoMap.put("cardAttr", cardAttr);//01 – 借记卡 02 – 贷记卡（含准贷记卡）

        //敏感信息不加密使用DemoBase.getPayerInfo方法
        contentData.put("payerInfo", DemoBase.getPayerInfo(payerInfoMap, "UTF-8"));

        //如果对机构号issCode 配置了敏感信息加密，必须1.送encryptCertId 2.对payerInfo，payeeInfo的值整体加密
        //目前二维码系统要求所有接入均采用加密方式，使用正式机构号测试的时候参考如下方法上送

        //contentData.put("payerInfo", DemoBase.getPayerInfoWithEncrpyt(payerInfoMap,"UTF-8"));
        //contentData.put("encryptCertId", AcpService.getEncryptCertId());


        contentData.put("payerComments", "付款方附言测试");

        contentData.put("backUrl", backUrl);
        Map<String, String> reqData = AcpService.signByCertInfo(contentData, "D:/certs/acp_test_sign_inst.pfx", "000000", DemoBase.encoding);             //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
        Map<String, String> rspData = AcpServiceCustomer.post(reqData, requestUrl, DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //TODO 记录对账流水信息comInfo字段数据 付款凭证号 voucherNum 清算主键settleKey 清算日期 settleDate
                } else {
                    //其他应答码为失败请排查原因
                    //TODO
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return ResponseEntity.ok(rspData);
    }


    /**
     * 支付结果回调
     */
    @RequestMapping(value = "/payResultResponse", method = RequestMethod.POST)
    @ResponseBody
    public void backRcvResponse(HttpServletRequest request) {
        logger.info("------------银联回调进入-------------");
        String encoding = request.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(request);
        LogUtil.printRequestLog(reqParam);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(reqParam, encoding)) {
            LogUtil.writeLog("验证签名结果[失败].");
            //验签失败，需解决验签问题

        } else {
            LogUtil.writeLog("验证签名结果[成功].");
            //交易成功，更新商户订单状态
            String orderNo = reqParam.get("orderNo");//获取订单号
            logger.info(orderNo);
            //遍历结果
            Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                logger.info("key= " + entry.getKey() + " and value= " + entry.getValue());
            }

        }
    }

    @PostMapping("/queryPay")
    @ApiOperation(value = "查询订单支付结果", notes = "查询订单支付结果")
    @ApiImplicitParams({@ApiImplicitParam(name = "txnNo", value = "交易序列号", required = true, dataType = "String", paramType = "query")})
    public ResponseEntity<Map<String, String>> queryPay(@ApiIgnore String txnNo)  {

        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0140000903");
        contentData.put("issCode", issCode);
        contentData.put("txnNo", txnNo);

        Map<String, String> reqData = AcpService.signByCertInfo(contentData, "D:/certs/acp_test_sign_inst.pfx", "000000", DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
        Map<String, String> rspData = AcpServiceCustomer.post(reqData,requestUrl,DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){

                }else{
                    //其他应答码为失败请排查原因
                    //TODO
                }
            }else{
                LogUtil.writeErrorLog("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        }else{
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return ResponseEntity.ok(rspData);
    }

}
