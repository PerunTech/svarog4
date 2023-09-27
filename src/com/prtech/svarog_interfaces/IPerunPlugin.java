package com.prtech.svarog_interfaces;

import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Interface to allow svarog bundles to register front end components and module
 * configuration. Each bundle which contains minified JavaScript with React
 * components must use this interface in order to register its presence in the
 * Svarog platform. When the bundle is registered for the first time Svarog will
 * use the methods to identify the plugin and register its data into the svarog
 * database. If a bundle registeres more than one Perun Plugin interface, only
 * the first one will be loaded. The second will raise an error.
 * 
 * @author ristepejov
 *
 */
public interface IPerunPlugin {
	/**
	 * Method to return the version of the plugin. The version is used for
	 * upgrading purposes. If the version of the plugin is higher then the one
	 * in the database in that case the flag {@link #replaceMenuOnNew()} and
	 * {@link #replaceContextMenuOnNew()} will be checked in order to upgrade
	 * the JSON configuration in the database
	 * 
	 * @return The current version of the plugin
	 */
	int getVersion();

	/**
	 * The context path on which the plugin is registered within the HTTP server
	 * 
	 * @return String representing the HTTP context
	 */
	String getContextName();

	/**
	 * The path of the minified JavaScript file containing the main React.JS
	 * component to be rendered in the frontend.
	 * 
	 * @return String representing the path to the file
	 */
	String getJsPluginUrl();

	/**
	 * The path of the icon to be displayed in the list of modules
	 * 
	 * @return String representing the path to the image
	 */
	String getIconPath();

	/**
	 * Label code to be used for translation purposes. The name of the module
	 * and its description can be fetched through the label code
	 * 
	 * @return Label code from the table Svarog_Labels
	 */
	String getLabelCode();

	/**
	 * Permission code to be used for authorisation purposes.
	 * 
	 * @return Permission code to be mapped to svarog permissions
	 */
	String getPermissionCode();

	/**
	 * Numeric value according to which the main menu with modules/plugings will
	 * be sorted
	 * 
	 * @return Numeric sort order
	 */
	int getSortOrder();

	/**
	 * Method to return the JSON object representing the main module menu. This
	 * menu is used to configure the frontend.
	 * 
	 * @param existingMenu
	 *            The existing menu configuration from the database is passed as
	 *            reference
	 * @param core
	 *            The SvCore instance used to validate permissions
	 * 
	 * @return The menu configuration for the plugin
	 */
	JsonObject getMenu(JsonObject existingMenu, ISvCore core);

	/**
	 * If this flag is set to true, svarog use the return value of the
	 * {@link #getMenu(JsonObject)} method to update the menu configuration in
	 * the database (if version update is needed)
	 * 
	 * @return
	 */
	boolean replaceMenuOnNew();

	/**
	 * Method to return the JSON object representing the module context menu.
	 * This menu is used to configure the frontend.
	 * 
	 * @param contextMap
	 *            The map of parameters from the frontend describing the context
	 *            for the menu request
	 * @param existingMenu
	 *            The existing menu configuration from the database is passed as
	 *            reference
	 * @param core
	 *            The SvCore instance used to validate permissions
	 * @return The configuration of the context menu for the plugin
	 */
	JsonObject getContextMenu(HashMap<String, String> contextMap, JsonObject existingMenu, ISvCore core);

	/**
	 * If this flag is set to true, svarog use the return value of the
	 * {@link #getContextMenu(HashMap, JsonObject)} method to update the menu
	 * configuration in the database (if version update is needed)
	 * 
	 * @return
	 */
	boolean replaceContextMenuOnNew();
	
	/**
	 * Method to return a list of dependencies of the plugin. The list of
	 * dependencies shall contain the context paths identiefied by the method
	 * {@link #getContextName()} of each of the other plugin instances
	 * 
	 * @return List of plugin dependencies of the plugin
	 */
	List<String> dependencies();

}
