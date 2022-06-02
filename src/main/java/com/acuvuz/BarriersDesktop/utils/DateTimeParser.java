package com.acuvuz.BarriersDesktop.utils;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public class DateTimeParser {
    public static String parseMovementIntervalFromElements(DatePicker datePicker, TextField hourField, TextField minuteField) {
        LocalDate localDate = datePicker.getValue();
        String resultString = "";

        if (localDate != null) {
            resultString = localDate.getDayOfMonth() + "." +
                    localDate.getMonthValue() + "." +
                    localDate.getYear() + "T";
        }
        else {
            resultString =  new SimpleDateFormat("dd.MM.yyyy").format(Timestamp.from(Instant.now())) + "T";
        }

        if (hourField.getText().length() != 0) {
            if (hourField.getText().matches("\\D")) {
                AlertModalCreator.createAlertModalWindow("Ошибка!", "Ошибка в поле часов фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += hourField.getText() + ":";
        }
        else {
            resultString += new SimpleDateFormat("HH").format(Timestamp.from(Instant.now())) + ":";

        }

        if (minuteField.getText().length() != 0) {
            if (minuteField.getText().matches("\\D")) {
                AlertModalCreator.createAlertModalWindow("Ошибка!", "Ошибка в поле минут фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += minuteField.getText().toString();
        }
        else {
            resultString += new SimpleDateFormat("mm").format(Timestamp.from(Instant.now()));
        }


        return resultString;
    }
    public static ArrayList<String> parseMovementInterval(DatePicker fromDate, TextField fromHour, TextField fromMinute,
                                                   DatePicker toDate, TextField toHour, TextField toMinute) {

        String fromString = parseMovementIntervalFromElements(fromDate, fromHour, fromMinute);
        String toString =  parseMovementIntervalFromElements(toDate, toHour, toMinute);

        var result = new ArrayList<String>();
        result.add(fromString);
        result.add(toString);

        return result;
    }
}
