package com.xiaocunzhe.unionpay.controller;

import com.jpay.unionpay.AcpService;
import com.jpay.unionpay.LogUtil;
import com.jpay.unionpay.SDKConfig;
import com.xiaocunzhe.util.AcpServiceCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
     * @param req
     * @param resp
     * @throws IOException
     */

    @PostMapping("/queryOrderInfo")
    public void queryOrderInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //获取到的二维码qrCode信息
        //String qrCode = req.getParameter("qrCode");
        String qrCode = "https://qr.95516.com/00010001/62021340258856296653121724026238";

        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0120000903");
        contentData.put("issCode", issCode);
        contentData.put("qrCode", qrCode);

        Map<String, String> reqData = AcpService.signByCertInfo(contentData,"D:/certs/acp_test_sign_inst.pfx", "000000",DemoBase.encoding);		 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
        Map<String, String> rspData = AcpServiceCustomer.post(reqData,requestUrl,DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode") ;
                String payeeInfoStr = rspData.get("payeeInfo") ;
                if(("00").equals(respCode)){
                    if(payeeInfoStr != null){
                        //有敏感信息加密时这么解：
                        //Map<String, String> payeeInfo = DemoBase.parsePayeeInfoEnc(payeeInfoStr, DemoBase.encoding);
                        //没有敏感信息加密时这么解：
                        //Map<String, String> payeeInfo = SDKUtil.parseQString(payeeInfoStr);
                        //resp.getWriter().write("payeeInfo明文: " + payeeInfo + "<br>");
                    }
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
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
        resp.getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
    }

    /**
     * 查询营销信息在查询订单之后调用
     * @param req
     * @param resp
     */
    @PostMapping("/queryCouponInfo")
    public void queryCouponInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //调用查询订单后收到的交易序列号
        //String txnNo = req.getParameter("txnNo");
        String txnNo = "201807092923325402161577000709142404";
        //返回的交易金额
        //String txnAmt = req.getParameter("txnAmt");
        String txnAmt = "100";
        /**
         * 查询订单之后的应答用户信息
         */
        //用户支付凭证卡号token
        String accNo = req.getParameter("accNo");
        String name = req.getParameter("name");
        String mobile = req.getParameter("mobile");
        String acctClass = req.getParameter("acctClass");
        String cardAttr = req.getParameter("cardAttr");

        /**
         * 收款方信息
         */
        String name_payeeInfo = req.getParameter("name_payeeInfo");
        String merCatCode = req.getParameter("merCatCode");
        String id = req.getParameter("id");

        /**
         * 组装请求报文
         */
        Map<String, String> contentData = new HashMap<String, String>();

        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0180000903");
        contentData.put("issCode", issCode);
        contentData.put("txnNo", txnNo);
        contentData.put("txnAmt", txnAmt);
        contentData.put("currencyCode", "156");

        Map<String, String> payerInfoMap = new HashMap<String, String>();
        payerInfoMap.put("accNo", accNo);
        payerInfoMap.put("name", name);
        payerInfoMap.put("mobile", mobile);//目前不校验
        payerInfoMap.put("acctClass", acctClass);
        payerInfoMap.put("cardAttr", cardAttr);//01 – 借记卡 02 – 贷记卡（含准贷记卡）

        Map<String, String> payeeInfoMap = new HashMap<String, String>();
        payeeInfoMap.put("name",name_payeeInfo);
        payeeInfoMap.put("id", id);
        payeeInfoMap.put("merCatCode", merCatCode);//目前不校验
        payeeInfoMap.put("termId", "49000002");

        //riskInfo必送，详细参考规范说明
        contentData.put("riskInfo", AcpService.base64Encode("{deviceID=123456999&deviceType=1&mobile=13525677809&accountIdHash=00000002&sourceIP=111.13.100.91}", DemoBase.encoding));

        //敏感信息不加密使用DemoBase.getPayerInfo方法
        //付款方信息
        contentData.put("payerInfo", DemoBase.getPayerInfo(payerInfoMap,"UTF-8"));
        //收款方信息
        contentData.put("payeeInfo", DemoBase.getPayeeInfo(payeeInfoMap,"UTF-8"));

        //如果对机构号issCode 配置了敏感信息加密，必须1.送encryptCertId 2.对payerInfo，payeeInfo的值整体加密
        //目前二维码系统要求所有接入均采用加密方式，使用正式机构号测试的时候参考如下方法上送
        //contentData.put("payerInfo", DemoBase.getPayerInfoWithEncrpyt(payerInfoMap,"UTF-8"));
        //contentData.put("payeeInfo", DemoBase.getPayeeInfoWithEncrpyt(payeeInfoMap,"UTF-8"));
        //contentData.put("encryptCertId", AcpService.getEncryptCertId());

        Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

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
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
        resp.getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
    }

}
