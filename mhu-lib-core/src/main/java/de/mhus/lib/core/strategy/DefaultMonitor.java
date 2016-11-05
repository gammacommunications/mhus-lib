package de.mhus.lib.core.strategy;

import de.mhus.lib.core.logging.Log;

public class DefaultMonitor implements Monitor {

	private long steps;
	private long step;
	private StringBuffer lineBuffer = new StringBuffer();
	private Log log = null;

	public DefaultMonitor(Class<?> owner) {
		log = Log.getLog(owner);
	}
	
	public long getSteps() {
		return steps;
	}

	@Override
	public void setSteps(long steps) {
		this.steps = steps;
	}

	public long getStep() {
		return step;
	}

	@Override
	public void setStep(long step) {
		this.step = step;
	}

	@Override
	public Log log() {
		return log;
	}

	@Override
	public void println() {
		synchronized (lineBuffer) {
			log().i("STDOUT", lineBuffer);
			lineBuffer = new StringBuffer();
		}
	}

	@Override
	public void println(Object... out) {
		print(out);
		println();
	}

	@Override
	public void print(Object... out) {
		synchronized (lineBuffer) {
			for (Object o : out)
				lineBuffer.append(o);
		}
	}

	@Override
	public void incrementStep() {
		step++;
	}

}
