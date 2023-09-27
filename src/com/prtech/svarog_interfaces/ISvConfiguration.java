package com.prtech.svarog_interfaces;

import java.sql.Connection;

/**
 * Interface which shall allow configuration of specific Svarog object types
 * during the execution of Svarog install, or upgrade processes. This interface
 * provides methods which are invoked in different stages of execution of the
 * svarog install/upgrade.
 * 
 * @author ristepejov
 *
 */
public interface ISvConfiguration {

	/**
	 * Enum of different types of updates executed during the Svarog update
	 * cycle
	 * 
	 * @author ristepejov
	 *
	 */
	public enum UpdateType {
		SCHEMA(1 << 0), LABELS(1 << 1), CODES(1 << 2), TYPES(1 << 3), LINKTYPES(1 << 4), ACL(1 << 5), SIDACL(
				1 << 6), FINAL(1 << 7);

		private final int type;

		UpdateType(int type) {
			this.type = type;
		}

		public int getUpdateType() {
			return type;
		}
	}

	/**
	 * Method to return a numeric order of execution of the upgrade. This is
	 * usefull when you want to ensure that certain upgrades are execute in a
	 * specified order. The interfaces which return lower execution order will
	 * be executed first.
	 * 
	 * @param updateType
	 *            The type of update for which we want to get the execution
	 *            order. This parameter allows for different types of upgrades
	 *            to be execution in different order.
	 * @return The execution order value
	 */
	int executionOrder(UpdateType updateType);

	/**
	 * Method to allow external classes and libraries to execute code <b>before
	 * the Svarog Schema updates</b> are executed. The svarog schema updates are
	 * creating modifying different database tables, views and indices. After
	 * the schema updates, the upgrade of labels is invoked
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeSchemaUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the schema updates have finished, the Labels upgrade is performed
	 * based on label objects generated in the JSON. This method allows to
	 * execute custom code <b>after the Schema update</b> and <b>before the
	 * Labels update</b> takes place. After the labels upgrade, Svarog will
	 * upgrade the system codes.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeLabelsUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the Label updates have finished, the Codes update is performed
	 * based on code objects generated in the JSON. This method allows to
	 * execute custom code <b>after the Labels update</b> and <b>before the
	 * Codes update</b> takes place. After the codes upgrade, Svarog will
	 * upgrade the system objects and fields descriptors (DBTs).
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeCodesUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the Code updates have finished, the Object Types update is
	 * performed based on object types records generated in the JSON. This
	 * method allows to execute custom code <b>after the Codes update</b> and
	 * <b>before the Object Types update</b> takes place. The object type update
	 * is the update of all object types (also known as DBT in Svarog) and their
	 * fields, corresponding to the previously executed schema update. After the
	 * Object Type upgrade, Svarog will upgrade the system link types.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeTypesUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the Object Type updates have finished, the Link Types update is
	 * performed based on link types records generated in the JSON. This method
	 * allows to execute custom code <b>after the Object Types update</b> and
	 * <b>before the Link Types update</b> takes place. The link type update is
	 * the update of all link types. After the Link Type upgrade, Svarog will
	 * upgrade the system ACLs.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeLinkTypesUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the Link Type updates have finished, the ACL update is performed
	 * based on ACL records generated in the JSON. This method allows to execute
	 * custom code <b>after the Link Types update</b> and <b>before the ACL
	 * update</b> takes place. The ACL update is the update the list of ACL
	 * objects in the system. After the ACL upgrade, Svarog will upgrade the
	 * system assignment of ACLs per SID.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeAclUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After the ACL updates have finished, the update of Sid<->ACL
	 * configuration is performed based on records generated in the JSON. This
	 * method allows to execute custom code <b>after the ACL updates</b> and
	 * <b>before the update SID<->ACL pairs</b> takes place. After the SID<->ACL
	 * upgrade, the Svarog upgrade is finalized.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)            
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String beforeSidAclUpdate(Connection conn, ISvCore core, String schema) throws Exception;

	/**
	 * After all Svarog upgrade stages have passed, the upgrade will be
	 * finalised. This method allows to execute custom code after all updates
	 * have successfully passed.
	 * 
	 * @param conn
	 *            Valid JDBC connection against which the queries shall be
	 *            executed
	 * @param core
	 *            Instance of a valid SvCore with system privileges (if
	 *            available)
	 * @param schema
	 *            The default schema name as configured in the svarog.parameters
	 * @returns A string message that will be printed as INFO in the install
	 *          process
	 * @throws Exception
	 *             If exception is thrown, the installation is aborted
	 */
	String afterUpdate(Connection conn, ISvCore core, String schema) throws Exception;

}
