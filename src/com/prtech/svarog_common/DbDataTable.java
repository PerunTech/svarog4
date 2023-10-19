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
/**
 * 
 */
package com.prtech.svarog_common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.prtech.svarog_common.DbDataField.DbFieldType;

/**
 * @author PR01
 * 
 */
public class DbDataTable extends Jsonable {
	private static ResourceBundle sqlKWResource = null;
	static String geometrySrid;

	public static void initSrid(String srid) {
		geometrySrid = srid;
	}

	Long objectId;

	// configuration about the parent object
	Long parentId;
	String parentName;

	// Database configuration like schema, table, repo
	String repoName;
	String tableName;
	String schema;

	// is the table/object system or repo
	Boolean isSystemTable = false;
	Boolean isRepoTable = false;

	// DataFields holding the object metadata
	DbDataField[] dbTableFields;

	// label containing description of the object (used in the GUI)
	String labelCode;

	// Caching parameters for this object type
	Boolean useCache;
	String cacheТype;
	Long cacheExpiry = 0L;
	Long cacheSize = 0L;

	// set flag if this table a configuration table managed by the svarog
	// install/upgrade
	Boolean isConfigTable = false;
	String configColumnName;

	// JSON string describing the GUI parameters of this object
	// should be removed and made deprecated in the future
	String guiMetadata;

	// set object_id of configuration table
	String configTypeName;

	// set relation type between impl object and conf object
	String configRelationType;

	// set object_id between impl object and object used in relation
	String configRelatedTypeName;

	public DbDataTable() {
	}

	public DbDataTable(ResourceBundle sqlKWResource) {
		DbDataTable.sqlKWResource = sqlKWResource;
	}

	private LinkedHashMap<String, DbDataField> mapDbTableFields = new LinkedHashMap<String, DbDataField>();

	public String getSQLTableElements() throws Exception {
		String retval = "";
		if (dbTableFields != null)
			for (int i = 0; i < dbTableFields.length; i++) {
				if (dbTableFields[i] != null) {
					retval += dbTableFields[i].getSQLString() + ",";
				}
			}
		retval = retval.substring(0, retval.length() - 1);
		return retval;
	}

	public String getSQLTableConstraints() {
		String retval = "";
		HashMap<String, String> constrMap = new HashMap<String, String>();
		if (dbTableFields != null)
			for (int i = 0; i < dbTableFields.length; i++) {
				if (dbTableFields[i] != null) {
					if (dbTableFields[i].getIsPrimaryKey()) {
						String currentPK = constrMap.get("DEFAULT_PK");
						constrMap.put("DEFAULT_PK",
								(currentPK != null ? currentPK + "," : "") + dbTableFields[i].getDbFieldName());
					}
					if (this.isRepoTable != null && this.isRepoTable) {
						if (dbTableFields[i].getIsUnique()) {
							String unqName = dbTableFields[i].getUnique_constraint_name();
							unqName = (unqName == null ? "DEFAULT_UNQ" : unqName);
							String currentUnq = constrMap.get(unqName);
							constrMap.put(unqName,
									(currentUnq != null ? currentUnq + "," : "") + dbTableFields[i].getDbFieldName());
						}
					}
				}
			}

		for (Entry<String, String> entry : constrMap.entrySet()) {
			String constr = entry.getValue();
			if (entry.getKey().equals("DEFAULT_PK"))
				constr = sqlKWResource.getString("CONSTRAINT") + " " + tableName + "_pkey "
						+ sqlKWResource.getString("PRIMARY_KEY") + "(" + constr + ")";
			else if (entry.getKey().equals("DEFAULT_UNQ"))
				constr = sqlKWResource.getString("CONSTRAINT") + " " + tableName + "_unq "
						+ sqlKWResource.getString("UNIQUE") + "(" + constr + ")";
			else
				constr = sqlKWResource.getString("CONSTRAINT") + " " + entry.getKey() + " "
						+ sqlKWResource.getString("UNIQUE") + "(" + constr + ")";

			retval += ", " + constr;

		}
		return retval;
	}

	public String[] getDefaultUniqueList() {
		ArrayList<String> lst = new ArrayList<String>();
		if (dbTableFields != null) {
			for (int i = 0; i < dbTableFields.length; i++) {
				if (dbTableFields[i].getIsUnique()) {
					String unqName = dbTableFields[i].getUnique_constraint_name();
					if (unqName == null)
						lst.add(dbTableFields[i].getDbFieldName());

				}

			}
			if (lst.size() > 0)
				return (String[]) lst.toArray();
			else
				return null;
		} else
			return null;

	}

	public HashMap<String, String> getSQLTableIndices() {
		HashMap<String, String> constrMap = new HashMap<String, String>();
		if (dbTableFields != null)
			for (int i = 0; i < dbTableFields.length; i++) {
				if (dbTableFields[i] != null) {
					if (!this.isRepoTable) {
						if (dbTableFields[i].getIsUnique()
								&& dbTableFields[i].getDbFieldType() != DbFieldType.GEOMETRY) {
							String unqName = dbTableFields[i].getUnique_constraint_name();
							unqName = (unqName == null ? "DEFAULT_UNQ" : unqName);
							String currentUnq = constrMap.get(unqName);
							constrMap.put(unqName,
									(currentUnq != null ? currentUnq + "," : "")
											+ sqlKWResource.getString("OBJECT_QUALIFIER_LEFT")
											+ dbTableFields[i].getDbFieldName()
											+ sqlKWResource.getString("OBJECT_QUALIFIER_RIGHT"));
						}
					}
					if ((dbTableFields[i].getIndexName() != null
							&& dbTableFields[i].getDbFieldType() != DbFieldType.GEOMETRY
							&& !dbTableFields[i].getIndexName().equals(""))) {
						String idxName = dbTableFields[i].getIndexName();
						String currentUnq = constrMap.get(idxName);
						if ((currentUnq != null && !currentUnq.contains(dbTableFields[i].getDbFieldName()))
								|| currentUnq == null) {
							if (dbTableFields[i].getDbFieldName().contains("_ID"))
								currentUnq = dbTableFields[i].getDbFieldName()
										+ (currentUnq != null ? "," + currentUnq : "");
							else
								currentUnq = (currentUnq != null ? currentUnq + "," : "")
										+ dbTableFields[i].getDbFieldName();
							constrMap.put(idxName, currentUnq);
						}
					}
				}

			}

		return constrMap;
	}

	public HashMap<String, String> getSQLSpatialIndices() {
		HashMap<String, String> constrMap = new HashMap<String, String>();
		if (dbTableFields != null)
			for (int i = 0; i < dbTableFields.length; i++) {
				if (dbTableFields[i] != null && dbTableFields[i].getDbFieldType() == DbFieldType.GEOMETRY) {
					if ((dbTableFields[i].getIndexName() != null && !dbTableFields[i].getIndexName().equals(""))) {
						String idxName = dbTableFields[i].getIndexName();
						String currentUnq = constrMap.get(idxName);
						if ((currentUnq != null && !currentUnq.contains(dbTableFields[i].getDbFieldName()))
								|| currentUnq == null) {
							if (dbTableFields[i].getDbFieldName().contains("_ID"))
								currentUnq = sqlKWResource.getString("OBJECT_QUALIFIER_LEFT")
										+ dbTableFields[i].getDbFieldName()
										+ sqlKWResource.getString("OBJECT_QUALIFIER_RIGHT")
										+ (currentUnq != null ? "," + currentUnq : "");
							else
								currentUnq = (currentUnq != null ? currentUnq + "," : "")
										+ sqlKWResource.getString("OBJECT_QUALIFIER_LEFT")
										+ dbTableFields[i].getDbFieldName()
										+ sqlKWResource.getString("OBJECT_QUALIFIER_RIGHT");
							constrMap.put(idxName, currentUnq);
						}
					}
				}

			}

		return constrMap;
	}

	public LinkedHashMap<String, DbDataField> getMapDbTableFields() {

		if (mapDbTableFields.size() != dbTableFields.length)
			syncMap();
		return mapDbTableFields;
	}

	private void syncMap() {
		mapDbTableFields.clear();
		for (int i = 0; i < dbTableFields.length; i++) {
			if (dbTableFields[i] != null && dbTableFields[i].getDbFieldName() != null)
				mapDbTableFields.put(dbTableFields[i].getDbFieldName().toUpperCase(), dbTableFields[i]);
		}
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public String getDbRepoName() {
		return repoName;
	}

	public void setDbRepoName(String dbRepoName) {
		this.repoName = dbRepoName;
	}

	public String getDbTableName() {
		return tableName;
	}

	public void setDbTableName(String dbTableName) {
		this.tableName = dbTableName.toUpperCase();
	}

	public String getDbSchema() {
		return schema;
	}

	public void setDbSchema(String dbSchema) {
		this.schema = dbSchema.toUpperCase();
	}

	public String[] getDbPrimaryKeys() {
		// function to return PKs
		return null;
	}

	public Boolean getIsSystemTable() {
		return isSystemTable;
	}

	public void setIsSystemTable(Boolean isSystemTable) {
		this.isSystemTable = isSystemTable;
	}

	public DbDataField[] getDbTableFields() {
		return dbTableFields;
	}

	public void setDbTableFields(DbDataField[] dbTableFields) {
		this.dbTableFields = dbTableFields;
		syncMap();
	}

	public Boolean getIsRepoTable() {
		return isRepoTable;
	}

	public void setIsRepoTable(Boolean repo_table) {
		this.isRepoTable = repo_table;
	}

	public String getLabel_code() {
		return labelCode;
	}

	public void setLabel_code(String label_code) {
		this.labelCode = label_code;
	}

	public Boolean getUse_cache() {
		return useCache;
	}

	public void setUse_cache(Boolean use_cache) {
		this.useCache = use_cache;
	}

	public Long getParent_id() {
		return parentId;
	}

	public void setParent_id(Long parent_id) {
		this.parentId = parent_id;
	}

	public String getGui_metadata() {
		return guiMetadata;
	}

	public void setGui_metadata(String gui_metadata) {
		this.guiMetadata = gui_metadata;
	}

	public static ResourceBundle getSqlKWResource() {
		return sqlKWResource;
	}

	public static void setRbConf(ResourceBundle sqlKWResource) {
		DbDataTable.sqlKWResource = sqlKWResource;
	}

	public String getCacheType() {
		return cacheТype;
	}

	public void setCacheType(String cacheType) {
		this.cacheТype = cacheType;
	}

	public long getCacheTTL() {
		return cacheExpiry;
	}

	public void setCacheTTL(long cacheTTL) {
		this.cacheExpiry = cacheTTL;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public Boolean getIsConfigTable() {
		return isConfigTable;
	}

	public void setIsConfigTable(Boolean is_config_table) {
		this.isConfigTable = is_config_table;
	}

	public String getConfigColumnName() {
		return configColumnName;
	}

	public void setConfigColumnName(String config_column_name) {
		this.configColumnName = config_column_name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parent_name) {
		this.parentName = parent_name;
	}

	public String getConfigRelationType() {
		return configRelationType;
	}

	public void setConfigRelationType(String configRelationType) {
		this.configRelationType = configRelationType;
	}

	public String getConfigTypeName() {
		return configTypeName;
	}

	public void setConfigTypeName(String configTypeName) {
		this.configTypeName = configTypeName;
	}

	public String getConfigRelatedTypeName() {
		return configRelatedTypeName;
	}

	public void setConfigRelatedTypeName(String configRelatedTypeName) {
		this.configRelatedTypeName = configRelatedTypeName;
	}

}