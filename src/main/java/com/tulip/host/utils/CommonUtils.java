package com.tulip.host.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
public class CommonUtils {

    private CommonUtils() {}

    public static Integer calculateAge(Date birthday) {
        Years age = Years.yearsBetween(new LocalDate(birthday), new LocalDate());
        return age.getYears();
    }

    public static String calculateDiscountPercent(double purchasePrice, double sellPrice) {
        double v = ((sellPrice - purchasePrice) / purchasePrice) * 100;
        return String.valueOf(Math.floor(v));
    }

    public static double calculatePurchasePrice(double discountPercent, double sellPrice) {
        Double v = sellPrice * (1 - (discountPercent / 100));
        return Math.ceil(v);
    }

    public static PageRequest getPageRequest(Sort.Direction sortType, String sortProperty, int page, int pageSize) {
        return PageRequest.of(page, pageSize, Sort.by(sortType, sortProperty));
    }

    public static PageRequest getPageRequest(int page) {
        return PageRequest.of(page, 10);
    }

    public static PageRequest getPageRequest(int page, int pageSize) {
        return PageRequest.of(page, pageSize);
    }

    public static Date formatToDate(String date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            log.error("Error while parsing string date : {} , formatter : {}", date, format);
            return null;
        }
    }

    public static String formatFromDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getMonthName(int month) {
        return Month.of(month).name();
    }
}
