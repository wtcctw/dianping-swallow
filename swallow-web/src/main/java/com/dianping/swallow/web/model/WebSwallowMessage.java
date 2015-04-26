package com.dianping.swallow.web.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
   private  String        					o_id;
   private  String      				  	c;
   private  String        					v;
   private  String      			  		s;
   private  Date        					gt;
   private  Map<String, String>        		ip;
   private  Map<String, String>				p;
   private  String        					t;
   public   String        					si;  //SOURCE_IP
   public   String          				mid; //message ID
   public   String     						gtstring;
   public   String     						ststring;

   public static final String  				TIMEFORMAT      = "yyyy-MM-dd HH:mm:ss";  //H means 24 hours
  
   
	public WebSwallowMessage() {
	}

	public WebSwallowMessage(BSONTimestamp id, String oid, String c, String v, String s, Date gt, Map<String,String> p , Map<String, String> _p,
									String t, String si) {
		this._id = id;
		this.o_id = oid;
		this.c = c;
		this.v = v;
		this.s = s;
		this.gt = gt;
		this.p = p;
		this.ip = _p;
		this.t = t;
		this.si = si;
	}

	public BSONTimestamp getId() {
		return _id;
	}

	public void setId(BSONTimestamp id) {
		this._id = id;
	}

	public String getOid() {
		return o_id;
	}

	public void setOid(String oid) {
		this.o_id = oid;
	}
	
	public String getMid() {
		return mid;
	}

	public void setMid(BSONTimestamp ts) {
		long tmp = MongoUtils.BSONTimestampToLong(ts);
		this.mid = Long.toString(tmp);
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

	public Map<String, String>  getP() {
		return p;
	}

	public void setPin(Map<String, String> p) {
		this.ip = p;
	}
	
	public Map<String, String>  getPin() {
		return ip;
	}

	public void setP(Map<String, String> _p) {
		this.p = _p;
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

	//数据库里面用的是秒为单位，而JAVA里面则是毫秒为单位，差了1000倍
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
    	int bs= ts.getId().getTime();
    	int thisbs = this.getId().getTime();
        if(thisbs != bs) {
            return thisbs - bs;
        }
        else{
            return thisbs - bs;
        }
    }
	
	@Override
	public String toString() {
		return _id + "::" + o_id + "::" + c + "::" + v + "::" + s + "::" + gt + "::" + p + "::" + ip + "::" + t + "::" + si + "::" +  mid + "::" + gtstring + "::" +  ststring;
	}

}