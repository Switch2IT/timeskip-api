package be.ehb.utils;

import be.ehb.factories.ExceptionFactory;
import be.ehb.i18n.Messages;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public final class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateUtils() {
    }

    public static LocalDate convertStringToDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormat.forPattern(DATE_FORMAT));
        } catch (Exception ex) {
            throw ExceptionFactory.invalidDateException(Messages.i18n.format("invalidDateFormat", DATE_FORMAT));
        }
    }

    public static Long convertHoursToMinutes(Double hours) {
        if (hours == null) {
            return 0L;
        } else {
            return Math.round(hours * 60);
        }

    }

    public static List<Date> getDatesBetween(String from, String to) {
        // If no dates are provided, just return dates from the last 30 days
        if (StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) {
            return getDatesBetween(LocalDate.now().minusDays(30).toString(DATE_FORMAT), LocalDate.now().toString(DATE_FORMAT));
        }
        if (from.equals(to)) {
            return Collections.singletonList(convertStringToDate(from).toDate());
        }
        LocalDate startDate = convertStringToDate(from);
        LocalDate endDate = convertStringToDate(to);
        if (startDate.isAfter(endDate)) {
            throw ExceptionFactory.invalidDateException(Messages.i18n.format("toBeforeFromDate"));
        }
        int days = Days.daysBetween(startDate, endDate).getDays();
        List<Date> dates = new ArrayList<>();
        for (int i = 0; i <= days; i++) {
            LocalDate temp = startDate.plusDays(i);
            dates.add(temp.toDate());
        }
        return dates;
    }

    public static String convertDateToString(LocalDate day) {
        return day.toString(DATE_FORMAT);
    }

    public static BigDecimal convertMinutesToHours(Long minutes) {
        if (minutes == null) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(minutes.doubleValue() / 60).setScale(1, BigDecimal.ROUND_HALF_UP);
        }
    }
}