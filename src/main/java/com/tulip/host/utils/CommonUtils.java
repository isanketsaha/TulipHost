package com.tulip.host.utils;

import java.util.Date;
import org.joda.time.LocalDate;
import org.joda.time.Years;

public class CommonUtils {

    private CommonUtils() {}

    public static Integer calculateAge(Date birthday) {
        Years age = Years.yearsBetween(new LocalDate(birthday), new LocalDate());
        return age.getYears();
    }
}
