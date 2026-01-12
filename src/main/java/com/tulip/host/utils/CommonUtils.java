package com.tulip.host.utils;

import com.tulip.host.domain.Employee;
import com.tulip.host.enums.UserRoleEnum;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.tulip.host.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
public class CommonUtils {

    public static Integer calculateAge(LocalDate birthday) {
        if (birthday != null) {
            return Period.between(birthday, LocalDate.now())
                .getYears();
        }
        return 0;
    }

    public static LocalDate isDateAfterCurrent(LocalDate date) {
        return date.isAfter(LocalDate.now()) ? LocalDate.now() : date;
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

    //    public static LocalDateTime formatToDateTime(String date, String format) {
    //        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format, Locale.ENGLISH));
    //    }

    //    public static LocalDate formatToDate(String date, String format) {
    //        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format, Locale.ENGLISH));
    //    }
    //
    public static String formatFromDate(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static String getMonthName(int month) {
        return Month.of(month)
            .name();
    }

    public static List<UserRoleEnum> findEligibleUG() {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String UG = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow();

        UserRoleEnum userRoleEnum = UserRoleEnum.valueOf(UG.split("_")[1]);
        return Arrays
            .stream(UserRoleEnum.values())
            .filter(group -> group.getPriority() <= userRoleEnum.getPriority())
            .collect(Collectors.toList());
    }

    public static boolean isDevProfile(String[] profiles) {
        log.info("Active profiles: {}", Arrays.toString(profiles));
        return Arrays.stream(profiles)
            .anyMatch(n -> n.toLowerCase().equals("dev"));
    }
}
