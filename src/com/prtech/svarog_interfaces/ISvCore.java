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

import java.sql.Connection;
import java.util.HashMap;
import com.prtech.svarog.SvAclKey;
import com.prtech.svarog.SvException;
import com.prtech.svarog_common.DbDataArray;
import com.prtech.svarog_common.DbDataObject;

/**
 * 
 * SvCore is the svarog god class anti-pattern. Since svarog was designed to be
 * anti-pattern it just complies with basic design principles: fast, simple,
 * configurable while breaking all best practices and patterns.
 * 
 */
public interface ISvCore {
	/**
	 * Method to open a database connection and associate it with the current
	 * SvCore instance This method explicitly opens a connection to the DB. You
	 * should let svarog decide if it needs a connection at all or everything is
	 * cached.
	 * 
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 * @return JDBC Connection object
	 */
	public Connection dbGetConn() throws SvException;

	/**
	 * Wrapper method for Connection.setAutoCommit(autoCommit). The wrapper is
	 * needed to get the tracked JDBC connection associated with this specific
	 * instance. If the connection is shared between multiple SvCore instances
	 * it will of course affect those
	 * 
	 * @param autoCommit
	 *            Boolean flag to enable/disable auto commit
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */
	public void dbSetAutoCommit(Boolean autoCommit) throws SvException;

	/**
	 * Wrapper method for Connection.commit(). The wrapper is needed to get the
	 * tracked JDBC connection associated with this specific instance. If the
	 * connection is shared between multiple SvCore instances it will of course
	 * affect those instances too.
	 * 
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */
	public void dbCommit() throws SvException;

	/**
	 * Wrapper method for Connection.rollback(). The wrapper is needed to get
	 * the tracked JDBC connection associated with this specific instance. If
	 * the connection is shared between multiple SvCore instances it will of
	 * course affect those instances too.
	 * 
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */
	public void dbRollback() throws SvException;

	/**
	 * Method to close a database connection associated with this SvCore
	 * instance. If the connection is shared between multiple SvCore instances,
	 * this method will not perform a real JDBC close on the connection, but
	 * rather decrease the usage count. Invoking DbClose on the last active
	 * SvCore instance will perform the actual closing of the connection. Before
	 * closing the connection, a rollback will be performed by default. If there
	 * was no {@link #dbCommit()} executed before and the connection was in
	 * AutoCommit=false mode, all data will be lost.
	 */
	public void release();

	/**
	 * Wrapper method for better legibility same as release(true);
	 */
	public void hardRelease();

	/**
	 * Method to close a database connection associated with this SvCore
	 * instance. If the connection is shared between multiple SvCore instances,
	 * this method will not perform a real JDBC close on the connection, but
	 * rather decrease the usage count. Invoking DbClose on the last active
	 * SvCore instance will perform the actual closing of the connection. Before
	 * closing the connection, a rollback will be performed by default. If there
	 * was no {@link #dbCommit()} executed before and the connection was in
	 * AutoCommit=false mode, all data will be lost.
	 * 
	 * @param hardRelease
	 *            enables releasing all connections up in the SvCore chain
	 */
	public void release(Boolean hardRelease);

	/**
	 * Method to return the SvCore object which was used for initiating this
	 * instance for the purpose of sharing a single DB connection.
	 * 
	 * @return SvCore reference
	 */
	public ISvCore getParentSvCore();

	/**
	 * Method to switch the current user under which the SvCore instance runs.
	 * In order to switch the user you must have SYSTEM.SUDO acl in your ACL
	 * list. To return the instance to the previous user, use resetUser
	 * 
	 * @param userName
	 *            The user name of the user
	 * @throws SvException
	 *             If the switch failed, an exception is thrown
	 */
	public void switchUser(String userName) throws SvException;

	/**
	 * Method to switch the current user under which the SvCore instance runs.
	 * In order to switch the user you must have SYSTEM.SUDO acl in your ACL
	 * list. To return the instance to the previous user, use resetUser
	 * 
	 * @param user
	 *            The object descriptor of the user we want to switch to
	 * @throws SvException
	 *             If the switch failed, an exception is thrown
	 */
	public void switchUser(DbDataObject user) throws SvException;

	/**
	 * Method to reset the SvCore instance back to the previous user under which
	 * it was running before {@link #switchUser(String)} was executed. In the
	 * linux world, this would equal to sudo su then exit.
	 * 
	 * @throws SvException
	 *             If there was no previous user, to reset to throw exception
	 */
	public void resetUser() throws SvException;

	/**
	 * Method to return the UserGroup configured as default associated with the
	 * user under which this SvCore instance is running.
	 * 
	 * @return DbDataObject containing the UserGroup
	 * @throws SvException
	 *             Re-throws any underlying exception
	 */
	DbDataObject getDefaultUserGroup() throws SvException;

	/**
	 * Method to return all user groups associated with a specific user.
	 * 
	 * @param user
	 *            The user object for which we want to get the groups
	 * @param returnOnlyDefault
	 *            Flag if we want to get the default user group or all groups
	 * @return The DbDataArray containing all linked groups.
	 * @throws SvException
	 *             re-throw underlying exception
	 */
	public DbDataArray getAllUserGroups(DbDataObject user, boolean returnOnlyDefault) throws SvException;

	/**
	 * Method to return all user groups associated with current user associated
	 * with the instance
	 * 
	 * @return The DbDataArray containing all linked groups.
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */
	public DbDataArray getUserGroups() throws SvException;

	/**
	 * Method to check if the current user has assigned the permission/ACL
	 * uniquely identified by permissionKey
	 * 
	 * @param permissionKey
	 *            The unique ID of the permission
	 * @return True if the user has the permission granted
	 */
	public boolean hasPermission(String permissionKey);

	public HashMap<String, DbDataObject> getPermissionsByKey() throws SvException;

	
	/**
	 * Method to get a {@link DbDataArray} holding all permissions (ACLs) for
	 * the current user
	 * 
	 * @return Reference to the {@link DbDataArray} holding the permissions.
	 *         Null if the user is system
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */
	public HashMap<SvAclKey, HashMap<String, DbDataObject>> getPermissions() throws SvException ;

	/**
	 * Method to get a {@link DbDataArray} holding all permissions (ACLs) for
	 * the current user
	 * 
	 * @return Reference to the {@link DbDataArray} holding the permissions.
	 *         Null if the user is system
	 * @throws SvException
	 *             Any underlying exception is re-thrown
	 */

	public DbDataObject getSessionLocale(String sessionToken) throws SvException;

	/**
	 * Method returning the locale object for a specific user
	 * 
	 * @param userObject
	 *            the user description for which we want to get the locale
	 * @return the locale object descriptor attached to the user object
	 * @throws SvException
	 *             any underlying SvException
	 */
	public String getUserLocaleId(DbDataObject userObject) throws SvException;

	/**
	 * Method returning the locale object for a specific user
	 * 
	 * @param userObject
	 *            the user description for which we want to get the locale
	 * @return the locale object descriptor attached to the user object
	 * @throws SvException
	 *             any underlying SvException
	 */
	public DbDataObject getUserLocale(DbDataObject userObject) throws SvException;

	/**
	 * Method returning the locale object for a specific user
	 * 
	 * @param userId
	 *            the user name of the user for which we want to get the locale
	 * @return the locale object descriptor attached to the user object
	 * @throws SvException
	 *             any underlying SvException
	 */
	public DbDataObject getUserLocale(String userId) throws SvException;

	/**
	 * Method to set a locale to a specific User in the system
	 * 
	 * @param userName
	 *            The user name of the of the user
	 * @param locale
	 *            The local which exists in the list of system locales
	 * @throws SvException
	 *             Any underlying exception
	 */
	public void setUserLocale(String userName, String locale) throws SvException;

	/**
	 * Method to create empty DbDataObject with fields map according to the type
	 * descriptor
	 * 
	 * @param dbt
	 *            The type descriptor of the object
	 * @return A DbDataObject instance configured according to the type
	 *         descriptor
	 */
	public DbDataObject createDboByType(DbDataObject dbt);

	public boolean isSystem();

	public boolean isService();

	public boolean isAdmin() throws SvException;

	public Boolean getAutoCommit();

	public void setAutoCommit(Boolean autoCommit);

	/**
	 * Method to get the current User for this SvCore instance
	 * 
	 * @return A {@link DbDataObject} reference holding the object descriptor
	 */
	public DbDataObject getInstanceUser();

	public Boolean getIsDebugEnabled();

	public void setIsDebugEnabled(Boolean isDebugEnabled);

	public Boolean getIsLongRunning();

	public void setIsLongRunning(Boolean isLongRunning);

	public long getCoreLastActivity();

	public void setCoreLastActivity(long coreLastActivity);

	public long getCoreCreation();

	public String getCoreTraceInfo();

	public String getSessionId();

	public DbDataObject getSaveAsUser();

	public void setSaveAsUser(DbDataObject saveAsUser);

	public void setInstanceUser(DbDataObject serviceUser) throws SvException;

}