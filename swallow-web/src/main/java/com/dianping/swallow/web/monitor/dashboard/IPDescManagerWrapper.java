package com.dianping.swallow.web.monitor.dashboard;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;


/**
 * @author mingdongli
 *
 * 2015年7月10日上午9:17:19
 */
@Component
public class IPDescManagerWrapper {
	
	private static final String BLANK = "Blank";
	
	private IPDesc ipdesc;
	
	@Resource(name = "ipDescManager")
	private IPDescManager ipDescManager;
	
	
	public String loadDpMobile(String ip){
		
		if(ipdesc == null){
			ipdesc = ipDescManager.getIPDesc(ip);
		}
		if(ipdesc == null){
			return BLANK;
		}
		return ipdesc.getDpMobile();
	}
	
	public String loadEmail(String ip){
		
		if(ipdesc == null){
			ipdesc = ipDescManager.getIPDesc(ip);
		}
		if(ipdesc == null){
			return BLANK;
		}
		return ipdesc.getEmail();
	}
	
	public String loadName(String ip){
		
		if(ipdesc == null){
			ipdesc = ipDescManager.getIPDesc(ip);
		}
		if(ipdesc == null){
			return BLANK;
		}
		return ipdesc.getName();
	}

	public String loadDpManager(String ip){
		
		if(ipdesc == null){
			ipdesc = ipDescManager.getIPDesc(ip);
		}
		if(ipdesc == null){
			return BLANK;
		}
		return ipdesc.getDpManager();
	}
	
	public void resetIpdesc(){
		
		ipdesc = null;
	}

}
