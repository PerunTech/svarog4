package com.prtech.svarog;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

public class CodeListTest {

	@Test
	public void getCodeListMap() throws SvException {
		try {
			CodeList cl = new CodeList();
			HashMap<String, Long> hm = cl.getCodeListValues(Sv.ROOT_CODELIST);
			System.out.println(hm.toString());
			hm = cl.getCodeListValues(hm.get("ORG_UNIT_TYPE"));
			System.out.println(hm.toString());
		} catch (Exception e) {
			fail("failed codelists test");
			// TODO: handle exception
		}
	}
}
