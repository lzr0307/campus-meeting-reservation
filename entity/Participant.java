package coursePractice.meetingMIS.entity;

import java.io.Serializable;

public class Participant implements Serializable {
    private String recordId;
    private String reservationId;
    private String staffNo;
    private boolean checkedIn;

    public Participant(String recordId, String reservationId, String staffNo, boolean checkedIn) {
        this.recordId = recordId;
        this.reservationId = reservationId;
        this.staffNo = staffNo;
        this.checkedIn = checkedIn;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    @Override
    public String toString() {
        return recordId + " | 预约:" + reservationId + " | 参会人:" + staffNo
                + " | 签到:" + (checkedIn ? "已签到" : "未签到");
    }
}
