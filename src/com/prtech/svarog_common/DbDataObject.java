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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.gson.JsonObject;
import com.prtech.svarog.svCONST;

public class DbDataObject extends Jsonable {
	/**
	 * DbDataObject repo constants
	 */
	public static final char[] PKID = { 'P', 'K', 'I', 'D' };
	public static final char[] META_PKID = { 'M', 'E', 'T', 'A', '_', 'P', 'K', 'I', 'D' };
	public static final char[] DT_INSERT = { 'D', 'T', '_', 'I', 'N', 'S', 'E', 'R', 'T' };
	public static final char[] DT_DELETE = { 'D', 'T', '_', 'D', 'E', 'L', 'E', 'T', 'E' };
	public static final char[] OBJECT_ID = { 'O', 'B', 'J', 'E', 'C', 'T', '_', 'I', 'D' };
	public static final char[] PARENT_ID = { 'P', 'A', 'R', 'E', 'N', 'T', '_', 'I', 'D' };
	public static final char[] OBJECT_TYPE = { 'O', 'B', 'J', 'E', 'C', 'T', '_', 'T', 'Y', 'P', 'E' };
	public static final char[] STATUS = { 'S', 'T', 'A', 'T', 'U', 'S' };
	public static final char[] USER_ID = { 'U', 'S', 'E', 'R', '_', 'I', 'D' };

	public static final ArrayList<char[]> repoFieldNames = new ArrayList<char[]>(
			Arrays.asList(PKID, META_PKID, DT_INSERT, DT_DELETE, OBJECT_ID, PARENT_ID, OBJECT_TYPE, STATUS, USER_ID));

	/**
	 * Static MAX DATE value to use for initialisation of DT_DELETE
	 */
	static DateTime MAX_DATE;
	/**
	 * PKID Unique versioning identifier of the object in the repo table
	 */
	Long pkid = 0L;
	/**
	 * Persistent ID of the object. Doesn't change with versioning updates
	 */
	Long object_id = 0L;
	/**
	 * Timestamp when the object version was saved to the database
	 */
	DateTime dt_insert;
	/**
	 * Timestamp when the object version was invalidated, i.e. a new version has
	 * become current If the object is still valid the value is equal to MAX_DATE
	 */
	DateTime dt_delete = MAX_DATE;
	/**
	 * Object Id of the parent
	 */
	Long parent_id = 0L;
	/**
	 * Object Id of the type of object (as per repo_tables)
	 */
	Long object_type = 0L;
	/**
	 * Status of the object. Constrained by the
	 */
	String status = svCONST.STATUS_VALID;
	/**
	 * Object id of the user who updated the object
	 */
	Long user_id = 0L;
	/**
	 * Hashmap containing all meta data about the object
	 */
	LinkedHashMap<SvCharId, Object> values;

	boolean isReadOnly = false;
	/**
	 * Flag to mark the object as dirty when cached
	 */
	private boolean is_dirty = true;

	/**
	 * Flag to mark if the object is of geometry type or not
	 */
	private boolean isGeometryType = false;
	/**
	 * Flag to mark if the object has the geometry fields loaded or not
	 */
	private boolean hasGeometry = false;

	/**
	 * Default constructor
	 */
	public DbDataObject() {
		values = new LinkedHashMap<SvCharId, Object>();
	}

	public DbDataObject(Long objectType, LinkedHashMap<SvCharId, Object> keyMap) {
		this.object_type = objectType;
		values = new LinkedHashMap<SvCharId, Object>(keyMap);
		// values.values().clear();
	}

	public DbDataObject(Long objectType) {
		this();
		this.object_type = objectType;
	}

	public DbDataObject(String objectTypeName) throws Exception {
		throw (new Exception("Invalid constructor"));
	}

	@Override
	public Boolean fromJson(JsonObject obj) {
		if (!isReadOnly) {
			is_dirty = true;
			return jsonIO.setMembersFromJson("", this, obj);
		}
		return false;
	}

	
	public boolean hasVal(String key) {
		return values.containsKey(key) && values.get(key) != null;

	}

	public Object getRepoVal(String key) {
		return getRepoVal((byte) svCONST.repoFieldNames.indexOf(key));

	}

	public Object getRepoVal(byte type) {
		Object retval = null;
		switch (type) {
		case SvCharId.OBJECT_ID:
			retval = object_id;
			break;
		case SvCharId.OBJECT_TYPE:
			retval = object_type;
			break;
		case SvCharId.PARENT_ID:
			retval = parent_id;
			break;
		case SvCharId.PKID:
			retval = pkid;
			break;
		case SvCharId.DT_INSERT:
			retval = dt_insert;
			break;
		case SvCharId.DT_DELETE:
			retval = dt_delete;
			break;
		case SvCharId.STATUS:
			retval = status;
			break;
		case SvCharId.USER_ID:
			retval = user_id;
			break;
		default:
			retval = null;
			break;
		}
		return retval;
	}

	public Object getVal(String key, boolean includeRepoFields) {
		return getVal(new SvCharId(key), includeRepoFields);
	}

	public Object getVal(SvCharId svKey) {
		return values.get(svKey);
	}

	public Object getVal(String key) {
		SvCharId svKey = new SvCharId(key);
		return values.get(svKey);
	}

	public void setVal(String key, Object obj) {
		if (!isReadOnly) {
			is_dirty = true;
			SvCharId svKey = new SvCharId(key);
			values.put(svKey, obj);
		}
	}

	public void setVal(SvCharId key, Object obj) {
		if (!isReadOnly) {
			is_dirty = true;
			values.put(key, obj);
		}
	}

	public JsonIO getMembersJson() {
		return jsonIO;
	}

	public Set<SvCharId> getMapKeys() {
		return values.keySet();
	}

	public void setMapKeys(Collection<SvCharId> keys) {
		values.keySet().addAll(keys);
	}

	@Deprecated
	public Long getObject_id() {
		return getObjectId();
	}

	@Deprecated
	public void setObject_id(Long object_id) {
		setObjectId(object_id);
	}

	/**
	 * Method to return the unique object id in the Svarog system. If the object has
	 * not been persisted to the database, the ID shall be 0
	 * 
	 * @return The unique object ID in the database
	 */
	public Long getObjectId() {
		return object_id;
	}

	/**
	 * Method to set the unique object id in the Svarog system.
	 * 
	 * @return The unique object ID in the database
	 */
	public void setObjectId(Long objectId) {
		if (!isReadOnly) {
			is_dirty = true;
			this.object_id = objectId;
		}
	}

	/**
	 * Method to replace the object value map with a new one
	 * 
	 * @param newValues The map containing the new values
	 */
	public void setValuesMap(LinkedHashMap<SvCharId, Object> newValues) {
		this.values = newValues;
	}

	/**
	 * Method returning a copy of the key map of values
	 */
	public LinkedHashMap<SvCharId, Object> getValuesMap() {
		@SuppressWarnings("unchecked")
		LinkedHashMap<SvCharId, Object> copy = (LinkedHashMap<SvCharId, Object>) values.clone();
		return copy;
	}

	@Deprecated
	public Set<Map.Entry<String, Object>> getValues() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(values.size());
		for (Map.Entry<SvCharId, Object> entry : values.entrySet())
			map.put(entry.getKey().toString(), entry.getValue());
		return map.entrySet();
	}

	@Deprecated
	public void setValues(LinkedHashMap<String, Object> values) {
		if (!isReadOnly) {
			is_dirty = true;
			this.values.clear();
			for (Map.Entry<String, Object> entry : values.entrySet()) {
				this.values.put(SvCharId.toSvCharId(entry.getKey()), entry.getValue());

			}
		}
	}

	/**
	 * Method which returns the current version id of object. If the object is
	 * persistent the version shall be 0
	 * 
	 * @return The version id
	 */
	public Long getPkid() {
		return pkid;
	}

	/**
	 * Method to set the current version id of object.
	 * 
	 */
	public void setPkid(Long pkid) {
		if (!isReadOnly) {
			is_dirty = true;
			this.pkid = pkid;
		}
	}

	/**
	 * Timestamp when the current version of the object has been persisted
	 * 
	 * @return Timestamp of DB persistence
	 */
	@Deprecated
	public DateTime getDt_insert() {
		return getDtInsert();
	}

	/**
	 * Timestamp when the current version of the object has been persisted
	 * 
	 */

	@Deprecated
	public void setDt_insert(DateTime dt_insert) {
		setDtInsert(dt_insert);
	}

	/**
	 * Timestamp when the current version of the object has been persisted
	 * 
	 * @return Timestamp of DB persistence
	 */
	public DateTime getDtInsert() {
		return dt_insert;
	}

	/**
	 * Timestamp when the current version of the object has been persisted
	 * 
	 * @param dtInsert The timestamp value to be set
	 * 
	 */

	public void setDtInsert(DateTime dtInsert) {
		if (!isReadOnly) {
			is_dirty = true;
			this.dt_insert = dtInsert;
		}
	}

	/**
	 * Timestamp when the current version of the object has been deleted
	 * 
	 */
	@Deprecated
	public DateTime getDt_delete() {
		return getDtDelete();
	}

	/**
	 * Method to set the timestamp when the current version of the object has been
	 * deleted
	 * 
	 * @param dtDelete The timestamp value to be set
	 */
	@Deprecated
	public void setDt_delete(DateTime dt_delete) {
		setDtDelete(dt_delete);
	}

	/**
	 * Timestamp when the current version of the object has been deleted
	 * 
	 */
	public DateTime getDtDelete() {
		return dt_delete;
	}

	/**
	 * Method to set the timestamp when the current version of the object has been
	 * deleted
	 * 
	 * @param dtDelete The timestamp value to be set
	 */
	public void setDtDelete(DateTime dtDelete) {
		if (!isReadOnly) {
			is_dirty = true;
			this.dt_delete = dtDelete;
		}
	}

	@Deprecated
	public Long getParent_id() {
		return getParentId();
	}

	@Deprecated
	public void setParent_id(Long parent_id) {
		setParentId(parent_id);
	}

	/**
	 * Method to get the object id of the parent object. The relation is reflecting
	 * the parent child relationship between the types.
	 * 
	 * @return Object id of the parent object
	 */
	public Long getParentId() {
		return parent_id;
	}

	/**
	 * Set the object id of another object as parent.
	 * 
	 * @param parentId The object id of the parent
	 */
	public void setParentId(Long parentId) {
		if (!isReadOnly) {
			is_dirty = true;
			this.parent_id = parentId;
		}
	}

	@Deprecated
	public Long getObject_type() {
		return getObjectType();
	}

	@Deprecated
	public void setObject_type(Long object_type) {
		setObjectType(object_type);
	}

	/**
	 * Return the object id of the object type
	 * 
	 * @return Object id of the type
	 */
	public Long getObjectType() {
		return object_type;
	}

	/**
	 * Set the type id of the object. The type id corresponds to the object id of
	 * the table
	 * 
	 * @param objectType object id of the type
	 */
	public void setObjectType(Long objectType) {
		if (!isReadOnly) {
			is_dirty = true;
			this.object_type = objectType;
		}
	}

	/**
	 * Method to return the current status of the object
	 * 
	 * @return The string value of the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Method to set the status of the object
	 * 
	 * @param status String status of the object
	 */
	public void setStatus(String status) {
		if (!isReadOnly) {
			is_dirty = true;
			this.status = status;
		}
	}

	@Deprecated
	public Long getUser_id() {
		return getUserId();
	}

	@Deprecated
	public void setUser_id(Long user_id) {
		setUserId(user_id);
	}

	/**
	 * Method to return the object id of the user which saved the object
	 * 
	 * @return
	 */
	public Long getUserId() {
		return user_id;
	}

	/**
	 * Method to set the object id of the user which saved the last version of the
	 * object
	 * 
	 * @param userId The object id of the user object
	 */
	public void setUserId(Long userId) {
		if (!isReadOnly) {
			is_dirty = true;
			this.user_id = userId;
		}
	}

	@Deprecated
	public boolean getIs_dirty() {
		return getIsDirty();
	}

	@Deprecated
	public void setIs_dirty(boolean is_dirty) {
		setIsDirty(is_dirty);
	}

	/**
	 * Method to check if the object has been modified after it was fetched from the
	 * database
	 * 
	 * @return Flag if the object was changed or not
	 */
	public boolean getIsDirty() {
		return is_dirty;
	}

	/**
	 * Method to set the dirty flag (object has been modified after it was fetched
	 * from the database)
	 * 
	 * @param isDirty Flag if the object was changed or not
	 */
	public void setIsDirty(boolean isDirty) {
		if (!isReadOnly) {
			this.is_dirty = isDirty;
		}
	}

	/**
	 * Return a flag is the object is read-only
	 * 
	 * @return Flag if the object is read-only
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * Return a flag is the object type is geometry type
	 * 
	 * @return Flag if the object is geometry type
	 */
	public boolean isGeometryType() {
		return isGeometryType;
	}

	/**
	 * Return a flag is the object type is geometry type
	 * 
	 * @return Flag if the object is geometry type
	 * @deprecated
	 */
	@Deprecated
	public void setGeometryType(boolean isGeometryType) {
		this.isGeometryType = isGeometryType;
		;
	}

	/**
	 * Set flag that the object has loaded the geometry from the database
	 * 
	 */
	public boolean getHasGeometry() {
		return hasGeometry;
	}

	/**
	 * Set flag that the object has loaded the geometry from the database
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void setHasGeometry(boolean hasGeometry) {
		this.hasGeometry = hasGeometry;
	}

	/**
	 * Method that return least value (skipping null values) of target column for
	 * each DbDataObject
	 * 
	 * @param targetColumn[] Column names for checking least value in
	 * @return Object
	 */
	public Double leastWithNullSkip(String[] targetColumn) {
		Double min = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0 && this.getVal(currColumnName) != null) {
				String val = this.getVal(currColumnName).toString();
				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		min = (Double) arrList.get(0);

		return min;
	}

	/**
	 * Method that return least value (with nvl if null) of target column for each
	 * DbDataObject
	 * 
	 * @param targetColumn[] Column names for checking least value in
	 * @return Object
	 */
	public Double leastWithNvl(String[] targetColumn) {
		Double min = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0) {
				String val;
				if (this.getVal(currColumnName) != null) {
					val = this.getVal(currColumnName).toString();
				} else {
					val = "0";
				}

				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		min = (Double) arrList.get(0);

		return min;
	}

	/**
	 * Method that return least value (if there is a null value, returns null) of
	 * target column for each DbDataObject
	 * 
	 * @param targetColumn[] Column names for checking least value in
	 * @return Object
	 */
	public Double least(String[] targetColumn) {
		Double min = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0) {
				String val;
				if (this.getVal(currColumnName) != null) {
					val = this.getVal(currColumnName).toString();
				} else {
					return null;
				}

				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		min = (Double) arrList.get(0);

		return min;
	}

	/**
	 * Method that return greatest value (skipping null values) of target column for
	 * each DbDataObject
	 * 
	 * @param targetColumn[] Column names for checking least value in
	 * @return Object
	 */
	public Double greatestWithNullSkip(String[] targetColumn) {
		Double max = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0 && this.getVal(currColumnName) != null) {
				String val = this.getVal(currColumnName).toString();
				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		max = (Double) arrList.get(arrList.size() - 1);

		return max;
	}

	/**
	 * Method that return greatest value (with nvl if null) of target column for
	 * each DbDataObject
	 * 
	 * @param targetColumn[] Column name for checking greatest value
	 * @return Object
	 */
	public Double greatestWithNvl(String[] targetColumn) {
		Double max = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0) {
				String val;
				if (this.getVal(currColumnName) != null) {
					val = this.getVal(currColumnName).toString();
				} else {
					val = "0";
				}

				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		max = (Double) arrList.get(arrList.size() - 1);

		return max;
	}

	/**
	 * Method that return greatest value (if there is a null value, returns null) of
	 * target column for each DbDataObject
	 * 
	 * @param targetColumn[] Column name for checking greatest value
	 * @return Object
	 */
	public Double greatest(String[] targetColumn) {
		Double min = null;
		ArrayList<Double> arrList = new ArrayList<>();

		if (targetColumn == null || targetColumn.length == 0) {
			return null;
		}

		for (int i = 0; i < targetColumn.length; i++) {
			String currColumnName = targetColumn[i];
			if (currColumnName.trim().length() > 0) {
				String val;
				if (this.getVal(currColumnName) != null) {
					val = this.getVal(currColumnName).toString();
				} else {
					return null;
				}

				arrList.add(Double.valueOf(val));
			}
		}

		Collections.sort(arrList);
		min = (Double) arrList.get(arrList.size() - 1);

		return min;
	}

	public Object getVal(SvCharId key, boolean includeRepoFields) {
		Object retval = null;
		if (includeRepoFields && key.type >= 0) {
			retval = getRepoVal(key.type);
		} else
			retval = getVal(key);

		return retval;
	}

}
