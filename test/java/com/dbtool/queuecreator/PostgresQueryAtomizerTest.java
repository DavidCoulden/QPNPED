package com.dbtool.queuecreator;

import org.junit.Test;

import com.dbtool.queuecreator.AST.SQLStatement;

public class PostgresQueryAtomizerTest {
	@Test
	public void testMethod() {
		PostgresqlQueryAtomizer atomizer = new PostgresqlQueryAtomizer(new FakeServiceRateCalculator());
		//SQLStatement statement = new SQLStatement("SELECT (SELECT count(*) FROM city WHERE id = country.capital) FROM country");
		//SQLStatement statement = new SQLStatement("SELECT * FROM city WHERE city.population > (SELECT COUNT(*) FROM countrylanguage JOIN country ON (country.code = countrylanguage.countrycode))");
		//SQLStatement statement = new SQLStatement("UPDATE city SET name = 'davidland' WHERE name = 'Smelly'");
		SQLStatement statement = new SQLStatement("SELECT * FROM city JOIN country ON city.sid>country.capital AND city.name < country.name AND city.name < country.continent");
		//SQLStatement statement = new SQLStatement("SELECT * FROM country WHERE country.name > 'a'");
		//SQLStatement statement = new SQLStatement("SELECT * FROM city,country WHERE city.id = 3 OR city.id = 5");
		atomizer.atomizeSqlStatment(statement);
	}
}
