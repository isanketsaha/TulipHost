package com.tulip.host.utils;

import static com.tulip.host.config.Constants.MONTH_YEAR_FORMAT;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.tulip.host.domain.FeesLineItem;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.FeesRuleType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.util.Precision;
import org.joda.time.LocalDate;
import org.joda.time.Months;
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

    public static int calculatePendingMonthFees(Student student, Date sessionFrom) {
        Set<Transaction> transactions = student.getTransactions();
        Transaction transaction = transactions.stream().findFirst().orElse(null);
        if (transaction != null) {
            Set<FeesLineItem> feesLineItem = transaction.getFeesLineItem();
            Optional<Date> lastPaidDate = feesLineItem
                .stream()
                .filter(fees -> fees.getFeesProduct().getApplicableRule().equals(FeesRuleType.MONTHLY))
                .map(u -> formatToDate(u.getMonth(), MONTH_YEAR_FORMAT))
                .max(Date::compareTo);
            if (lastPaidDate.isPresent()) {
                return Months
                    .monthsBetween(
                        new LocalDate(lastPaidDate.get()).dayOfMonth().withMaximumValue(),
                        new LocalDate(new Date()).withDayOfMonth(1)
                    )
                    .getMonths();
            } else {
                Date date = student.getCreatedDate().before(sessionFrom) ? sessionFrom : student.getCreatedDate();
                return new LocalDate(date).withDayOfMonth(1).isAfter(new LocalDate(new Date()).withDayOfMonth(1))
                    ? Months.monthsBetween(new LocalDate(date).withDayOfMonth(1), new LocalDate(new Date()).withDayOfMonth(1)).getMonths()
                    : 0;
            }
        } else {
            Date date = student.getCreatedDate().before(sessionFrom) ? sessionFrom : student.getCreatedDate();
            return new LocalDate(date).withDayOfMonth(1).isAfter(new LocalDate(new Date()).withDayOfMonth(1))
                ? Months.monthsBetween(new LocalDate(date).withDayOfMonth(1), new LocalDate(new Date()).withDayOfMonth(1)).getMonths()
                : 0;
        }
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
}
