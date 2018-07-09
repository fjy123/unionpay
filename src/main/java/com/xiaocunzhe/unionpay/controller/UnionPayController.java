package com.xiaocunzhe.unionpay.controller;


import com.jpay.ext.kit.DateKit;
import com.jpay.unionpay.AcpService;
import com.jpay.unionpay.LogUtil;
import com.jpay.unionpay.SDKConfig;
import com.jpay.unionpay.SDKConstants;
import com.jpay.unionpay.SDKUtil;
import com.jpay.unionpay.UnionPayApi;
import com.xiaocunzhe.util.AcpServiceCustomer;
import com.xiaocunzhe.util.UnionPayCustomerApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/unionpay")
public class UnionPayController {
    private static final Logger logger = LoggerFactory.getLogger(UnionPayController.class);

    private static final String merId = "777290058110097";

    /**
     * 无跳转支付银联侧开通
     */
    @RequestMapping(value = "/openCardFront", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public void openCardFront(HttpServletResponse response) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            logger.info(orderId);
            String txnTime = DateKit.toStr(new Date(), DateKit.UnionTimeStampPattern);
            logger.info(txnTime);
            Map<String, String> reqData = UnionPayCustomerApiConfig.builder()
                    .setTxnType("79")
                    .setTxnSubType("00")
                    .setBizType("000902")
                    .setChannelType("07") //渠道类型，07-PC，08-手机
                    .setMerId(merId)
                    .setAccessType("0")
                    .setAccType("01")
                    .setOrderId(orderId)//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
                    .setTxnTime(txnTime)//订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
                    .setTokenPayData("{trId=62000000001&tokenType=01}")
                    .setBackUrl(SDKConfig.getConfig().getBackUrl())
                    .setFrontUrl(SDKConfig.getConfig().getFrontUrl())
                    .setEncryptCertId(AcpService.getEncryptCertId())//加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
                    .createMap("D:/certs/acp_test_sign.pfx");
            UnionPayApi.frontRequest(response, reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台回调
     */
    @RequestMapping(value = "/backRcvResponse", method = {RequestMethod.POST, RequestMethod.GET})
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

            String orderId = reqParam.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
            String customerInfo = reqParam.get("customerInfo");
            if (null != customerInfo) {
                Map<String, String> customerInfoMap = AcpService.parseCustomerInfo(customerInfo, "UTF-8");
                LogUtil.writeLog("customerInfoMap明文: " + customerInfoMap);
            }

            String accNo = reqParam.get("accNo");
            //如果配置了敏感信息加密证书，可以用以下方法解密，如果不加密可以不需要解密
            if (null != accNo) {
                accNo = AcpService.decryptData(accNo, "UTF-8");
                LogUtil.writeLog("accNo明文: " + accNo);
            }

            String tokenPayData = reqParam.get("tokenPayData");
            if (null != tokenPayData) {
                Map<String, String> tokenPayDataMap = SDKUtil.parseQString(tokenPayData.substring(1, tokenPayData.length() - 1));
                String token = tokenPayDataMap.get("token");//这样取
                LogUtil.writeLog("tokenPayDataMap明文: " + tokenPayDataMap);
            }

            String respCode = reqParam.get("respCode");
        }
    }

    /**
     * 查询开通状态
     * @param req
     * @param resp
     * @throws IOException
     */
    @RequestMapping("/tokenOpenQuery")
    public void tokenOpenQuery(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String orderId = "1531108596479";
        try {
            Map<String, String> reqData = UnionPayCustomerApiConfig.builder()
                    .setTxnType("78")
                    .setTxnSubType("02")
                    .setBizType("000902")
                    .setChannelType("07") //渠道类型，07-PC，08-手机
                    .setMerId(merId)
                    .setAccessType("0")
                    .setOrderId(orderId)//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
                    .setTxnTime(DateKit.toStr(new Date(), DateKit.UnionTimeStampPattern))//订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
                    .createMap("");
            String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();
            //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
            Map<String, String> rspData = AcpServiceCustomer.post(reqData,requestBackUrl,DemoBase.encoding);
            StringBuffer parseStr = new StringBuffer("");
            if (!rspData.isEmpty()) {
                if (AcpService.validate(rspData, DemoBase.encoding)) {
                    logger.info("验证签名成功");
                    String respCode = rspData.get("respCode");
                    if (("00").equals(respCode)) {
                        //成功
                        parseStr.append("<br>解析敏感信息加密信息如下（如果有）:<br>");
                        String customerInfo = rspData.get("customerInfo");
                        if (null != customerInfo) {
                            Map<String, String> cm = AcpService.parseCustomerInfo(customerInfo, "UTF-8");
                            parseStr.append("customerInfo明文: " + cm + "<br>");
                        }
                        String an = rspData.get("accNo");
                        if (null != an) {
                            an = AcpService.decryptData(an, "UTF-8");
                            parseStr.append("accNo明文: " + an);
                        }
                    } else {
                        //其他应答码为失败请排查原因或做失败处理
                    }
                } else {
                    logger.error("验证签名失败");
                    // 检查验证签名失败的原因
                }
            } else {
                //未返回正确的http状态
                logger.error("未获取到返回报文或返回http状态码非200");
            }
            String reqMessage = getHtmlResult(reqData);
            String rspMessage = getHtmlResult(rspData);
            resp.getWriter().write("</br>请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + parseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/DeleteToken")
    public  void DeleteToken( HttpServletResponse resp) throws IOException {
        String token="6235240000020851024";
        String orderId="1531108596479";
        String txnTime="20180709115636";
        Map<String, String> reqData = UnionPayCustomerApiConfig.builder()
                .setTxnType("74")
                .setTxnSubType("01")
                .setBizType("000902")
                .setChannelType("07") //渠道类型，07-PC，08-手机
                .setMerId(merId)
                .setAccessType("0")
                .setOrderId(orderId)//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
                .setTxnTime(txnTime)//订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
                .setTokenPayData("{token="+token+"&trId=62000000001}")
                .createMap("D:/certs/acp_test_sign.pfx");       				   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        /**对请求参数进行签名并发送http post请求，接收同步应答报文**/
        String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpServiceCustomer.post(reqData,requestBackUrl,DemoBase.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //成功
                    //TODO
                }else{
                    //其他应答码为失败请排查原因或做失败处理
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
        resp.getWriter().write("请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
    }





    /**
     * 获取请求参数中所有的信息
     * 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
     * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    // System.out.println("======为空的字段名===="+en);
                    res.remove(en);
                }
            }
        }
        return res;
    }

    /**
     * 将回调参数转为Map
     *
     * @param notifyStr
     * @return {Map<String, String>}
     */
    public static Map<String, String> getAllRequestParamToMap(final String notifyStr) {
        Map<String, String> res = new HashMap<String, String>();
        try {
            logger.info("收到通知报文：" + notifyStr);
            String[] kvs = notifyStr.split("&");
            for (String kv : kvs) {
                String[] tmp = kv.split("=");
                if (tmp.length >= 2) {
                    String key = tmp[0];
                    String value = URLDecoder.decode(tmp[1], "UTF-8");
                    res.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.info("getAllRequestParamStream.UnsupportedEncodingException error: " + e.getClass() + ":"
                    + e.getMessage());
        }
        return res;
    }

    /**
     * 组装请求，返回报文字符串用于显示
     *
     * @param data
     * @return {String}
     */
    public static String getHtmlResult(Map<String, String> data) {

        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            if ("respCode".equals(key)) {
                sf.append("<b>" + key + "=" + value + "</br></b>");
            } else
                sf.append(key + "=" + value + "</br>");
        }
        return sf.toString();
    }


}
