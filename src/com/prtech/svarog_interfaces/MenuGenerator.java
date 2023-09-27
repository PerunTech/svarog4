package com.prtech.svarog_interfaces;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class describes the process for designing an automated menu lists per
 * bundle/module It may be inherited/used in any bundle-plugin in order to
 * provide the automated list
 * 
 * @author zpetr
 *
 */
public abstract class MenuGenerator {

	static final Logger log4j = LogManager.getLogger(MenuGenerator.class.getName());

	/**
	 * Gson instance
	 */
	Gson gson;
	/**
	 * JsonObject description
	 */
	JsonObject initialJsonObject;
	/**
	 * The first key in the {@link JsonArray}, which keeps information about the
	 * module title, usually label code of type {@link String}
	 */
	String moduleCode;
	/**
	 * The first key in the value of the first key, which keeps the information
	 * about the menu title, usually label code of type {@link String}
	 */
	String menuCode;
	/**
	 * {@link ISvCore} instance
	 */
	ISvCore svr;
	/**
	 * Final result
	 */
	JsonArray result;

	public JsonArray getResult() {
		return result;
	}

	public void setResult(JsonArray result) {
		this.result = result;
	}

	public MenuGenerator(JsonObject initialJsonObject, String moduleCode, String menuCode, ISvCore svr) {
		super();
		// First construct the required menu list object
		this.gson = new Gson();
		this.initialJsonObject = initialJsonObject;
		this.moduleCode = moduleCode;
		this.menuCode = menuCode;
		this.svr = svr;
		// Then calculate the menu list items
		this.result = generateMenuItems();
	}

	/**
	 * Main method in order to get the final results with menu items for
	 * specific moduleCode and menuCode
	 * 
	 * It does not have any specific parameters, because it use the one defined
	 * in the class and sent through the constructor
	 * 
	 * @author zpetr
	 *
	 */
	JsonArray generateMenuItems() {
		JsonArray result = null;
		if (this.initialJsonObject == null || this.initialJsonObject.get(moduleCode) == null) {
			return null;
		}
		try {
			result = new JsonArray();
			JsonObject getObject = (JsonObject) this.initialJsonObject.get(moduleCode);
			JsonArray jArrayMenuItems = (JsonArray) getObject.get(this.menuCode);
			for (int i = 0; i < jArrayMenuItems.size(); i++) {
				JsonObject tempJObj = createNewInstanceJsonObject((JsonObject) jArrayMenuItems.get(i));
				if (!(!checkIfMenuItemIsPermitable(tempJObj, svr) || !checkIfMenuItemHasProperStructure(tempJObj))) {
					JsonArray tempSubMenu = processSubmenuItems(tempJObj, svr);
					if (tempSubMenu != null) {
						tempJObj.add("sub-menu", tempSubMenu);
					}
					result.add(tempJObj);
				}
			}
		} catch (Exception e) {
			log4j.error(e);
		}
		return result;
	}

	/**
	 * Iterative method in order to do additional checks of the subMenuItems
	 * 
	 * @param menuItem
	 *            The menu item for which we want to validate the sub menu items
	 * @param svr
	 *            SvCore instance in order to get the user permission list
	 *            through the session
	 * @author zpetr
	 *
	 */
	private JsonArray processSubmenuItems(JsonObject menuItem, ISvCore svr) {
		JsonArray resultMenuItems = null;
		if (menuItem.has("sub-menu") && menuItem.get("sub-menu") != null) {
			resultMenuItems = new JsonArray();
			JsonArray subMenuItems = (JsonArray) menuItem.get("sub-menu");
			for (int i = 0; i < subMenuItems.size(); i++) {
				JsonObject tempObj = (JsonObject) subMenuItems.get(i);
				if (!(!checkIfMenuItemIsPermitable(tempObj, svr) || !checkIfMenuItemHasProperStructure(tempObj))) {
					resultMenuItems.add(tempObj);
				}
			}
		}
		return resultMenuItems;
	}

	/**
	 * Method for creating new json instace. Useful when we modify the json per
	 * session
	 * 
	 * @param refJsonObject
	 *            Referent json object
	 * @return
	 */
	private JsonObject createNewInstanceJsonObject(JsonObject refJsonObject) {
		JsonObject jObj = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : refJsonObject.entrySet()) {
			jObj.add(entry.getKey(), entry.getValue());
		}
		return jObj;
	}

	/**
	 * Boolean check if the menu item satisfies the structure defined with
	 * convention\ It can be changed according user/project needs, but it can
	 * affect data lose for previous versions, so a proper consolidation is
	 * needed before changing it
	 * 
	 * @param menuItem
	 *            - the menu item which is the subject of validation
	 * 
	 * @author zpetr
	 *
	 */
	private Boolean checkIfMenuItemHasProperStructure(JsonObject menuItem) {
		Boolean result = true;
		if (!menuItem.has("labelCode") || !menuItem.has("id") || !menuItem.has("menuItemLevel")
				|| !menuItem.has("order") || !menuItem.has("permissionCode") || !menuItem.has("contextMenuLabelCode")) {
			result = false;
		}
		return result;
	}

	/**
	 * Boolean check to estimate if the menu item is accessible for the current
	 * user
	 * 
	 * @param menuItem
	 *            - the menu item for which we are checking the permission for
	 *            the logged in user
	 * @param svr
	 *            -SvCore instance in order to get the user permission list
	 *            through the session
	 * @author zpetr
	 *
	 */
	private Boolean checkIfMenuItemIsPermitable(JsonObject menuItem, ISvCore svr) {
		Boolean result = true;
		if (menuItem.has("permissionCode") && !menuItem.get("permissionCode").isJsonNull()) {
			if (!svr.hasPermission(menuItem.get("permissionCode").getAsString())) {
				result = false;
			}
		}
		return result;
	}
}
