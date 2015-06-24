package com.dianping.swallow.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午3:20:59
 */
@Controller
public class DumpMessageController extends AbstractMenuController {

	public static final String FILEPATH = "/data/appdatas/swalllowweb/";

	private static final String FILE = "file";

	private static final String STATUS = "status";

	@Resource(name = "messageService")
	private MessageService messageService;

	@RequestMapping(value = "/console/download")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		
		return new ModelAndView("message/filedownload", createViewMap());
	}

	@RequestMapping(value = "/console/message/auth/dump", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object dumpMessageByTime(String topic, String startdt,
			String stopdt, HttpServletRequest request,
			HttpServletResponse response) {
		List<String> topicFiles = getListFile(topic);

		if (StringUtils.isBlank(stopdt + startdt)) { // just query filename
			return getResponse(topicFiles, ResponseStatus.SUCCESS.getStatus());
		}
		String post = new SimpleDateFormat("yyyyMMddHHmm'.gz'")
				.format(new Date());
		StringBuffer sb = new StringBuffer();
		String filename = sb.append(topic).append("_").append(post).toString();
		File dir = new File(FILEPATH + filename);
		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (IOException e) {
				logger.error("create file error", e);
				return getResponse(topicFiles, ResponseStatus.IOEXCEPTION.getStatus());
			}
		}
		topicFiles.add(filename);

		int status = messageService.exportMessage(topic,
				startdt, stopdt, filename);
		return getResponse(topicFiles, status);
	}

	private Map<String, Object> getResponse(List<String> topicFiles, int status) {

		Map<String, Object> map = new HashMap<String, Object>();
		Collections.sort(topicFiles);
		Collections.reverse(topicFiles);
		map.put(FILE, topicFiles);
		map.put(STATUS, status);
		return map;
	}
	
	private List<String> getListFile(String topic){
		List<String> topicFiles = new ArrayList<String>();
		File file = new File(FILEPATH);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}

		File[] allFiles = file.listFiles();
		for (File f : allFiles) {
			String fname = f.getName();
			if (fname.startsWith(topic + "_") && !topicFiles.contains(fname)) {
				topicFiles.add(fname);
			}
		}
		return topicFiles;
	}

	@Override
	protected String getMenu() {
		return "download";
	}

}
