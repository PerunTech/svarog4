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
package com.prtech.svarog_interfaces;

import com.prtech.svarog.SvException;

public interface II18n {
	/**
	 * The method returns a text representation for a label code in the default
	 * configured locale
	 * 
	 * @param labelCode
	 *            the label code for which i18n will return a localised string
	 * @return String representation of the label
	 */
	public String getI18nText(String labelCode);

	/**
	 * The method returns a text description for a label code in the default
	 * configured locale
	 * 
	 * @param labelCode
	 *            the label code for which i18n will return a localised string
	 * @return String representation of the label
	 * @throws SvException
	 */
	public String getI18nLongText(String labelCode);

	/**
	 * The method returns a text representation for a label code in the default
	 * configured locale
	 * 
	 * @param languageId
	 *            the locale which i18n use to localise the label
	 * @param labelCode
	 *            the label code for which i18n will return a localised string
	 * @return String representation of the label
	 */
	public String getI18nText(String languageId, String labelCode);

	/**
	 * The method returns a text description for a label code in the default
	 * configured locale
	 * 
	 * @param languageId
	 *            the locale which i18n use to localise the label
	 * @param labelCode
	 *            the label code for which i18n will return a localised string
	 * @return String representation of the label
	 * @throws SvException
	 */
	public String getI18nLongText(String languageId, String labelCode);

}
