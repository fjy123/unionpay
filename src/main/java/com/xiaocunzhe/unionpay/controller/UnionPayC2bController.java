package com.xiaocunzhe.unionpay.controller;

import com.jpay.unionpay.AcpService;
import com.jpay.unionpay.LogUtil;
import com.jpay.unionpay.SDKConfig;
import com.xiaocunzhe.util.AcpServiceCustomer;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * C2B消费
 */
@Controller
@RequestMapping("/unionpayC2B")
public class UnionPayC2bController {
    private static final Logger logger = LoggerFactory.getLogger(UnionPayC2bController.class);
    private static final String merId = "777290058110097";
    private static final String issCode = "90880020";

    /**
     * 申请C2B码
     *
     * @return
     */
    @PostMapping("/applyQrNo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "accNo", dataType = "String", required = true, value = "用于支付的token"),
            @ApiImplicitParam(paramType = "query", name = "mobile", dataType = "String", required = true, value = "用户的手机号码")
    })
    public String applyQrNo(@RequestParam("accNo") String accNo, @RequestParam("mobile") String mobile) throws IOException {
        //本次被扫交易结果通知地址
        String backUrl = "http://fangjingyao.tunnel.qydev.com/union/bsAsynNotifiy";
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", "1.0.0");
        contentData.put("reqType", "0210000903");
        contentData.put("issCode", issCode);
        contentData.put("qrType", "35"); //35借记卡 51 贷记卡 40 其他

        //付款方申码交易主键
        contentData.put("qrOrderNo", DemoBase.getOrderId());
        contentData.put("qrOrderTime", DemoBase.getCurrentTime());
        //riskInfo必送，详细参考规范说明
        //TODO 风控信息切环境需要修改
        contentData.put("riskInfo", AcpService.base64Encode("{deviceID=123456999&deviceType=1&mobile=13525677809&accountIdHash=00000002&sourceIP=111.13.100.91}", DemoBase.encoding));

        Map<String, String> payerInfoMap = new HashMap<String, String>();
        payerInfoMap.put("accNo", accNo);
        //账户类型1 代表个人类账户
        payerInfoMap.put("acctClass", "1");
        //手机号必送
        payerInfoMap.put("mobile", mobile);
        //01 – 借记卡 02 – 贷记卡（含准贷记卡）
        payerInfoMap.put("cardAttr", "01");
        //敏感信息不加密使用DemoBase.getPayerInfo方法
        contentData.put("payerInfo", DemoBase.getPayerInfo(payerInfoMap, "UTF-8"));
        contentData.put("reqReserved", "reserved" + DemoBase.getOrderId());
        //c2b交易通知发送地址
        contentData.put("backUrl", backUrl);
        Map<String, String> reqData = AcpService.signByCertInfo(contentData, "D:/certs/acp_test_sign_inst.pfx", "000000", DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestUrl = SDKConfig.getConfig().getQrcB2cIssBackTransUrl();
        Map<String, String> rspData = AcpServiceCustomer.post(reqData, requestUrl, DemoBase.encoding);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //成功代表获取到C2B码解析进行生成付款码的操作
                    String qrNo = rspData.get("qrNo");
                    logger.info(qrNo);
                    //TODO 生成二维码以及条形码返回给前端显示
                } else {
                    //其他应答码为失败打印应答码，后续补充处理逻辑
                    logger.error("应答码" + respCode + rspData.get("respMsg"));
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return null;
    }


}
