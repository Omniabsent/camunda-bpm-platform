package org.camunda.bpm.engine.eventbuffer;

import java.util.Date;
import java.util.LinkedList;

import org.camunda.bpm.engine.impl.bpmn.parser.EventSubscriptionDeclaration;

public class StaticBuffer {
	private static LinkedList<BufferedSignal> signals = new LinkedList<BufferedSignal>();

	public static LinkedList<BufferedSignal> getEventsForDeclaration(EventSubscriptionDeclaration declaration) {
		return signals;
	}
	
	public static void addSignal(String name, Object payload) {
		signals.add(new BufferedSignal(name, payload));
	}
	
	public static class BufferedSignal {
		public String signalName;
		public Object signalPayload;
		private long timestamp;
		
		public BufferedSignal(String name, Object payload) {
			this.signalName = name;
			this.signalPayload = payload;
			this.timestamp = new Date().getTime();
		}
		
		public String toString() {
			return "BufferedSignal[" + signalName + ", " + signalPayload + "]";
		}
	}
	
}
