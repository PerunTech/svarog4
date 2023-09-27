package com.prtech.svarog_interfaces;

import com.prtech.svarog.SvException;
import com.prtech.svarog_common.DbDataArray;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.ResponseHandler;

public interface ISample {
	/**
	 * Constructor,
	 * 
	 * @param params
	 * @param sample DbDataArray for the sample that we are getting items out of
	 */

	public DbDataArray getRandomSample(ISvCore svr) throws SvException;

	/**
	 * method to get random sample , which is controlPercent * randomPercent of the
	 * total sample Array
	 * 
	 * @param svr SvReader connected to database
	 * @return DbDataArray of all objects that are random extracted
	 * @throws SvException
	 */
	public DbDataArray getRandomSampleV1(ISvCore svr) throws SvException;

	public DbDataArray getRiskSample(ISvCore svr) throws SvException;

	/**
	 * method to extract top and bottom percent of the sample by parameters
	 * controlPercent x riskPercent x topRiskPercent + controlPercent x riskPercent
	 * x lowRiskPercent
	 * 
	 * @param svr SvReader connected to database
	 * @return DbDataArray of all objects that are random extracted
	 * @throws SvException
	 */
	public DbDataArray getRiskSampleTopBottom(ISvCore svr) throws SvException;

	public DbDataArray getADHOCSample(ISvCore svr) throws SvException;

	public DbDataObject createSampleObject(Long parent_id, Long batchId, String name, String note)
			throws SvException;

	public ResponseHandler changeStatus(Long scoreId, String toStatus, String token) throws SvException;

	public void setRisk_percent(Double riskPercent);

	public abstract void setAlreadyExtracted(DbDataArray extracted);

}
