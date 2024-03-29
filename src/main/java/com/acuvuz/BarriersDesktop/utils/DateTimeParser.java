package com.acuvuz.BarriersDesktop.utils;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public class DateTimeParser {
    public DateTimeParser() {
        alertModalCreator = new AlertModalCreator();
    }
    final AlertModalCreator alertModalCreator;
    public String parseMovementIntervalFromElements(Boolean isFrom,
                                                           // This boolean is only for "To" time field
                                                           Boolean todayDefault,
                                                           DatePicker datePicker, TextField hourField, TextField minuteField) {
        LocalDate localDate = datePicker.getValue();
        String resultString = "";

        // DATE
        if (localDate != null) {
            var day = localDate.getDayOfMonth() < 10 ? "0" + localDate.getDayOfMonth(): localDate.getDayOfMonth();
            var month = localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue(): localDate.getMonthValue();
            resultString = day + "." +
                    month + "." +
                    localDate.getYear() + "T";
        }
        else {
            if (todayDefault) resultString = getTodayDateDefaultValue();
            else resultString = get1980DateDefaultValue();
        }

        //HOUR
        if (hourField.getText().length() != 0) {
            if (hourField.getText().matches("\\D")) {
                alertModalCreator.createAlertModalWindow("Ошибка!", "Ошибка в поле часов фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += hourField.getText() + ":";
        }
        else {
            if (!isFrom)
                resultString += "23:";
            else
                resultString += "00:";
        }

        //MINUTE
        if (minuteField.getText().length() != 0) {
            if (minuteField.getText().matches("\\D")) {
                alertModalCreator.createAlertModalWindow("Ошибка!", "Ошибка в поле минут фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += minuteField.getText();
        }
        else {
            if (!isFrom)
                resultString += "59";
            else
                resultString += "00";
        }

        return resultString;
    }
    public ArrayList<String> parseMovementInterval(
                                Boolean todayDefault,
                                DatePicker fromDate, TextField fromHour, TextField fromMinute,
                               DatePicker toDate, TextField toHour, TextField toMinute) {

        String fromString = parseMovementIntervalFromElements(true, todayDefault,
                fromDate, fromHour, fromMinute);
        String toString =  parseMovementIntervalFromElements(false, true,
                toDate, toHour, toMinute);

        var result = new ArrayList<String>();
        result.add(fromString);
        result.add(toString);

        return result;
    }

    public String getTodayDateDefaultValue() {
        return new SimpleDateFormat("dd.MM.yyyy").format(Timestamp.from(Instant.now())) + "T";
    }

    public String get1980DateDefaultValue() {
        return "01.01.1980T";
    }

    public String parseTimestamp(String timestamp) {
        String date = timestamp.split("T")[0];
        var splittedDate = date.split("-");
        String parsedDate = splittedDate[2] + "." +
                splittedDate[1] + "." +splittedDate[0];
        String time = timestamp
                .split("T")[1]
                .split("\\+")[0]
                .split("\\.")[0];

        return time + " " + parsedDate;
    }
}
