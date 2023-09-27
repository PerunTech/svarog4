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
package com.prtech.svarog_common;

import org.joda.time.DateTime;

/**
 * Simple factory class, to make a DbDataObject read-only. Its one way
 * operation.
 * 
 * @author ristepejov
 *
 */
public class DboFactory {

	/**
	 * Set the protected member DbDataObject.isReadOnly to true
	 * 
	 * @param dbo
	 *            The DbDataObject instance which will be flagged as read-only
	 */
	public static void makeDboReadOnly(DbDataObject dbo) {
		dbo.isReadOnly = true;
	}

	/**
	 * Set the protected member DbDataObject.hasGeometry to true
	 * 
	 * @param dbo
	 *            The DbDataObject instance which will be flagged as
	 *            hasGeometry=true
	 */
	public static void dboHasGeometry(DbDataObject dbo) {
		dbo.setHasGeometry(true);
	}

	/**
	 * Set the protected member DbDataObject.isGeometryType to true
	 * 
	 * @param dbo
	 *            The DbDataObject instance which will be flagged as
	 *            isGeometryType=true
	 */
	public static void dboIsGeometryType(DbDataObject dbo) {
		dbo.setGeometryType(true);
	}
	/**
	 * Revert the protected member DbDataObject.isReadOnly to true
	 * 
	 * @param dbo
	 *            The DbDataObject instance which will be flagged as read-only
	 */
	static public void setInitialMaxDate(DateTime dtMax) {
			DbDataObject.MAX_DATE = dtMax;
	}
}
