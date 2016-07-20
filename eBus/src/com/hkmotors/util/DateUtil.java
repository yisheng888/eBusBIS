package com.hkmotors.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	private DateUtil() {
	}

	public static final DateFormat DAY_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static final DateFormat MINUTE_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm"); // HH24/hh12

	public static final DateFormat SECOND_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String format(Date date) {
		return SECOND_FORMATER.format(date);
	}

	public static String formateToDay(Date date) {
		if (date == null) {
			return "";
		}
		return DAY_FORMATER.format(date);
	}

	public static String format(Date date, String format) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(format).format(date);
	}

	public static String formatNow(String format) {
		return new SimpleDateFormat(format).format(now());
	}
	
	public static String formatNow() {
		return SECOND_FORMATER.format(now());
	}

	public static String formateToMinute(Date date) {
		if (date == null) {
			return "";
		}
		return MINUTE_FORMATER.format(date);
	}

	public static String formateToSecond(Date date) {
		if (date == null) {
			return "";
		}
		return SECOND_FORMATER.format(date);
	}

	public static Date parseWithDay(String date) throws ParseException {
		return DAY_FORMATER.parse(date);
	}

	public static Date parse(String date, String format) throws ParseException {

		return new SimpleDateFormat(format).parse(date);
	}

	public static Date parseWithMinute(String date) throws ParseException {
		return MINUTE_FORMATER.parse(date);
	}

	public static Date parseWithSecond(String date) throws ParseException {
		return SECOND_FORMATER.parse(date);
	}

	public static Date addMinute(Date time, long minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime() + minute * 60 * 1000);
		return calendar.getTime();
	}

	public static Date addMonth(Date time, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime());
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	public static Date addYear(Date time, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime());
		calendar.add(Calendar.YEAR, year);
		return calendar.getTime();
	}

	public static Date minusDay(Date time, long day) {
		return minusHour(time, day * 24);

	}

	public static Date minusHour(Date time, long hour) {
		return minusMinute(time, hour * 60);
	}

	public static Date minusMinute(Date time, long minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime() - minute * 60 * 1000);

		return calendar.getTime();
	}

	public static Date addHour(Date time, long hours) {
		return addMinute(time, hours * 60);
	}

	public static Date addDay(Date time, long days) {
		return addHour(time, days * 24);
	}

	public static boolean isAbsoluteMidnight(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY) == 0 && c.get(Calendar.MINUTE) == 0
				&& c.get(Calendar.SECOND) == 0;
	}

	public static Date parse(String date) throws ParseException {

		if (date.indexOf(':') == -1) {
			return parseWithDay(date);
		}

		if (date.indexOf(':') == date.lastIndexOf(':')) {
			return parseWithMinute(date);
		}
		return parseWithSecond(date);

	}

	public static String now(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINESE);
		return df.format(now());

	}

	public static Date now() {
		return Calendar.getInstance().getTime();
	}

	public static Date today() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static boolean isLeapYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return isLeapYear(c);
	}

	public static boolean isLeapYear(Calendar c) {
		return isLeapYear(c.get(Calendar.YEAR));
	}

	public static boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
	}

	public static int computeIssueAge(Date birthDate, Date effectiveDate) {
		Calendar birthCalendar = Calendar.getInstance();
		birthCalendar.setTime(birthDate);

		Calendar effectiveCalendar = Calendar.getInstance();
		effectiveCalendar.setTime(effectiveDate);

		int age = effectiveCalendar.get(Calendar.YEAR)
				- birthCalendar.get(Calendar.YEAR);

		if (birthCalendar.get(Calendar.DAY_OF_YEAR) > effectiveCalendar
				.get(Calendar.DAY_OF_YEAR)) {
			age = age - 1;
		}

		return age;
	}
	
}
