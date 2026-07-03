package coursePractice.meetingMIS.view;

import coursePractice.meetingMIS.dao.MeetingService;
import coursePractice.meetingMIS.entity.AdminStaff;

import java.util.Scanner;

public class ConsoleUI {
    private final MeetingService service;
    private final InputUtil input;

    public ConsoleUI(MeetingService service) {
        this.service = service;
        this.input = new InputUtil(new Scanner(System.in));
    }

    public void start() {
        while (true) {
            System.out.println("\n=== 校内会议预约与排程管理系统（控制台备用入口）===");
            String staffNo = input.text("工号（exit退出）：");
            if ("exit".equalsIgnoreCase(staffNo)) {
                return;
            }
            String password = input.text("密码：");
            service.login(staffNo, password).ifPresentOrElse(this::printHome, () -> System.out.println("账号或密码错误。"));
        }
    }

    private void printHome(AdminStaff user) {
        System.out.println("登录成功：" + user.getName() + "，角色：" + user.getRole().getLabel());
        System.out.println("图形界面请运行 run.bat，本入口仅保留为备用。");
        System.out.println("当前部门：");
        service.departments().forEach(System.out::println);
        System.out.println("当前会议室：");
        service.rooms().forEach(System.out::println);
    }
}
