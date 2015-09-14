package com.dianping.swallow.web.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author mingdongli
 *
 *         2015年6月24日下午1:43:19
 */
public class MessageDump {

	@Id
	private String _id;

	@Indexed(name = "IX_TOPIC", direction = IndexDirection.DESCENDING)
	private String topic;

	private String name;

	private Date time;

	private Date startdt;

	private Date stopdt;

	private String filename;

	private boolean finished;

	private String desc;

	public MessageDump() {

	}

	public String get_id() {
		return _id;
	}

	public MessageDump set_id(String _id) {
		this._id = _id;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public MessageDump setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getName() {
		return name;
	}

	public MessageDump setName(String name) {
		this.name = name;
		return this;
	}

	public Date getTime() {
		return time;
	}

	public MessageDump setTime(Date time) {
		this.time = time;
		return this;
	}

	public Date getStartdt() {
		return startdt;
	}

	public MessageDump setStartdt(Date startdt) {
		this.startdt = startdt;
		return this;
	}

	public Date getStopdt() {
		return stopdt;
	}

	public MessageDump setStopdt(Date stopdt) {
		this.stopdt = stopdt;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public MessageDump setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public boolean isFinished() {
		return finished;
	}

	public MessageDump setFinished(boolean finished) {
		this.finished = finished;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public MessageDump setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	@Override
	public String toString() {
		return "MessageDump [_id=" + _id + ", topic=" + topic + ", name=" + name + ", time=" + time + ", startdt="
				+ startdt + ", stopdt=" + stopdt + ", filename=" + filename + ", finished=" + finished + ", desc="
				+ desc + "]";
	}

}
