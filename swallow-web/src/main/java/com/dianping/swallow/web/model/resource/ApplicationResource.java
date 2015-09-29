package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:01:37
 */
@Document(collection = "APPLICATION_RESOURCE")
public class ApplicationResource extends BaseResource{
	
	@Indexed(name = "IX_APPLICATION", direction = IndexDirection.ASCENDING)
	private String application;
	
	private IPDesc iPDesc;
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public IPDesc getiPDesc() {
		return iPDesc;
	}

	public void setiPDesc(IPDesc iPDesc) {
		this.iPDesc = iPDesc;
	}
	
	@Override
	public String toString() {
		return "ApplicationResource [application=" + application + ", iPDesc=" + iPDesc + "]";
	}

	@JsonIgnore
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(application)) {
			return true;
		}
		return false;
	}

}
