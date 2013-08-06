package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.internal.message.SwallowMessage;

public class MessageWrapper {

   private SwallowMessage message;
   private boolean        isBackupMessage;

   public MessageWrapper(SwallowMessage message, boolean isBackupMessage) {
      super();
      this.message = message;
      this.isBackupMessage = isBackupMessage;
   }

   public SwallowMessage getMessage() {
      return message;
   }

   public void setMessage(SwallowMessage message) {
      this.message = message;
   }

   public boolean isBackupMessage() {
      return isBackupMessage;
   }

   public void setBackupMessage(boolean isBackupMessage) {
      this.isBackupMessage = isBackupMessage;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MessageWrapper other = (MessageWrapper) obj;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      return true;
   }

}
