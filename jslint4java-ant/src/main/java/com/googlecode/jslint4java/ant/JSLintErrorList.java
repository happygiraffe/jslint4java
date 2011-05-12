package com.googlecode.jslint4java.ant;

import java.util.HashMap;
import java.util.Map;

public class JSLintErrorList {
	private Map<String, String> errors = new HashMap<String, String>();
	
	/*
	 * Steps to recreate the HashMap from the .js file:
	 * 
	 * 1. Pull out the "bundle" variable definition from fulljslint.js; paste into text editor
	 * 2. Replace all the spacing at the start of each line so that each line starts at column 1
	 * 3. Ensure any messages that span 2+ lines in the JS are trimmed onto a single line (search for the + character; should be no occurrences)
	 * 4. Search for: ",\n" (including quotes) and replace with: ");\nerrorss.put(" (with quotes)
	 * 5. Now search for: : " (colon space quote) and replace with ", " (quote comma space quote)
	 * 6. You will have to manually 'fix' the first and last message as the search/replace misses these
	 * 7. We now have to swap around the code and message....
	 * 8. Regex search for: (".*"), (".*") and replace with: $2, $1
	 * 9. Paste over the code in the constructor below.
	 * 
	 */
	
	public JSLintErrorList() {
		errors.put("'{a}' is a function.", "a_function");
		errors.put("'{a}' is a statement label.", "a_label");
		errors.put("'{a}' is not allowed.", "a_not_allowed");
		errors.put("'{a}' is not defined.", "a_not_defined");
		errors.put("'{a}' used out of scope.", "a_scope");
		errors.put("ADsafe violation.", "adsafe");
		errors.put("ADsafe violation: '{a}'.", "adsafe_a");
		errors.put("ADsafe autocomplete violation.", "adsafe_autocomplete");
		errors.put("ADSAFE violation: bad id.", "adsafe_bad_id");
		errors.put("ADsafe violation: Wrap the widget in a div.", "adsafe_div");
		errors.put("ADSAFE: Use the fragment option.", "adsafe_fragment");
		errors.put("ADsafe violation: Missing ADSAFE.go.", "adsafe_go");
		errors.put("Currently, ADsafe does not operate on whole HTML documents. It operates on <div> fragments and .js files.", "adsafe_html");
		errors.put("ADsafe violation: id does not match.", "adsafe_id");
		errors.put("ADsafe violation: Missing ADSAFE.id or ADSAFE.go.", "adsafe_id_go");
		errors.put("ADsafe lib violation.", "adsafe_lib");
		errors.put("ADsafe: The second argument to lib must be a function.", "adsafe_lib_second");
		errors.put("ADSAFE violation: missing ID_.", "adsafe_missing_id");
		errors.put("ADsafe name violation: '{a}'.", "adsafe_name_a");
		errors.put("ADsafe script placement violation.", "adsafe_placement");
		errors.put("ADsafe violation: An id must have a '{a}' prefix", "adsafe_prefix_a");
		errors.put("ADsafe script violation.", "adsafe_script");
		errors.put("ADsafe unapproved script source.", "adsafe_source");
		errors.put("ADsafe subscript '{a}'.", "adsafe_subscript_a");
		errors.put("ADsafe violation: Disallowed tag '{a}'.", "adsafe_tag");
		errors.put("'{a}' is already defined.", "already_defined");
		errors.put("The '&&' subexpression should be wrapped in parens.", "and");
		errors.put("Do not assign to the exception parameter.", "assign_exception");
		errors.put("Expected an assignment or function call and instead saw an expression.", "assignment_function_expression");
		errors.put("Attribute '{a}' not all lower case.", "attribute_case_a");
		errors.put("Avoid '{a}'.", "avoid_a");
		errors.put("Bad assignment.", "bad_assignment");
		errors.put("Bad hex color '{a}'.", "bad_color_a");
		errors.put("Bad constructor.", "bad_constructor");
		errors.put("Bad entity.", "bad_entity");
		errors.put("Bad HTML string", "bad_html");
		errors.put("Bad id: '{a}'.", "bad_id_a");
		errors.put("Bad for in variable '{a}'.", "bad_in_a");
		errors.put("Bad invocation.", "bad_invocation");
		errors.put("Bad name: '{a}'.", "bad_name_a");
		errors.put("Do not use 'new' for side effects.", "bad_new");
		errors.put("Bad number '{a}'.", "bad_number");
		errors.put("Bad operand.", "bad_operand");
		errors.put("Bad type.", "bad_type");
		errors.put("Bad url string.", "bad_url");
		errors.put("Do not wrap function literals in parens unless they are to be immediately invoked.", "bad_wrap");
		errors.put("Combine this with the previous 'var' statement.", "combine_var");
		errors.put("Expected a conditional expression and instead saw an assignment.", "conditional_assignment");
		errors.put("Confusing use of '{a}'.", "confusing_a");
		errors.put("Confusing regular expression.", "confusing_regexp");
		errors.put("A constructor name '{a}' should start with an uppercase letter.", "constructor_name_a");
		errors.put("Unexpected control character '{a}'.", "control_a");
		errors.put("A css file should begin with @charset 'UTF-8';", "css");
		errors.put("Unexpected dangling '_' in '{a}'.", "dangling_a");
		errors.put("Dangerous comment.", "dangerous_comment");
		errors.put("Only properties should be deleted.", "deleted");
		errors.put("Duplicate '{a}'.", "duplicate_a");
		errors.put("Empty block.", "empty_block");
		errors.put("Empty case.", "empty_case");
		errors.put("Empty class.", "empty_class");
		errors.put("eval is evil.", "evil");
		errors.put("Expected '{a}'.", "expected_a");
		errors.put("Expected '{a}' and instead saw '{b}'.", "expected_a_b");
		errors.put("Expected '{a}' to match '{b}' from line {c} and instead saw '{d}'.", "expected_a_b_from_c_d");
		errors.put("Expected an at-rule, and instead saw @{a}.", "expected_at_a");
		errors.put("Expected '{a}' at column {b}, not column {c}.", "expected_a_at_b_c");
		errors.put("Expected an attribute, and instead saw [{a}].", "expected_attribute_a");
		errors.put("Expected an attribute value and instead saw '{a}'.", "expected_attribute_value_a");
		errors.put("Expected a class, and instead saw .{a}.", "expected_class_a");
		errors.put("Expected a number between 0 and 1 and instead saw '{a}'", "expected_fraction_a");
		errors.put("Expected an id, and instead saw #{a}.", "expected_id_a");
		errors.put("Expected an identifier and instead saw '{a}'.", "expected_identifier_a");
		errors.put("Expected an identifier and instead saw '{a}' (a reserved word).", "expected_identifier_a_reserved");
		errors.put("Expected a linear unit and instead saw '{a}'.", "expected_linear_a");
		errors.put("Expected a lang code, and instead saw :{a}.", "expected_lang_a");
		errors.put("Expected a CSS media type, and instead saw '{a}'.", "expected_media_a");
		errors.put("Expected a name and instead saw '{a}'.", "expected_name_a");
		errors.put("Expected a non-standard style attribute and instead saw '{a}'.", "expected_nonstandard_style_attribute");
		errors.put("Expected a number and instead saw '{a}'.", "expected_number_a");
		errors.put("Expected an operator and instead saw '{a}'.", "expected_operator_a");
		errors.put("Expected a percentage and instead saw '{a}'", "expected_percent_a");
		errors.put("Expected a positive number and instead saw '{a}'", "expected_positive_a");
		errors.put("Expected a pseudo, and instead saw :{a}.", "expected_pseudo_a");
		errors.put("Expected a CSS selector, and instead saw {a}.", "expected_selector_a");
		errors.put("Expected a small number and instead saw '{a}'", "expected_small_a");
		errors.put("Expected exactly one space between '{a}' and '{b}'.", "expected_space_a_b");
		errors.put("Expected a string and instead saw {a}.", "expected_string_a");
		errors.put("Excepted a style attribute, and instead saw '{a}'.", "expected_style_attribute");
		errors.put("Expected a style pattern, and instead saw '{a}'.", "expected_style_pattern");
		errors.put("Expected a tagName, and instead saw {a}.", "expected_tagname_a");
		errors.put("The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.", "for_if");
		errors.put("Function statements should not be placed in blocks. Use a function expression or move the statement to the top of the outer function.", "function_block");
		errors.put("The Function constructor is eval.", "function_eval");
		errors.put("Don't make functions within a loop.", "function_loop");
		errors.put("Function statements are not invocable. Wrap the whole function invocation in parens.", "function_statement");
		errors.put("Use the function form of \"use strict\".", "function_strict");
		errors.put("get/set are ES5 features.", "get_set");
		errors.put("HTML confusion in regular expression '<{a}'.", "html_confusion_a");
		errors.put("Avoid HTML event handlers.", "html_handlers");
		errors.put("Expected an identifier in an assignment and instead saw a function invocation.", "identifier_function");
		errors.put("Implied eval is evil. Pass a function instead of a string.", "implied_evil");
		errors.put("Unexpected 'in'. Compare with undefined, or use the hasOwnProperty method instead.", "infix_in");
		errors.put("Insecure '{a}'.", "insecure_a");
		errors.put("Use the isNaN function to compare with NaN.", "isNaN");
		errors.put("Label '{a}' on '{b}' statement.", "label_a_b");
		errors.put("lang is deprecated.", "lang");
		errors.put("A leading decimal point can be confused with a dot: '.{a}'.", "leading_decimal_a");
		errors.put("Missing '{a}'.", "missing_a");
		errors.put("Missing '{a}' after '{b}'.", "missing_a_after_b");
		errors.put("Missing option value.", "missing_option");
		errors.put("Missing property name.", "missing_property");
		errors.put("Missing space between '{a}' and '{b}'.", "missing_space_a_b");
		errors.put("Missing url.", "missing_url");
		errors.put("Missing \"use strict\" statement.", "missing_use_strict");
		errors.put("Mixed spaces and tabs.", "mixed");
		errors.put("Move the invocation into the parens that contain the function.", "move_invocation");
		errors.put("Move 'var' declarations to the top of the function.", "move_var");
		errors.put("Missing name in function statement.", "name_function");
		errors.put("Nested comment.", "nested_comment");
		errors.put("Nested not.", "not");
		errors.put("Do not use {a} as a constructor.", "not_a_constructor");
		errors.put("'{a}' has not been fully defined yet.", "not_a_defined");
		errors.put("'{a}' is not a function.", "not_a_function");
		errors.put("'{a}' is not a label.", "not_a_label");
		errors.put("'{a}' is out of scope.", "not_a_scope");
		errors.put("'{a}' should not be greater than '{b}'.", "not_greater");
		errors.put("Unexpected parameter '{a}' in get {b} function.", "parameter_a_get_b");
		errors.put("Expected parameter (value) in set {a} function.", "parameter_set_a");
		errors.put("Missing radix parameter.", "radix");
		errors.put("Read only.", "read_only");
		errors.put("Redefinition of '{a}'.", "redefinition_a");
		errors.put("Reserved name '{a}'.", "reserved_a");
		errors.put("{a} ({b}% scanned).", "scanned_a_b");
		errors.put("A regular expression literal can be confused with '/='.", "slash_equal");
		errors.put("Expected to see a statement and instead saw a block.", "statement_block");
		errors.put("Stopping. ", "stopping");
		errors.put("Strange loop.", "strange_loop");
		errors.put("Strict violation.", "strict");
		errors.put("['{a}'] is better written in dot notation.", "subscript");
		errors.put("A '<{a}>' must be within '<{b}>'.", "tag_a_in_b");
		errors.put("Line too long.", "too_long");
		errors.put("Too many errors.", "too_many");
		errors.put("A trailing decimal point can be confused with a dot: '.{a}'.", "trailing_decimal_a");
		errors.put("type is unnecessary.", "type");
		errors.put("Unclosed string.", "unclosed");
		errors.put("Unclosed comment.", "unclosed_comment");
		errors.put("Unclosed regular expression.", "unclosed_regexp");
		errors.put("Unescaped '{a}'.", "unescaped_a");
		errors.put("Unexpected '{a}'.", "unexpected_a");
		errors.put("Unexpected character '{a}' in {b}.", "unexpected_char_a_b");
		errors.put("Unexpected comment.", "unexpected_comment");
		errors.put("Unexpected property '{a}'.", "unexpected_member_a");
		errors.put("Unexpected space between '{a}' and '{b}'.", "unexpected_space_a_b");
		errors.put("It is not necessary to initialize '{a}' to 'undefined'.", "unnecessary_initialize");
		errors.put("Unnecessary \"use strict\".", "unnecessary_use");
		errors.put("Unreachable '{a}' after '{b}'.", "unreachable_a_b");
		errors.put("Unrecognized style attribute '{a}'.", "unrecognized_style_attribute_a");
		errors.put("Unrecognized tag '<{a}>'.", "unrecognized_tag_a");
		errors.put("Unsafe character.", "unsafe");
		errors.put("JavaScript URL.", "url");
		errors.put("Use the array literal notation [].", "use_array");
		errors.put("Spaces are hard to count. Use {{a}}.", "use_braces");
		errors.put("Use the object literal notation {}.", "use_object");
		errors.put("'{a}' was used before it was defined.", "used_before_a");
		errors.put("Variable {a} was not declared correctly.", "var_a_not");
		errors.put("Weird assignment.", "weird_assignment");
		errors.put("Weird condition.", "weird_condition");
		errors.put("Weird construction. Delete 'new'.", "weird_new");
		errors.put("Weird program.", "weird_program");
		errors.put("Weird relation.", "weird_relation");
		errors.put("Weird ternary.", "weird_ternary");
		errors.put("Wrap an immediate function invocation in parentheses to assist the reader in understanding that the expression is the result of a function, and not the function itself.", "wrap_immediate");
		errors.put("Wrap the /regexp/ literal in parens to disambiguate the slash operator.", "wrap_regexp");
		errors.put("document.write can be a form of eval.", "write_is_wrong");
	}
	
	public String getErrorCodeForRawMessage(String rawMessage) {
		return errors.get(rawMessage);
	}
}