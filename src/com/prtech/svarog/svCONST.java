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
package com.prtech.svarog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.DboFactory;

public final class svCONST {

	/**
	 * Log4j instance used for logging
	 */
	static final Logger log4j = LogManager.getLogger(svCONST.class.getName());
	// Constants used in the svarog core
	public static final String maintenanceThreadId = "SV_MAINTENANCE_THRD";

	/**
	 * Constant representing the maximal value for record validity
	 */

	public static final String STATUS_VALID = "VALID";
	/*
	 * These are the core system security functions
	 */
	public static final String SUDO_ACL = "system.sudo";
	public static final String NULL_GEOMETRY_ACL = "system.null_geometry";
	public static final String INSECURE_SQL_ACL = "system.insecure_sql";

	// finally the list of repofields is in svarog
	// WARNING the values must be all UPPER CASE
	public static final ArrayList<String> repoFieldNames = initRepoNames();

	public static final long SYSTEM_FILESTORE_ID = -1;

	/* Svarog system objects will always have an ID less than 1000 */
	/* if the amount of system objects exceeds 1000, this needs to be changed */

	public static final long MAX_SYS_OBJECT_ID = 10000;
	public static final long MAX_INT = 1101000000;

	public static final int MAX_ACTIONS_PER_RULE = 100;
	/* SVAROG ERROR CODES */
	public static final long SUCCESS = 0;
	public static final long GENERAL_ERROR = -1000;
	public static final long NO_DATA_OBJ_FOUND = -1001;
	public static final long NO_CFG_TABLE_FOUND = -1002;
	public static final long OBJ_NOT_UPDATEABLE = -1003;
	public static final long OBJ_NOT_IDENTIFIED = -1004;
	public static final long SAVE_TO_TABLE_FORBIDDEN = -1005;
	public static final long FIELD_MUST_HAVE_VALUE = -1006;
	public static final long INVALID_JSON_FORMAT = -1007;
	public static final long VALUE_OUT_OF_RANGE = -1008;
	public static final long OBJ_LINK_INVALID = -1009;
	public static final long OBJ_ID_NOTEXIST = -1010;
	public static final long UNDEFINED_LINK_TYPE = -1011;
	public static final long EXTERNAL_USER_MUST_HAVE_LINK = -1012;
	public static final long USER_ALREADY_EXISTS = -1013;
	public static final long FORM_MUST_HAVE_TYPE = -1014;
	public static final long FORM_TYPE_IS_INVALID = -1015;
	public static final long FORM_MUST_HAVE_PARENT = -1016;
	public static final long NOT_AUTHORIZED = -1017;
	public static final long FORM_TYPE_IS_SINGLE_ENTRY = -1018;
	public static final long FORM_MAX_COUNT_EXCEEDED = -1019;

	/* CODES CONSTANTS */
	public static final long CODES_CACHE_TYPE = 496;
	public static final long CODES_FILE_TYPES = 497;
	public static final long CODES_FIELD_DATATYPES = 498;
	public static final long CODES_UNIQUE_LEVEL = 499;
	public static final long CODES_STATUS = 500;
	// WARNING all system codes will receive an ID between
	// CODES_STATUS=500 and MAX_SYSTEM_ID=1000

	/* SVAROG CORE OBJECT TYPE IDs */
	// Object types which are not writeable via saveObject
	public static final long OBJECT_TYPE_REPO = 1;
	public static final long OBJECT_TYPE_FILE = 2;
	public static final long OBJECT_TYPE_FIELD_SORT = 3;
	public static final long OBJECT_TYPE_SEQUENCE = 4;
	public static final long OBJECT_TYPE_SECURITY_LOG = 5;

	/**
	 * Object ID Constant of the object which describes the cluster coordinator.
	 */
	public static final long OBJECT_TYPE_CONFIGURATION_LOG= 47;
	public static final long CLUSTER_COORDINATOR_ID = 48;
	public static final long OBJECT_ID_HEADQUARTER = 49;
	
	// Object types which are managed via saveObject
	public static final long MIN_WRITEABLE_OBJID = 50;
	public static final long OBJECT_TYPE_TABLE = MIN_WRITEABLE_OBJID;
	public static final long OBJECT_TYPE_FIELD = MIN_WRITEABLE_OBJID + 1;
	public static final long OBJECT_TYPE_CODE = MIN_WRITEABLE_OBJID + 2;
	public static final long OBJECT_TYPE_LOCALE = MIN_WRITEABLE_OBJID + 3;
	public static final long OBJECT_TYPE_LABEL = MIN_WRITEABLE_OBJID + 4;
	public static final long OBJECT_TYPE_LINK = MIN_WRITEABLE_OBJID + 5;
	public static final long OBJECT_TYPE_LINK_TYPE = MIN_WRITEABLE_OBJID + 6;
	public static final long OBJECT_TYPE_USER = MIN_WRITEABLE_OBJID + 7;
	public static final long OBJECT_TYPE_GROUP = MIN_WRITEABLE_OBJID + 8;
	public static final long OBJECT_TYPE_ACL = MIN_WRITEABLE_OBJID + 9;
	public static final long OBJECT_TYPE_WORKFLOW = MIN_WRITEABLE_OBJID + 10;
	public static final long OBJECT_TYPE_SID_ACL = MIN_WRITEABLE_OBJID + 11;
	public static final long OBJECT_TYPE_ORG_UNITS = MIN_WRITEABLE_OBJID + 12;
	public static final long OBJECT_TYPE_FORM_TYPE = MIN_WRITEABLE_OBJID + 13;
	public static final long OBJECT_TYPE_FORM_FIELD_TYPE = MIN_WRITEABLE_OBJID + 14;
	public static final long OBJECT_TYPE_FORM = MIN_WRITEABLE_OBJID + 15;
	public static final long OBJECT_TYPE_FORM_FIELD = MIN_WRITEABLE_OBJID + 16;
	// Rule engine
	public static final long OBJECT_TYPE_RULE = MIN_WRITEABLE_OBJID + 17;
	public static final long OBJECT_TYPE_ACTION = MIN_WRITEABLE_OBJID + 18;
	public static final long OBJECT_TYPE_EXECUTION = MIN_WRITEABLE_OBJID + 19;
	public static final long OBJECT_TYPE_RESULT = MIN_WRITEABLE_OBJID + 20;
	// Batch execution engine
	@Deprecated
	public static final long OBJECT_TYPE_JOB_TYPE = MIN_WRITEABLE_OBJID + 21;
	@Deprecated
	public static final long OBJECT_TYPE_JOB = MIN_WRITEABLE_OBJID + 22;
	@Deprecated
	public static final long OBJECT_TYPE_TASK_TYPE = MIN_WRITEABLE_OBJID + 23;
	@Deprecated
	public static final long OBJECT_TYPE_TASK = MIN_WRITEABLE_OBJID + 24;
	@Deprecated
	public static final long OBJECT_TYPE_TASK_DETAIL = MIN_WRITEABLE_OBJID + 25;
	@Deprecated
	public static final long OBJECT_TYPE_JOB_TASK = MIN_WRITEABLE_OBJID + 26;
	@Deprecated
	public static final long OBJECT_TYPE_JOB_OBJECT = MIN_WRITEABLE_OBJID + 27;

	public static final long OBJECT_TYPE_PARAM_TYPE = MIN_WRITEABLE_OBJID + 28;
	public static final long OBJECT_TYPE_PARAM = MIN_WRITEABLE_OBJID + 29;
	public static final long OBJECT_TYPE_PARAM_VALUE = MIN_WRITEABLE_OBJID + 30;
	// UI_STRUCTURE
	public static final long OBJECT_TYPE_RENDER_ENGINE = MIN_WRITEABLE_OBJID + 31;
	public static final long OBJECT_TYPE_UI_STRUCTURE_TYPE = MIN_WRITEABLE_OBJID + 32;
	public static final long OBJECT_TYPE_UI_STRUCTURE_SOURCE = MIN_WRITEABLE_OBJID + 33;
	// NOTE
	public static final long OBJECT_TYPE_NOTES = MIN_WRITEABLE_OBJID + 34;
	// CONTACT_DATA
	public static final long OBJECT_TYPE_CONTACT_DATA = MIN_WRITEABLE_OBJID + 35;
	// EVENT/NOTIFICATION STRUCTURE
	public static final long OBJECT_TYPE_EVENT = MIN_WRITEABLE_OBJID + 36;
	public static final long OBJECT_TYPE_NOTIFICATION = MIN_WRITEABLE_OBJID + 37;
	public static final long OBJECT_TYPE_CONVERSATION = MIN_WRITEABLE_OBJID + 38;
	public static final long OBJECT_TYPE_MESSAGE = MIN_WRITEABLE_OBJID + 39;

	// Spatial Data Infrastructure
	public static final long OBJECT_TYPE_SDI_SYSBOUNDS = -1;
	public static final long OBJECT_TYPE_SDI_UNITS = MIN_WRITEABLE_OBJID + 40;
	public static final long OBJECT_TYPE_SDI_BOUNDS = MIN_WRITEABLE_OBJID + 41;
	public static final long OBJECT_TYPE_SDI_COVER = MIN_WRITEABLE_OBJID + 42;
	public static final long OBJECT_TYPE_SDI_USE = MIN_WRITEABLE_OBJID + 43;
	public static final long OBJECT_TYPE_SDI_GEOJSONFILE = MIN_WRITEABLE_OBJID + 44;
	public static final long OBJECT_TYPE_SDI_DESCRIPTOR = MIN_WRITEABLE_OBJID + 45;
	public static final long OBJECT_TYPE_SDI_SERVICE = MIN_WRITEABLE_OBJID + 46;

	// Object types which are used in Svarog-Batch
	// ID: OBJECT_TYPE_BATCH_JOB_TYPE for table name BATCH_JOB_TYPE
	public static final long OBJECT_TYPE_BATCH_JOB_TYPE = MIN_WRITEABLE_OBJID + 47;
	// ID: OBJECT_TYPE_BATCH_JOB for table name BATCH_JOB
	public static final long OBJECT_TYPE_BATCH_JOB = MIN_WRITEABLE_OBJID + 48;
	// ID: OBJECT_TYPE_BATCH_JOB_TEMPLATE for table name BATCH_JOB_TEMPLATE
	public static final long OBJECT_TYPE_BATCH_JOB_TEMPLATE = MIN_WRITEABLE_OBJID + 49;
	// ID: OBJECT_TYPE_BATCH_JOB_PARAM for table name BATCH_JOB_PARAM
	public static final long OBJECT_TYPE_BATCH_JOB_PARAM = MIN_WRITEABLE_OBJID + 50;
	// ID: OBJECT_TYPE_BATCH_JOB_PARAM for table name BATCH_JOB_PARAM
	public static final long OBJECT_TYPE_BATCH_JOB_CONFIG = MIN_WRITEABLE_OBJID + 51;
	// ID: OBJECT_TYPE_EXECUTORS
	public static final long OBJECT_TYPE_EXECUTORS = MIN_WRITEABLE_OBJID + 52;

	public static final long OBJECT_TYPE_CLUSTER = MIN_WRITEABLE_OBJID + 53;

	public static final long OBJECT_TYPE_RESERVED_DONTUSE1 = MIN_WRITEABLE_OBJID + 54;

	public static final long OBJECT_TYPE_RESERVED_DONTUSE2 = MIN_WRITEABLE_OBJID + 55;

	public static final long OBJECT_TYPE_PERUN_PLUGIN = MIN_WRITEABLE_OBJID + 56;

	public static final long OBJECT_TYPE_EXECUTOR_PACK = MIN_WRITEABLE_OBJID + 57;

	public static final long OBJECT_TYPE_EXECPACK_ITEM = MIN_WRITEABLE_OBJID + 58;

	public static final long OBJECT_TYPE_SYS_PARAMS = MIN_WRITEABLE_OBJID + 59;

	public static final long OBJECT_TYPE_GRID = MIN_WRITEABLE_OBJID + 60;
	
	public static final long OBJECT_TYPE_USER_PARAMS = MIN_WRITEABLE_OBJID + 61;
	
	/* SVAROG WELL KNOWN SECURITY IDs */
	public static final String SID_NOBODY_UID = "S-1-0-0";
	public static final String SID_EVERYONE_UID = "S-1-1-0";
	public static final String SID_GUESTS_UID = "S-1-5-32-546";
	public static final String SID_USERS_UID = "S-1-5-32-545";
	public static final String SID_POWER_USERS_UID = "S-1-5-32-547";
	public static final String SID_ADMINISTRATORS_UID = "S-1-5-32-544";

	public static final long SID_NOBODY = -1;
	public static final long SID_EVERYONE = -2;
	public static final long SID_GUESTS = -3;
	public static final long SID_USERS = -4;
	public static final long SID_POWER_USERS = -5;
	public static final long SID_ADMINISTRATORS = -6;
	public static final long OBJECT_USER_SERVICE = -12;
	public static final long OBJECT_USER_SYSTEM = -13;

	public static DbDataObject systemUser = getSystemUser();
	public static DbDataObject serviceUser = getServiceUser();
	public static DbDataObject usersGroup = getUsersGroup();
	public static DbDataObject adminsGroup = getAdminsGroup();

	private static DbDataObject getServiceUser() {
		DbDataObject serviceUser = new DbDataObject();
		serviceUser.setObjectId(svCONST.OBJECT_USER_SERVICE);
		serviceUser.setVal("USER_NAME", "USER_SERVICE");
		DboFactory.makeDboReadOnly(serviceUser);
		return serviceUser;
	}

	/**
	 * Method to initialise the array holding the repo field name constants.
	 * 
	 * @return
	 */
	private static ArrayList<String> initRepoNames() {
		ArrayList<String> retVal = new ArrayList<>(DbDataObject.repoFieldNames.size());
		for (char[] rf : DbDataObject.repoFieldNames) {
			retVal.add(new String(rf));
		}
		return retVal;
	}

	private static DbDataObject getSystemUser() {
		DbDataObject systemUser = new DbDataObject();
		systemUser.setObjectId(svCONST.OBJECT_USER_SYSTEM);
		systemUser.setVal("USER_NAME", "USER_SYSTEM");
		DboFactory.makeDboReadOnly(systemUser);
		return systemUser;
	}

	private static DbDataObject getUsersGroup() {
		DbDataObject dboUserGroup = new DbDataObject();
		dboUserGroup.setObjectType(svCONST.OBJECT_TYPE_GROUP);
		dboUserGroup.setObjectId(svCONST.SID_USERS);
		dboUserGroup.setVal("GROUP_TYPE", "USERS");
		dboUserGroup.setVal("GROUP_UID", svCONST.SID_USERS_UID);
		dboUserGroup.setVal("GROUP_NAME", "USERS");
		dboUserGroup.setVal("E_MAIL", "user@user.com");
		dboUserGroup.setVal("GROUP_SECURITY_TYPE", "POA");
		DboFactory.makeDboReadOnly(dboUserGroup);
		return dboUserGroup;
	}

	private static DbDataObject getAdminsGroup() {
		DbDataObject dboUserGroup = new DbDataObject();
		dboUserGroup.setObjectType(svCONST.OBJECT_TYPE_GROUP);
		dboUserGroup.setObjectId(svCONST.SID_ADMINISTRATORS);
		dboUserGroup.setVal("GROUP_TYPE", "ADMINISTRATORS");
		dboUserGroup.setVal("GROUP_UID", svCONST.SID_ADMINISTRATORS_UID);
		dboUserGroup.setVal("GROUP_NAME", "ADMINISTRATORS");
		dboUserGroup.setVal("E_MAIL", "admin@admin.com");
		dboUserGroup.setVal("GROUP_SECURITY_TYPE", "FULL");
		DboFactory.makeDboReadOnly(dboUserGroup);
		return dboUserGroup;
	}

	public svCONST() {
	}
}
