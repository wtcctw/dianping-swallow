package com.dianping.swallow.web.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.dianping.swallow.web.service.ServiceLifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.UserType;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author mingdongli
 *         <p/>
 *         2015年5月14日下午8:04:43
 */
@Service("userService")
public class UserServiceImpl extends AbstractSwallowService implements UserService, Runnable, ServiceLifecycle {

    private static final String DELIMITOR = ",";

    private static final String FACTORY_NAME = "UserServiceImpl";

    @Value("${swallow.web.admin.defaultadmin}")
    private String defaultAdmin;

    @Autowired
    private AdministratorDao administratorDao;

    @Resource(name = "topicResourceService")
    private TopicResourceService topicResourceService;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

    private Set<String> adminSet = new HashSet<String>();

    @PostConstruct
    public void cacheAdminSet() {

        scheduledExecutorService.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
        logger.info("Init adminSet successfully.");
    }

    @Override
    protected void doInitialize() throws Exception {

        String[] admins = defaultAdmin.split(DELIMITOR);
        for (String admin : admins) {
            loadCachedAdministratorSet().add(admin);
            createUser(admin, UserType.ADMINISTRATOR);
            logger.info("admiSet add admin " + admin);
        }
    }

    @Override
    public Pair<Long, List<Administrator>> loadUserPage(BaseDto baseDto) {

        Long totalNumOfTopic = administratorDao.countAdministrator();
        List<Administrator> administratorList = administratorDao.findFixedAdministrator(baseDto);

        return new Pair<Long, List<Administrator>>(totalNumOfTopic, administratorList);
    }

    @Override
    public boolean createUser(String name, UserType auth) {

        if (auth.equals(UserType.ADMINISTRATOR)) {
            this.loadCachedAdministratorSet().add(name);
        } else {
            this.loadCachedAdministratorSet().remove(name);
        }
        return this.updateUser(name, auth);
    }

    @Override
    public boolean removeUser(String name) {
        this.loadCachedAdministratorSet().remove(name);
        int n = administratorDao.deleteByName(name);
        if (n != 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Administrator> findAll() {
        return administratorDao.findAll();
    }

    @Override
    public boolean updateUser(String name, UserType auth) {
        Administrator admin = administratorDao.readByName(name);
        if (admin == null) {
            return doneCreateAdmin(name, auth);
        } else {
            admin.setName(name).setRole(auth).setDate(new Date());
            return administratorDao.saveAdministrator(admin);
        }
    }

    private boolean doneCreateAdmin(String name, UserType auth) {

        Administrator admin = buildAdministrator(name, auth);
        return administratorDao.createAdministrator(admin);
    }

    private Administrator buildAdministrator(String name, UserType role) {
        Administrator admin = new Administrator();
        admin.setName(name).setRole(role).setDate(new Date());
        return admin;
    }

    @Override
    public Set<String> loadCachedAdministratorSet() {

        return adminSet;
    }

    @Override
    public boolean createOrUpdateUser(String username) {
        Administrator admin = administratorDao.readByName(username);
        if (admin != null) {
            UserType role = admin.getRole();
            if (role.equals(UserType.ADMINISTRATOR)) {
                return updateUser(username, role);
            }
        }

        return switchTopicOwnerAndVisitor(username);
    }

    private boolean switchTopicOwnerAndVisitor(String username) {
        if (isTopicOwner(username)) {
            return this.updateUser(username, UserType.USER);
        } else {
            return this.updateUser(username, UserType.VISITOR);
        }
    }

    private boolean isTopicOwner(String username) {

        Collection<Set<String>> topicUsers = topicResourceService.loadCachedTopicToAdministrator().values();
        for (Set<String> set : topicUsers) {
            if (set.contains(username)) {
                return true;
            }
        }
        return false;
    }

    private void loadAmdin() {
        List<Administrator> aList = findAll();
        if (!aList.isEmpty()) {
            for (Administrator list : aList) {
                UserType role = list.getRole();
                String name = list.getName();
                if (role.equals(UserType.ADMINISTRATOR)) {
                    if (loadCachedAdministratorSet().add(name)) {
                    }
                }
            }
        }
    }

    @Override
    public void run() {

        SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "updateAdminSet");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {

                loadAmdin();
            }
        });

    }

}
