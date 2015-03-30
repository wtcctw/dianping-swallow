package com.dianping.swallow.common.internal.processor;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午3:07:10
 */
public class MessageProcessorChain extends AbstractProcessor implements Processor{
	
	private List<Processor> processors = new LinkedList<Processor>();
	
	public MessageProcessorChain(Processor ... processors){
		
		for(Processor processor : processors){
			this.processors.add(processor);
		}
		
	}

	@Override
	public void beforeSend(SwallowMessage message) throws SwallowException {
		for(Processor processor : processors){
			processor.beforeSend(message);
		}
	}

	@Override
	public void beforeOnMessage(SwallowMessage message) throws SwallowException {
		
		for(int i = processors.size() - 1 ; i >=0 ; i--){
			Processor processor = processors.get(i);
			processor.beforeOnMessage(message);
		}
		
	}

	@Override
	public void afterOnMessage(SwallowMessage message) throws SwallowException {
		
		for(int i = processors.size() - 1 ; i >=0 ; i--){
			Processor processor = processors.get(i);
			processor.afterOnMessage(message);
		}
	}
	
	public void addProcessor(Processor processor){
		processors.add(processor);
	}

	public void removeProcessor(Processor processor){
		processors.remove(processor);
	}
}
