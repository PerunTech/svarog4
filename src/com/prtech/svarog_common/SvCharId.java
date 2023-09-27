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

import java.util.Arrays;

import com.prtech.svarog.svCONST;

/**
 * A class providing fast unique identification using ASCI based strings. The id
 * will be case insensitive if the string is ASCII, otherwise the ID will act in
 * case-sensitive manner The best use of this class is for representing
 * identification of objects in the database which are written in latin script
 * using ASCII characters. You can use it with different charsets, but only for
 * ASCII the upper case conversion guarantees case-insensitive identification.
 * 
 * @author XPS13
 *
 */
public class SvCharId {

	public static final byte PKID = 0;
	public static final byte META_PKID = 1;
	public static final byte DT_INSERT = 2;
	public static final byte DT_DELETE = 3;
	public static final byte OBJECT_ID = 4;
	public static final byte PARENT_ID = 5;
	public static final byte OBJECT_TYPE = 6;
	public static final byte STATUS = 7;
	public static final byte USER_ID = 8;
	public static final byte OTHER = -1;
	public static final int[] REPO_HASHES = { getHash(DbDataObject.PKID), getHash(DbDataObject.META_PKID),
			getHash(DbDataObject.DT_INSERT), getHash(DbDataObject.DT_DELETE), getHash(DbDataObject.OBJECT_ID),
			getHash(DbDataObject.PARENT_ID), getHash(DbDataObject.OBJECT_TYPE), getHash(DbDataObject.STATUS),
			getHash(DbDataObject.USER_ID) };
	/**
	 * The char array holding the value of the ID
	 */
	char[] value;
	/**
	 * The hash code of the ID
	 */
	int hash;

	/**
	 * 
	 */
	final byte type;

	/**
	 * Public constructor from char[]
	 * 
	 * @param value the char[] reference holding the string array ID
	 */
	public SvCharId(char[] value) {
		int repoIndex = DbDataObject.repoFieldNames.indexOf(value);
		this.type = (byte) repoIndex;
		if (repoIndex >= 0 && repoIndex < DbDataObject.repoFieldNames.size()) {
			this.value = DbDataObject.repoFieldNames.get(repoIndex);
		} else
			this.value = value;
		prepare();
	}

	/**
	 * Public constructor from String object
	 * 
	 * @param strValue The string object on which the ID is based
	 */
	public SvCharId(String strValue) {
		int repoIndex = svCONST.repoFieldNames.indexOf(strValue);
		this.type = (byte) repoIndex;
		if (repoIndex >= 0 && repoIndex < DbDataObject.repoFieldNames.size()) {
			this.value = DbDataObject.repoFieldNames.get(repoIndex);
		}
		if (strValue != null)
			this.value = strValue.toCharArray();
		else
			value = null;
		prepare();
	}

	/**
	 * Public constructor from byte, containing the byte id of a repo field
	 * 
	 * @param type the byte id of a repo field index as per
	 *             DbDataObject.repoFieldNames
	 */
	public SvCharId(byte type) {
		if (type >= 0 && type < DbDataObject.repoFieldNames.size()) {
			this.value = DbDataObject.repoFieldNames.get((int) type);
		} else
			this.value = null;
		this.type = type;
		prepare();

	}

	/**
	 * Preparation method which calculates the hash and converts all small case
	 * ASCII chars to upper case.
	 */
	private void prepare() {
		if (type >= 0 && type < DbDataObject.repoFieldNames.size()) {
			hash = REPO_HASHES[type];
		}
		if (value != null) {
			hash = getHash(value);
		} else
			hash = 0;

	}

	static int getHash(char[] value) {
		int hash;
		int firstLower;
		/* Now check if there are any characters that need to be changed. */
		hash = 0;
		for (firstLower = 0; firstLower < value.length;) {
			int c = (int) value[firstLower];
			if (c >= 97 && c <= 122) {
				c -= 32;
				value[firstLower] = (char) c;
			}
			firstLower += 1;
			hash = 31 * hash + c;
		}
		return hash;
	}

	/**
	 * Method returning a copy of the char array
	 * 
	 * @return a char array holding the ID
	 */
	public char[] toCharArray() {
		char[] retVal = null;
		if (value != null) {
			retVal = new char[value.length];
			System.arraycopy(value, 0, retVal, 0, value.length);
		}
		return retVal;
	}

	/**
	 * Method returning a string version of the ID
	 */
	public String toString() {
		String retVal = null;
		if (value != null) {
			retVal = new String(value);
		} else
			retVal = new String("");
		return retVal;
	}

	/**
	 * Overrider hashCode method
	 */
	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * Overriden equals operator
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SvCharId)) {
			return false;
		}
		return Arrays.equals(value, ((SvCharId) obj).value);
	}

	public static SvCharId toSvCharId(String id) {
		return new SvCharId(id);
	}
}