package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Variable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VariableHandlerTest {

	private VariableHandler handler;
	private List<Variable> variables;

	@Before
	public void init() {
		variables = new ArrayList<>();
		variables.add(Variable.builder().key("foo").value("bar").build());
		variables.add(Variable.builder().key("port").value("8080").build());

		handler = new VariableHandler(variables);
	}

	@Test
	public void testEmpty() {
		String output = handler.sub(null);
		assertEquals("", output);
	}

	@Test
	public void testNoVars() {
		String output = handler.sub("hello world");
		assertEquals("hello world", output);
	}

	@Test
	public void testSimpleVars() {
		String output = handler.sub("hello ${port} world");
		assertEquals("hello 8080 world", output);

		output = handler.sub("hello ${foo} world");
		assertEquals("hello bar world", output);

		output = handler.sub("hello ${donkey} world");
		assertEquals("hello ${donkey} world", output);
	}

	@Test
	public void testDoubleVars() {
		String output = handler.sub("hello ${foo} ${bar} ${port} world");
		assertEquals("hello bar ${bar} 8080 world", output);
	}

	@Test(expected = NumberFormatException.class)
	public void testIntBad1() {
		handler.subForInt("${foo}");
	}

	@Test(expected = NumberFormatException.class)
	public void testIntBad2() {
		handler.subForInt("x${port}");
	}

	@Test
	public void testIntGood1() {
		int port = handler.subForInt("${port}");
		assertEquals(8080, port);
	}

	@Test
	public void testIntGood2() {
		int port = handler.subForInt("10${port}");
		assertEquals(108080, port);
	}
}
