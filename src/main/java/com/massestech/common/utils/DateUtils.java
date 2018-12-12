package com.massestech.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间操作工具类
 */
public class DateUtils {

	/**
	 * 取得当前日期时间，格式：yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentDateTime() {
		return formatDateByStyle(new Date(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 取得当前日期时间，格式自定义
	 */
	public static String getCurrentDate(String style) {
		return formatDateByStyle(new Date(), style);
	}

	/**
	 * 取得当前日期，格式：yyyy-MM-dd
	 */
	public static String getCurrentDate() {
		return formatDateByStyle(new Date(), "yyyy-MM-dd");
	}

	/**
	 * 取得当前时间，格式：HH:mm:ss
	 */
	public static String getCurrentTime() {
		return formatDateByStyle(new Date(), "HH:mm:ss");
	}

	/**
	 * 日期字符串转换成Date,style:格式自定义

	 */
	public static Date parse(String str, String style) {
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		Date date = null;
		try {
			date = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return date;
	}

	/**
	 * 日期转换成字符串,style:格式自定义

	 */
	public static String formatDateByStyle(Date date, String style) {
		if (date == null || StringUtils.isEmpty(style)) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		return formatter.format(date);
	}

	public static String formatDate(Date date) {
		return formatDateByStyle(date, "yyyy-MM-dd");
	}

	/**
	 * 日期转换成字符串，格式：yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateTime(Date date) {
		return formatDateByStyle(date, "yyyy-MM-dd HH:mm:ss");
	}

	/** 获取当前日期X天后的日期
	 * @param day 天数
	 * @return
	 */
	public static Date getDateAfterDay (int day) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	/** 获取日期X天后的日期
	 * @param day 天数
	 * @return
	 */
	public static Date getDateAfterDay (Date date, int day) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	public static Date getDateAfterDay (String dateStr, int day) {
		Date date = parse(dateStr, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	public static String getDateAfterDayStr (String dateStr, int day) {
		Date date = parse(dateStr, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return formatDate(calendar.getTime());
	}

	/**
	 * 获取当月开始的一天
	 * @return
	 */
	public static Date getMonthBeginDate() {
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat datef=new SimpleDateFormat("yyyy-MM-dd");
		//当前月的第一天
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		Date beginTime=cal.getTime();
		String beginTime1=datef.format(beginTime)+" 00:00:00";
		return beginTime;
	}

	/**
	 * 获取当月结束的最后一天
	 * @return
	 */
	public static Date getMonthEndDate() {
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat datef=new SimpleDateFormat("yyyy-MM-dd");
		//当前月的最后一天
		cal.set( Calendar.DATE, 1 );
		cal.roll(Calendar.DATE, - 1 );
		Date endTime=cal.getTime();
		return endTime;
	}
}