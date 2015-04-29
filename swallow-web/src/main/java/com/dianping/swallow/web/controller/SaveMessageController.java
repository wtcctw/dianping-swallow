package com.dianping.swallow.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.message.SwallowMessage;

@Controller
@Component
public class SaveMessageController {
	
	private static final String                V                  ="0.7.1";
	private static final String                IP                 ="127.0.0.1";
	
	private static MessageDAO mdi;
	static{
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		mdi = (MessageDAOImpl) ctx.getBean("messageDAO");
	}
	
	private static final Logger logger = LoggerFactory
			.getLogger(TopicController.class);
	
	@RequestMapping(value = "/console/message/sendmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void retransmit(@RequestParam(value = "param[]") String[] param,
							@RequestParam("topic") String topic) {
		String topicName = topic.trim();
		int length = param.length;
		
		if(length <= 0)
			return;
		if(length == 1){
			String tmpString = param[0].trim();
			String subString = tmpString.substring(1,tmpString.length()-1);
			long tmplong = Long.parseLong(subString.replace("\"", ""));
			doGetAndSaveMessage(topicName, tmplong);
		}
		else{
			for(int i = 0; i < length; ++i){
				if(i != 0 && i != length - 1){
					System.out.println(param[i]);
					long tmplong = Long.parseLong(param[i].trim().replace("\"", ""));
					doGetAndSaveMessage(topicName, tmplong);
				}
				else if(i == 0){
					System.out.println(param[i].substring(1));
					long tmplong = Long.parseLong(param[i].substring(1).trim().replace("\"", ""));
					doGetAndSaveMessage(topicName, tmplong);
				} 
				else{
					System.out.println(param[i].substring(0, param[i].length()-1));
					long tmplong = Long.parseLong(param[i].substring(0, param[i].trim().length()-1).trim().replace("\"", ""));
					doGetAndSaveMessage(topicName, tmplong);
				}
			}
		}
		
		return;
	}
	
	private boolean doGetAndSaveMessage(String topic, long mid){
		SwallowMessage sm = new SwallowMessage();
		sm = mdi.getMessage(topic, mid);
		if(sm != null){
			mdi.saveMessage(topic, sm);
			return true;
		}
		else{
			if(logger.isInfoEnabled())
				logger.info(topic + " is not in database!");
			return false;
		}
	}
	
	
	@RequestMapping(value = "/console/message/sendgroupmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public RedirectView sendGroupMessages(@RequestParam(value = "name") String topic,
							@RequestParam("textarea") String text,HttpServletRequest request,
							HttpServletResponse response) {
		System.out.println(topic);
		System.out.println(text);
		String topicName = topic.trim();
		String textarea  = text.trim();
		if(textarea.isEmpty()){
			if(logger.isInfoEnabled())
				logger.info(topicName + " is not in whitelist!");
			return new RedirectView(request.getContextPath() + "/console/message");
		}
		String[] contents = textarea.split("\\n");
		int pieces = contents.length;
		if(pieces == 0){
			if(logger.isInfoEnabled())
				logger.info(text + " parses failed because of wrong delimitor, please use '\n' to enter line!");
			return new RedirectView(request.getContextPath() + "/console/message");
		}
		for(int i = 0; i < pieces; ++i){
			saveNewMessage(topicName, contents[i]);
		}
		return new RedirectView(request.getContextPath() + "/console/message");
	}
	
	private void saveNewMessage(String topicName, String content){
		SwallowMessage sm = new SwallowMessage();
		setMessageProperty(sm, content);
		mdi.saveMessage(topicName, sm);
	}
	
	private void setMessageProperty(SwallowMessage sm, String c){
		sm.setContent(c);
		sm.setVersion(V);
		sm.setGeneratedTime(new Date());
		//sm.setInternalProperties();
	}
}
