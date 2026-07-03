package coursePractice.meetingMIS.view;

import coursePractice.meetingMIS.dao.MeetingService;

public class SwingApp {
    public static void main(String[] args) {
        LookAndFeelUtil.apply();
        MeetingService service = new MeetingService();
        new SwingUI(service).start();
    }
}
