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

import java.util.HashMap;

public interface ISvCodeList {

	/**
	 * Method to return the key/value map containig the object ids and codes of the
	 * root categories
	 * 
	 * @return Key/value map with categorie Id and code
	 */
	public HashMap<Long, String> getCodeCategoriesId();

	/**
	 * Method to return the key/value map containig the object ids and label text of
	 * the child codes for a specified code list id. The method translates the codes
	 * to the labes according to the requested locale
	 * 
	 * @param languageId       the Id of the locale, as available in Svarog System
	 *                         Locales
	 * @param codeListObjectId the id of the code list
	 * @return Key/value map with categorie Id and label text
	 */
	public HashMap<Long, String> getCodeListId(String languageId, Long codeListObjectId);

	/**
	 * Method to return the key/value map containig the object ids and code values
	 * of the child codes for a specified code list id. The method does not perform
	 * any sort of translation
	 * 
	 * @param codeListObjectId the id of the code list
	 * @return Key/value map with categorie Id and user code
	 */
	public HashMap<Long, String> getCodeListId(Long codeListObjectId);

	/**
	 * 
	 * Method to return sorted list of codes based on the parent code name.
	 * 
	 * @param codeListObjectId The ID of the list
	 * @return data array containing the code list
	 */
	public DbDataArray getCodeListBase(Long codeListObjectId);

	/**
	 * Method to return a list of all configured Code Lists within the Svarog system
	 * (at ROOT level).
	 * 
	 * @param languageId the Id of the locale, as available in Svarog System Locales
	 * @return Map of Code/Label pairs
	 */
	public HashMap<Long, String> getCodeCategoriesId(String languageId);

	/**
	 * Method to return a list of all configured Code Lists within the Svarog system
	 * (at ROOT level).
	 * 
	 * @return Map of Code/Label pairs
	 */
	public HashMap<String, String> getCodeCategories();

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
	public HashMap<String, String> getCodeList(String languageId, Long codeListObjectId, Boolean includeLabels);

	/**
	 * Method to return a configured Code List within the Svarog system.
	 * 
	 * @param codeListObjectId the object Id of the code list
	 * @param includeLabels    Flag if the list items should be translated according
	 *                         to the locale labels
	 * @return Map of User Code/Label pairs
	 */
	public HashMap<String, String> getCodeList(Long codeListObjectId, Boolean includeLabels);

	/**
	 * Method to return a configured Categories within the Svarog system.
	 * 
	 * @param languageId    the object Id of the language
	 * @param includeLabels Flag if the list items should be translated according to
	 *                      the locale labels
	 * @return Map of User Code/Label pairs
	 */
	public HashMap<String, String> getCodeCategories(String languageId, Boolean includeLabels);
}
