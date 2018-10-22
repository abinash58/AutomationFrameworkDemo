package com.tests.module3;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CalculatorTest {

	private Calculator calculator;

	@BeforeClass
	public void init() {
		calculator = new Calculator();
	}

	@Test(dataProvider = "AdditionDataProviderTwoParams")
	public void testAddTwoNos(int x, int y, int expected) {
		int actual = calculator.add(x, y);
		Assert.assertEquals(actual, expected);
	}

	@DataProvider(name = "AdditionDataProviderTwoParams")
	public Object[][] generateAdditionDataTwoParams() {
		Object[][] dataSet = {
								{ 1, 2, 3 },
								{ -1, -2, -3 },
								{ -1, 1, 0 },
								{ 90129019, -90129020, -1 },
								{ 0, 0, 0 },
							};
		return dataSet;
	}
}