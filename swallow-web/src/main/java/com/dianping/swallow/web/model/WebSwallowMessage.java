package com.dianping.swallow.web.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.bson.types.BSONTimestamp;

import com.dianping.swallow.web.util.MongoUtils;

/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:06:15
 */
public class WebSwallowMessage implements Comparable<WebSwallowMessage> {

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
  
   
	public WebSwallowMessage() {
	}

	public WebSwallowMessage(BSONTimestamp id, BSONTimestamp oid, String c, String v, String s, Date gt, String p , String i_p,
									String t, String si) {
		this._id = id;
		this.o_id = oid;
		this.c = c;
		this.v = v;
		this.s = s;
		this.gt = gt;
		this.p = p;
		this._p = i_p;
		this.t = t;
		this.si = si;
	}

	public BSONTimestamp get_id() {
		return _id;
	}

	public void set_id(BSONTimestamp _id) {
		this._id = _id;
	}

	
	public BSONTimestamp getO_id() {
		return o_id;
	}

	public void setO_id(BSONTimestamp o_id) {
		this.o_id = o_id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(BSONTimestamp ts) {
		long tmp = MongoUtils.BSONTimestampToLong(ts);
		this.mid = Long.toString(tmp);
	}
	
	public String getMo_id() {
		return mo_id;
	}

	public void setMo_id(BSONTimestamp ts) {
		long tmp = MongoUtils.BSONTimestampToLong(ts);
		this.mo_id = Long.toString(tmp);
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}
	
	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}
	
	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}
	
	public Date getGt() {
		return gt;
	}

	public void setGt(Date gt) {
		this.gt = gt;
	}


	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String get_p() {
		return _p;
	}

	public void set_p(String _p) {
		this._p = _p;
	}

	public String  getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}
	
	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}
	
	public String  getStstring() {
		return ststring;
	}

	public void setStstring(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds)*1000;
		this.ststring = new SimpleDateFormat(TIMEFORMAT).format(new Date(millions));
	}
	
	public String  getGtstring() {
		return gtstring;
	}

	public void setGtstring(Date gt) {
		this.gtstring = new SimpleDateFormat(TIMEFORMAT).format(gt);
	}
	
    @Override
    public int compareTo(WebSwallowMessage ts) {
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