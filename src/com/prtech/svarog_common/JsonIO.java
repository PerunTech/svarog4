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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.prtech.svarog.svCONST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class JsonIO {
	static final Logger log4j = LogManager.getLogger(JsonIO.class.getName());

	/**
	 * Default constructor, sets the timezone to UTC
	 */
	public JsonIO() {
		// DateTimeZone.setDefault(DateTimeZone.UTC);
	}

	Object getBaseJsonPrimitive(JsonPrimitive jsonElement) {

		if (jsonElement.isNumber()) {
			Object obj = jsonElement.getAsBigDecimal();
			if (((BigDecimal) obj).scale() == 0)
				obj = jsonElement.getAsLong();

			return obj;
		} else if (jsonElement.isString()) {
			String val = jsonElement.getAsString();
			if (val.length() > 8 && val.substring(4, 5).equals("-") && val.substring(7, 8).equals("-")) {
				// possibly a date
				try {
					DateTime dt = new DateTime(val);
					return dt;
				} catch (Exception e) {
					return val;
				}
			}
			return val;
		} else if (jsonElement.isBoolean())
			return jsonElement.getAsBoolean();

		return null;

	}

	/**
	 * A function that returns the Java primitive type for a specific class from
	 * a JsonElement The function can convert: String,
	 * Boolean,Integer,Long,Double,Float,BigDecimal,BigInteger DateTime and Enum
	 * 
	 * @param clazz
	 *            The class type of the object which should be assigned
	 * @param jsonElement
	 *            The JsonElemen from which the value should be converted
	 * @return Object of the same type as clazz
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Object getPrimitiveValue(Class<?> clazz, JsonPrimitive jsonElement) throws ClassNotFoundException {
		if (jsonElement == null)
			return null;
		if (clazz.equals(String.class))
			return jsonElement.getAsString();
		else if (clazz.equals(Boolean.class))
			return jsonElement.getAsBoolean();
		else if (clazz.equals(Integer.class))
			return jsonElement.getAsInt();
		else if (clazz.equals(Long.class))
			return jsonElement.getAsLong();
		else if (clazz.equals(Double.class))
			return jsonElement.getAsDouble();
		else if (clazz.equals(Float.class))
			return jsonElement.getAsFloat();
		else if (clazz.equals(BigDecimal.class))
			return jsonElement.getAsBigDecimal();
		else if (clazz.equals(BigInteger.class))
			return jsonElement.getAsBigInteger();
		else if (clazz.equals(DateTime.class))
			return new DateTime(jsonElement.getAsString());
		else if (clazz.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>) Class.forName(clazz.getName()), jsonElement.getAsString());
		} else if (clazz.equals(Object.class)) {
			return getBaseJsonPrimitive(jsonElement);
		}
		return null;

	}

	// private static final Logger log4j = LogManager.getLogger(DbUtil.class
	// .getName());

	private JsonPrimitive getJsonPrimitive(Class<?> type, Object fieldValue, JsonObject exParams) {
		JsonPrimitive js = null;

		if (type.equals(String.class))
			js = new JsonPrimitive((String) fieldValue);
		else if (type.equals(Boolean.class))
			js = new JsonPrimitive((Boolean) fieldValue);
		else if (type.equals(Integer.class) || type.equals(Long.class) || type.equals(Double.class)
				|| type.equals(Float.class) || type.equals(BigDecimal.class) || type.equals(BigInteger.class))
			js = new JsonPrimitive((Number) fieldValue);
		else if (type.equals(DateTime.class)) {
			if (exParams == null)
				js = new JsonPrimitive((String) (fieldValue == null ? null : ((DateTime) fieldValue).toString()));
			else {
				if (exParams.get("field_type") != null) {
					String sFieldType = exParams.get("field_type").getAsString();
					if (sFieldType.equals("TIMESTAMP")) {
						if (exParams.get("dt_dateformat") == null || exParams.get("dt_dateformat") == null)
							js = new JsonPrimitive(
									(String) (fieldValue == null ? null : ((DateTime) fieldValue).toString()));
						else {
							js = new JsonPrimitive((String) (fieldValue == null ? null
									: (((DateTime) fieldValue).toString(exParams.get("dt_dateformat").getAsString()
											+ " " + exParams.get("dt_timeformat").getAsString()))));

						}
						// render full datetime
					} else if (sFieldType.equals("DATE")) {
						// render short datetime
						js = new JsonPrimitive((String) (fieldValue == null ? null
								: (((DateTime) fieldValue).toString(exParams.get("dt_dateformat").getAsString()))));

					} else if (sFieldType.equals("TIME")) {
						// render short datetime
						js = new JsonPrimitive((String) (fieldValue == null ? null
								: (((DateTime) fieldValue).toString(exParams.get("dt_timeformat").getAsString()))));

					} else
						js = new JsonPrimitive(
								(String) (fieldValue == null ? null : ((DateTime) fieldValue).toString()));
				} else
					js = new JsonPrimitive((String) (fieldValue == null ? null : ((DateTime) fieldValue).toString()));

			}
		} else if (fieldValue instanceof Enum)
			js = new JsonPrimitive((String) (fieldValue == null ? null : (String) fieldValue.toString()));
		return js;
	}

	private Boolean addPrimitiveProperty(JsonObject jsonObject, Class<?> type1, String fieldName, Object fieldValue,
			JsonObject exParams) {

		if (fieldValue == null) {
			jsonObject.add(fieldName, JsonNull.INSTANCE);
			return true;
		} else {
			Class<?> type = fieldValue.getClass();
			JsonPrimitive jsProperty = getJsonPrimitive(type, fieldValue, exParams);
			if (jsProperty != null)
				jsonObject.add(fieldName, jsProperty);
			else
				return false;
		}
		return true;
	}

	private Boolean addComplexObject(JsonObject jsonObject, String fieldName, Object fieldValue, JsonObject exParams,
			Boolean isSimple) {
		JsonObject innerMJson = null;
		if (fieldValue instanceof Jsonable) {
			innerMJson = getMembersJsonImpl("", fieldValue, exParams, isSimple);
			jsonObject.add(fieldName, innerMJson);
		} else {
			if (log4j.isDebugEnabled())
				log4j.trace("Object is not instanceof Jsonable. Fall back to .toString()");
			jsonObject.addProperty(fieldName, fieldValue.toString());
		}
		return true;
	}

	private Boolean addPrimitiveArrayItem(JsonArray jsonObject, Class<?> type, Object fieldValue, JsonObject exParams) {

		if (fieldValue == null) {
			jsonObject.add(JsonNull.INSTANCE);
			return true;
		} else {
			JsonPrimitive jsProperty = getJsonPrimitive(type, fieldValue, exParams);
			if (jsProperty != null)
				jsonObject.add(jsProperty);
			else
				return false;
		}
		return true;

	}

	private Boolean addComplexArrayItem(JsonArray jsonObject, String fieldName, Object fieldValue, JsonObject exParams,
			Boolean isSimple) {

		JsonObject innerMJson = null;
		if (fieldValue instanceof Jsonable) {
			innerMJson = getMembersJsonImpl("", fieldValue, exParams, isSimple);
			jsonObject.add(innerMJson);
		} else {
			if (log4j.isDebugEnabled())
				log4j.debug("Object is not instanceof Jsonable. Fall back to .toString()");
			jsonObject.add(getJsonPrimitive(String.class, fieldValue.toString(), exParams));
		}
		return true;

	}

	/**
	 * Function to get the JsonObject from an object via reflection. The
	 * function tries to get the predefined getter method for retrieving a
	 * MembersJson object from the class. If it can get the MembersJson object
	 * then tries to invoke the method "getMembersToJson" on the MembersJson
	 * object in order to get the JsonObject for the Object obj
	 * 
	 * @param obj
	 *            The Object from which the JsonObject should be retrieved
	 * @return The JsonObject for the obj parameter
	 */
	private JsonObject getMembersJsonImpl(String startsWith, Object obj, JsonObject exParams, Boolean isSimple) {
		if (obj == null)
			return null;
		JsonIO mJson = null;
		JsonObject jsonObject = null;
		try {

			Class currClass = obj.getClass();
			while (currClass != null && !currClass.getName().endsWith("Jsonable")) {
				currClass = currClass.getSuperclass();
			}
			Method method = currClass.getDeclaredMethod("getJsonIO", (Class<?>[]) null);

			mJson = (JsonIO) method.invoke(obj, (Object[]) null);

			if (mJson == null)
				return null;

			Class<?>[] cArg = new Class[4];
			cArg[0] = String.class;
			cArg[1] = Object.class;
			cArg[2] = JsonObject.class;
			cArg[3] = Boolean.class;

			Method jsMethod = mJson.getClass().getDeclaredMethod("getMembersToJson", cArg);
			jsonObject = (JsonObject) jsMethod.invoke(mJson, startsWith, obj, exParams, isSimple);

		} catch (Exception ex) {
			log4j.error("Member object of type " + obj.getClass().getCanonicalName() + " is not Json enabled!", ex);
		}
		return jsonObject;
	}

	/**
	 * Legacy override to return always the complex JSON
	 * 
	 * @param startsWith
	 *            A string prefix for each of the fields which we need in the
	 *            JSON
	 * @param obj
	 *            A object whose fields should be converted to JSON
	 * @return A JsonObject variable generated from the class
	 */

	public JsonObject getMembersToJson(String startsWith, Object obj, JsonObject exParams) {
		return getMembersToJson(startsWith, obj, exParams, false);
	}

	/**
	 * A function which iterates over the member fields of the Object obj
	 * searching for ones starting with String startsWith. Then the set of
	 * fields used for building a Json is used to populate a JsonObject. If you
	 * use an empty string as parameter startsWith, it will serialize all
	 * fields.
	 * 
	 * @param startsWith
	 *            A string prefix for each of the fields which we need in the
	 *            JSON
	 * @param obj
	 *            A object whose fields should be converted to JSON
	 * @return A JsonObject variable generated from the class
	 */
	public JsonObject getMembersToJson(String startsWith, Object obj, JsonObject exParams, Boolean isSimple) {
		JsonObject jsonObject = new JsonObject();
		JsonObject classObject = new JsonObject();
		// LinkedHashMap<String, Object> retMap = new LinkedHashMap<String,
		// Object>();
		Class<?> outerClass = obj.getClass();
		Field[] memberFields = outerClass.getDeclaredFields();

		for (int i = 0; i < memberFields.length; i++) {
			String fieldName = memberFields[i].getName();
			if (fieldName.equals("isReadOnly"))
				continue;

			if (!memberFields[i].getType().equals(this.getClass()) && fieldName.startsWith(startsWith)
					&& !Modifier.isFinal(memberFields[i].getModifiers())
					&& !Modifier.isPrivate(memberFields[i].getModifiers())
					&& !Modifier.isStatic(memberFields[i].getModifiers())) {
				Object fieldValue = null;
				try {
					memberFields[i].setAccessible(true);
					fieldValue = memberFields[i].get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// try to add the primitives to the JSON
				// if it fails, the variable is not primitive!
				if (!addPrimitiveProperty(jsonObject, memberFields[i].getType(), fieldName, fieldValue, exParams)) {

					// test if it is an array and iterate over it
					if (memberFields[i].getType().isArray()) {
						JsonArray arr = new JsonArray();
						if (fieldValue != null) {
							Object[] obar = (Object[]) fieldValue;
							for (int j = 0; j < obar.length; j++) {
								if (!addPrimitiveArrayItem(arr, memberFields[i].getType().getComponentType(), obar[j],
										null)) {
									addComplexArrayItem(arr, startsWith, obar[j], exParams, isSimple);
								}
							}
						}
						jsonObject.add(fieldName, arr);
						// log4j.info(fields[i].getType().getComponentType());
					} else if (fieldValue instanceof List<?>) {
						JsonArray arr = new JsonArray();
						if (fieldValue != null) {
							List<?> obar = (List<?>) fieldValue;
							for (int j = 0; j < obar.size(); j++) {
								if (!addPrimitiveArrayItem(arr, obar.get(j).getClass(), obar.get(j), null)) {
									addComplexArrayItem(arr, startsWith, obar.get(j), exParams, isSimple);
								}
							}
						}
						jsonObject.add(fieldName, arr);
						// log4j.info(fields[i].getType().getComponentType());
					} else if (fieldValue instanceof Map<?, ?>) {
						if (fieldValue != null) {
							JsonArray arr = new JsonArray();
							JsonObject jObj = null;
							for (Entry<?, ?> entry : ((Map<?, ?>) fieldValue).entrySet()) {
								if (entry.getValue() != null) {
									// if we need to get a simple JSON don't go
									// with the complex object route
									if (!isSimple) {
										jObj = new JsonObject();
										Boolean isPrimitive = addPrimitiveProperty(jObj, entry.getValue().getClass(),
												entry.getKey().toString(), entry.getValue(), null);
										if (!isPrimitive)
											addComplexObject(jObj, entry.getKey().toString(), entry.getValue(),
													exParams, isSimple);
										arr.add(jObj);
									} else {
										addPrimitiveProperty(jsonObject, entry.getValue().getClass(),
												entry.getKey().toString(), entry.getValue(), null);
									}

								}
							}
							if (!isSimple)
								jsonObject.add(fieldName, arr);

						}
					} else {
						// if the variable is not primitive, check if has a
						// predefined
						// method with returns MembersJson class, if it does get
						// the json
						addComplexObject(jsonObject, fieldName, fieldValue, exParams, isSimple);

					}
				}
				;

			}
		}
		if (isSimple)
			classObject = jsonObject;
		else
			classObject.add(obj.getClass().getCanonicalName(), jsonObject);

		return classObject;
	}

	// private format
	/**
	 * Serialising a DbDataObject to a row format JSON element
	 * 
	 * @param startsWith
	 *            A string prefix for each of the fields which we need in the
	 *            JSON. If left empty it will serialise all primitive fields
	 *            (except final, static, private)
	 * @param obj
	 *            The DbDataObject instance which is subject of serialisation
	 * @param objTypeDbt
	 *            DbDataArray type of object describing the repo fields
	 * @param objTypeDbf
	 *            DbDataArray type of object describing the fields in the Values
	 *            Map
	 * @param exParams
	 *            A Map containing additional formatting parameters like
	 *            date/time format
	 * @return
	 */
	private JsonObject getRowFromDbObject(String startsWith, DbDataObject obj, DbDataArray objTypeDbt,
			DbDataArray objTypeDbf, JsonObject exParams, JsonParser jParser, ISvCodeList codeList) {

		Field[] memberFields = obj.getClass().getDeclaredFields();
		// map the single object to a row
		JsonObject row = new JsonObject();
		// the array holding the actual values
		JsonArray cell = new JsonArray();

		// iterate all object fields for serialisation
		for (int i = 0; i < memberFields.length; i++) {
			String fieldName = memberFields[i].getName();

			// make sure we only seralise non final/static/private members
			if (!memberFields[i].getType().equals(this.getClass()) && fieldName.startsWith(startsWith)
					&& !Modifier.isFinal(memberFields[i].getModifiers())
					&& !Modifier.isPrivate(memberFields[i].getModifiers())
					&& !Modifier.isStatic(memberFields[i].getModifiers())) {

				// get member field value
				Object fieldValue = null;
				try {
					memberFields[i].setAccessible(true);
					fieldValue = memberFields[i].get(obj);

				} catch (Exception e) {
					e.printStackTrace();
				}
				// Assign a unique ID of the row
				if (fieldName.equals("object_id"))
					addPrimitiveProperty(row, memberFields[i].getType(), "id", fieldValue, exParams);
				// Assign a unique ID of the row
				if (codeList != null && fieldName.equals("status") && fieldValue != null)
					fieldValue = codeList.getCodeList(svCONST.CODES_STATUS, true).get(fieldValue.toString());

				if (exParams != null) {
					if (objTypeDbt.getItemByIdx(fieldName, svCONST.OBJECT_TYPE_REPO) != null)
						exParams.addProperty("field_type", (String) objTypeDbt
								.getItemByIdx(fieldName, svCONST.OBJECT_TYPE_REPO).getVal("FIELD_TYPE"));
				}
				/*
				 * if(exParams==null) exParams=new JsonObject();
				 * 
				 * exParams.add("gui_metadata", jParser.parse( (String)
				 * objTypeDbf.getItemByIdx(fieldName,
				 * obj.getObject_type()).getVal("GUI_METADATA")));
				 */
				if (fieldValue == null)
					cell.add((Boolean)null);
				else
					addPrimitiveArrayItem(cell, memberFields[i].getType(), fieldValue, exParams);
				if (exParams != null)
					exParams.remove("field_type");

				// do loop over the "values" map and serialise
				if (fieldName.equals("values") && (fieldValue instanceof Map<?, ?>)) {
					if (fieldValue != null) {
						for (Entry<?, ?> entry : ((Map<?, ?>) fieldValue).entrySet()) {
							if (entry.getKey() != null) {
								DbDataObject currDbf = objTypeDbf.getItemByIdx(entry.getKey().toString(),
										obj.getObject_type());
								if (exParams != null) {
									if (currDbf != null)
										exParams.addProperty("field_type", (String) currDbf.getVal("FIELD_TYPE"));
								}
								Object objVal = entry.getValue();

								if (currDbf != null) {
									Long codListId = (Long) currDbf.getVal("code_list_id");
									if (codListId != null && objVal != null && codeList != null)
										objVal = codeList.getCodeList(codListId, true).get(objVal.toString());
								}

								addPrimitiveArrayItem(cell, (objVal != null ? objVal.getClass() : String.class), objVal,
										exParams);
							}
						}
					}

				}
			}
		} // end of the single object serialisation
		row.add("cell", cell);
		// if we serialise DbDataObject, the return object is a row
		return row;
	}

	@SuppressWarnings("deprecation")
	public JsonObject getTabularJson(String startsWith, Object obj, DbDataArray objTypeDbt, DbDataArray objTypeDbf,
			JsonObject exParams) {

		return getTabularJson(startsWith, obj, objTypeDbt, objTypeDbf, exParams, null);

	}

	/**
	 * A method which returns a JSON formated table if the object is
	 * DbDataArray. It iterates over the member fields of the Objects in the
	 * Items obj searching for ones starting with String startsWith. Then the
	 * set of fields used for building a Json is used to populate a JsonObject.
	 * If you use an empty string as parameter startsWith, it will serialize all
	 * fields.
	 * 
	 * @param startsWith
	 *            A string prefix for each of the fields which we need in the
	 *            JSON
	 * @param obj
	 *            A object whose fields should be converted to JSON
	 * @return A JsonObject variable generated from the class
	 */
	@SuppressWarnings("deprecation")
	public JsonObject getTabularJson(String startsWith, Object obj, DbDataArray objTypeDbt, DbDataArray objTypeDbf,
			JsonObject exParams, ISvCodeList codeList) {

		objTypeDbf.rebuildIndex("FIELD_NAME");
		// objTypeDbt.rebuildIndex("FIELD_NAME");
		JsonParser jParser = new JsonParser();
		JsonObject jsonObject = new JsonObject();
		JsonArray rows = new JsonArray();
		jsonObject.add("rows", rows);
		Class<?> outerClass = obj.getClass();
		// serialisation of an Array of Objects
		if (outerClass.equals(DbDataArray.class)) {
			ArrayList<DbDataObject> items = ((DbDataArray) obj).getItems();

			// for each of the objects invoke the serialisation recursively
			for (int z = 0; z < items.size(); z++) {
				DbDataObject innerObj = items.get(z);
				rows.add(getRowFromDbObject(startsWith, innerObj, objTypeDbt, objTypeDbf, exParams, jParser, codeList));

			}
			;

		} else
		// serialisation of a single object
		if (outerClass.equals(DbDataObject.class)) {
			rows.add(getRowFromDbObject(startsWith, (DbDataObject) obj, objTypeDbt, objTypeDbf, exParams, jParser,
					codeList));
		}

		return jsonObject;
	}

	private Boolean setPrimitiveField(Field field, Object targetObject, JsonElement jsonElement)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {

		Object obj = getPrimitiveValue(field.getType(), jsonElement.getAsJsonPrimitive());

		if (obj != null) {
			field.set(targetObject, obj);
		} else
			return false;
		return true;
	}

	private Boolean setArrayField(Field field, Object targetObject, JsonArray jsonArray, Boolean isSimple)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException,
			ArrayIndexOutOfBoundsException, ClassNotFoundException {

		Object[] fieldArray = null; // for handling arrays
		Map fieldMap = null;
		boolean useSvCharId = field.getName().equals("values") && targetObject instanceof DbDataObject;
		List<Object> fieldArrList = null;
		Class<?> fType = field.getType().getComponentType();
		;
		// dirty hack to get the class type of array items
		if (fType == null) {
			ParameterizedType listType = (ParameterizedType) field.getGenericType();
			fType = (Class<?>) listType.getActualTypeArguments()[0];
		}
		if (targetObject.getClass().equals(DbDataArray.class) && field.getType().equals(ArrayList.class))
			fType = DbDataObject.class;

		Boolean isMap = Map.class.isAssignableFrom(field.getType());
		Boolean isListType = (ArrayList.class.isAssignableFrom(field.getType())
				|| LinkedList.class.isAssignableFrom(field.getType()));

		if (isMap) {
			if (useSvCharId)
				fieldMap = (Map<SvCharId, Object>) new LinkedHashMap<SvCharId, Object>();
			else
				fieldMap = (Map<Object, Object>) new LinkedHashMap<Object, Object>();
		} else if (isListType) {
			if (LinkedList.class.isAssignableFrom(field.getType()))
				fieldArrList = new LinkedList<Object>();
			else
				fieldArrList = new ArrayList<Object>();

		}

		else
			fieldArray = (Object[]) Array.newInstance(fType, jsonArray.size());

		for (int i = 0; i < jsonArray.size(); i++) {

			if (jsonArray.get(i) != null) {
				if (jsonArray.get(i).isJsonObject()) {
					if (isMap) {
						for (Entry<String, JsonElement> entry : jsonArray.get(i).getAsJsonObject().entrySet()) {
							String fieldName = entry.getKey();
							Object fieldValue = new Object();
							JsonElement el = entry.getValue();
							if (el.isJsonPrimitive()) {
								fieldValue = getBaseJsonPrimitive(el.getAsJsonPrimitive());
							} // complex
							else if (el.isJsonObject()) {
								JsonObject innerObj = el.getAsJsonObject();
								String clazz = null;
								for (Entry<String, JsonElement> e : innerObj.entrySet()) {
									if (e.getKey().contains("com.prtech.svarog"))
										clazz = e.getKey();
								}
								if (clazz != null) {
									fType = Class.forName(clazz);
									Object complexObj = fType.newInstance();
									setMembersFromJsonImpl(complexObj, innerObj, isSimple);
									fieldValue = complexObj;
								}
							}
							if (useSvCharId)
								fieldMap.put(new SvCharId(fieldName), fieldValue);
							else
								fieldMap.put(fieldName.toUpperCase(), fieldValue);

						}
					} else {
						JsonObject innerObj = jsonArray.get(i).getAsJsonObject();
						String clazz = null;
						for (Entry<String, JsonElement> e : innerObj.entrySet()) {
							if (e.getKey().contains("com.prtech.svarog"))
								clazz = e.getKey();
						}
						if (clazz != null) {
							fType = Class.forName(clazz);
							Object complexObj = fType.newInstance();
							setMembersFromJsonImpl(complexObj, innerObj, isSimple);
							if (isListType)
								fieldArrList.add(complexObj);
							else
								Array.set(fieldArray, i, complexObj);
						}
					}
				} else if (fieldArray != null || fieldArrList != null) {
					Object primitiveObj = getPrimitiveValue(fType, jsonArray.get(i).getAsJsonPrimitive());
					if (primitiveObj != null)
						if (isListType)
							fieldArrList.add(primitiveObj);
						else
							Array.set(fieldArray, i, primitiveObj);
					else
						log4j.error("Unsupported array type: " + fType.toString() + " in class"
								+ targetObject.getClass().toString());

				}
				// after all primitives are done, handle the objects

			}
		}
		if (isMap)
			field.set(targetObject, fieldMap);
		else if (isListType)
			field.set(targetObject, fieldArrList);
		else
			field.set(targetObject, fieldArray);

		return true;
	}

	private Boolean setMembersFromJsonImpl(Object obj, JsonObject jsonObject, Boolean isSimple) {
		if (obj == null)
			return false;
		JsonIO mJson = null;
		Boolean retval = true;
		Method jsMethod = null;
		try {
			try {
				if (!isSimple) {
					Class<?>[] cArg = new Class[1];
					cArg[0] = JsonObject.class;
					jsMethod = obj.getClass().getDeclaredMethod("fromJson", cArg);
					if (jsMethod != null)
						retval = (Boolean) jsMethod.invoke(obj, jsonObject);
				}
			} catch (NoSuchMethodException ex) {
				jsMethod = null;
			}
			if (jsMethod == null) {
				Class currClass = obj.getClass();
				while (currClass != null && !currClass.getName().endsWith("Jsonable")) {
					currClass = currClass.getSuperclass();
				}
				Method method = currClass.getDeclaredMethod("getJsonIO", (Class<?>[]) null);

				mJson = (JsonIO) method.invoke(obj, (Object[]) null);

				if (mJson == null)
					return null;

				Class<?>[] cArg = new Class[4];
				cArg[0] = String.class;
				cArg[1] = Object.class;
				cArg[2] = JsonObject.class;
				cArg[3] = Boolean.class;
				jsMethod = mJson.getClass().getDeclaredMethod("setMembersFromJson", cArg);
				retval = (Boolean) jsMethod.invoke(mJson, "", obj, jsonObject, isSimple);
			}

		} catch (Exception ex) {
			log4j.error("Member object is not Json enabled!", ex);
			retval = false;
		}
		return retval;
	}

	private Boolean setObjectField(Field field, Object targetObject, JsonObject jsonObject, Boolean isSimple)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {

		if (log4j.isDebugEnabled())
			log4j.trace("Trying to instatiate " + field.getType().toString());

		Object obj = null;
		if (Modifier.isAbstract(field.getType().getModifiers())) {
			String className = null;
			for (Entry<String, JsonElement> jsItem : jsonObject.entrySet()) {
				if (jsItem.getKey().startsWith("com.prtech.svarog")) {
					className = jsItem.getKey();
					break;
				}
			}
			obj = Class.forName(className).newInstance();
		} else
			obj = field.getType().newInstance();
		setMembersFromJsonImpl(obj, jsonObject, isSimple);
		field.set(targetObject, obj);
		return true;
	}

	/**
	 * A function to set a member of a class to a specified value from a
	 * JsonElement.
	 * 
	 * @param field
	 *            The field which is set
	 * @param targetObject
	 *            The object to which the field belongs
	 * @param jsonElement
	 *            The JsonElement from which the value is set
	 * @return True if the field is set correctly, False if the field could not
	 *         be set
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private Boolean setField(Field field, Object targetObject, JsonElement jsonElement, Boolean isSimple)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {

		// set primitive types
		if (jsonElement.isJsonPrimitive()) {
			if (!setPrimitiveField(field, targetObject, jsonElement))
				return false;
		} else if (jsonElement.isJsonNull()) {
			field.set(targetObject, null);
		} else if (jsonElement.isJsonArray()) {
			setArrayField(field, targetObject, jsonElement.getAsJsonArray(), isSimple);
		} else if (jsonElement.isJsonObject()) {
			setObjectField(field, targetObject, jsonElement.getAsJsonObject(), isSimple);
		} else
			return false;
		return true;
	}

	public Boolean setMembersFromJson(String startsWith, Object obj, JsonObject jsonObject) {
		return setMembersFromJson(startsWith, obj, jsonObject, false);
	}

	private boolean isDboBase(String fieldName) {
		return svCONST.repoFieldNames.contains(fieldName.toUpperCase());

	}

	public Boolean setMembersFromJson(String startsWith, Object obj, JsonObject jsonObject, Boolean isSimple) {
		Class<?> outerClass = obj.getClass();
		String fieldName = null;
		JsonObject classObject = null;
		if (!isSimple)
			classObject = (JsonObject) jsonObject.get(outerClass.getCanonicalName());
		else
			classObject = jsonObject;
		if (classObject == null) {
			{
				log4j.error("Class " + outerClass.getCanonicalName() + " could not be found in JSON: "
						+ jsonObject.toString());
			}
			return false;
		}
		Field field = null;
		LinkedHashMap<SvCharId, Object> fieldMap = null;
		try {
			Field valuesField = null;

			if (obj instanceof DbDataObject) {
				valuesField = outerClass.getDeclaredField("values");
				valuesField.setAccessible(true);
				fieldMap = new LinkedHashMap<SvCharId, Object>();
				valuesField.set(obj, fieldMap);
			}
			for (Entry<String, JsonElement> entry : classObject.entrySet()) {
				fieldName = entry.getKey();
				JsonElement fieldValue = entry.getValue();

				if (!isSimple || isDboBase(fieldName))
					field = outerClass.getDeclaredField(fieldName);
				else
					field = null;
				if (fieldName.equals("isReadOnly"))
					continue;
				try {
					if (field != null) {
						field.setAccessible(true);
						if (!setField(field, obj, fieldValue, isSimple)) {
							log4j.info(
									"Class:" + outerClass.toString() + ", field:" + fieldName + ", could not be set");
						}
					} else if (fieldValue.isJsonPrimitive()) {
						fieldMap.put(new SvCharId(fieldName), getBaseJsonPrimitive(fieldValue.getAsJsonPrimitive()));
					} else if (obj instanceof DbDataArray && fieldName.equals("items")) {
						field = outerClass.getDeclaredField(fieldName);
						field.setAccessible(true);
						ArrayList<DbDataObject> innerItems = new ArrayList<DbDataObject>();
						JsonArray jsonArray = fieldValue.getAsJsonArray();
						for (int i = 0; i < jsonArray.size(); i++) {
							JsonObject jo = jsonArray.get(i).getAsJsonObject();
							DbDataObject newDbo = new DbDataObject();
							setMembersFromJson(startsWith, newDbo, jo, isSimple);
							innerItems.add(newDbo);
						}
						field.set(obj, innerItems);
					}

					// field.set(obj, fieldValue.);
				} catch (Exception ex) {
					log4j.error("Failed setting class members for class:" + outerClass.toString() + "from JSON", ex);
					return false;
				}

			}
		} catch (java.lang.NoSuchFieldException ex) {
			log4j.trace("JSON key doesn't exist as field in object:" + fieldName);
		} catch (Exception ex) {
			log4j.error("Failed to set field value from JSON", ex);
			return false;
		}
		return true;

	}
}
