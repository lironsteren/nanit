package com.sterenl.nanit.utiles;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import org.joda.time.DateTime;

import java.util.Calendar;

public class DateDialogUtils {
    private DateDialogUtils() {
    }

    public static void showDateDialog(Context context, DateTime selectedDate, DateDialogRange dateDialogRange, final OnDateSelectedListener listener) {
        DateTime newDate = new DateTime();
        if (selectedDate != null) {
            newDate = selectedDate;
        }

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                listener.onDateSelected(dateTime);
            }

        }, newDate.getYear(), newDate.getMonthOfYear() - 1, newDate.getDayOfMonth());

        if (dateDialogRange == DateDialogRange.PAST || dateDialogRange == DateDialogRange.FUTURE) {

            int[] calendarFields = new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
            Calendar limitingCalendar = Calendar.getInstance();
            for (int calendarField : calendarFields) {
                limitingCalendar.set(calendarField, limitingCalendar.getMinimum(calendarField));
            }

            switch (dateDialogRange) {
                case PAST:
                    dialog.getDatePicker().setMaxDate(limitingCalendar.getTimeInMillis());
                    break;
                case FUTURE:
                    dialog.getDatePicker().setMinDate(limitingCalendar.getTimeInMillis());
                    break;
            }
        }
        dialog.show();
    }

    public enum DateDialogRange {
        PAST, FUTURE, ALL
    }

    public interface OnDateSelectedListener {
        void onDateSelected(DateTime date);
    }
}
