package com.dianping.swallow.common.internal.util;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.config.ClientCustomConfig;
import com.dianping.swallow.common.internal.config.impl.ClientCustomConfigImpl;
import com.dianping.swallow.common.internal.pool.DefaultThreadExceptionHandler;
import com.dianping.swallow.common.internal.util.log.LoggerLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mengwenchao
 *
 * 2015年3月10日 下午4:12:49
 */
public class SwallowHelper {

	private static Logger logger = LogManager.getLogger(SwallowHelper.class);
	
	private static String version = null;
	
	public static void initialize(){

		new DefaultThreadExceptionHandler();
		//Cat.initialize(new File(Cat.getCatHome(), "client.xml"));
	}

	public static void clientInitialize(){

		initialize();
		ClientCustomConfig clientCustomConfig = ClientCustomConfigImpl.getInstance();

		if (clientCustomConfig.isLog4j2Enabled()) {
			LoggerLoader.init();
		}
	}
	
	public static String getVersion(){

		if(version != null){
			return version;
		}
		
		version = getCurrentVersion();

		if(logger.isInfoEnabled()){
			logger.info("[getVersion]" + version);
		}

		return version;
		
	}

	private static String getCurrentVersion() {
		
		URL url = SwallowHelper.class.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath();
		String version = null;

		if(logger.isInfoEnabled()){
			logger.info("[getCurrentVersion]" + url);
		}
		
		if(path.endsWith(".jar")){
			 version = extractVersion(path);
		}else{
			version = getVersionFromWorkspace(path);
		}
		
		if(version == null){
			throw new IllegalStateException("can not find version from:" + url);
		}

		return version;
	}

	private static String getVersionFromWorkspace(String path) {
		
		File classes = new File(path);
		File pomFile = new File(classes.getParentFile().getParentFile(), "pom.xml");
		
		if(!pomFile.exists() || !pomFile.isFile()){
			throw new IllegalStateException("pom.xml file can not be found!");
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[getVersionFromWorkspace]" + pomFile);
		}

		BufferedReader br = null;
		try {
			 br = new BufferedReader(new FileReader(pomFile));
			while(true){
				
				String line = br.readLine();
				if(line == null){
					break;
				}
				
				if(line.indexOf("<version>") >= 0){
					if(logger.isInfoEnabled()){
						logger.info("[getVersionFromWorkspace]" + line);
					}
					version =  extractVersion(line);
					break;
				}
			}
			
		} catch (IOException e) {
			throw new IllegalStateException("io exception " + path, e);
		}finally{
			if( br != null ){
				try {
					br.close();
				} catch (IOException e) {
					throw new IllegalStateException("io exception while closing " + path, e);
				}
			}
		}
		
		return version;
	}

	private static String extractVersion(String line) {
		
		Pattern pattern = Pattern.compile(".*?(\\d[\\d.]+\\d(-SNAPSHOT)?).*");
		Matcher matcher = pattern.matcher(line);
		String version = null;
		
		while(matcher.find()){
			version = matcher.group(1);
		}
		
		return version;
	}


	public static void main(String []argc){

		System.out.println(getVersion());
	}
}
