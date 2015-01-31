package com.mxo2.votingapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mxo2.votingapp.utils.Log;

/**
 * This class is having all the methods used for validating a particular input
 * in terms of the required pattern.
 **/
public class Validation {
	public static boolean isPatternMatches(String regexPattern, String str) {
		Pattern pattern = Pattern.compile(regexPattern,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean isValidEmailAddress(String emailAddress) {
		boolean temp = false;
		String emailPattern = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})";
		if (emailAddress.matches(emailPattern)) {
			temp = true;
		}
		return temp;
	}

	public static boolean isValidWebSite(String website) {
		boolean temp = false;

		String urlPattern = "(http(s)?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ;,./?%&=]*)?";

		// String urlPattern =
		// "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
		if (website.matches(urlPattern)) {
			temp = true;
		}
		return temp;
	}

	public static boolean isValidUsername(String input) {
		return (isValidAlphaNumeric(input) || isValidEmailAddress(input));
	}

	public static boolean isValidAlphabet(String input) {
		String expression = "^\\s*([a-zA-Z -_!@#$%&*]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidUrl(String input) {
		String expression = "((?:http|https)://)?(?:www\\.)[\\w\\d\\-_]+\\.\\w{2,3}(\\.\\w{2})?(/(?<=/)(?:[\\w\\d\\-./_]+)?)?";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidAlphabet_lastname(String input) {
		String expression = "^\\s*([a-zA-Z -_!@#$%&*]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidNumeric(String input) {
		String expression = "^\\s*([0-9]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidPhoneNumber(String input) {
		String expression = "^\\s*([0-9 -.()]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidMobileNumber(String input) {
		String expression = "^\\s*([0-9]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidAlphaNumeric(String input) {
		String expression = "^\\s*([0-9a-zA-Z-]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidAlphaNumericWithSpace(String input) {
		String expression = "^\\s*([0-9a-zA-Z -_/]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean isValidNonSpecialCharacters(String input) {
		return !(input.contains("#") || input.contains("~")
				|| input.contains("!") || input.contains("\\")
				|| input.contains("@") || input.contains("$")
				|| input.contains("%") || input.contains("^")
				|| input.contains("*") || input.contains("(")
				|| input.contains(")") || input.contains("=")
				|| input.contains("{") || input.contains("}")
				|| input.contains("<") || input.contains(">")
				|| input.contains("`") || input.contains("[") || input
					.contains("]"));
	}

	public static boolean isValidAlphaNumericWithoutSpecialCharacter(
			String input) {
		String expression = "^\\s*([0-9a-zA-Z -]*)\\s*$";
		return isPatternMatches(expression, input);
	}

	public static boolean containsDigit(final String s) {
		for (char c : s.toCharArray()) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsLetter(final String s) {
		for (char c : s.toCharArray()) {
			if (Character.isLetter(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidGeoCoordinate(String input) {
		boolean correct = false;
		String expression = "^\\s*([0-9.-]*)\\s*$";
		try {
			Double value = Double.valueOf(input);
			if ((value <= 180.00) && (value >= -180.00)) {
				correct = isPatternMatches(expression, input);
			}
			return correct;
		} catch (Exception e) {
			Log.v("Exception --> ", e.toString());
			return false;
		}

	}

}
