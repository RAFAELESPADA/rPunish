package dev.wplugins.waze.gerementions.util;

import java.util.concurrent.TimeUnit;


public class Util {

    @Deprecated
    public static String formatTime(long t) {
        final long dy = TimeUnit.NANOSECONDS.toDays(t) - TimeUnit.DAYS.toDays(TimeUnit.NANOSECONDS.toDays(t));
        final long hr = TimeUnit.NANOSECONDS.toHours(t) - TimeUnit.DAYS.toHours(TimeUnit.NANOSECONDS.toDays(t));
        final long min = TimeUnit.NANOSECONDS.toMinutes(t) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(t));
        final long sec = TimeUnit.NANOSECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(t));

        if (dy >= 1) {
            return String.format("%d dias", dy);
        } else if (hr >= 1) {
            return String.format("%d horas", hr);
        } else if (min >= 1) {
            return String.format("%d minutos", min);
        } else {
            return String.format("%d segundos", sec);
        }
    }

    public static String fromLongWithoutDiff(final Long log) {
        String time = "";
        final long totalLenth = log;
        final long seconds = totalLenth / 1000L;
        time = fromLong(time, seconds);
        return time;
    }

    public static String fromLong(final Long log) {
        String time = "";
        final long totalLenth = log;
        final long timeLefting = totalLenth - System.currentTimeMillis();
        final long seconds = timeLefting / 1000L;
        time = fromLong(time, seconds);
        return time;
    }

    private static String fromLong(String restingTime, final long lenth) {
        final int days = (int) TimeUnit.SECONDS.toDays(lenth);
        final long hours = TimeUnit.SECONDS.toHours(lenth) - days * 24L;
        final long minutes = TimeUnit.SECONDS.toMinutes(lenth) - TimeUnit.SECONDS.toHours(lenth) * 60L;
        final long seconds = TimeUnit.SECONDS.toSeconds(lenth) - TimeUnit.SECONDS.toMinutes(lenth) * 60L;
        String totalDay = String.valueOf(days) + ((days > 1) ? " dias " : " dia ");
        String totalHours = String.valueOf(hours) + ((hours > 1L) ? " horas " : " hora ");
        String totalMinutes = String.valueOf(minutes) + ((minutes > 1L) ? " minutos " : " minuto ");
        String totalSeconds = String.valueOf(seconds) + ((seconds > 1L) ? " segundos." : " segundo");
        if (days == 0) {
            totalDay = "";
        }
        if (hours == 0L) {
            totalHours = "";
        }
        if (minutes == 0L) {
            totalMinutes = "";
        }
        if (seconds == 0L) {
            totalSeconds = "";
        }
        restingTime = String.valueOf(totalDay) + totalHours + totalMinutes + totalSeconds;
        restingTime = restingTime.trim();
        if (restingTime.equals("")) {
            restingTime = "0 segundos";
        }
        return restingTime;
    }
}
