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

import java.util.ResourceBundle;

public class DbDataField extends Jsonable {

	private static ResourceBundle sqlKWResource = null;

	static String sysGeometrySrid;
	
	public static void initSrid(String srid) {
		sysGeometrySrid = srid;
	}
	
	public enum DbFieldType {
		NVARCHAR, BOOLEAN, NUMERIC, TIMESTAMP, DATE, TIME, BLOB, TEXT, GEOMETRY, UNDEFINED
	};

	Long object_id;
	String field_name;
	DbFieldType field_type;
	// in case of a numeric field, dbFieldLength is considered as precision
	Integer field_size = 0;
	Integer field_scale;
	String sequence_name;
	Boolean is_null = true;
	Boolean is_unique = false;
	Boolean is_primary_key = false;
	String index_name;
	String unique_constraint_name;
	String unique_level = "NOT_UNIQUE";
	String referentialTable;
	String refereftialField;

	// to implement
	String label_code;
	String default_expression;
	String gui_editor_type;
	String gui_editor_params;
	String defaultClause;
	String code_list_user_code;
	Long code_list_id;
	String gui_metadata;
	Boolean is_updateable = true;
	Integer sort_order;

	String geometryType;
	String geometrySrid;

	public DbDataField(ResourceBundle sqlKWResource) {
		DbDataField.sqlKWResource = sqlKWResource;
	}

	public DbDataField() {
	}

	/*
	 * public Class<?> getJavaType() { switch (field_type) { case BOOLEAN:
	 * return Boolean.class; case NUMERIC: return BigDecimal.class; case
	 * TIMESTAMP: return DateTime.class; case NVARCHAR: return String.class;
	 * 
	 * } return null; }
	 */
	/*
	 * public int getJdbcType() { switch (field_type) { case BOOLEAN: return
	 * java.sql.Types.BOOLEAN; case NUMERIC: return java.sql.Types.NUMERIC; case
	 * TIMESTAMP: return java.sql.Types.TIMESTAMP; case NVARCHAR: return
	 * java.sql.Types.NVARCHAR;
	 * 
	 * } return 0; }
	 */

	public String getSQLString() throws Exception {

		String sqlType = "";
		if (field_type != null)
			sqlType = sqlKWResource.getString(field_type.toString());
		else
			throw (new Exception("Field " + field_name + " doesn't have a field type!!!!"));
		String sizeSuffix = (field_size != null && field_size != 0 ? field_size.toString() : null);

		if (sizeSuffix != null && field_type != DbFieldType.BOOLEAN && field_type != DbFieldType.BLOB) {
			if (field_scale != null && field_scale != 0)
				sizeSuffix = sizeSuffix + "," + field_scale.toString();
			sizeSuffix = "(" + sizeSuffix + ")";
		} else
			sizeSuffix = "";

		String sqlNullable = (is_null != null && !is_null ? sqlKWResource.getString("NOT_NULL") : "");

		sqlType = sqlType.replace("{SIZE}", sizeSuffix);
		if (this.field_type.equals(DbFieldType.GEOMETRY)) {
			sqlType = sqlType.replace("{GEOMETRY_TYPE}", geometryType);
			sqlType = sqlType.replace("{SRID}", sysGeometrySrid);
		}

		return sqlKWResource.getString("OBJECT_QUALIFIER_LEFT") + field_name
				+ sqlKWResource.getString("OBJECT_QUALIFIER_RIGHT") + " " + sqlType + " " + sqlNullable;

	}

	public Long getObjectId() {
		return object_id;
	}

	public void setObjectId(Long objectId) {
		this.object_id = objectId;
	}

	public String getDbFieldName() {
		return field_name;
	}

	public void setDbFieldName(String dbFieldName) {
		this.field_name = dbFieldName.toUpperCase();
	}

	public DbFieldType getDbFieldType() {
		return field_type;
	}

	public void setDbFieldType(DbFieldType dbFieldType) {
		this.field_type = dbFieldType;
	}

	public void setDbFieldType(String dbFieldType) {
		this.field_type = DbFieldType.valueOf(dbFieldType);
	}

	public Integer getDbFieldSize() {
		return field_size;
	}

	public void setDbFieldSize(Integer dbFieldSize) {
		this.field_size = dbFieldSize;
	}

	public String getLabelCode() {
		return label_code;
	}

	public void setLabelCode(String labelCode) {
		this.label_code = labelCode;
	}

	public String getDbDefaultExpression() {
		return default_expression;
	}

	public void setDbDefaultExpression(String dbDefaultExpression) {
		this.default_expression = dbDefaultExpression;
	}

	public String getGuiEditorType() {
		return gui_editor_type;
	}

	public void setGuiEditorType(String guiEditorType) {
		this.gui_editor_type = guiEditorType;
	}

	public String getGuiEditorExt() {
		return gui_editor_params;
	}

	public void setGuiEditorExt(String guiEditorExt) {
		this.gui_editor_params = guiEditorExt;
	}

	public String getDbSequenceName() {
		return sequence_name;
	}

	public void setDbSequenceName(String dbSequenceName) {
		this.sequence_name = dbSequenceName;
	}

	public Boolean getIsNull() {
		return is_null;
	}

	public void setIsNull(Boolean isNull) {
		this.is_null = isNull;
	}

	public String getDefaultClause() {
		return defaultClause;
	}

	public void setDefaultClause(String defaultClause) {
		this.defaultClause = defaultClause;
	}

	public Integer getDbFieldScale() {
		return field_scale;
	}

	public void setDbFieldScale(Integer dbFieldScale) {
		this.field_scale = dbFieldScale;
	}

	public Boolean getIsUnique() {
		return is_unique;
	}

	public void setIsUnique(Boolean isUnique) {
		if (isUnique && unique_level.equals("NOT_UNIQUE"))
			unique_level = "TABLE";
		this.is_unique = isUnique;
	}

	public Boolean getIsPrimaryKey() {
		return is_primary_key;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.is_primary_key = isPrimaryKey;
	}

	public String getIndexName() {
		return index_name;
	}

	public void setIndexName(String indexName) {
		this.index_name = indexName;
	}

	public String getLabel_code() {
		return label_code;
	}

	public void setLabel_code(String label_code) {
		this.label_code = label_code;
	}

	public String getUnique_constraint_name() {
		return unique_constraint_name;
	}

	public void setUnique_constraint_name(String unique_constraint_name) {
		this.unique_constraint_name = unique_constraint_name;
	}

	public String getCode_user_code() {
		return this.code_list_user_code;
	}

	public void setCode_list_user_code(String code_list_user_code) {
		this.code_list_user_code = code_list_user_code;
	}

	public String getUnique_level() {
		return unique_level;
	}

	public void setUnique_level(String unique_level) {
		this.unique_level = unique_level;
	}

	public Long getCode_list_id() {
		return code_list_id;
	}

	public void setCode_list_id(Long code_list_id) {
		this.code_list_id = code_list_id;
	}

	public String getGui_metadata() {
		return gui_metadata;
	}

	public void setGui_metadata(String gui_metadata) {
		this.gui_metadata = gui_metadata;
	}

	public Boolean getIs_updateable() {
		return is_updateable;
	}

	public void setIs_updateable(Boolean is_updateable) {
		this.is_updateable = is_updateable;
	}

	public Integer getSort_order() {
		return sort_order;
	}

	public void setSort_order(Integer sort_order) {
		this.sort_order = sort_order;
	}

	public static ResourceBundle getSqlKWResource() {
		return sqlKWResource;
	}

	public static void setSqlKWResource(ResourceBundle sqlKWResource) {
		DbDataField.sqlKWResource = sqlKWResource;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}

	public String getGeometrySrid() {
		return geometrySrid;
	}

	public void setGeometrySrid(String geometrySrid) {
		this.geometrySrid = geometrySrid;
	}
	
	public void setReferentialTable(String referentialTable) {
		this.referentialTable = referentialTable;
	}
	
	public String getReferentialTable() {
		return referentialTable;
	}
	
	public void setRefereftialField(String refereftialField) {
		this.refereftialField = refereftialField;
	}
	
	public String getRefereftialField() {
		return refereftialField;
	}
}
