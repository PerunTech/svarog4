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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.SvCharId;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class SvUtil {

	protected static final Logger log4j = SvConf.getLogger(SvUtil.class);

	public static final PrecisionModel sdiPrecision = new PrecisionModel(SvConf.getSDIPrecision());
	public static final GeometryFactory sdiFactory = initFactory();

	/**
	 * Public enum to provide information of Java Basic Datatypes in Bytes
	 * 
	 * @author ristepejov
	 *
	 */
	public enum sizeof {
		;
		/**
		 * Well the size of BYTE in bytes is always 1
		 */
		public static final int BYTE = 1;
		public static final int INT = Integer.SIZE / Byte.SIZE;
		public static final int LONG = Long.SIZE / Byte.SIZE;
		public static final int FLOAT = Float.SIZE / Byte.SIZE;
		public static final int DOUBLE = Double.SIZE / Byte.SIZE;
	}

	/***
	 * Method for generating a MD5 hash from a string
	 * 
	 * @param pass The string from which a hash should be generated
	 * @return The hash in string format
	 */
	public static String getMD5(String pass) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			byte[] data = pass.getBytes();
			m.update(data, 0, data.length);
			BigInteger i = new BigInteger(1, m.digest());
			return String.format("%1$032X", i);
		} catch (Exception ex) {
			log4j.error("Error generating MD5. ", ex);
		}
		return "";
	}

	private static GeometryFactory initFactory() {
		int srid = 0;
		if (!SvConf.getSDISrid().equals(Sv.SQL.NULL))
			srid = Integer.parseInt(SvConf.getSDISrid());
		return new GeometryFactory(sdiPrecision, srid);
	}

	/**
	 * Method to match a given field name of a DbDataObject to a required value. The
	 * filter value can be null, meaning it would match a dbo which has no field
	 * set. If the dbo is null or the field name, it will return false.
	 * 
	 * @param dbo             The {@link DbDataObject} to whose field should be
	 *                        matched
	 * @param filterFieldName The name of the object field, as returned by
	 *                        {@link DbDataObject} getVal method
	 * @param filterValue     The value which we want to match
	 * @return False if the value has not been matched or the input parameters were
	 *         null (except filterValue)
	 */
	public static boolean fieldMatchValue(DbDataObject dbo, SvCharId filterFieldName, Object filterValue) {
		if (dbo == null || filterFieldName == null)
			return false;
		Object fieldValue = dbo.getVal(filterFieldName, true);
		if (fieldValue == null)
			return false;
		else
			return fieldValue.equals(filterValue);
	}

	/**
	 * Method for uppercasing an ASCII string. This method ONLY uppercases the
	 * standard asci chars from a-z to A-Z. It doesn't perform ANSI/unicode
	 * uppercase.
	 * 
	 * @param inStr The string to be uppercased
	 * @return array of uppercased chars
	 */
	public static String svUpperCase(String inStr) {
		char value[] = inStr.toCharArray();
		int firstLower;
		final int len = inStr.length();

		/* Now check if there are any characters that need to be changed. */

		for (firstLower = 0; firstLower < len;) {
			int c = (int) value[firstLower];
			if (c >= 97 && c <= 122) {
				value[firstLower] = (char) (c - 32);
			}
			firstLower += 1;
		}
		return new String(value);
	}

	/**
	 * Method for uppercasing an ASCII string. This method ONLY uppercases the
	 * standard asci chars from a-z to A-Z. It doesn't perform ANSI/unicode
	 * uppercase.
	 * 
	 * @param inStr The string to be uppercased
	 * @return array of uppercased chars
	 */
	public static String svUpperCase(char[] inStr) {
		char value[] = new char[inStr.length];
		System.arraycopy(inStr, 0, value, 0, inStr.length);
		int firstLower;
		final int len = inStr.length;

		/* Now check if there are any characters that need to be changed. */

		for (firstLower = 0; firstLower < len;) {
			int c = (int) value[firstLower];
			if (c >= 97 && c <= 122) {
				value[firstLower] = (char) (c - 32);
			}
			firstLower += 1;
		}
		return new String(value);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	static JsonObject readJsonFromFile(String fileName, boolean replaceSvarogTags) {
		// init the table configs
		InputStream fis = null;
		JsonObject jobj = null;
		try {
			fis = new FileInputStream(fileName);
			String json = IOUtils.toString(fis, "UTF-8");
			if (replaceSvarogTags) {
				json = json.replace("{MASTER_REPO}", SvConf.getMasterRepo());
				json = json.replace("{DEFAULT_SCHEMA}", SvConf.getDefaultSchema());
				json = json.replace("{REPO_TABLE_NAME}", SvConf.getMasterRepo());
			}
			Gson gson = new Gson();
			jobj = gson.fromJson(json, JsonElement.class).getAsJsonObject();
		} catch (IOException e) {
			log4j.error("File " + fileName + " was not found or its not readable", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					log4j.error("File stream close raised exceptin", e);
				}
		}
		return jobj;
	}

	/**
	 * Simple function for writing a string variable to a file
	 * 
	 * @param fileName the file to which the string should be written
	 * @param strValue the String which should be written
	 */
	public static void saveStringToFile(String fileName, String strValue) {
		FileOutputStream fop = null;
		File file;
		Writer out = null;
		try {

			file = new File(fileName);
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			boolean exists = file.exists();
			if (!exists)
				exists = file.createNewFile();

			if (exists) {
				fop = new FileOutputStream(file);
				out = new BufferedWriter(new OutputStreamWriter(fop, "UTF8"));

				// get the content in bytes
				out.append(strValue);

				out.flush();
			} else
				log4j.error("Error writing to file. File " + fileName + " doesn't exists");

		} catch (IOException e) {
			log4j.error("Error writing to file.", e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				log4j.error("Error writing to file.", e);
			}

			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				log4j.error("Error writing to file.", e);
			}
		}

	}

	/**
	 * Method to produce a string with list of IP addresses of the system
	 * 
	 * @param includHeartBeatPort If the flag is set, the heart beat port will be
	 *                            appended at the end of the ip delimited by colon
	 * @param delimiter           The string delimiter will be used to delimit ip
	 *                            addesses. The colon can not be used as IP
	 *                            delimiter
	 * @return String of ip addresses concatenated with delimiter.
	 * @throws UnknownHostException
	 */
	static String getIpAdresses(boolean includHeartBeatPort, String delimiter) throws UnknownHostException {
		StringBuilder sbr = new StringBuilder();
		for (InetAddress ad : SvUtil.getLocalHostLANAddress()) {
			if (!ad.getHostAddress().toString().equals(SvConf.getVmBridgeIPAddress()))
				sbr.append(ad.getHostAddress().toString() + (includHeartBeatPort ? ":" + SvConf.getHeartBeatPort() : "")
						+ delimiter);
		}
		sbr.setLength(sbr.length() - 1);
		return sbr.toString();

	}

	/**
	 * Returns an <code>InetAddress</code> object encapsulating what is most likely
	 * the machine's LAN IP address.
	 * <p/>
	 * This method is intended for use as a replacement of JDK method
	 * <code>InetAddress.getLocalHost</code>, because that method is ambiguous on
	 * Linux systems. Linux systems enumerate the loopback network interface the
	 * same way as regular LAN network interfaces, but the JDK
	 * <code>InetAddress.getLocalHost</code> method does not specify the algorithm
	 * used to select the address returned under such circumstances, and will often
	 * return the loopback address, which is not valid for network communication.
	 * Details <a href=
	 * "http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
	 * <p/>
	 * This method will scan all IP addresses on all network interfaces on the host
	 * machine to determine the IP address most likely to be the machine's LAN
	 * address. If the machine has multiple IP addresses, this method will prefer a
	 * site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the
	 * machine has one (and will return the first site-local address if the machine
	 * has more than one), but if the machine does not hold a site-local address,
	 * this method will return simply the first non-loopback address found (IPv4 or
	 * IPv6).
	 * <p/>
	 * If this method cannot find a non-loopback address using this selection
	 * algorithm, it will fall back to calling and returning the result of JDK
	 * method <code>InetAddress.getLocalHost</code>.
	 * <p/>
	 *
	 * @throws UnknownHostException If the LAN address of the machine cannot be
	 *                              found.
	 */
	static ArrayList<InetAddress> getLocalHostLANAddress() throws UnknownHostException {
		ArrayList<InetAddress> addressList = new ArrayList<InetAddress>();
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
					.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<?> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it
							// immediately...
							addressList.add(inetAddr);
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily
							// site-local.
							// Store it as a candidate to be returned if
							// site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback
							// non-site-local addresses as candidates,
							// only the first. For subsequent iterations,
							// candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null && addressList.size() == 0) {
				// We did not find a site-local address, but we found some other
				// non-loopback address.
				// Server might have a non-site-local address assigned to its
				// NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				addressList.add(candidateAddress);
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			} else if (addressList.size() == 0)
				addressList.add(jdkSuppliedAddress);
			return addressList;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	/**
	 * Method to fetch the calling class name from the stack trace.
	 * 
	 * @return The class name of the caller class
	 */
	public static String getCallerClassName(Class<?> thisClass) {
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		String threadClassName = java.lang.Thread.class.getName();
		String svUtilClassName = SvUtil.class.getName();
		String thisClassName = thisClass.getName();
		String callerClass = null;
		for (StackTraceElement strace : traces) {
			String currClass = strace.getClassName();
			if (currClass.equals(threadClassName) || currClass.equals(svUtilClassName)
					|| currClass.equals(thisClassName))
				continue;
			callerClass = currClass;
			break;
		}
		return callerClass;
	}

}
