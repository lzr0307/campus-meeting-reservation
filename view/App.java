package coursePractice.meetingMIS.view;

import coursePractice.meetingMIS.dao.MeetingService;

public class App {
    public static void main(String[] args) {
        MeetingService service = new MeetingService();
        new ConsoleUI(service).start();
    }
}
