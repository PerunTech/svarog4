package com.prtech.svarog_common;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.prtech.svarog.SvException;
import com.prtech.svarog.svCONST;

/**
 * Extension of the {@link DbQueryObject} class that supports the GROUP BY and
 * HAVING SQL clauses
 * 
 * @see DbQueryObject#getSQLExpression(Boolean forcePhysicalTables, Boolean
 *      includeGeometries)
 */
public class ExtendedDbQueryObject extends DbQueryObject {
	/**
	 * Fields that will be used in the GROUP BY clause in the database query
	 */
	List<String> groupByFields;
	/**
	 * The condition for the HAVING clause
	 */
	String havingCondition;

	public ExtendedDbQueryObject() {
	}

	/**
	 * Default ExtendedDbQueryObject constructor.
	 * 
	 * @param dbt             The object descriptor for the table based on which DQO
	 *                        will generate a query
	 * @param search          Search criteria
	 * @param referenceDate   The reference date for which we'll fetch the dataset
	 * @param joinToNext      The next object join type
	 * @param groupByFields   The list of fields in the GROUP BY clause
	 * @param havingCondition Condition for the HAVING clause
	 * @throws SvException If mandatory parameters are omitted an Exception is
	 *                     thrown
	 */
	public ExtendedDbQueryObject(DbDataObject dbt, DbSearch search, DateTime referenceDate, DbJoinType joinToNext,
			List<String> groupByFields, String havingCondition) throws SvException {
		super(dbt, search, referenceDate, joinToNext);
		this.groupByFields = groupByFields;
		this.havingCondition = havingCondition;
	}

	/**
	 * Constructor to construct a ExtendedDbQueryObject based on dbt.
	 * 
	 * @param dbt             The Object Type descriptor for which this DQO will
	 *                        generate a query
	 * @param search          Search criteria
	 * @param joinToNext      Criteria for joining to the next DQO
	 * @param linkToNext      The link descriptor for joining by link
	 * @param linkToNextType  The type of join to the next DQO
	 * @param orderByFields   The list of fields in the ORDER BY clause
	 * @param groupByFields   The list of fields in the GROUP BY clause
	 * @param havingCondition Condition for the HAVING clause
	 * @param referenceDate   The reference date of the DQO
	 * @throws SvException If mandatory parameters are omitted an Exception is
	 *                     thrown
	 */
	public ExtendedDbQueryObject(DbDataObject dbt, DbSearch search, DbJoinType joinToNext, DbDataObject linkToNext,
			LinkType linkToNextType, ArrayList<String> orderByFields, List<String> groupByFields,
			String havingCondition, DateTime referenceDate) throws SvException {
		super(dbt, search, joinToNext, linkToNext, linkToNextType, orderByFields, referenceDate);
		this.groupByFields = groupByFields;
		this.havingCondition = havingCondition;
	}

	@Override
	public StringBuilder getSQLExpression(Boolean forcePhysicalTables, Boolean includeGeometries) throws SvException {
		if (subQuery != null && !subQuery.equals(""))
			return new StringBuilder().append(subQuery);

		if (repo == null || repoFields == null || dbt == null || dbtFields == null)
			throw (new SvException("system.error.dqo_missing_dbt", svCONST.systemUser, null, this));

		String prefix = this.getSqlTablePrefix() != null ? this.getSqlTablePrefix()
				: "tbl" + this.getReturnTypeSequence();

		StringBuilder sqlQry = null;
		if (forcePhysicalTables) {
			sqlQry = getTableSql("rep" + this.getReturnTypeSequence(), prefix, includeGeometries);
			if (search != null)
				sqlQry.append(" WHERE " + search.getSQLExpression(prefix));

		} else {
			sqlQry = getTableSql(null, prefix, includeGeometries);
			if (search != null)
				sqlQry.append(" WHERE " + search.getSQLExpression(prefix));
		}

		if (groupByFields != null && groupByFields.size() > 0) {
			sqlQry.append(" GROUP BY ");
			for (String fldName : groupByFields) {
				sqlQry.append(fldName + ",");
			}
			sqlQry.deleteCharAt(sqlQry.length() - 1);
		}

		if (havingCondition != null && !havingCondition.trim().isEmpty()) {
			sqlQry.append(" HAVING ").append(havingCondition);
		}

		if (orderByFields != null && orderByFields.size() > 0) {
			sqlQry.append(" ORDER BY ");
			for (String fldName : orderByFields) {
				sqlQry.append(fldName + ",");
			}
			sqlQry.deleteCharAt(sqlQry.length() - 1);
		}
		return sqlQry;
	}
}
