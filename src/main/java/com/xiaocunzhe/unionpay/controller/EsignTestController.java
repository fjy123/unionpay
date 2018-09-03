package com.xiaocunzhe.unionpay.controller;

import com.timevale.esign.sdk.tech.bean.OrganizeBean;
import com.timevale.esign.sdk.tech.bean.PersonBean;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
import com.timevale.esign.sdk.tech.impl.constants.LegalAreaType;
import com.timevale.esign.sdk.tech.impl.constants.OrganRegType;
import com.timevale.esign.sdk.tech.service.AccountService;
import com.timevale.esign.sdk.tech.v3.client.ServiceClient;
import com.timevale.esign.sdk.tech.v3.client.ServiceClientManager;
import com.xiaocunzhe.esign.constant.DemoConfig;
import com.xiaocunzhe.esign.core.AccountHelper;
import com.xiaocunzhe.esign.core.SealHelper;
import com.xiaocunzhe.esign.exception.DemoException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

/**
 * Created by fangjingyao on 2018/9/2
 */
@RestController
public class EsignTestController {

    /***
     * <ul>
     * <li>方法名称：获取客户端</li>
     * <li>方法用途：获取已初始化客户端，通过客户端可获取SDK提供的各种电子签名服务</li>
     * <li>Demo封装方法：doGetServiceClient</li>
     * <li>SDK接口名称:ServiceClientManager.get(String projectId)</li>
     * </ul>
     *
     * @throws DemoException
     */
    public static ServiceClient doGetServiceClient() throws DemoException {
        ServiceClient serviceClient = ServiceClientManager.get(DemoConfig.PROJECT_ID);
        if (null == serviceClient) {
            String exMsg = MessageFormat.format("ServiceClient为空,获取[{0}]的客户端失败,请重新注册客户端", DemoConfig.PROJECT_ID);
            throw new DemoException(exMsg);
        } else {
            System.out.println(MessageFormat.format("获取[{0}]的客户端成功", DemoConfig.PROJECT_ID));
        }
        return serviceClient;
    }


    /**
     * 创建个人用户
     * @return
     * @throws DemoException
     */
    @PostMapping("/addPersonAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "idNo", dataType = "String", required = true, value = "身份证号码"),
            @ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "用户姓名")
    })
    public  String addPersonAccount(@RequestParam("idNo") String idNo, @RequestParam("name") String name) throws DemoException {
        ServiceClient serviceClient = doGetServiceClient();
        PersonBean personBean = new PersonBean();
        // 姓名
//        personBean.setName("欣哲");
//        // 证件号码
//        personBean.setIdNo("310101199003073412");

        personBean.setName(name);
        // 证件号码
        personBean.setIdNo(idNo);
        // 用于接收签署验证码的手机号码,可空
        // personBean.setMobile("");

        // 个人归属地：
        // MAINLAND-大陆身份证|HONGKONG-香港居民来往内地通行证|MACAO-澳门居民来往内地通行证|TAIWAN-台湾居民来往大陆通行证
        // PASSPORT-中国护照|FOREIGN-外籍证件|OTHER-其他
        personBean.setPersonArea(LegalAreaType.MAINLAND);

        // 所属公司,可空
        // personBean.setOrgan("XX有限公司");
        // 职位,可空
        // personBean.setTitle("部门经理");

        // 个人客户账户AccountId
        AccountService accountService = serviceClient.accountService();
        AddAccountResult addAccountResult = accountService.addAccount(personBean);
        String accountId = null;
        if (0 != addAccountResult.getErrCode()) {
            String exMsg = MessageFormat.format("创建个人客户账户失败：errCode = {0},msg = {1}",
                    String.valueOf(addAccountResult.getErrCode()), addAccountResult.getMsg());
            throw new DemoException(exMsg);
        } else {
            accountId = addAccountResult.getAccountId();
            System.out.println(
                    MessageFormat.format("创建个人客户账户成功：accountId = {0},可将该AccountId保存到贵司数据库以便日后直接使用.", accountId));
        }
        return accountId;
    }




    /**
     * 创建企业用户
     * @return
     * @throws DemoException
     */
    @PostMapping("/addOrganizeBean")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "企业注册名"),
            @ApiImplicitParam(paramType = "query", name = "uscc", dataType = "String", required = true, value = "社会信用代码")
    })
    public  String addOrganizeBean(@RequestParam("uscc") String uscc, @RequestParam("name") String name) throws DemoException {
        ServiceClient serviceClient = doGetServiceClient();
        OrganizeBean organizeBean = new OrganizeBean();
        // 企业名称
        //organizeBean.setName("天之云信息科技有限公司");// 天之云信息科技有限公司为公共使用，请不要随意更改该企业的名称
        // 单位类型，0-普通企业，1-社会团体，2-事业单位，3-民办非企业单位，4-党政及国家机构
        organizeBean.setOrganType(0);
        // 企业注册类型，NORMAL:组织机构代码号，MERGE：多证合一，传递社会信用代码号,REGCODE:企业工商注册码,默认NORMAL
        organizeBean.setRegType(OrganRegType.MERGE);
        // 组织机构代码号、社会信用代码号或工商注册号
        //organizeBean.setOrganCode("91360823092907952B");// 52227058XT51M4AL62为公共使用，请不要随意更改该企业的证件号码
        // 用于接收签署验证码的手机号码,可空
        // organizeBean.setMobile("");
        organizeBean.setName(name);
        organizeBean.setOrganCode(uscc);
        // 公司地址,可空
        // organizeBean.setAddress("杭州城落霞峰7号");
        // 经营范围,可空
        // organizeBean.setScope("");

        // 注册类型,1-代理人注册,2-法人注册,0-缺省注册无需法人或代理人信息
        int userType = 0;
        switch (userType) {
            case 0:
                // 0-缺省注册无需法人或代理人信息
                organizeBean.setUserType(0);
                break;
            case 1:
                // 1-代理人注册
                organizeBean.setUserType(1);
                // 代理人姓名，当注册类型为1时必填
                organizeBean.setAgentName("艾利");
                // 代理人身份证号，当注册类型为1时必填
                organizeBean.setAgentIdNo("220301198705170035");
                break;
            case 2:
                // 2-法人注册
                organizeBean.setUserType(2);
                // 法定代表姓名，当注册类型为2时必填
                organizeBean.setLegalName("天云");
                // 法定代表人归属地,0-大陆，1-香港，2-澳门，3-台湾，4-外籍，默认0
                organizeBean.setLegalArea(0);
                // 法定代表身份证号/护照号，当注册类型为2时必填
                organizeBean.setLegalIdNo("220301198705170019");
                break;
        }

        // // 企业客户账户AccountId
        String organizeAccountId = AccountHelper.doAddAccount(serviceClient, organizeBean);
        return organizeAccountId;
    }


    /**
     * 创建个人模板印章
     * @return
     * @throws DemoException
     */
    @PostMapping("/addTemplateSeal")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "accountId", dataType = "String", required = true, value = "用户标识")
    })
    public  String addTemplateSeal(@RequestParam("accountId") String accountId) throws DemoException {
        ServiceClient serviceClient = doGetServiceClient();
        // 印章模板类型,可选SQUARE-正方形印章 | RECTANGLE-矩形印章 | BORDERLESS-无框矩形印章
        PersonTemplateType personTemplateType = PersonTemplateType.RECTANGLE;

        // 印章颜色：RED-红色 | BLUE-蓝色 | BLACK-黑色
        SealColor sealColor = SealColor.RED;

        // 个人模板印章SealData
        String personSealData = SealHelper.doAddTemplateSeal(serviceClient, accountId, personTemplateType, sealColor);
        return personSealData;
    }


}
