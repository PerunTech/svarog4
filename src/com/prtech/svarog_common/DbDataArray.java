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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.prtech.svarog.SvException;
import com.prtech.svarog.svCONST;

/**
 * Class holding a set of DbDataObjects. Includes methods for filtering,
 * indexing, etc.
 * 
 * @author XPS13
 *
 */
public class DbDataArray extends Jsonable {
	/**
	 * String variable holding the name of the field according to which the array
	 * will be indexed by using {@link #rebuildIndex(String)} or
	 * {@link #rebuildIndex(String, Boolean)}
	 */
	SvCharId indexField = null;

	/**
	 * Filter class providing means for filtering the set of objects
	 */
	IDbFilter filter = null;

	/**
	 * The list of DbDataObjects in the set
	 */
	ArrayList<DbDataObject> items = new ArrayList<DbDataObject>();

	/**
	 * The index list populated by using {@link #rebuildIndex(String)} or
	 * {@link #rebuildIndex(String, Boolean)}
	 */
	HashMap<String, DbDataObject> idxItems = new HashMap<String, DbDataObject>();

	/**
	 * Default empty constructor
	 */
	public DbDataArray() {
	}

	/**
	 * Default empty constructor
	 */
	public DbDataArray(List<DbDataObject> dba) {
		items = new ArrayList<>(dba);
	}

	/**
	 * Overloaded method to sort by specific field name. This version includes
	 * indexing including parent ID.
	 * 
	 * @param idxField The field to be used for indexing
	 */
	public void rebuildIndex(String idxField) {
		rebuildIndex(idxField, false);
	}

	/**
	 * The method to rebuild the index by allowing inclusion of the parent Id too.
	 * 
	 * @param idxField        The field to be used for indexing
	 * @param excludeParentId Flag to exclude the parent Id if required
	 */
	public void rebuildIndex(String idxField, Boolean excludeParentId) {
		indexField = new SvCharId(idxField);
		String idxKey = null;
		for (DbDataObject obj : items) {
			if (obj.getVal(indexField, true) != null) {
				idxKey = (excludeParentId ? "" : obj.getParentId().toString())
						+ obj.getVal(indexField, true).toString().toUpperCase();
				idxItems.put(idxKey, obj);
			}
		}
	}

	public DbDataArray applyFilter(IDbFilter filter) {
		DbDataArray retVal = new DbDataArray();

		for (DbDataObject obj : items) {
			if (filter.filterObject(obj))
				retVal.addDataItem(obj);
		}
		return retVal;
	}

	/**
	 * Fetch an item by key, field name including a parent id
	 * 
	 * @param key      The key according to which the object should be fetched from
	 *                 the index
	 * @param parentId The parentId of the object
	 * @return The resulting DbDataObject if found
	 */
	public DbDataObject getItemByIdx(String key, Long parentId) {
		return idxItems.get(parentId.toString() + key.toUpperCase());
	}

	/**
	 * Fetch an item by key, field name without parent ID
	 * 
	 * @param key The key according to which the object should be fetched from the
	 *            index
	 * @return The resulting DbDataObject if found
	 */
	public DbDataObject getItemByIdx(String key) {
		return idxItems.get(key.toUpperCase());
	}

	/**
	 * Method for adding a DbDataObject to the collection
	 * 
	 * @param obj The object to be added
	 */
	public void addDataItem(DbDataObject obj) {
		items.add(obj);
		if (indexField != null) {
			try {
				idxItems.put((String) obj.getVal(indexField), obj);
			} catch (Exception ex) {
			}
		}
		// return this;
	}

	public ArrayList<DbDataObject> getItems() {
		return items;
	}

	public void setItems(ArrayList<DbDataObject> items) {
		this.items = items;
	}

	public int size() {
		return items.size();
	}

	public Boolean isEmpty() {
		return items.isEmpty();
	}

	
	public DbDataObject get(int index) {
		return items.get(index);
	}

	public void set(int index, DbDataObject element) {
		this.items.set(index, element);
	}

	public DbDataArray(String idxField) {
		if (idxField != null)
			indexField = new SvCharId(idxField);
	}

	public ArrayList<DbDataObject> getSortedItems(final String fieldName) {
		return getSortedItems(fieldName, false);
	}

	public ArrayList<DbDataObject> getSortedItems(final String fieldName, final boolean includeRepoFields) {
		Collections.sort(items, new Comparator<DbDataObject>() {
			public int compare(DbDataObject o1, DbDataObject o2) {
				if (o1.getVal(fieldName, includeRepoFields) != null && o2.getVal(fieldName, includeRepoFields) != null)
					return compareTo(o1.getVal(fieldName, includeRepoFields), o2.getVal(fieldName, includeRepoFields));
				else if (o1.getVal(fieldName, includeRepoFields) == null
						&& o2.getVal(fieldName, includeRepoFields) == null)
					return 0;
				else if (o1.getVal(fieldName, includeRepoFields) == null)
					return -1;
				else
					return 1;
			}
		});
		return items;
	}

	/**
	 * Method for sorting the list of DbDataObjects. It only allows sorting
	 * according to metadata
	 * 
	 * @param val  The first value to compare
	 * @param val2 The second value to compare
	 * @return Result if the first value is greater than the second
	 */
	protected int compareTo(Object val, Object val2) {
		if (val.getClass().equals(String.class))
			return ((String) val).compareTo((String) val2);
		if (val.getClass().equals(Integer.class))
			return ((Integer) val).compareTo((Integer) val2);
		if (val.getClass().equals(Long.class))
			return ((Long) val).compareTo((Long) val2);
		return 0;
	}

	/**
	 * To be checked if the jsoN string of first object in the DbDataArray (it's
	 * considered that each object in the Array belongs to same object type)
	 * contains (all) the in: fields
	 * 
	 * @param fields Array of fields name
	 * @return Boolean
	 */

	private Boolean checkIfFieldExists(String[] fields) {
		Boolean result = false;
		if (items.size() > 0) {
			result = true;
			DbDataObject tempObj = items.get(0);
			for (String fieldName : fields) {
				if (!tempObj.toJson().toString().toLowerCase().contains(fieldName.toLowerCase())) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Method that grouping DbDataObject by key according value on columns in the
	 * array (if groupColumn is null that all DbDataObject belong in one group).
	 * 
	 * @param groupColumn Array of column name
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value ( {@link DbDataArray} of {@link DbDataObject} which are from
	 *         the same group)
	 */

	public HashMap<String, DbDataArray> groupItemsByColumn(String[] groupColumn) {
		HashMap<String, DbDataArray> arrGroupItems = new HashMap<>();

		String key = "";
		DbDataArray tmpArr = null;
		if (groupColumn != null && groupColumn.length > 0) {
			if (!checkIfFieldExists(groupColumn)) {
				return arrGroupItems;
			}
		} else {
			key = "allItems";
		}

		for (DbDataObject obj : items) {

			if (!key.equals("allItems")) {
				key = "";
				for (String str : groupColumn) {
					if (svCONST.repoFieldNames.indexOf(str) >= 0) {
						key += "|" + (obj.getRepoVal(str) != null ? obj.getRepoVal(str).toString() : "null");
					} else {
						key += "|" + (obj.getVal(str) != null ? obj.getVal(str).toString() : "null");
					}
				}
				key = key.substring(1);
			}
			tmpArr = arrGroupItems.get(key) != null ? arrGroupItems.get(key) : new DbDataArray();
			tmpArr.addDataItem(obj);
			arrGroupItems.put(key, tmpArr);

		}

		return arrGroupItems;
	}

	/**
	 * Method that sum the value of the target column for DbDataObjects.
	 * 
	 * @param columnName Column name by which the values are summarized
	 * @return BigDecimal
	 */
	public BigDecimal sum(String columnName) {
		BigDecimal sum = new BigDecimal("0");
		BigDecimal val = null;

		for (DbDataObject obj : items) {
			val = obj.getVal(columnName) != null ? new BigDecimal(obj.getVal(columnName).toString()) : null;

			if (val == null) {
				continue;
			}
			sum = sum.add(val);
		}
		return sum;
	}

	/**
	 * Method that sum the value of the target column for DbDataObjects.
	 * 
	 * @param hOperation   Type of horizontal operation to get value of target
	 *                     columns for sum
	 * 
	 * @param targetColumn Array of column name for sum
	 * @param groupColumn  Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value ( sum value for the same group)
	 */
	public HashMap<String, Double> sum(String hOperation, String[] targetColumn, String[] groupColumn) {
		HashMap<String, Double> result = new HashMap<>();
		Double sum = null;
		Double val = null;
		Boolean isHOperation = false;

		if (hOperation == null && targetColumn.length > 1) {
			return null;
		}
		if (hOperation != null && targetColumn.length > 1) {
			isHOperation = true;
		}

		HashMap<String, DbDataArray> arrGroupItems = groupItemsByColumn(groupColumn);

		for (Entry<String, DbDataArray> entry : arrGroupItems.entrySet()) {
			for (DbDataObject obj : entry.getValue().getItems()) {
				sum = result.get(entry.getKey());
				if (isHOperation) {
					if (hOperation.equals("MIN")) {
						val = obj.leastWithNvl(targetColumn);
					} else if (hOperation.equals("MAX")) {
						val = obj.greatestWithNvl(targetColumn);
					} else {
						return result;
					}
				} else {
					val = obj.getVal(targetColumn[0]) != null ? Double.valueOf(obj.getVal(targetColumn[0]).toString())
							: new Double(0);
				}
				result.put(entry.getKey(), sum != null ? sum + val : val);
			}

		}
		return result;
	}

	/**
	 * Method that count items for each group of DbDataObjects
	 * 
	 * @param groupColumn Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value (number on items of the same group)
	 */
	public HashMap<String, Integer> count(String[] groupColumn) {
		HashMap<String, Integer> result = new HashMap<>();
		HashMap<String, DbDataArray> arrGroupItems = groupItemsByColumn(groupColumn);

		for (Entry<String, DbDataArray> entry : arrGroupItems.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getItems().size());
		}

		return result;
	}

	/**
	 * Method that count items for each group of DbDataObjects, that accomplishes
	 * certain condition(s)
	 * 
	 * @param targetColumn    Array of column name for condition
	 * @param columnValue     Array of value for each target column to compare
	 * @param logicalOperator Logical operator (AND/OR)
	 * @param groupColumn     Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value (number on items of the same group)
	 */
	public HashMap<String, Integer> countIf(String[] targetColumn, String[] columnValue, String logicalOperator,
			String[] groupColumn) {
		HashMap<String, Integer> result = new HashMap<>();
		Integer count = 0;
		Boolean conditionAccomplished = false;
		if (targetColumn == null || columnValue == null) {
			return result;
		}
		if (targetColumn.length != columnValue.length) {
			return result;
		}
		if (targetColumn.length > 1 && logicalOperator == null) {
			return result;
		}
		if (targetColumn.length == 1 && logicalOperator == null) {
			logicalOperator = "AND";
		}
		HashMap<String, DbDataArray> arrGroupItems = groupItemsByColumn(groupColumn);

		for (Entry<String, DbDataArray> entry : arrGroupItems.entrySet()) {
			count = 0;
			conditionAccomplished = false;
			for (DbDataObject obj : entry.getValue().getItems()) {
				for (int i = 0; i < targetColumn.length; i++) {
					if (logicalOperator.equals("AND")) {
						conditionAccomplished = obj.getVal(targetColumn[i]) != null
								? obj.getVal(targetColumn[i]).toString().equals(columnValue[i])
								: false;

						if (!conditionAccomplished) {
							break;
						}
					} else if (logicalOperator.equals("OR")) {
						conditionAccomplished = obj.getVal(targetColumn[i]) != null
								? obj.getVal(targetColumn[i]).toString().equals(columnValue[i])
								: false;

						if (conditionAccomplished) {
							break;
						}
					}
				}
				if (conditionAccomplished) {
					count++;
				}

			}
			result.put(entry.getKey(), count);
		}

		return result;
	}

	/**
	 * Method that return minimum value of target column for each group of
	 * DbDataObjects
	 * 
	 * @param targetColumn Column name for checking minimum value
	 * @param groupColumn  Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value (minimum)
	 */
	public HashMap<String, Double> least(String targetColumn, String[] groupColumn) {
		HashMap<String, Double> result = new HashMap<>();
		Double min = null;
		ArrayList<Double> arrList = null;
		HashMap<String, DbDataArray> arrGroupItems = groupItemsByColumn(groupColumn);

		for (Entry<String, DbDataArray> entry : arrGroupItems.entrySet()) {
			arrList = new ArrayList<>();
			for (DbDataObject obj : entry.getValue().getItems()) {

				min = obj.getVal(targetColumn) != null ? Double.valueOf(obj.getVal(targetColumn).toString())
						: new Double(0);
				arrList.add(min);

			}
			Collections.sort(arrList);

			min = (Double) arrList.get(0);

			result.put(entry.getKey(), min);
		}

		return result;
	}

	/**
	 * Method that return maximum value of target column for each group of
	 * DbDataObjects
	 * 
	 * @param targetColumn Column name for checking maximum value
	 * @param groupColumn  Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value (maximum)
	 */
	public HashMap<String, Double> greatest(String targetColumn, String[] groupColumn) {
		HashMap<String, Double> result = new HashMap<>();
		Double max = null;
		ArrayList<Double> arrList = null;
		HashMap<String, DbDataArray> arrGroupItems = groupItemsByColumn(groupColumn);

		for (Entry<String, DbDataArray> entry : arrGroupItems.entrySet()) {
			arrList = new ArrayList<>();
			for (DbDataObject obj : entry.getValue().getItems()) {

				max = obj.getVal(targetColumn) != null ? Double.valueOf(obj.getVal(targetColumn).toString())
						: new Double(0);
				arrList.add(max);

			}
			Collections.sort(arrList);

			max = (Double) arrList.get(arrList.size() - 1);

			result.put(entry.getKey(), max);
		}

		return result;
	}

	/**
	 * Method that return average value of target columns for each group of
	 * DbDataObjects
	 * 
	 * @param hOperation   Type of horizontal operation to get value of target
	 *                     columns for sum
	 * @param targetColumn Column name for sum than calculate average value
	 * @param groupColumn  Array of column name by group
	 * @return HashMap of couple key (string merged from values for the columns) and
	 *         value (average)
	 */
	public HashMap<String, Double> avg(String hOperation, String[] targetColumn, String[] groupColumn) {
		HashMap<String, Double> result = new HashMap<>();
		HashMap<String, Double> arrSumItems = sum(hOperation, targetColumn, groupColumn);
		HashMap<String, Integer> arrCountItems = count(groupColumn);

		for (Entry<String, Double> entry : arrSumItems.entrySet()) {
			result.put(entry.getKey(), entry.getValue() / arrCountItems.get(entry.getKey()));
		}

		return result;
	}

	/**
	 * Method for getting distinct values for some DbDataArray for appropriate
	 * columns
	 * 
	 * @param items   The data set for evaluation
	 * @param columns List of column/field names for which the distinct count should
	 *                be done
	 * @return HashMap<String, List<Object>> where key is the field name and value
	 *         is the list of the distinct-ed object by the appropriate key
	 * @throws SvException
	 */
	public Map<String, List<Object>> getDistinctValuesPerColumns(List<String> listOfColumns,
			DbDataArray fieldsPerObjectType) throws SvException {
		Map<String, List<Object>> result = new HashMap<String, List<Object>>();
		if (!items.isEmpty() && listOfColumns != null && listOfColumns.size() > 0) {
			// cross-check column validation according object type
			// DbDataArray fieldsPerObjectType =
			// svr.getObjectsByParentId(items.get(0).getObject_type(),
			// svCONST.OBJECT_TYPE_FIELD, null, 0, 0);
			if (fieldsPerObjectType.size() > 0) {
				for (DbDataObject tempField : fieldsPerObjectType.getItems()) {
					String fieldName = tempField.getVal("FIELD_NAME").toString();
					if (listOfColumns.contains(fieldName)) {
						result.put(fieldName, null);
					}
				}
			}
			if (listOfColumns.contains("STATUS")) {
				result.put("STATUS", null);
			}

			if (result.size() > 0) {
				for (DbDataObject tempDbo : items) {
					Iterator<Entry<String, List<Object>>> it = result.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, List<Object>> pair = it.next();
						String currColumnName = pair.getKey();
						List<Object> distinctValuesForKeyFound;
						if (pair.getValue() == null) {
							distinctValuesForKeyFound = new ArrayList<Object>();
						} else {
							distinctValuesForKeyFound = pair.getValue();
						}

						if (tempDbo.getVal(currColumnName) != null) {
							Object value = tempDbo.getVal(currColumnName);
							if (!distinctValuesForKeyFound.contains(value)) {
								distinctValuesForKeyFound.add(value);
							}
						} else if (currColumnName.equals("STATUS")) {
							Object value = tempDbo.getStatus();
							if (!distinctValuesForKeyFound.contains(value)) {
								distinctValuesForKeyFound.add(value);
							}
						}
						result.put(currColumnName, distinctValuesForKeyFound);
					}
				}
			}

		}
		return result;
	}

}