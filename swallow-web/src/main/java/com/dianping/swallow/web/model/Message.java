package com.dianping.swallow.web.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.bson.types.BSONTimestamp;

import com.dianping.swallow.common.internal.util.MongoUtils;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:06:15
 */
public class Message implements Comparable<Message> {

	// id will be used for storing MongoDB _id
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
   public   String        					si;  //SOURCE_IP
   public   String          				mo_id; //message ID
   public   String          				mid; //message ID
   public   String     						gtstring;
   public   String     						ststring;

   public static final String  				TIMEFORMAT      = "yyyy-MM-dd HH:mm:ss";  //H means 24 hours
  
   
	public Message() {
	}

	public BSONTimestamp get_id() {
		return _id;
	}

	public Message set_id(BSONTimestamp _id) {
		this._id = _id;
		return this;
	}

	
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
	
	public String getMo_id() {
		return mo_id;
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
	
	public String getSi() {
		return si;
	}

	public Message setSi(String si) {
		this.si = si;
		return this;
	}
	
	public String  getStstring() {
		return ststring;
	}

	public Message setStstring(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds)*1000;
		this.ststring = new SimpleDateFormat(TIMEFORMAT).format(new Date(millions));
		return this;
	}
	
	public String  getGtstring() {
		return gtstring;
	}

	public Message setGtstring(Date gt) {
		this.gtstring = new SimpleDateFormat(TIMEFORMAT).format(gt);
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

	@Override
	public String toString() {
		return "WebSwallowMessage [_id=" + _id + ", o_id=" + o_id + ", c=" + c
				+ ", v=" + v + ", s=" + s + ", gt=" + gt + ", p=" + p + ", _p="
				+ _p + ", t=" + t + ", si=" + si + ", mo_id=" + mo_id
				+ ", mid=" + mid + ", gtstring=" + gtstring + ", ststring="
				+ ststring + "]";
	}


}