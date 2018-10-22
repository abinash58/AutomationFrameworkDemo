package com.mentorstudies.framework.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class TestRunner {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.load(TestRunner.class.getClassLoader().getResourceAsStream("config.properties"));

		// Read test methods from excel file
		String testCaseFileName = properties.getProperty("TEST_CASES_FILE");
		InputStream testCaseFileIS = TestRunner.class.getClassLoader().getResourceAsStream(testCaseFileName);
		XSSFWorkbook workbook = new XSSFWorkbook(testCaseFileIS);
		XSSFSheet sheet = workbook.getSheetAt(0);

		Map<String, List<String>> classMethodsMap = new HashMap<String, List<String>>();
		String currentClassName = null;

		Iterator<Row> allRowsItr = sheet.rowIterator();
		while (allRowsItr.hasNext()) {
			Row currentRow = allRowsItr.next();
			String cell1Value = currentRow.getCell(0).getStringCellValue();
			if (cell1Value.equalsIgnoreCase("TEST CLASS")) {
				currentClassName = currentRow.getCell(1).getStringCellValue();
				classMethodsMap.put(currentClassName, new ArrayList<String>());
				continue;
			}
			String methodName = currentRow.getCell(1).getStringCellValue();
			boolean isEnabled = currentRow.getCell(2).getBooleanCellValue();
			if (isEnabled == true) {
				List<String> classMethodsToRun = classMethodsMap.get(currentClassName);
				classMethodsToRun.add(methodName);
			}
		}
		workbook.close();

		TestNG testNG = new TestNG();

		XmlSuite suite = new XmlSuite();
		suite.setName(properties.getProperty("TEST_SUITE_NAME"));

		XmlTest test1 = new XmlTest(suite);
		test1.setName(properties.getProperty("TEST_NAME"));

		List<XmlClass> classes = new ArrayList<XmlClass>();
		for (Entry<String, List<String>> testCaseEntry : classMethodsMap.entrySet()) {
			String className = testCaseEntry.getKey();
			List<String> methods = testCaseEntry.getValue();

			XmlClass xmlClass = new XmlClass(className);

			List<XmlInclude> xmlMethods = new ArrayList<XmlInclude>();
			for (String eachMethod : methods) {
				xmlMethods.add(new XmlInclude(eachMethod));
			}
			if (methods.size() >= 1) {
				xmlClass.setIncludedMethods(xmlMethods);
				classes.add(xmlClass);
			}
		}

		test1.setXmlClasses(classes);

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		testNG.setXmlSuites(suites);

		testNG.run();
	}
}
