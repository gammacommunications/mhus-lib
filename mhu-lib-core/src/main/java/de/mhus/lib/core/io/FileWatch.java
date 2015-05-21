package de.mhus.lib.core.io;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class FileWatch {

	private File file;
	private Timer timer;
	private long period = 30 * 1000;
	private long modified = -2;
	private Listener listener;
	private boolean started = false;
	private TimerTask task;
	private long lastRun;
	private boolean startHook;

	public FileWatch(File fileToWatch, Listener listener) {
		this(fileToWatch, null, 30000, true, listener);
	}
	
	public FileWatch(File fileToWatch, Timer timer, Listener listener) {
		this(fileToWatch, timer, 30000, true, listener);
	}
	/**
	 * Watch a file or directory (one level!) against changes. Check the modify date to recognize
	 * a change. It has two ways to work:
	 * 1. Manual check, every time you use the file, call the checkFile() method.
	 * 2. Use of a timer.
	 * 
	 * @param fileToWatch
	 * @param timer
	 * @param period
	 * @param startHook
	 * @param listener
	 */
	public FileWatch(File fileToWatch, Timer timer, long period, boolean startHook, Listener listener) {
		file = fileToWatch;
		this.timer = timer;
		this.period = period;
		this.startHook = startHook;
		this.listener = listener;
	}
	
	public FileWatch doStart() {
		if (started) return this;
		started = true; // do not need sync...
		if (startHook) checkFile(); // init
		if (timer != null) {
			this.task = new TimerTask() {

				@Override
				public void run() {
					checkFile();
				}
				
			};
			timer.schedule(task, period, period);
		}
		return this;
	}
	
	public FileWatch doStop() {
		if (!started) return this;
		if (task != null)
			task.cancel();
		return this;
	}

	public void checkFile() {
		if (System.currentTimeMillis() - lastRun < period) return;
		lastRun = System.currentTimeMillis();
		try {
			long modSum = 0;
			if (file.exists()) {
				if (file.isFile())
					modSum = file.lastModified();
				else
				if (file.isDirectory()) {
					for (File f : file.listFiles()) {
						if (f.isFile() && !f.isHidden()) {
							modSum+=f.lastModified();
						}
					}
				}
			} else {
				modSum = -1;
			}
			
			if (modified != -2 && listener != null && modified != modSum) {
				listener.onFileChanged(this);
			}
			modified = modSum;
			
		} catch (Throwable t) {
			if (listener != null)
				listener.onFileWatchError(this,t);
		}
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public String toString() {
		return file != null ? file.getAbsolutePath() : "?";
	}
	
	public static interface Listener {

		void onFileChanged(FileWatch fileWatch);

		void onFileWatchError(FileWatch fileWatch, Throwable t);
		
	}
}
