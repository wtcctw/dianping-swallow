package com.dianping.swallow.web.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserProfileDto;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.IPDescService;

/**
 * @author qiyin
 */
@Service("ipDescService")
public class IPDescServiceImpl implements IPDescService {

    private static final Logger logger = LoggerFactory.getLogger(IPDescServiceImpl.class);

    private static final String COMMA_SPLIT = ",";

    @Autowired
    private UserService baUserService;

    @Override
    public void addEmail(IPDesc ipDesc) {
        try {
            if (ipDesc == null) {
                return;
            }
            String strDpMobile = ipDesc.getDpMobile();
            Set<String> dpEmails = getEmailsByStrMobile(strDpMobile);
            ipDesc.setEmail(getStrEmails(ipDesc.getEmail(), dpEmails));

            String strOpMobile = ipDesc.getOpMobile();
            Set<String> opEmails = getEmailsByStrMobile(strOpMobile);
            ipDesc.setOpEmail(getStrEmails(ipDesc.getOpEmail(), opEmails));
        } catch (Exception e) {
            logger.error("[addEmail]", e);
        }
    }

    private Set<String> getEmailsByStrMobile(String strMobile) {
        String mobiles[] = null;
        if (StringUtils.isNotBlank(strMobile)) {
            mobiles = strMobile.split(COMMA_SPLIT);
        }
        Set<String> emails = null;
        if (mobiles != null && mobiles.length > 0) {
            emails = new HashSet<String>();
            for (String mobile : mobiles) {
                if (StringUtils.isNotBlank(mobile)) {
                    emails.addAll(getEmailsByMobile(mobile.trim()));
                }
            }
        }
        return emails;
    }

    private Set<String> getEmailsByMobile(String mobile) {
        List<UserProfileDto> userInfos = baUserService.getEmployeeInfoByKeyword(mobile);
        Set<String> emails = new HashSet<String>();
        if (userInfos != null) {
            for (UserProfileDto userInfo : userInfos) {
                if (StringUtils.isNotBlank(userInfo.getEmail())) {
                    emails.add(userInfo.getEmail().trim());
                }
            }
        }
        return emails;
    }

    private String getStrEmails(String strEmail, Set<String> mobileEmails) {
        Set<String> emails = null;
        if (mobileEmails == null) {
            emails = new HashSet<String>();
        } else {
            emails = mobileEmails;
        }
        if (StringUtils.isNotBlank(strEmail)) {
            String[] emailArr = StringUtils.split(strEmail, COMMA_SPLIT);
            if (emailArr != null) {
                for (String email : emailArr) {
                    if (StringUtils.isNotBlank(email)) {
                        emails.add(email.trim());
                    }
                }
            }
        }
        return StringUtils.join(emails, COMMA_SPLIT);
    }

}
