package com.dianping.swallow.common.internal.message;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.message.Message;

public class SwallowMessage implements Serializable, Message {

   private static final long   serialVersionUID = -7019466307875540596L;

   private Date                generatedTime;

   private Long                messageId;

   private Map<String, String> properties;

   private Map<String, String> internalProperties = new HashMap<String, String>();

   private String              version;

   private String              content;

   private String              sha1;

   private String              type;

   private String              sourceIp;

   private Long                backupMessageId;

   @Override
   public Date getGeneratedTime() {
      return generatedTime;
   }

   public void setGeneratedTime(Date generatedTime) {
      this.generatedTime = generatedTime;
   }

   @Override
   public Long getMessageId() {
      return messageId;
   }

   public void setMessageId(Long messageId) {
      this.messageId = messageId;
   }

   /**
    * 获取消息<em>发送方</em>使用的Swallow的版本号
    * 
    * @return
    */
   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   @Override
   public Map<String, String> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, String> properties) {
      this.properties = properties;
   }

   public Map<String, String> getInternalProperties() {
      return new HashMap<String, String>(internalProperties);
   }
   
   public void putInternalProperty(String key, String value){
	   this.internalProperties.put(key, value);
   }
   
   public String getInternalProperty(String key){
	   return internalProperties.get(key);
   }
   
   public void putInternalProperties(Map<String, String> properties){
	   
	   if(properties != null){
		   this.internalProperties.putAll(properties);
	   }
   }

   @Override
   public String getSha1() {
      return sha1;
   }

   public void setSha1(String sha1) {
      this.sha1 = sha1;
   }

   @Override
   public <T> T transferContentToBean(Class<T> clazz) {
      JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
      return jsonBinder.fromJson(content, clazz);
   }

   public long size(){
	   //估算，非严格
	   return (long) (content.length()*2*1.2);
   }
   
   @Override
   public String getContent() {
      return content;
   }

   public void setContent(Object content) {
      if (content instanceof String) {
         this.content = (String) content;
      } else {
         JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
         this.content = jsonBinder.toJson(content);
      }
   }

   @Override
   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @Override
   public String getSourceIp() {
      return sourceIp;
   }

   public void setSourceIp(String sourceIp) {
      this.sourceIp = sourceIp;
   }

   public Long getBackupMessageId() {
      return backupMessageId;
   }

   public void setBackupMessageId(Long backupMessageId) {
      this.backupMessageId = backupMessageId;
   }

   public boolean isBackup() {
      return backupMessageId != null;
   }

   @Override
   public String toString() {
      return "SwallowMessage [generatedTime=" + generatedTime + ", messageId=" + messageId + ", backupMessageId="
            + backupMessageId + ", properties=" + properties + ", internalPropertiess=" + internalProperties
            + ", version=" + version + ", sha1=" + sha1 + ", type=" + type + ", sourceIp=" + sourceIp + ", content="
            + content + "]";
   }

   public String toKeyValuePairs() {
      return toSuccessKeyValuePairs() + "&content=" + content;
   }

   public String toSuccessKeyValuePairs() {
      return "generatedTime=" + generatedTime + "&messageId=" + messageId + "&backupMessageId=" + backupMessageId
            + "&properties=" + properties + "&internalPropertiess=" + internalProperties + "&version=" + version
            + "&sha1=" + sha1 + "&type=" + type + "&sourceIp=" + sourceIp;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
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
      if (getClass() != obj.getClass()) {
         return false;
      }
      SwallowMessage other = (SwallowMessage) obj;
      if (messageId == null) {
         if (other.messageId != null) {
            return false;
         }
      } else if (!messageId.equals(other.messageId)) {
         return false;
      }
      return true;
   }
}
