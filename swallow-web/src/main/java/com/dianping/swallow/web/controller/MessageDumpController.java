package com.dianping.swallow.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午3:20:59
 */
@Controller
public class MessageDumpController extends AbstractSidebarBasedController {

	public static final String FILEPATH = "/data/appdatas/swalllowweb/";

	private static final String MESSAGE = "message";

	private static final String STATUS = "status";

	@Resource(name = "topicService")
	private TopicService topicService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "messageDumpService")
	private MessageDumpService messageDumpService;

	@Autowired
	private ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/download")
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("tool/filedownload", createViewMap());
	}

	@RequestMapping(value = "/console/message/auth/dump", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object dumpMessageByTime(String topic, String startdt, String stopdt, HttpServletRequest request,
			HttpServletResponse response) {

		String post = new SimpleDateFormat("yyyyMMddHHmmss'.gz'").format(new Date());
		StringBuffer sb = new StringBuffer();
		String filename = sb.append(topic).append("_").append(post).toString();
		File dir = new File(FILEPATH + filename);
		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (IOException e) {
				logger.error("create file error", e);
				return getResponse(ResponseStatus.IOEXCEPTION.getMessage(), ResponseStatus.IOEXCEPTION.getStatus());
			}
		}

		String username = extractUsernameUtils.getUsername(request);

		int status = messageDumpService.execDumpMessageTask(topic, startdt, stopdt, filename, username);
		return getResponse(ResponseStatus.findByStatus(status), status);
	}

	private Map<String, Object> getResponse(String message, int status) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(MESSAGE, message);
		map.put(STATUS, status);
		return map;
	}

	@RequestMapping(value = "/console/download/filename", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object loadFilename(int offset, int limit, String topic, HttpServletRequest request,
			HttpServletResponse response) {

		String username = extractUsernameUtils.getUsername(request);
		if (StringUtils.isNotEmpty(topic)) {
			;
		} else if (userService.loadCachedAdministratorSet().contains(username)) {
			return messageDumpService.loadAllDumpMessage();
		} else {
			List<String> t = topicService.loadTopicNames(username);
			if (t == null || t.size() == 0) {
				topic = "";
			} else {
				topic = StringUtils.join(t, ",");
			}
		}
		return messageDumpService.loadSpecificDumpMessage(offset, limit, topic);

	}

	@RequestMapping(value = "/console/download/auth/removefile", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object removeFilename(String topic, String filename, HttpServletRequest request, HttpServletResponse response) {

		try {
			int status = messageDumpService.removeDumpMessage(filename);
			if (status == 1) {
				deleteFile(FILEPATH + filename);
				logger.info(String.format("Delete file %s and remove record in database successfully", filename));
			}
			return status;
		} catch (MongoException e) {
			logger.info("MongoException when remove messagedump", e);
			return ResponseStatus.MONGOWRITE.getStatus();
		}

	}

	private boolean deleteFile(String sPath) {
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	@Override
	protected String getMenu() {
		return "tool";
	}
	
	@Override
	protected String getSide() {
		
		return "tool";
	}
	
	private String subSide = "download";

	@Override
	public String getSubSide() {
		
		return subSide;
	}
}
