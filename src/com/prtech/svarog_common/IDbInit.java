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

import java.util.ArrayList;

/**
 * Interface which allows one to add custom database objects to a svarog
 * instance.
 * 
 * Each class which implements this interface shall provide two lists of
 * objects: 1. Custom Object types (your database tables which Svarog should
 * create in the database) 2. Object instances which svarog should save into the
 * database as per your own configuration
 * 
 * If you need more flexible configuration you should look at ISvConfiguration
 * interface
 */
public interface IDbInit {

	/**
	 * method returning a list of object types your OSGi Svarog Plug-in needs
	 * 
	 * @return
	 */
	public ArrayList<DbDataTable> getCustomObjectTypes();

	/**
	 * method returning a list of object instances your OSGi Svarog Plug-in needs to work
	 * 
	 * @return
	 */
	public ArrayList<DbDataObject> getCustomObjectInstances();

}
