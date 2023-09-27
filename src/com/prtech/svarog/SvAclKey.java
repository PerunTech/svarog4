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

import com.prtech.svarog_common.DbDataObject;

/**
 * Enumeration holding flags about access over svarog objects.
 * 
 * @author XPS13
 *
 */
public class SvAclKey {
	private final long objectId;
	private final long objectTypeId;
	private final int hash;

	SvAclKey(DbDataObject dboAcl) {
		if (dboAcl.getVal("acl_object_id") != null)
			this.objectId = (Long) dboAcl.getVal("acl_object_id");
		else
			this.objectId = dboAcl.getObject_id();
		if (dboAcl.getVal("acl_object_type") != null)
			this.objectTypeId = (Long) dboAcl.getVal("acl_object_type");
		else
			this.objectTypeId = dboAcl.getObject_type();

		hash = (Long.toString(objectId) + "-" + Long.toString(objectTypeId)).hashCode();
	}

	public long getObjectId() {
		return objectId;
	}

	public long getObjectTypeId() {
		return objectTypeId;
	}

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
		if (!(obj instanceof SvAclKey)) {
			return false;
		}
		return (Long.toString(objectId) + "-" + Long.toString(objectTypeId)).equals(
				Long.toString(((SvAclKey) obj).objectId) + "-" + Long.toString(((SvAclKey) obj).objectTypeId));
	}

}
