package com.tulip.host.utils;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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

    public static PageRequest getPageRequest(String sortType, String sortProperty, int page, int pageSize) {
        Sort.Direction direction = ASC.toString().equalsIgnoreCase(sortType) ? ASC : DESC;

        return PageRequest.of(page, pageSize, Sort.by(direction, sortProperty));
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
}
