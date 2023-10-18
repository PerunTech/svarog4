package com.prtech.svarog_common;

import java.util.ArrayList;
import java.util.List;

/**
 * The DbDataTableExtension is class intended to provide means to extend an
 * existing object which is already in use by another bundle. This class allows
 * you to extend the object in order to fit your custom needs without need to
 * modify the original global bundle
 */
public class DbDataTableExtension extends DbDataTable {
	// Database configuration like schema, table, repo
	String baseTableName;
	String baseSchema;
	// DataFields holding the object metadata
	ArrayList<DbDataField> dbTableFields = new ArrayList<>();

	/**
	 * Method to specify a field which shall be added, or modified. If the field
	 * doesn't exist, the system will add it. If it exists, it will modify it.
	 * 
	 * @param dataField
	 */
	public void addDbDataField(DbDataField dataField) {
		this.dbTableFields.add(dataField);
	}

	/**
	 * Method which returns the list of fields which should be modified
	 * 
	 * @return
	 */
	public List<DbDataField> getDbDataFields() {
		return dbTableFields;
	}

	/**
	 * Getter of name of the base table which should be extended
	 * 
	 * @return
	 */
	public String getBaseTableName() {
		return baseTableName;
	}

	/**
	 * Setter of name of the base table which should be extended
	 * 
	 * @return
	 */
	public void setBaseTableName(String baseTableName) {
		this.baseTableName = baseTableName;
	}

	/**
	 * Getter of name of the schema of base table which should be extended
	 * 
	 * @return
	 */
	public String getBaseSchema() {
		return baseSchema;
	}

	/**
	 * Setter of name of the schema of base table which should be extended
	 * 
	 * @return
	 */
	public void setBaseSchema(String baseSchema) {
		this.baseSchema = baseSchema;
	}

}
