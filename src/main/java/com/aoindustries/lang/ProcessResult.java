/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.lang;

import com.aoindustries.util.BufferManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Contains the result of executing a process, including return code, standard output, and standard error.
 *
 * @author  AO Industries, Inc.
 */
public class ProcessResult {

	/**
	 * Executes the provided command and gets the result in the system default character set.
	 */
	public static ProcessResult exec(String[] command) throws IOException {
		return getProcessResult(Runtime.getRuntime().exec(command), Charset.defaultCharset());
	}

	/**
	 * Executes the provided command and gets the result in the provided character set.
	 */
	public static ProcessResult exec(String[] command, Charset charset) throws IOException {
		return getProcessResult(Runtime.getRuntime().exec(command), charset);
	}

	/**
	 * Gets the result of the provided process in the system default character set.
	 */
	public static ProcessResult getProcessResult(Process process) throws IOException {
		return getProcessResult(process, Charset.defaultCharset());
	}

	/**
	 * Gets the result of the provided process in the provided character set.
	 */
	public static ProcessResult getProcessResult(final Process process, final Charset charset) throws IOException {
		// Close the input immediately
		process.getOutputStream().close();

		// Read stdout in background thread
		final StringBuilder stdout = new StringBuilder();
		final IOException[] stdoutException = new IOException[1];
		Thread stdoutThread = new Thread(
			new Runnable() {
				@Override
				public void run() {
					try {
						Reader stdoutIn = new InputStreamReader(process.getInputStream(), charset);
						try {
							char[] buff = BufferManager.getChars();
							try {
								int count;
								while((count=stdoutIn.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
									synchronized(stdout) {
										stdout.append(buff, 0, count);
									}
								}
							} finally {
								BufferManager.release(buff, false);
							}
						} finally {
							stdoutIn.close();
						}
					} catch(IOException exc) {
						synchronized(stdoutException) {
							stdoutException[0] = exc;
						}
					}
				}
			}
		);
		stdoutThread.start();

		// Read stderr in background thread
		final StringBuilder stderr = new StringBuilder();
		final IOException[] stderrException = new IOException[1];
		Thread stderrThread = new Thread(
			new Runnable() {
				@Override
				public void run() {
					try {
						Reader stderrIn = new InputStreamReader(process.getErrorStream(), charset);
						try {
							char[] buff = BufferManager.getChars();
							try {
								int count;
								while((count=stderrIn.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
									synchronized(stderr) {
										stderr.append(buff, 0, count);
									}
								}
							} finally {
								BufferManager.release(buff, false);
							}
						} finally {
							stderrIn.close();
						}
					} catch(IOException exc) {
						synchronized(stderrException) {
							stderrException[0] = exc;
						}
					}
				}
			}
		);
		stderrThread.start();

		try {
			// Wait for full read of stdout
			stdoutThread.join();

			// Wait for full read of stderr
			stderrThread.join();

			// Wait for process to exit
			int exitVal = process.waitFor();

			// Check for exceptions in threads
			synchronized(stdoutException) {
				if(stdoutException[0]!=null) throw stdoutException[0];
			}
			synchronized(stderrException) {
				if(stderrException[0]!=null) throw stderrException[0];
			}

			// Get output
			String stdoutStr;
			synchronized(stdout) {
				stdoutStr = stdout.toString();
			}
			String stderrStr;
			synchronized(stderr) {
				stderrStr = stderr.toString();
			}

			// Return results
			return new ProcessResult(
				exitVal,
				stdoutStr,
				stderrStr
			);
		} catch(InterruptedException err) {
			IOException ioErr = new InterruptedIOException();
			ioErr.initCause(err);
			throw ioErr;
		}
	}

	private final int exitVal;
	private final String stdout;
	private final String stderr;

	private ProcessResult(int exitVal, String stdout, String stderr) {
		this.exitVal = exitVal;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	public int getExitVal() {
		return exitVal;
	}

	public String getStdout() {
		return stdout;
	}

	public String getStderr() {
		return stderr;
	}
}
