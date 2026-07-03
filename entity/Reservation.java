package coursePractice.meetingMIS.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable {
    private String reservationId;
    private String subject;
    private String departmentId;
    private String applicantNo;
    private String roomNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;
    private String description;
    private ConfirmationStatus status;

    public Reservation(String reservationId, String subject, String departmentId, String applicantNo,
                       String roomNo, LocalDateTime startTime, LocalDateTime endTime,
                       int attendeeCount, String description) {
        this.reservationId = reservationId;
        this.subject = subject;
        this.departmentId = departmentId;
        this.applicantNo = applicantNo;
        this.roomNo = roomNo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeeCount = attendeeCount;
        this.description = description;
        this.status = ConfirmationStatus.PENDING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getApplicantNo() {
        return applicantNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ConfirmationStatus getStatus() {
        return status;
    }

    public void setStatus(ConfirmationStatus status) {
        this.status = status;
    }

    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(endTime) && end.isAfter(startTime);
    }

    @Override
    public String toString() {
        return reservationId + " | " + subject + " | 部门:" + departmentId + " | 申请人:" + applicantNo
                + " | 会议室:" + roomNo + " | " + startTime + " 至 " + endTime
                + " | 人数:" + attendeeCount + " | " + status.getLabel() + " | " + description;
    }
}
