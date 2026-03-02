package com.tulip.host.utils;

import com.tulip.host.domain.Employee;
import com.tulip.host.enums.CommunicationChannel;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.service.communication.CommunicationRequest;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            return Period.between(birthday, LocalDate.now()).getYears();
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
        return Month.of(month).name();
    }

    public static List<UserRoleEnum> findEligibleUG() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String UG = authorities.stream().map(GrantedAuthority::getAuthority).findFirst().orElseThrow();

        UserRoleEnum userRoleEnum = UserRoleEnum.valueOf(UG.split("_")[1]);
        return Arrays.stream(UserRoleEnum.values())
            .filter(group -> group.getPriority() <= userRoleEnum.getPriority())
            .collect(Collectors.toList());
    }

    public static boolean isProdProfile(String[] profiles) {
        log.debug("Active profiles: {}", Arrays.toString(profiles));
        return Arrays.stream(profiles).anyMatch(n -> n.toLowerCase().equals("prod"));
    }

    /**
     * Checks if communication should be sent based on profile and communication
     * enabled flag.
     * Logic:
     * - If profile is prod: always returns true (communication always sent in
     * production, flag is ignored)
     * - If profile is NOT prod: returns the value of communicationEnabled flag
     * - communicationEnabled = true: communication is sent (for testing in dev)
     * - communicationEnabled = false: communication is skipped (default for dev)
     *
     * @param profiles             Active Spring profiles
     * @param communicationEnabled Flag indicating if communication is enabled (only
     *                             applies in non-prod environments)
     * @return true if communication should be sent, false otherwise
     */
    public static boolean isProdProfile(String[] profiles, boolean communicationEnabled) {
        boolean isProd = isProdProfile(profiles);
        if (isProd) {
            // In production, always send communication regardless of flag
            return true;
        }
        // In non-prod, only send if explicitly enabled via flag
        if (communicationEnabled) {
            log.debug("Communication enabled in non-prod environment via communicationEnabled flag");
            return true;
        }
        log.debug("Communication disabled in non-prod environment. Set communication_enabled: true to enable");
        return false;
    }

    public static String formatRecipient(CommunicationRequest request) {
        if (request.getChannel().equals(CommunicationChannel.EMAIL)) {
            return StringUtils.join(request.getMailRecipient(), ", ");
        } else if (request.getChannel().equals(CommunicationChannel.SMS)) {
            if (request.getSmsRecipient() == null || request.getSmsRecipient().isEmpty()) {
                return null;
            }
            return request
                .getSmsRecipient()
                .stream()
                .flatMap(item -> item.entrySet().stream())
                .filter(entry -> "mobiles".equalsIgnoreCase(entry.getKey()))
                .map(entry -> String.valueOf(entry.getValue()))
                .collect(Collectors.joining(", "));
        }
        return null;
    }

    /**
     * Sanitizes input strings for safe use in file names.
     * Removes characters unsafe for HTML/JS contexts to prevent XSS injection.
     * Allows only alphanumeric characters, hyphens, underscores, and periods.
     */
    public static String sanitizeFileName(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    /**
     * Sanitizes S3 keys for safe use in client responses.
     * Removes characters unsafe for HTML/JS contexts to prevent XSS injection.
     * Allows alphanumeric characters, hyphens, underscores, periods, and forward slashes.
     */
    public static String sanitizeKey(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.' || c == '/') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }
}
