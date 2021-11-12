/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.fabdouglas.meeseeks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main REST endpoint resource.
 */
@RestController
@RequestMapping("/api")
public class MeeseeksResource {

	private final AtomicLong counter = new AtomicLong();

	/**
	 * Shared thread safe counter.
	 */
	private static final AtomicLong staticCounter = new AtomicLong();

	@Autowired
	private RestartEndpoint restartEndpoint;

	@GetMapping()
	@ResponseBody
	public String echo(@RequestParam(name = "m", required = false, defaultValue = "hello") String name) {
		System.out.println("echo message " + name);
		return name;
	}

	@GetMapping("echo")
	@ResponseBody
	public String echo0(@RequestParam(name = "m", required = false, defaultValue = "hello") String name) {
		System.out.println("echo message " + name);
		return name;
	}

	@GetMapping("counter")
	@ResponseBody
	public long counter() {
		System.out.println("Increment and get counter");
		return counter.incrementAndGet();
	}

	@GetMapping("counter-static")
	@ResponseBody
	public long counterStatic() {
		System.out.println("Increment and get static counter");
		return staticCounter.incrementAndGet();
	}

	@GetMapping("get")
	@ResponseBody
	public long counterNoIncrement() {
		System.out.println("Get counter");
		return counter.get();
	}

	@GetMapping("get-static")
	@ResponseBody
	public long counterStaticNoIncrement() {
		System.out.println("Get static counter");
		return staticCounter.get();
	}

	@GetMapping("exit")
	@ResponseBody
	public void exit(@RequestParam(name = "code", required = false, defaultValue = "1") int code) {
		System.out.println("Exit request with code " + code);
		System.exit(code);
	}

	@GetMapping("sleep")
	@ResponseBody
	public void sleep(@RequestParam(name = "millis", required = false, defaultValue = "1000") long millis)
			throws InterruptedException {
		System.out.println("Sleep for " + millis + "ms");
		Thread.sleep(millis);
	}

	@GetMapping("cpu")
	@ResponseBody
	public void cpu(@RequestParam(name = "millis", required = false, defaultValue = "1000") long millis)
			throws InterruptedException {
		final var start = System.currentTimeMillis();
		System.out.println("CPU for " + millis + "ms");
		while (System.currentTimeMillis() - start < millis) {
			// Nothing to do
		}
	}

	/**
	 * Request a restart of the current application context in a separated thread.
	 */
	@GetMapping("restart")
	public void restart() {
		System.out.println("Restart context");
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
		System.out.println("Allocate about " + nb_kilobytes + " Kilobytes of RAM");
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
		System.out.println("Sleeping for " + millis + " milliseconds");
		TimeUnit.MILLISECONDS.sleep(millis);
	}

}
