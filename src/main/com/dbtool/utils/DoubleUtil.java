package com.dbtool.utils;

public class DoubleUtil {
	private static final double EPSILON = 1e-10;
	
	public static boolean fuzzyEqual(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}
}
