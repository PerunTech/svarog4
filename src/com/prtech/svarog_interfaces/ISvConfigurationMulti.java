package com.prtech.svarog_interfaces;

import java.util.List;

/**
 * Interface which shall allow continuous Run of SvConfiguration. A normal
 * instance of ISvConfiguration shall be executed only once. After the execution
 * Svarog will mark the class as executed and will not execute it again. If an
 * interface of Multi type, in that case Svarog will check if the existing
 * version has been executed. If the version has not been executed, it will
 * execute it again. The currently executed version is passed as input
 * parameter, so if one wants to make sure that the configuration will always be
 * executed, it should use the current version as base and increment
 * 
 * @author ristepejov
 *
 */
public interface ISvConfigurationMulti extends ISvConfiguration {
	/**
	 * Svarog will call this method in order to discover if the interface version
	 * needs to be executed.
	 * 
	 * @param currentVersion Svarog will provide the last executed version as input,
	 *                       so the interface can decide what kind of upgrade it
	 *                       will invoke
	 * @return The version number of the interface
	 */
	int getVersion(int currentVersion);

	/**
	 * Method which provides information about the types of configuration which this
	 * configurator will provide. If the update is not in the List svarog will not
	 * call the update configuration method
	 * 
	 * @return List of UpdateType configurations provided by this ISvConfiguration
	 */
	List<UpdateType> getUpdateTypes();

}
