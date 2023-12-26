package com.prtech.svarog;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

public class CodeListTest {

	@Test
	public void getCodeListMap() throws SvException {
		try(CodeList cl = new CodeList()) {
			
			HashMap<String, Long> hm = cl.getCodeListValues(Sv.ROOT_CODELIST);
			if (hm.containsKey("ORG_UNIT_TYPE")) {
				hm = cl.getCodeListValues(hm.get("ORG_UNIT_TYPE"));
				if (hm.size() < 1)
					fail("codelists fail");

			} else
				fail("codelists fail");
		} catch (Exception e) {
			fail("failed codelists test");
			// TODO: handle exception
		}
	}
}
