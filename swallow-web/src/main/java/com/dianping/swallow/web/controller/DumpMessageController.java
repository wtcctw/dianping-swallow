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

import com.dianping.swallow.web.service.DumpMessageService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午3:20:59
 */
@Controller
public class DumpMessageController extends AbstractMenuController {

	public static final String PATH = "/data/appdatas/swalllowweb/";

	private static final String FILE = "file";

	private static final String STATUS = "status";

	@Resource(name = "fileDownloadService")
	private DumpMessageService fileDownloadService;

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
			return getResponse(topicFiles, ResponseStatus.SUCCESS);
		}
		String post = new SimpleDateFormat("yyyyMMddHHmm'.gz'")
				.format(new Date());
		String filename = topic + "_" + post;
		File dir = new File(PATH + filename);
		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (IOException e) {
				logger.error("create file error", e);
				return getResponse(topicFiles, ResponseStatus.E_IOEXCEPTION);
			}
		}
		topicFiles.add(filename);

		int status = fileDownloadService.exportMessageByTimeSpan(topic,
				startdt, stopdt, filename);
//		try {
//			Thread.sleep(8000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
		File file = new File(PATH);
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
		return "";
	}

}
