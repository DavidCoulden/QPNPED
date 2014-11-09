package com.dbtool.utils;
/**
 * Enum used to denote whether client think times are exponential or deterministic
 */
public enum ClientThinkTimeDistribution {
	EXPONENTIAL, DETERMINISTIC;
	
	public static ClientThinkTimeDistribution getValue(String enumStr) {
		try {
			return ClientThinkTimeDistribution.valueOf(enumStr.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
}
