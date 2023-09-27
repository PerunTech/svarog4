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
package com.prtech.svarog;

import org.joda.time.DateTime;

import com.google.gson.JsonObject;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.Jsonable;
import com.prtech.svarog_interfaces.II18n;

/**
 * Svarog specific exception class. The SvException class unifies Svarog runtime
 * exception handling. It provides a label code which can be easily localised to
 * give translated error messages. It shall associate the user which caused the
 * exception as well as the related context such as Configuration or User data
 * 
 * @author XPS13
 *
 */
public class SvException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static II18n i18n = null;

	String userLocale = null;

	DbDataObject instanceUser = null;
	Jsonable userData = null;
	Object configData = null;

	DateTime timeStamp = new DateTime();

	private String i18nLabelCode;

	public SvException(String svarogLabelCode, DbDataObject instanceUser) {
		super(svarogLabelCode);
		this.i18nLabelCode = svarogLabelCode;
		if (instanceUser == null)
			this.instanceUser = svCONST.systemUser;
		else
			this.instanceUser = instanceUser;
	}

	/**
	 * Default constructor to be used with the Svarog Exception.
	 * 
	 * @param svarogLabelCode
	 */
	public SvException(String svarogLabelCode, DbDataObject instanceUser, Throwable cause) {
		this(svarogLabelCode, instanceUser);
		initCause(cause);
		this.i18nLabelCode = svarogLabelCode;
		this.instanceUser = instanceUser;

	}

	public String getMessage() {
		return getFormattedMessage(false);

	}

	public String getExceptionId() {
		return this.getInstanceUser().getVal("USER_NAME") + "." + this.getTimeStamp().getMillis();

	}

	/**
	 * Method to return localised text based on the label code and the available
	 * I18n object. If no I18n is available it will return the label code
	 * 
	 * @return Localised text message or label code
	 */
	public String getLocalisedText() {
		String text;
		if (i18n != null) {
			if (userLocale != null)
				text = i18n.getI18nText(userLocale, this.getLabelCode());
			else
				text = i18n.getI18nText(this.getLabelCode());
		} else
			text = this.getLabelCode();
		return text;
	}

	/**
	 * Method to convert the configuration objects to readable text
	 * 
	 * @return
	 */
	public String getConfigText() {
		return (this.getConfigData() != null
				? (this.getConfigData() instanceof Jsonable ? ((Jsonable) this.getConfigData()).toJson().toString()
						: this.getConfigData().toString())
				: "N/A");
	}

	/**
	 * Method to convert the user objects to readable text
	 * 
	 * @return
	 */
	public String getUserText() {
		return (this.getUserData() != null ? this.getUserData().toJson().toString() : "N/A. ");
	}

	/**
	 * Getting a formatted message for legacy reasons to include the localised text
	 * 
	 * @return Text representation of the exception
	 */
	public String getFormattedMessage() {
		return getFormattedMessage(true);
	}

	/**
	 * Readable representation of the expcetion. Used mostly for writing into log
	 * files
	 * 
	 * @param isLocalised If the message should be localised using the I18n instance
	 * @return String message with exception details
	 */
	public String getFormattedMessage(boolean isLocalised) {
		StringBuilder errMessage = new StringBuilder(100);
		errMessage.append("Eror ID:" + getExceptionId() + System.lineSeparator());
		errMessage.append("Error Code:" + this.getLabelCode() + System.lineSeparator());
		if (isLocalised)
			errMessage.append("Error Message:" + getLocalisedText() + System.lineSeparator());

		errMessage.append("Config Data:" + getConfigText() + System.lineSeparator());
		errMessage.append("User Data:" + getUserText() + System.lineSeparator());
		return errMessage.toString();

	}

	/**
	 * Legacy text message in JSON format
	 * 
	 * @return String in JSON format
	 */
	public String getJsonMessage() {
		return getJson(false).toString();

	}

	/**
	 * Method to return a JsonObject of the exception
	 * 
	 * @param isLocalised If the object should contain localised text descriptions
	 * @return Json object of the exception
	 */
	public JsonObject getJson(boolean isLocalised) {
		JsonObject obj = new JsonObject();
		obj.addProperty("ERROR_ID", getExceptionId());
		obj.addProperty("Label_Code", this.getLabelCode());
		if (isLocalised)
			obj.addProperty("Error_Message", getLocalisedText());

		if (this.getConfigData() != null && this.getConfigData() instanceof Jsonable)
			obj.add("Config_Data", ((Jsonable) this.getConfigData()).toJson());
		else
			obj.addProperty("Config_Data", getConfigText());
		obj.add("User_Data", (this.getUserData() != null ? this.getUserData().toJson() : new JsonObject()));
		return obj;

	}

	/**
	 * Constructor that accepts a label code as well as DbDataObject to support with
	 * debugging.
	 * 
	 * @param svarogLabelCode
	 * @param dbo
	 */
	public SvException(String svarogLabelCode, DbDataObject instanceUser, Jsonable userData, Object configData,
			Throwable cause) {
		this(svarogLabelCode, instanceUser, userData, configData);
		initCause(cause);
	}

	/**
	 * Constructor to support constructing an exception with available extended
	 * application data
	 * 
	 * @param svarogLabelCode The label code of the exception
	 * @param instanceUser    The user under which the svarog instance was running
	 * @param userData        The user data which is related to the exception
	 * @param configData      The configuration data related to the exception
	 */
	public SvException(String svarogLabelCode, DbDataObject instanceUser, Jsonable userData, Object configData) {
		this(svarogLabelCode, instanceUser);
		this.userData = userData;
		this.configData = configData;
	}

	/**
	 * Getter method to return the svarog Label Code to be used for getting a I18n
	 * message.
	 * 
	 * @return String label code to be used with {@link I18n}
	 */
	public String getLabelCode() {
		return i18nLabelCode;
	}

	public DbDataObject getInstanceUser() {
		return instanceUser;
	}

	public void setInstanceUser(DbDataObject instanceUser) {
		this.instanceUser = instanceUser;
	}

	public Jsonable getUserData() {
		return userData;
	}

	public void setUserData(DbDataObject userData) {
		this.userData = userData;
	}

	public Object getConfigData() {
		return configData;
	}

	public void setConfigData(Object configData) {
		this.configData = configData;
	}

	public DateTime getTimeStamp() {
		return timeStamp;
	}

	public String getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(String userLocale) {
		this.userLocale = userLocale;
	}

	public void setUserData(Jsonable userData) {
		this.userData = userData;
	}

}
