/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.fabdouglas.meeseeks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Main REST endpoint resource.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MeeseeksResource {

	private final AtomicLong counter = new AtomicLong();

	private final static Queue<FileInputStream> openedFiles = new LinkedList<>();

	/**
	 * Shared thread safe counter.
	 */
	private static final AtomicLong staticCounter = new AtomicLong();

	@Autowired
	private RestartEndpoint restartEndpoint;

	@GetMapping()
	@ResponseBody
	public String echo(@RequestParam(name = "m", required = false, defaultValue = "hello") String name) {
		log.info("echo message " + name);
		return name;
	}

	@GetMapping("echo")
	@ResponseBody
	public String echo0(@RequestParam(name = "m", required = false, defaultValue = "hello") String name) {
		log.info("echo message " + name);
		return name;
	}

	@GetMapping("counter")
	@ResponseBody
	public long counter() {
		log.info("Increment and get counter");
		return counter.incrementAndGet();
	}

	/**
	 * Open new files and return current opened files count.
	 * 
	 * @return opened files count.
	 */
	@GetMapping("file-open")
	@ResponseBody
	public long fileOpen(@RequestParam(name = "files", required = false, defaultValue = "1") int nb)
			throws FileNotFoundException {
		log.info("Create " + nb + " new file handles");
		for (var files = nb; files-- > 0;) {
			openedFiles.add(new FileInputStream("/usr/local/app/app.jar"));
		}
		return openedFiles.size();
	}

	/**
	 * Close the last opened files and return current opened files count.
	 */
	@GetMapping("file-close")
	@ResponseBody
	public long fileClose(@RequestParam(name = "files", required = false, defaultValue = "1") int nb)
			throws IOException {
		log.info("Create a new file handle");
		for (var files = nb; files-- > 0;) {
			openedFiles.poll().close();
		}
		return openedFiles.size();
	}

	@GetMapping("counter-static")
	@ResponseBody
	public long counterStatic() {
		log.info("Increment and get static counter");
		return staticCounter.incrementAndGet();
	}

	@GetMapping("get")
	@ResponseBody
	public long counterNoIncrement() {
		log.info("Get counter");
		return counter.get();
	}

	@GetMapping("get-static")
	@ResponseBody
	public long counterStaticNoIncrement() {
		log.info("Get static counter");
		return staticCounter.get();
	}

	@GetMapping("exit")
	@ResponseBody
	public void exit(@RequestParam(name = "code", required = false, defaultValue = "1") int code) {
		log.info("Exit request with code " + code);
		System.exit(code);
	}

	@GetMapping("sleep")
	@ResponseBody
	public void sleep(@RequestParam(name = "millis", required = false, defaultValue = "1000") long millis)
			throws InterruptedException {
		log.info("Sleep for " + millis + "ms");
		Thread.sleep(millis);
	}

	@GetMapping("cpu")
	@ResponseBody
	public void cpu(@RequestParam(name = "millis", required = false, defaultValue = "1000") long millis)
			throws InterruptedException {
		final var start = System.currentTimeMillis();
		log.info("CPU for " + millis + "ms");
		while (System.currentTimeMillis() - start < millis) {
			// Nothing to do
		}
	}

	/**
	 * Request a restart of the current application context in a separated thread.
	 */
	@GetMapping("restart")
	public void restart() {
		log.info("Restart context");
		final Thread restartThread = new Thread(() -> restartEndpoint.restart(), "Restart");
		restartThread.setDaemon(false);
		restartThread.start();
	}

	@GetMapping("mem")
	@ResponseBody
	public void mem(@RequestParam(name = "millis", required = false, defaultValue = "1000") long millis,
			@RequestParam(name = "nb_kilobytes", required = false, defaultValue = "1000") long nb_kilobytes)
			throws InterruptedException {
		// allocate memory
		log.info("Allocate about " + nb_kilobytes + " Kilobytes of RAM");
		final var mem = new ArrayList<String>();
		for (var i = 0L; i < nb_kilobytes; i++) {
			// allocate about 1K of memory
			for (int j = 0; j < 20; j++) {
				// 43 bytes + 8 bytes for reference
				mem.add(new String(
						"123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123"));
			}
		}
		// keep the memory for some time
		log.info("Sleeping for " + millis + " milliseconds");
		TimeUnit.MILLISECONDS.sleep(millis);
	}

}
