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
 *  
 *******************************************************************************/
package com.prtech.svarog;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.prtech.svarog_common.DbDataArray;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.ISvCodeList;
import com.prtech.svarog.svCONST;

/**
 * Default class for implementing CodeLists in the Svarog system. It used for
 * decoding of key/value, such as option lists, drop downs or other lists which
 * require localisation. The CodeList supports tree like structures where one
 * code has sub-codes
 * 
 * @author ristepejov
 *
 */
public class CodeList extends SvCore implements ISvCodeList {
	/**
	 * Log4j instance used for logging
	 */
	private static final Logger log4j = SvConf.getLogger(CodeList.class);

	/**
	 * Constructor to create a SvUtil object according to a user session. This is
	 * the default constructor available to the public, in order to enforce the
	 * svarog security mechanisms based on the logged on user.
	 * 
	 * @param session_id User session for which the CodeList is instantiated
	 * @throws SvException Pass through for any underlying exception in the super
	 *                     contructor
	 */
	public CodeList(String session_id) throws SvException {
		super(session_id);
	}

	/**
	 * Constructor to create a SvUtil object according to a user session. This is
	 * the default constructor available to the public, in order to enforce the
	 * svarog security mechanisms based on the logged on user.
	 * 
	 * @param session_id   User session for which the CodeList is instantiated
	 * @param sharedSvCore The shared SvCore instance which is used for the JDBC
	 *                     connection sharing
	 * @throws SvException Pass through for any underlying exception in the super
	 *                     contructor
	 */
	public CodeList(String session_id, SvCore sharedSvCore) throws SvException {
		super(session_id, sharedSvCore);
	}

	/**
	 * Default Constructor. This constructor can be used only within the svarog
	 * package since it will run with system priveleges.
	 * 
	 * @param sharedSvCore The shared SvCore instance which is used for the JDBC
	 *                     connection sharing
	 * @throws SvException Pass through for any underlying exception in the super
	 *                     contructor
	 */
	public CodeList(SvCore sharedSvCore) throws SvException {
		super(sharedSvCore);
	}

	/**
	 * Default Constructor. This constructor can be used only within the svarog
	 * package since it will run with system priveleges.
	 * 
	 * @throws SvException Pass through for any underlying exception in the super
	 *                     contructor
	 */
	CodeList() throws SvException {
		super(svCONST.systemUser, null);
	}

	/**
	 * Method to return the key/value map containig the object ids and codes of the
	 * root categories
	 * 
	 * @return Key/value map with categorie Id and code
	 */
	public HashMap<Long, String> getCodeCategoriesId() {
		return getCodeCategoriesId(SvConf.getDefaultLocale());
	}

	/**
	 * Method to return the key/value map containig the object ids and label text of
	 * the child codes for a specified code list id. The method translates the codes
	 * to the labes according to the requested locale
	 * 
	 * @return Key/value map with categorie Id and label text
	 */
	public HashMap<Long, String> getCodeListId(String languageId, Long codeListObjectId) {
		return getCodeListId(languageId, codeListObjectId, true);

	}

	/**
	 * Method to return the key/value map containig the object ids and code values
	 * of the child codes for a specified code list id. The method does not perform
	 * any sort of translation
	 * 
	 * @param codeListObjectId the id of the code list
	 * @return Key/value map with categorie Id and user code
	 */
	public HashMap<Long, String> getCodeListIdValues(Long codeListObjectId) {
		return getCodeListId(SvConf.getDefaultLocale(), codeListObjectId, false);
	}

	/**
	 * Method to return the key/value map containig the object ids and codes
	 * 
	 * @param codeListObjectId The object id of the list
	 * @return Key/value map with categorie Id and label text
	 */
	public HashMap<String, Long> getCodeListValues(Long codeListObjectId) {
		HashMap<String, Long> catList = new HashMap<>();
		DbDataArray object = getCodeListBase(codeListObjectId);
		for (DbDataObject dbo : object.getItems()) {
			catList.put(dbo.getAsString("CODE_VALUE"), dbo.getObjectId());
		}
		return catList;
	}

	/**
	 * 
	 * @param languageId       The ID of the locale into which the labels will be
	 *                         translated
	 * @param codeListObjectId The Parent ID of the code list. Special case is 0
	 *                         which returns the root level
	 * @param traslateLabels   Flag used to translate labels or return codes.
	 * @return A map of ObjectId/String pairs (code or translated label)
	 */
	public HashMap<Long, String> getCodeListId(String languageId, Long codeListObjectId, boolean traslateLabels) {
		String langId = languageId != null ? languageId : SvConf.getDefaultLocale();

		HashMap<Long, String> catList = new HashMap<Long, String>();
		DbDataArray object = getCodeListBase(codeListObjectId);
		for (DbDataObject dbo : object.getItems()) {
			String value = (traslateLabels ? I18n.getText(langId, dbo.getAsString("label_code"))
					: dbo.getAsString("CODE_VALUE"));
			catList.put(dbo.getObjectId(), value);
		}

		return catList;

	}

	public HashMap<Long, String> getCodeListId(Long codeListObjectId) {
		return getCodeListId(SvConf.getDefaultLocale(), codeListObjectId);
	}

	/**
	 * 
	 * Method to return sorted list of codes based on the parent code name.
	 * 
	 * @param codeListName The code of the list
	 * @return data array containing the code list
	 */
	public DbDataArray getCodeListBase(String codeListName) {
		HashMap<String, Long> lists = null;
		Long codeListObjectId = 0L;
		lists = getCodeListValues(Sv.ROOT_CODELIST);
		// find the crop codes
		codeListObjectId = lists.get(codeListName);

		SvReader svr = null;
		DbDataArray object = null;
		try {
			svr = new SvReader(this);
			object = svr.getObjectsByParentId(codeListObjectId, svCONST.OBJECT_TYPE_CODE, null, null, null,
					"SORT_ORDER");
		} catch (SvException e) {
			log4j.error("Error loading the code list " + codeListObjectId + ":" + e.getFormattedMessage());
		} finally {
			if (svr != null)
				svr.release();
		}
		return object;

	}

	/**
	 * Method to return sorted list of codes based on the parent code object Id.
	 * 
	 * @param codeListObjectId the Id of the list
	 */
	public DbDataArray getCodeListBase(Long codeListObjectId) {
		SvReader svr = null;
		DbDataArray object = null;
		try {
			svr = new SvReader(this);
			object = svr.getObjectsByParentId(codeListObjectId, svCONST.OBJECT_TYPE_CODE, null, null, null,
					"SORT_ORDER");
		} catch (SvException e) {
			log4j.error("Error loading the code list " + codeListObjectId + ":" + e.getFormattedMessage());
		} finally {
			if (svr != null)
				svr.release();
		}
		return object;

	}

	/**
	 * Method to return a list of all configured Code Lists within the Svarog system
	 * (at ROOT level).
	 * 
	 * @param languageId the Id of the locale, as available in Svarog System Locales
	 * @return Map of Code/Label pairs
	 */
	public HashMap<Long, String> getCodeCategoriesId(String languageId) {
		return getCodeListId(languageId, 0L);
	}

	/**
	 * Method to return a list of all configured Code Lists within the Svarog system
	 * (at ROOT level).
	 * 
	 * @return Map of Code/Label pairs
	 */
	public HashMap<String, String> getCodeCategories() {
		return getCodeCategories(SvConf.getDefaultLocale(), true);
	}

	/**
	 * Method to return a configured Code List within the Svarog system.
	 * 
	 * @param languageId       the Id of the locale, as available in Svarog System
	 *                         Locales
	 * @param codeListObjectId the object Id of the code list
	 * @param includeLabels    Flag if the list items should be translated according
	 *                         to the locale labels
	 * @return Map of User Code/Label pairs
	 */
	public HashMap<String, String> getCodeList(String languageId, Long codeListObjectId, Boolean includeLabels) {
		String langId = languageId != null ? languageId : SvConf.getDefaultLocale();

		HashMap<String, String> catList = new HashMap<String, String>();
		DbDataArray object = getCodeListBase(codeListObjectId);
		for (DbDataObject dbo : object.getItems()) {
			String label = "";
			if (includeLabels)
				label = I18n.getText(langId, (String) dbo.getVal("label_code"));
			catList.put((String) dbo.getVal("CODE_VALUE"), label);
		}

		return catList;

	}

	public HashMap<String, String> getCodeList(Long codeListObjectId, Boolean includeLabels) {
		return getCodeList(SvConf.getDefaultLocale(), codeListObjectId, includeLabels);
	}

	public HashMap<String, String> getCodeCategories(String languageId, Boolean includeLabels) {
		return getCodeList(languageId, 0L, includeLabels);
	}

}
