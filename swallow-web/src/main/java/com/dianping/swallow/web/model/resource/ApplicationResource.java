package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	private String email;

	private String opManager;

	private String opMobile;

	private String opEmail;

	private String dpManager;

	private String dpMobile;
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpManager() {
		return opManager;
	}

	public void setOpManager(String opManager) {
		this.opManager = opManager;
	}

	public String getOpMobile() {
		return opMobile;
	}

	public void setOpMobile(String opMobile) {
		this.opMobile = opMobile;
	}

	public String getOpEmail() {
		return opEmail;
	}

	public void setOpEmail(String opEmail) {
		this.opEmail = opEmail;
	}

	public String getDpManager() {
		return dpManager;
	}

	public void setDpManager(String dpManager) {
		this.dpManager = dpManager;
	}

	public String getDpMobile() {
		return dpMobile;
	}

	public void setDpMobile(String dpMobile) {
		this.dpMobile = dpMobile;
	}

	@JsonIgnore
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(application)) {
			return true;
		}
		return false;
	}

}
