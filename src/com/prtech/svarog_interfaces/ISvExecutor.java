/*******************************************************************************
 *   Copyright (c) 2013, 2019 Perun Technologii DOOEL Skopje.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Apache License
 *   Version 2.0 or the Svarog License Agreement (the "License");
 *   You may not use this file except in compliance with the License. 
 *  
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See LICENSE file in the project root for the specific language governing 
 *   permissions and limitations under the License.
 *******************************************************************************/
package com.prtech.svarog_interfaces;

import java.util.Map;

import org.joda.time.DateTime;

import com.prtech.svarog.SvException;

/**
 * Interface to describe the standard Svarog Executors in the OSGI world. Each
 * service which implements this interface, will be reachable through the Svarog
 * Executor sub-system. The Svarog Executor, shall allow different OSGI bundles
 * to communicate between each other through Svarog.
 * 
 * @author ristepejov
 *
 */
public interface ISvExecutor {
	/**
	 * Method to get the version UID of the executor.
	 * 
	 * @return Returns the version of the executor. It is used to differentiate
	 *         between different versions, with different start/end dates.
	 */
	public long versionUID();

	/**
	 * Method to describe the resulting class
	 * 
	 * @return Returns the Class<?> type of the object returned by the execute
	 *         method
	 */
	public Class<?> getReturningType();

	/**
	 * Method to provide the category of the executor. The combination of
	 * Category and Name uniquely identifies an executor
	 * 
	 * @return String containing the category name.
	 */
	public String getCategory();

	/**
	 * Method to provide the name of the executor. The combination of Category
	 * and Name uniquely identifies an executor
	 * 
	 * @return String containing the executor name.
	 */
	public String getName();

	/**
	 * Method to provide a textual description of the executor
	 * 
	 * @return String containing the description of the executor.
	 */
	public String getDescription();

	/**
	 * Start date from which the executor is considered valid. The Svarog
	 * Executor service will ignore the start date from the bundle itself if
	 * there is different value configured in the svarog database. The database
	 * start date overrides the value provided by this method.
	 * 
	 * @return Date from which the executor can be called
	 */
	public DateTime getStartDate();

	/**
	 * End date until which the executor is considered valid. The Svarog
	 * Executor service will ignore the end date from the bundle itself if there
	 * is different value configured in the svarog database. The database end
	 * date overrides the value provided by this method.
	 * 
	 * @return Date until which the executor can be called
	 */
	public DateTime getEndDate();

	/**
	 * Main method to be called from the service bundle. The actual execution of
	 * the service is done by this method.
	 * 
	 * @param params
	 *            Map holding the params to be passed
	 * @param svCore
	 *            Instance of ISvCore which is executing the request
	 * @return Object of type described by {@link #getReturningType()}
	 * @throws SvException
	 *             Throws any underlying exception
	 */
	public Object execute(Map<String, Object> params, ISvCore svCore) throws SvException;
}
