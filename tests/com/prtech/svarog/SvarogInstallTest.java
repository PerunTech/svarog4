package com.prtech.svarog;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.prtech.svarog_common.DbDataArray;
import com.prtech.svarog_common.DbDataField;
import com.prtech.svarog_common.DbDataObject;
import com.prtech.svarog_common.DbDataTable;
import com.prtech.svarog_common.DbDataTableExtension;
import com.prtech.svarog_common.IDbInit;
import com.prtech.svarog_common.DbDataField.DbFieldType;

public class SvarogInstallTest {

	@Test
	public void shouldUpgradeConfig() {
		try (SvReader svr = new SvReader()) {
			DbDataObject dbt = SvCore.getDbt(svCONST.OBJECT_TYPE_FIELD);
			DbDataObject dbo1 = new DbDataObject(dbt.getObjectId());
			DbDataObject dbo2 = new DbDataObject(dbt.getObjectId());
			DbDataArray dboFields = svr.getFields(dbt.getObjectId());
			// set the fields which should not trigger update and see if we need
			// upgrade
			dbo2.setVal(Sv.GUI_METADATA, "SOME");
			dbo2.setVal(Sv.EXTENDED_PARAMS, "SOME");
			if (SvarogInstall.shouldUpgradeConfig(dbo1, dbo2, dboFields))
				fail("shouldUpgrade returned true!");
			// no upgrade needed we are good, now set a field which must trigger
			// upgrade the label code is number 12!
			String fldName = dboFields.getItems().get(12).getVal("FIELD_NAME").toString();
			dbo2.setVal(fldName, "SOME");
			if (!SvarogInstall.shouldUpgradeConfig(dbo1, dbo2, dboFields))
				fail("shouldUpgrade returned false!");

			dbt = SvCore.getDbt(57L);
			System.out.println(dbt.getVal(Sv.GUI_METADATA).toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception was raised");
		}

	}

	@Test
	public void testTableExtension() {
		try {
			DbDataTableExtension testExt = new DbDataTableExtension();
			testExt.setBaseSchema(DbInit.CONST_DEFAULT_SCHEMA);
			testExt.setBaseTableName(Sv.REPO_TABLE_NAME + "_rules");

			DbDataField dbf6 = new DbDataField();
			dbf6.setDbFieldName("RULE_LABEL");
			dbf6.setDbFieldType(DbFieldType.NVARCHAR);
			dbf6.setDbFieldSize(200);
			dbf6.setIsNull(true);
			dbf6.setLabel_code(Sv.MASTER_REPO + Sv.DOT + "rule_label200");
			testExt.addDbDataField(dbf6);

			DbDataField dbf7 = new DbDataField();
			dbf7.setDbFieldName("RULE_LABEL_EXT");
			dbf7.setDbFieldType(DbFieldType.NVARCHAR);
			dbf7.setDbFieldSize(200);
			dbf7.setIsNull(true);
			dbf7.setLabel_code(Sv.MASTER_REPO + Sv.DOT + "rule_label_ext");
			testExt.addDbDataField(dbf7);

			Map<IDbInit, String> dbInits = new HashMap<>();
			Map<String, List<DbDataTable>> tables = DbInit.getDbInitTableList(dbInits);
			DbDataTable original = DbInit.findBaseTable(tables, testExt.getBaseTableName(), testExt.getBaseSchema());
			int originalSizeCount = original.getDbTableFields().length;

			List<DbDataTable> extList = new ArrayList<>();
			extList.add(testExt);
			tables.put("extension_test.jar", extList);

			tables = DbInit.applyDbDataTableExtensions(tables);
			if (tables.get("extension_test.jar").size() > 0)
				fail("The extension was not removed from the tables list");

			DbDataTable updated = DbInit.findBaseTable(tables, testExt.getBaseTableName(), testExt.getBaseSchema());
			int updatedSizeCount = updated.getDbTableFields().length;

			if (updatedSizeCount != originalSizeCount + 1)
				fail("The original field count is not incread by one");
			boolean foundNewField = false;
			for (int i = 0; i < updated.getDbTableFields().length; i++) {
				DbDataField dbf = updated.getDbTableFields()[i];
				if (dbf.getDbFieldName().equalsIgnoreCase("RULE_LABEL")) {
					if (!dbf.getDbFieldSize().equals(200))
						fail("The original field size was not set to 200");
					if (!dbf.getLabelCode().equalsIgnoreCase(Sv.MASTER_REPO + Sv.DOT + "rule_label200"))
						fail("The original field label was not set to:" + Sv.MASTER_REPO + Sv.DOT + "rule_label200");

				}
				if (dbf.getDbFieldName().equalsIgnoreCase("RULE_LABEL_EXT")) {
					foundNewField = true;
				}

			}
			if (!foundNewField)
				fail("The new field was not found");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception was raised");
		}

	}
}
