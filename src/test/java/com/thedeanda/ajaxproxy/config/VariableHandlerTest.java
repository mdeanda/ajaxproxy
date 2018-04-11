package com.thedeanda.ajaxproxy.config;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.config.model.Variable;

public class VariableHandlerTest {

	private VariableHandler handler;
	private List<Variable> variables;

	@Before
	public void init() {
		handler = new VariableHandler();

		variables = new ArrayList<>();
		variables.add(Variable.builder().key("foo").value("bar").build());
		variables.add(Variable.builder().key("port").value("8080").build());
	}

	@Test
	public void testEmpty() {
		String output = handler.sub(null, null);
		assertEquals("", output);
	}

	@Test
	public void testNoVars() {
		String output = handler.sub(variables, "hello world");
		assertEquals("hello world", output);
	}

	@Test
	public void testSimpleVars() {
		String output = handler.sub(variables, "hello ${port} world");
		assertEquals("hello 8080 world", output);

		output = handler.sub(variables, "hello ${foo} world");
		assertEquals("hello bar world", output);

		output = handler.sub(variables, "hello ${donkey} world");
		assertEquals("hello ${donkey} world", output);
	}

	@Test
	public void testDoubleVars() {
		String output = handler.sub(variables, "hello ${foo} ${bar} ${port} world");
		assertEquals("hello bar ${bar} 8080 world", output);
	}

	@Test(expected = NumberFormatException.class)
	public void testIntBad1() {
		handler.subForInt(variables, "${foo}");
	}

	@Test(expected = NumberFormatException.class)
	public void testIntBad2() {
		handler.subForInt(variables, "x${port}");
	}

	@Test
	public void testIntGood1() {
		int port = handler.subForInt(variables, "${port}");
		assertEquals(8080, port);
	}

	@Test
	public void testIntGood2() {
		int port = handler.subForInt(variables, "10${port}");
		assertEquals(108080, port);
	}
}
