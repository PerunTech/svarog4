package com.prtech.svarog_interfaces;

import com.prtech.svarog.SvException;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.ResponseHandler;

public interface ISvarogScore {
	public DbDataObject scoring(ISvCore svr) throws SvException;

	public DbDataObject createSoreObject(Long parent_id, Long batchId, String name, String note) throws SvException;

	public ResponseHandler changeStatus(Long scoreId, String toStatus, String token) throws SvException, Exception;
}
