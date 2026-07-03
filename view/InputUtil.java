package coursePractice.meetingMIS.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Scanner scanner;

    public InputUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    public String text(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int integer(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(text(prompt));
            } catch (NumberFormatException e) {
                System.out.println("请输入整数。");
            }
        }
    }

    public LocalDate date(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(text(prompt + "（yyyy-MM-dd）："), DATE);
            } catch (DateTimeParseException e) {
                System.out.println("日期格式错误。");
            }
        }
    }

    public LocalDateTime dateTime(String prompt) {
        while (true) {
            try {
                return LocalDateTime.parse(text(prompt + "（yyyy-MM-dd HH:mm）："), DATE_TIME);
            } catch (DateTimeParseException e) {
                System.out.println("时间格式错误。");
            }
        }
    }
}
