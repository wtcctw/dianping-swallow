package com.dianping.swallow.web.task;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.MessageDumpController;
import com.dianping.swallow.web.service.MessageDumpService;

/**
 * @author mingdongli
 *
 *         2015年6月17日下午6:26:07
 */
@Component
public class ClearDownloadFileTask {

	private static final String TIMEFORMATE = "yyyyMMddHHmmss";

	private final Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = "messageDumpService")
	private MessageDumpService messageDumpService;

	@Scheduled(fixedDelay = 86400000)
	public void clearFile() {
		logger.info("Start clear files that exists more than 7 days.");
		File file = new File(MessageDumpController.FILEPATH);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
			return;
		}
		File[] allFiles = file.listFiles();
		for (File f : allFiles) {
			String fname = f.getName();
			if (checkIfOutOfTime(fname)) {
				boolean deleted = f.delete();
				if (deleted) {
					logger.info(String.format("Delete file %s", fname));
					messageDumpService.removeDumpMessage(fname);
					logger.info(String.format("Remove messagedump %s", fname));
				} else {
					logger.info(String.format("Error when delete file %s", fname));
				}
			}
		}
	}

	private boolean checkIfOutOfTime(String filename) {
		String[] pieces = filename.split("_|\\.");
		if (pieces.length < 3) {
			logger.info("File name format is wrong");
			return true;
		} else {
			String datestring = pieces[pieces.length - 2];
			SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMATE);
			try {
				Date date = sdf.parse(datestring);
				long diff = new Date().getTime() - date.getTime();
				long days = diff / 86400000;
				if (days > 6 || days < 0) {
					return true;
				} else {
					return false;
				}
			} catch (ParseException e) {
				logger.info("Error when parse date in filename");
				return true;
			}
		}
	}
}
