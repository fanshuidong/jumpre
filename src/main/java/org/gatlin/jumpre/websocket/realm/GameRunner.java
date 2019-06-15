package org.gatlin.jumpre.websocket.realm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gatlin.jumpre.websocket.menu.QueueType;

public enum GameRunner {
	
	INSTANCE;
	
//	private LinkedList<String> queue = new LinkedList<String>();
	private Map<QueueType,LinkedList<String>> queues = new HashMap<QueueType,LinkedList<String>>();
	private ExecutorService runner = Executors.newCachedThreadPool();
	
	public synchronized void remove(String uid) {
		for(LinkedList<String> queue : queues.values())
			queue.remove(uid);
	}
	
	public synchronized void push(String uid,QueueType queueType) {
		LinkedList<String> queue;
		if(queues.containsKey(queueType)) {
			 queue = queues.get(queueType);
			 queue.push(uid);
		}else {
			queue = new LinkedList<String>();
			queue.push(uid);
			queues.put(queueType, queue);
		}
	}
	
	public synchronized void match() {
		for(LinkedList<String> queue : queues.values()) {
			while (queue.size() >= 2) {
				Room room = new Room(queue.poll(), queue.poll());
				runner.submit(() -> room.start());
			}
		}
	}
}
