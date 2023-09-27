package com.prtech.svarog_common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.prtech.svarog.SvException;
import com.prtech.svarog.svCONST;
import com.prtech.svarog_common.Jsonable;
import com.prtech.svarog_interfaces.II18n;

public class ResponseHandler extends Jsonable {
	/**
	 * Enum type for the type of response
	 * 
	 * @author ristepejov
	 *
	 */
	public enum MessageType {
		SUCCESS, ERROR, WARNING, INFO, EXCEPTION
	}

	static II18n i18n = null;
	static String defaultLocale = null;

	private MessageType msgType;
	private String labelCode;
	private String userLocale;
	private String msgTitle;
	private String msgText;
	private String msgData;
	private JsonElement jData;
	private JsonObject responseObject;

	/**
	 * Constructor to create a handler by type, locale and label code
	 * @param type The message type of the handler
	 * @param labelCode The label code used for localisation
	 * @param userLocale The user locale to be used for translation
	 * @throws SvException 
	 */
	public ResponseHandler(MessageType type, String labelCode, String userLocale) {
		this.userLocale = userLocale;
		create(type, labelCode);
	}

	public ResponseHandler(MessageType type, String labelCode) {
		this(type, labelCode, defaultLocale);
	}

	/**
	 * Empty constructor
	 */
	public ResponseHandler() {

	}

	/**
	 * Method to return the user locale, or default if user local is null
	 * 
	 * @return string id of the locale
	 */
	private String getLocale() {
		return userLocale == null ? userLocale : userLocale;
	}

	/**
	 * method to create response handler in case of SvException but when is
	 * returned as JsonObject , we read the error_message and error_id and
	 * create error handler, we can also overwrite the error message if we
	 * specify svarog.label_code as parameter that will be DB translated
	 * 
	 * @param e
	 *            SvException
	 * @param str
	 *            String if we want to overwrite the message we put label_code,
	 *            else we can use null or ""
	 * @return ResponseHandler "EXCEPTION" with formated title and message and
	 *         no data
	 */
	public static ResponseHandler responseHandlerByException(JsonObject afterJson, String titleOverwrite) {
		ResponseHandler jrh = new ResponseHandler();
		String errCode = "";

		if (afterJson.has("ERROR_ID"))
			errCode = afterJson.get("ERROR_ID").getAsString();
		String errTitle = "";
		if (titleOverwrite != null)
			errTitle = titleOverwrite;
		if ((titleOverwrite == null || "".equals(titleOverwrite)) && afterJson.has("Error_Message"))
			errTitle = afterJson.get("Error_Message").getAsString();
		String customType = "ERROR";
		if ("error.invalid_session".equalsIgnoreCase(errTitle) || "system.under.maintenance".equalsIgnoreCase(errTitle))
			customType = "EXCEPTION";
		jrh.create(customType, (i18n != null ? i18n.getI18nText(defaultLocale, errTitle) : errTitle), errCode,
				new JsonObject());
		return jrh;
	}

	/**
	 * method to create response handler in case of SvException, we read the
	 * error_message and error_id and create error handler, we can also
	 * overwrite the error message if we specify svarog.label_code as parameter
	 * that will be DB translated
	 * 
	 * @param e
	 *            SvException
	 * @param str
	 *            String if we want to overwrite the message we put label_code,
	 *            else we can use null or ""
	 * @return ResponseHandler "EXCEPTION" with formated title and message and
	 *         no data
	 */
	public static ResponseHandler responseHandlerByException(SvException e, String titleOverwrite) {
		Gson gs = new Gson();
		String beforeJson = e.getJsonMessage();
		JsonObject afterJson = gs.fromJson(beforeJson, JsonObject.class);
		return responseHandlerByException(afterJson, titleOverwrite);
	}

	/**
	 * method to create response handler in case of SvException, we read the
	 * error_message and error_id and create error handler,
	 * 
	 * @param e
	 *            SvException
	 * @return ResponseHandler "EXCEPTION" with formated title and message and
	 *         no data
	 */
	public static ResponseHandler responseHandlerByException(SvException e) {

		String title = i18n == null ? e.getLabelCode() : i18n.getI18nText(e.getLabelCode());
		return responseHandlerByException(e, title);
	}

	/**
	 * method to return the values from the response handles as an JsonObject,
	 * this one will just return the responseObject
	 * 
	 * @return JsonObject value that is internally saved in responseObject
	 */
	public JsonObject getAll() {
		return responseObject;
	}

	/**
	 * method to return the values from the response handles as an JsonObject,
	 * this one will generate the response Object from all the values like:
	 * responseType, responseMessage , rsponseTitle , and data that we are
	 * returning back
	 * 
	 * @return JsonObject created in time of calling this method
	 */
	public JsonObject getAllv1() {
		JsonObject rObject = new JsonObject();
		if (msgType != null)
			rObject.addProperty("type", msgType.toString());
		if (labelCode != null)
			rObject.addProperty("label_code", labelCode.toString());
		if (msgTitle != null && msgTitle != "")
			rObject.addProperty("title", msgTitle);
		if (msgText != null && msgData != "")
			rObject.addProperty("message", msgText);
		if (msgData != null && msgData != "")
			rObject.addProperty("data", msgData);
		if (jData != null)
			rObject.addProperty("data", jData.toString());
		return rObject;
	}

	/**
	 * method to create response handler , this is standard response , with type
	 * of the response, messages, and data if execution was success, data to be
	 * returned is String
	 * 
	 * @param typee
	 *            String type of the response, values are: SUCCESS, ERROR,
	 *            WARNING, INFO, EXCEPTION
	 * @param title
	 *            String short title that will be shown on the response
	 * @param message
	 *            String long message like exception dump
	 * @param data
	 *            String if the response was expected to be String we add that
	 *            data here
	 */
	public void create(String typee, String title, String message, String data) {
		responseObject = createBasicData(typee, title, message);
		if (data != null && data != "") {
			responseObject.addProperty("data", data);
			msgData = data;
		}
	}

	public void create(MessageType typee, String title, String message, String data) {
		responseObject = createBasicData(typee, title, message);
		if (data != null && data != "") {
			responseObject.addProperty("data", data);
			msgData = data;
		}
	}

	/**
	 * Method to create a response with Type and Label code. Based on the label
	 * code and the locale the system shall translate it.
	 * 
	 * @param typee
	 * @param labelCode
	 */
	public void create(MessageType type, String labelCode) {

		String title = i18n == null ? labelCode : i18n.getI18nText(getLocale(), labelCode);
		String message = i18n == null ? labelCode : i18n.getI18nLongText(getLocale(), labelCode);
		responseObject = createBasicData(type, title, message);
	}

	/**
	 * method to create response handler , this is standard response , with type
	 * of the response, messages, and data if execution was success, data to be
	 * returned is JsonObject
	 * 
	 * @param typee
	 *            String type of the response, values are: SUCCESS, ERROR,
	 *            WARNING, INFO, EXCEPTION
	 * @param title
	 *            String short title that will be shown on the response
	 * @param message
	 *            String long message like exception dump
	 * @param data
	 *            JsonElement if the response was expected to be JsonObject we
	 *            add that data here
	 */
	public JsonObject create(String typee, String title, String message, JsonElement data) {
		responseObject = createBasicData(typee, title, message);
		if (data != null) {
			responseObject.add("data", data);
			jData = data;
		}
		return responseObject;
	}

	public JsonObject create(MessageType typee, String title, String message, JsonElement data) {
		responseObject = createBasicData(typee, title, message);
		if (data != null) {
			responseObject.add("data", data);
			jData = data;
		}
		return responseObject;
	}

	/**
	 * method to create response handler basic data that is shared between
	 * String JsonArray and JsonObject
	 * 
	 * @param typee
	 *            String type of the response, values are: SUCCESS, ERROR,
	 *            WARNING, INFO, EXCEPTION
	 * @param title
	 *            String short title that will be shown on the response
	 * @param message
	 *            String long message like exception dump
	 * @return JsonObject
	 */
	private JsonObject createBasicData(String typee, String title, String message) {
		responseObject = new JsonObject();
		if (typee != null && typee != "") {
			responseObject.addProperty("type", typee);
			switch (typee.toUpperCase()) {
			case "SUCCESS":
				msgType = MessageType.SUCCESS;
				break;
			case "ERROR":
				msgType = MessageType.ERROR;
				break;
			case "WARNING":
				msgType = MessageType.WARNING;
				break;
			case "INFO":
				msgType = MessageType.INFO;
				break;
			case "EXCEPTION":
				msgType = MessageType.EXCEPTION;
				break;
			default:
			}
		}
		if (title != null && title != "") {
			responseObject.addProperty("title", title);
			msgTitle = title;
		}
		if (message != null && message != "") {
			responseObject.addProperty("message", message);
			msgText = message;
		}
		responseObject.addProperty("label_code", labelCode);
		return responseObject;
	}

	private JsonObject createBasicData(MessageType typee, String title, String message) {
		responseObject = new JsonObject();
		if (typee != null) {
			responseObject.addProperty("type", typee.toString());
			msgType = typee;
		}
		if (title != null && title != "") {
			responseObject.addProperty("title", title);
			msgTitle = title;
		}
		if (message != null && message != "") {
			responseObject.addProperty("message", message);
			msgText = message;
		}
		responseObject.addProperty("label_code", labelCode);
		return responseObject;
	}

	/**
	 * method to create response handler , this is standard response , with type
	 * of the response, messages, and data if execution was success, data to be
	 * returned is empty String
	 * 
	 * @param typee
	 *            String type of the response, values are: SUCCESS, ERROR,
	 *            WARNING, INFO, EXCEPTION
	 * @param title
	 *            String short title that will be shown on the response
	 * @param message
	 *            String long message like exception dump
	 * @param data
	 *            String if the response was expected to be String we add that
	 *            data here
	 */
	public void create(String typee, String title, String message) {
		create(typee, title, message, "");
	}

	public void create(MessageType typee, String title, String message) {
		create(typee, title, message, "");
	}

	public JsonObject addJsonObjectElementData(JsonElement data) {
		if (data != null) {
			responseObject.add("data", data);
			jData = data;
		}
		return responseObject;
	}

	public MessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(MessageType msgType) {
		this.msgType = msgType;
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}

	public String getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(String userLocale) {
		this.userLocale = userLocale;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	public String getMsgData() {
		return msgData;
	}

	public void setMsgData(String msgData) {
		this.msgData = msgData;
	}

	public JsonElement getjData() {
		return jData;
	}

	public void setjData(JsonElement jData) {
		this.jData = jData;
	}

	public static II18n getI18n() {
		return i18n;
	}

	public static void setI18n(II18n i18n) {
		ResponseHandler.i18n = i18n;
	}

	public static String getDefaultLocale() {
		return defaultLocale;
	}

	public static void setDefaultLocale(String defaultLocale) {
		ResponseHandler.defaultLocale = defaultLocale;
	}
}