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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Interface providing basic methods needed by the svarog core to work on
 * different SQL or noSQL database systems
 * 
 * @author Riste Pejov
 *
 */
public interface ISvDatabaseIO {

	/**
	 * Method to allow svarog to initialize the system SRID of the database and
	 * let the Database method be aware of the currently active srid of GIS data
	 * sets
	 * 
	 * @param srid
	 *            The SRID to be used for any GIS operations.
	 */
	public void initSrid(String srid);

	/**
	 * Method to allow svarog to read a blob from the record set at position
	 * into Geometry
	 * 
	 * @param resultSet
	 *            The jdbc result set from which the geometry shall be read
	 * @param columnIndex
	 *            the column ordinal in the resultset containing the geometry
	 * @return Geometry object
	 */
	public byte[] getGeometry(ResultSet resultSet, int columnIndex) throws SQLException;

	/**
	 * Method to allow svarog to bind a geometry to the prepared statement at
	 * specified position
	 * 
	 * @param preparedStatement
	 *            The jdbc preparedStatement to from which the geometry shall
	 *            bound
	 * @param position
	 *            the position on which the geometry shall be bound
	 * @param value
	 *            The geometry instance
	 */
	public void setGeometry(PreparedStatement preparedStatement, int position, byte[] value) throws Exception;

	/**
	 * Method to return the Timestamp class which the specific jdbc driver uses
	 * for fetching date time/timestamp columns. The columns of this type will
	 * be translated to Joda DateTime
	 * 
	 * @return
	 */
	Class<?> getTimeStampClass();

	/**
	 * If the specific database implementation uses a stored procedure for
	 * inserting repo objects or uses the classic JDBC insert batch. For example
	 * the MSSQL jdbc driver is not able to return the generated keys and we
	 * need to use a stored procedure
	 * 
	 * @return
	 */
	boolean getOverrideInsertRepo();

	/**
	 * If the repo insert is overriden by the handler, than a custom insert
	 * statement needs to be generated
	 * 
	 * @param conn
	 *            The JDBC connection used to prepare the statement
	 * @param defaultStatement
	 *            The default statement as prepared by SVAROG
	 * @param schema
	 *            The database schema in which the repo table resides
	 * @param repoName
	 *            The name of the repo table in which the objects shall be
	 *            inserted
	 * @return An instance of Prepared Statement ready for execution
	 * @throws SQLException
	 */
	PreparedStatement getInsertRepoStatement(Connection conn, String defaultStatement, String schema, String repoName)
			throws SQLException;

	/**
	 * If the handler uses a specific STRUCT for passing data to the custom
	 * procedure/function for inserting repo data
	 * 
	 * @param conn
	 *            The connection used for preparing the structure.
	 * @param maxSize
	 *            The maximum number of repo objects expected in this batch
	 * @return An instance of the structure
	 */
	Object getInsertRepoStruct(Connection conn, int maxSize) throws SQLException;

	/**
	 * For each of the saved objects, the method to batch the repo records is
	 * called
	 * 
	 * @param insertRepoStruct
	 *            If a custom STRUCT is used, this will be pointer
	 * @param PKID
	 *            The PKID of the object which is saved
	 * @param oldMetaPKID
	 *            The old PKID of the Metatable
	 * @param objectId
	 *            The object id
	 * @param dtInsert
	 *            The timestamp of the operation
	 * @param maxDateSql
	 *            The validity of the object
	 * @param parentId
	 *            The parent id
	 * @param objType
	 *            The type of object
	 * @param objStatus
	 *            The status of the object
	 * @param userId
	 *            The user which is executing the save
	 * @throws SQLException
	 */
	void addRepoBatch(Object insertRepoStruct, Long PKID, Long oldMetaPKID, Long objectId, Timestamp dtInsert,
			Timestamp maxDateSql, Long parentId, Long objType, String objStatus, Long userId, int rowIndex)
			throws SQLException;

	/**
	 * After all objects have been properly batched, the repoSaveGetKeys method
	 * will execute the statement prepared by getInsertRepoStatement along with
	 * the structure generated by getInsertRepoStruct which is populated for
	 * each object with addRepoBatch method.
	 * 
	 * @param repoInsert
	 *            The insert statement
	 * @param insertRepoStruct
	 *            The structure holding the repo objects for the batch
	 * @return A map holding the pairs of generated PKID/ObjectId
	 * @throws SQLException
	 */
	Map<Long, Long> repoSaveGetKeys(PreparedStatement repoInsert, Object insertRepoStruct) throws SQLException;

	/**
	 * Method to prepare array type specific to the database in case of
	 * executing procedures under ther Rule Engine
	 * 
	 * @param conn
	 *            The JDBC connection used to prepare the array type
	 * @param arrayType
	 *            The name of the type in the database
	 */
	void prepareArrayType(Connection conn, String arrayType) throws SQLException;

	/**
	 * The name of the handler type. This should match the list of supported
	 * databases. Currently POSTGRES, ORACLE, MSSQL
	 * 
	 * @return String with one of the above constants
	 */
	String getHandlerType();

	/**
	 * String specific to the target database to prepare the geometry column to
	 * be read by a EWKB Reader
	 * 
	 * @param fieldName
	 *            The field name holding the geometry
	 * @return A string to be used as part of the select statement
	 */
	String getGeomReadSQL(String fieldName);

	/**
	 * The database specifics for writing a WKB geometry to the target database.
	 * 
	 * @return A string to be used as part of the insert statement
	 */
	String getGeomWriteSQL();

	/**
	 * Method for generating SQL string accepting 4 points to generate an
	 * envelope. Used in BBOX where clauses
	 * 
	 * @param geomName
	 *            The geometry name used in the BBOX
	 * @return A string to be used as part of the select statement
	 */
	String getBBoxSQL(String geomName);

	/**
	 * Method returning the delimiter of database scripts. By using this
	 * delimiter, Svarog will split the script into multiple statements which
	 * are executed on its own
	 * 
	 * @return The string delimiter
	 */
	String getDbScriptDelimiter();

	/**
	 * Method to fetch a script with the specific name from the dbHandler
	 * 
	 * @param scriptName
	 *            The script name to be fetched
	 * @return The script content
	 */
	String getSQLScript(String scriptName);

	/**
	 * Method to get a resource bundle representing the list of SQL key words
	 * for the specific database handler
	 * 
	 * @return The resource bundle containing the keywords
	 */
	ResourceBundle getSQLKeyWordsBundle();

	/**
	 * Internal Method of the Database handler, which will be invoked BEFORE any
	 * Svarog UPGRADE/INSTALL takes place. The method can stop the installation
	 * by throwing an exception
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeInstall(Connection conn, String schema) throws Exception;

	/**
	 * Internal Method of the Database handler, which will be invoked AFTER any
	 * Svarog UPGRADE/INSTALL takes place. It can be used to validate the
	 * install or do some post processing
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 */
	String afterInstall(Connection conn, String schema);

}
