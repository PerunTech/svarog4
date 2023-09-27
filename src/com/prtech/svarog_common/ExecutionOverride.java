package com.prtech.svarog_common;

import com.prtech.svarog_interfaces.ISvCore;

/**
 * This class is supposed to be used for overriding the default code execution
 * of a method or a service. The goal is to provide option for external code to
 * replace a working nominal use case of a project.
 * 
 * @author ristepejov
 *
 */
public abstract class ExecutionOverride {
	final String executorName;

	public ExecutionOverride(String executorName, ISvCore svc) {
		this.executorName = executorName;
	}

	Boolean shouldOverride() {
		return null;
	}

}
