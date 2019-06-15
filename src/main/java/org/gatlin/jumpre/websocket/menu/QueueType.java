package org.gatlin.jumpre.websocket.menu;
/**
 * 比赛队列
 * @author fansd
 * @date 2019年6月15日 上午8:53:31
 */
public enum QueueType {
	queue_130,
	queue_50_130,
	queue_50;
	
	public static QueueType match(int scope) {
		if(scope < 50) {
			return queue_50;
		}else if(scope >= 50 && scope <= 130) {
			return queue_50_130;
		}else {
			return queue_130;
		}
	}
}
