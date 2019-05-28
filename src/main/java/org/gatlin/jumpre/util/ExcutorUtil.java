package org.gatlin.jumpre.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExcutorUtil {

	public static final ScheduledExecutorService excuter = Executors.newScheduledThreadPool(10);
}
