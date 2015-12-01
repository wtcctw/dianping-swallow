package com.dianping.swallow.web.model;

import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.web.util.BSONTimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.BSONTimestamp;
import org.springframework.data.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:06:15
 */
public class Message implements Comparable<Message> {

	@Id
	private  BSONTimestamp        			_id;
	private  BSONTimestamp        			o_id;
	private  String      				  	c;
	private  String        					v;
	private  String      			  		s;
	private  Date        					gt;
	private  String        					p;
	private  String							_p;
	private  String        					t;
	private  String        					si;  //SOURCE_IP
	private  String          				mo_id; //message ID
	private  String          				mid; //message ID
	private  String     						gtstring;
	private  String     						ststring;
	private  String							retransmit;


	public static final String  				TIMEFORMAT      = "yyyy-MM-dd HH:mm:ss";  //H means 24 hours


	public Message() {
	}

	@JsonSerialize(using=BSONTimestampSerializer.class)
	public BSONTimestamp get_id() {
		return _id;
	}

	public Message set_id(BSONTimestamp _id) {
		this._id = _id;
		return this;
	}


	@JsonSerialize(using=BSONTimestampSerializer.class)
	public BSONTimestamp getO_id() {
		return o_id;
	}

	public Message setO_id(BSONTimestamp o_id) {
		this.o_id = o_id;
		return this;
	}

	public String getMid() {
		return mid;
	}

	public Message setMid(BSONTimestamp ts) {
		long tmp = MongoUtils.BSONTimestampToLong(ts);
		this.mid = Long.toString(tmp);
		return this;
	}

	public Message setMo_id(BSONTimestamp ts) {
		long tmp = MongoUtils.BSONTimestampToLong(ts);
		this.mo_id = Long.toString(tmp);
		return this;
	}

	public String getC() {
		return c;
	}

	public Message setC(String c) {
		this.c = c;
		return this;
	}

	public String getV() {
		return v;
	}

	public Message setV(String v) {
		this.v = v;
		return this;
	}

	public String getS() {
		return s;
	}

	public Message setS(String s) {
		this.s = s;
		return this;
	}

	public Date getGt() {
		return gt;
	}

	public Message setGt(Date gt) {
		this.gt = gt;
		return this;
	}


	public String getP() {
		return p;
	}

	public Message setP(String p) {
		this.p = p;
		return this;
	}

	public String get_p() {
		return _p;
	}

	public Message set_p(String _p) {
		this._p = _p;
		return this;
	}

	public String  getT() {
		return t;
	}

	public Message setT(String t) {
		this.t = t;
		return this;
	}

	public Message setSi(String si) {
		this.si = si;
		return this;
	}

	public Message setStstring(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds)*1000;
		this.ststring = new SimpleDateFormat(TIMEFORMAT).format(new Date(millions));
		return this;
	}

	public Message setGtstring(Date gt) {
		this.gtstring = new SimpleDateFormat(TIMEFORMAT).format(gt);
		return this;
	}

	public Message setRetransmit(String retransmit) {
		this.retransmit = retransmit;
		return this;
	}

	@Override
	public int compareTo(Message ts) {
		int bs= ts.get_id().getTime();
		int thisbs = this.get_id().getTime();
		if(thisbs != bs) {
			return thisbs - bs;
		}
		else{
			return thisbs - bs;
		}
	}

}