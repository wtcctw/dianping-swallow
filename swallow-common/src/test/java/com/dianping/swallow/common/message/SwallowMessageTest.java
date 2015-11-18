package com.dianping.swallow.common.message;


import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;

public class SwallowMessageTest extends AbstractTest{

	
	
	@Test
	public void testEfficiency(){
		
		int count = 1;
		
		JsonBinder binder = JsonBinder.getNonEmptyBinder();
		
		Long begin =   System.currentTimeMillis();
		
		for(int i=0;i < count; i++){
			
			SwallowMessage message = createMessage();
			String json = binder.toJson(message);
			@SuppressWarnings("unused")
			SwallowMessage newMessage = binder.fromJson(json, SwallowMessage.class);
		}
		
		Long end = System.currentTimeMillis();
		
		System.out.println("Total:" + (end - begin));
	}

	
	@Test
	public void testJson(){
		
		JsonBinder binder = JsonBinder.getNonEmptyBinder();
		
		
		SwallowMessage message = createMessage();
		
		String json = binder.toJson(message);
		
		SwallowMessage newMessage = binder.fromJson(json, SwallowMessage.class);

		
		Assert.assertEquals(message, newMessage);
	
		Assert.assertTrue(equalsWithoutMessageId(message, newMessage));
		Assert.assertEquals(message.getInternalProperties(), newMessage.getInternalProperties());
	}
	
    @Test
    public void testTransferContentToBean() throws Exception {
        //自定义bean
        SwallowMessage msg = createMessage();
        DemoBean demoBean = new DemoBean();
        demoBean.a = 1;
        demoBean.b = "b";
        msg.setContent(demoBean);
        Assert.assertEquals("{\"a\":1,\"b\":\"b\"}", msg.getContent());
        Assert.assertEquals(demoBean, msg.transferContentToBean(DemoBean.class));
        //      System.out.println("<p>输出中文</p>");
    }

    @Test
    public void testHashcode() throws Exception {
        //自定义bean
        SwallowMessage msg = createMessage();
        DemoBean demoBean = new DemoBean();
        demoBean.a = 1;
        demoBean.b = "b";
        msg.setGeneratedTime(null);
        msg.setContent(demoBean);
    }

    @Test
    public void testToString() throws Exception {
        //自定义bean
        SwallowMessage msg = createMessage();
        DemoBean demoBean = new DemoBean();
        demoBean.a = 1;
        demoBean.b = "b";
        msg.setGeneratedTime(null);
        msg.setContent(demoBean);
        Assert.assertNotNull(msg.toString());
    }

    @Test
    public void testEquals() throws Exception {
        SwallowMessage msg = createMessage();
        msg.setGeneratedTime(null);
        SwallowMessage msg2 = createMessage();
        msg2.setGeneratedTime(null);
        Assert.assertTrue(msg.equals(msg2));
        msg2.setMessageId(2L);
        Assert.assertFalse(msg.equals(msg2));
    }

    @Test
    public void testEqualsWithoutMessageId() throws Exception {
        SwallowMessage msg = createMessage();
        msg.setGeneratedTime(null);
        SwallowMessage msg2 = createMessage();
        msg2.setGeneratedTime(null);
        Assert.assertTrue(msg.equals(msg2));
        msg2.setMessageId(2L);
        Assert.assertFalse(msg.equals(msg2));
        Assert.assertTrue(equalsWithoutMessageId(msg, msg2));
    }

    @Test
    public void testGetters() throws Exception {
        SwallowMessage msg = createMessage();
        Assert.assertEquals("this is a SwallowMessage", msg.getContent());
        Assert.assertEquals("sha-1 string", msg.getSha1());
        Assert.assertEquals("localhost", msg.getSourceIp());
        Assert.assertEquals("feed", msg.getType());
        Assert.assertEquals("0.6.0", msg.getVersion());
        Assert.assertNotNull(msg.getGeneratedTime());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("property-key", "property-value");
        Assert.assertEquals(map, msg.getProperties());
    }

    static class DemoBean {

        private int    a;
        private String b;

        public DemoBean() {
            super();
        }

        @Override
        public String toString() {
            return String.format("DemoBean [a=%s, b=%s]", a, b);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + a;
            result = prime * result + ((b == null) ? 0 : b.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DemoBean)) {
                return false;
            }
            DemoBean other = (DemoBean) obj;
            if (a != other.a) {
                return false;
            }
            if (b == null) {
                if (other.b != null) {
                    return false;
                }
            } else if (!b.equals(other.b)) {
                return false;
            }
            return true;
        }

    }

}
