package coursePractice.meetingMIS.dao;

import coursePractice.meetingMIS.dbutil.SQLHelper;
import coursePractice.meetingMIS.entity.AdminStaff;
import coursePractice.meetingMIS.entity.ConfirmationLog;
import coursePractice.meetingMIS.entity.ConfirmationStatus;
import coursePractice.meetingMIS.entity.Department;
import coursePractice.meetingMIS.entity.MeetingRoom;
import coursePractice.meetingMIS.entity.Participant;
import coursePractice.meetingMIS.entity.Reservation;
import coursePractice.meetingMIS.entity.Role;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MeetingService {
    public Optional<AdminStaff> login(String staffNo, String password) {
        staffNo = code(staffNo, "工号", 20);
        password = text(password, "密码", 80);
        String sql = "select * from admin_staff where staff_no=? and login_password=?";
        return queryOne(sql, this::staffFrom, staffNo, password);
    }

    public List<Department> departments() {
        return queryList("select * from department order by department_id", this::departmentFrom);
    }

    public List<MeetingRoom> rooms() {
        return queryList("select * from meeting_room order by room_no", this::roomFrom);
    }

    public List<AdminStaff> staff() {
        return queryList("select * from admin_staff order by staff_no", this::staffFrom);
    }

    public List<Reservation> reservations() {
        return queryList("select * from reservation order by start_time desc", this::reservationFrom);
    }

    public List<ConfirmationLog> confirmationLogs() {
        return queryList("select * from confirmation_log order by confirmation_time desc", this::confirmationFrom);
    }

    public List<Participant> participants() {
        return queryList("select * from participant order by record_id", this::participantFrom);
    }

    public void addDepartment(String id, String name, String description) {
        id = code(id, "部门编号", 20);
        name = text(name, "部门名称", 80);
        description = optionalText(description, "部门简介", 255);
        execute("insert into department(department_id, department_name, description) values(?,?,?)", id, name, description);
    }

    public void updateDepartment(String id, String name, String description) {
        id = code(id, "部门编号", 20);
        name = text(name, "部门名称", 80);
        description = optionalText(description, "部门简介", 255);
        execute("update department set department_name=?, description=? where department_id=?", name, description, id);
    }

    public void deleteDepartment(String id) {
        id = code(id, "部门编号", 20);
        execute("delete from department where department_id=?", id);
    }

    public void addRoom(String roomNo, String name, String location, int capacity, String equipment) {
        roomNo = code(roomNo, "会议室编号", 20);
        name = text(name, "会议室名称", 80);
        location = optionalText(location, "位置", 120);
        equipment = optionalText(equipment, "设备", 255);
        requireRange(capacity, "容量", 1, 10000);
        execute("insert into meeting_room(room_no, room_name, location, capacity, equipment) values(?,?,?,?,?)",
                roomNo, name, location, capacity, equipment);
    }

    public void updateRoom(String roomNo, String name, String location, int capacity, String equipment) {
        roomNo = code(roomNo, "会议室编号", 20);
        name = text(name, "会议室名称", 80);
        location = optionalText(location, "位置", 120);
        equipment = optionalText(equipment, "设备", 255);
        requireRange(capacity, "容量", 1, 10000);
        execute("update meeting_room set room_name=?, location=?, capacity=?, equipment=? where room_no=?",
                name, location, capacity, equipment, roomNo);
    }

    public void deleteRoom(String roomNo) {
        roomNo = code(roomNo, "会议室编号", 20);
        execute("delete from meeting_room where room_no=?", roomNo);
    }

    public void addStaff(String staffNo, String name, String gender, String departmentId,
                         String title, String phone, String password, Role role) {
        staffNo = code(staffNo, "工号", 20);
        name = text(name, "姓名", 80);
        gender = gender(gender);
        departmentId = code(departmentId, "部门编号", 20);
        title = optionalText(title, "职务", 80);
        phone = phone(phone);
        password = text(password, "密码", 80);
        execute("insert into admin_staff(staff_no, staff_name, gender, department_id, title, phone, login_password, role) values(?,?,?,?,?,?,?,?)",
                staffNo, name, gender, departmentId, title, phone, password, role.name());
    }

    public void updateStaff(String staffNo, String name, String gender, String departmentId,
                            String title, String phone, Role role) {
        staffNo = code(staffNo, "工号", 20);
        name = text(name, "姓名", 80);
        gender = gender(gender);
        departmentId = code(departmentId, "部门编号", 20);
        title = optionalText(title, "职务", 80);
        phone = phone(phone);
        execute("update admin_staff set staff_name=?, gender=?, department_id=?, title=?, phone=?, role=? where staff_no=?",
                name, gender, departmentId, title, phone, role.name(), staffNo);
    }

    public void resetPassword(String staffNo, String password) {
        staffNo = code(staffNo, "工号", 20);
        password = text(password, "密码", 80);
        execute("update admin_staff set login_password=? where staff_no=?", password, staffNo);
    }

    public void setRoomManager(String staffNo) {
        staffNo = code(staffNo, "工号", 20);
        execute("update admin_staff set role=? where staff_no=?", Role.ROOM_MANAGER.name(), staffNo);
    }

    public void updatePersonalInfo(String staffNo, String phone, String title) {
        staffNo = code(staffNo, "工号", 20);
        phone = phone(phone);
        title = optionalText(title, "职务", 80);
        execute("update admin_staff set phone=?, title=? where staff_no=?", phone, title, staffNo);
    }

    public Reservation submitReservation(AdminStaff applicant, String subject, String roomNo,
                                         LocalDateTime start, LocalDateTime end,
                                         int attendeeCount, String description) {
        subject = text(subject, "会议主题", 120);
        roomNo = code(roomNo, "会议室编号", 20);
        description = optionalText(description, "会议说明", 500);
        requireRange(attendeeCount, "参会人数", 1, 10000);
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("开始时间必须早于结束时间。");
        }
        MeetingRoom room = findRoom(roomNo);
        if (attendeeCount > room.getCapacity()) {
            throw new IllegalArgumentException("参会人数超过会议室容量。");
        }
        String id = nextId("Y");
        execute("insert into reservation(reservation_id, subject, department_id, applicant_no, room_no, start_time, end_time, attendee_count, description, status) values(?,?,?,?,?,?,?,?,?,?)",
                id, subject, applicant.getDepartmentId(), applicant.getStaffNo(), roomNo,
                Timestamp.valueOf(start), Timestamp.valueOf(end), attendeeCount, description, ConfirmationStatus.PENDING.name());
        return findReservation(id);
    }

    public List<MeetingRoom> availableRooms(LocalDate date) {
        return rooms();
    }

    public List<Reservation> reservationsOnDate(LocalDate date) {
        return reservations().stream()
                .filter(r -> r.getStartTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Reservation::getStartTime))
                .collect(Collectors.toList());
    }

    public boolean hasApprovedConflict(String roomNo, LocalDateTime start, LocalDateTime end, String excludingId) {
        roomNo = code(roomNo, "会议室编号", 20);
        String checkedRoomNo = roomNo;
        return reservations().stream()
                .filter(r -> r.getStatus() == ConfirmationStatus.APPROVED)
                .filter(r -> r.getRoomNo().equalsIgnoreCase(checkedRoomNo))
                .filter(r -> !r.getReservationId().equals(excludingId))
                .anyMatch(r -> r.overlaps(start, end));
    }

    public void approveReservation(String reservationId, String managerNo, boolean approved, String opinion) {
        reservationId = code(reservationId, "预约编号", 20);
        managerNo = code(managerNo, "确认人工号", 20);
        opinion = optionalText(opinion, "确认意见", 500);
        Reservation reservation = findReservation(reservationId);
        if (reservation.getStatus() != ConfirmationStatus.PENDING) {
            throw new IllegalArgumentException("只能处理待确认预约。");
        }
        ConfirmationStatus status = approved ? ConfirmationStatus.APPROVED : ConfirmationStatus.REJECTED;
        if (approved && hasApprovedConflict(reservation.getRoomNo(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getReservationId())) {
            throw new IllegalArgumentException("该会议室在该时段已有已确认预约，不能通过。");
        }
        execute("update reservation set status=? where reservation_id=?", status.name(), reservationId);
        execute("insert into confirmation_log(confirmation_id, reservation_id, confirmer_no, status, opinion, confirmation_time) values(?,?,?,?,?,?)",
                nextId("C"), reservationId, managerNo, status.name(), opinion, Timestamp.valueOf(LocalDateTime.now()));
    }

    public void cancelReservation(String reservationId, String applicantNo) {
        reservationId = code(reservationId, "预约编号", 20);
        applicantNo = code(applicantNo, "申请人工号", 20);
        Reservation reservation = findReservation(reservationId);
        if (!reservation.getApplicantNo().equalsIgnoreCase(applicantNo)) {
            throw new IllegalArgumentException("只能撤销本人预约。");
        }
        if (reservation.getStatus() != ConfirmationStatus.PENDING) {
            throw new IllegalArgumentException("只有审批通过前的待确认预约允许撤销。");
        }
        execute("update reservation set status=? where reservation_id=?", ConfirmationStatus.CANCELLED.name(), reservationId);
    }

    public void addParticipant(String reservationId, String staffNo) {
        reservationId = code(reservationId, "预约编号", 20);
        staffNo = code(staffNo, "参会人工号", 20);
        Reservation reservation = findReservation(reservationId);
        AdminStaff staff = findStaff(staffNo);
        if (!staff.getDepartmentId().equalsIgnoreCase(reservation.getDepartmentId())) {
            throw new IllegalArgumentException("只能登记本部门参会人员。");
        }
        execute("insert into participant(record_id, reservation_id, staff_no, checked_in) values(?,?,?,?)",
                nextId("P"), reservationId, staffNo, false);
    }

    public void checkIn(String recordId) {
        recordId = code(recordId, "签到记录编号", 20);
        execute("update participant set checked_in=true where record_id=?", recordId);
    }

    public List<Reservation> filterReservations(LocalDate date, String departmentId, String roomNo) {
        return reservations().stream()
                .filter(r -> date == null || r.getStartTime().toLocalDate().equals(date))
                .filter(r -> departmentId == null || departmentId.isBlank() || r.getDepartmentId().equalsIgnoreCase(departmentId))
                .filter(r -> roomNo == null || roomNo.isBlank() || r.getRoomNo().equalsIgnoreCase(roomNo))
                .sorted(Comparator.comparing(Reservation::getStartTime))
                .collect(Collectors.toList());
    }

    public Map<String, Long> roomUsageCount() {
        return reservations().stream()
                .filter(r -> r.getStatus() == ConfirmationStatus.APPROVED)
                .collect(Collectors.groupingBy(Reservation::getRoomNo, Collectors.counting()));
    }

    public Map<String, Long> departmentMeetingCount() {
        return reservations().stream()
                .filter(r -> r.getStatus() == ConfirmationStatus.APPROVED)
                .collect(Collectors.groupingBy(Reservation::getDepartmentId, Collectors.counting()));
    }

    public void exportUsageRecords(Path file) {
        List<String> lines = reservations().stream()
                .filter(r -> r.getStatus() == ConfirmationStatus.APPROVED)
                .map(Reservation::toString)
                .collect(Collectors.toList());
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("导出失败：" + e.getMessage());
        }
    }

    public void changePassword(String staffNo, String oldPassword, String newPassword) {
        staffNo = code(staffNo, "工号", 20);
        oldPassword = text(oldPassword, "原密码", 80);
        newPassword = text(newPassword, "新密码", 80);
        AdminStaff staff = findStaff(staffNo);
        if (!staff.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("原密码错误。");
        }
        resetPassword(staffNo, newPassword);
    }

    public Department findDepartment(String id) {
        id = code(id, "部门编号", 20);
        return queryOne("select * from department where department_id=?", this::departmentFrom, id)
                .orElseThrow(() -> new IllegalArgumentException("部门不存在。"));
    }

    public MeetingRoom findRoom(String roomNo) {
        roomNo = code(roomNo, "会议室编号", 20);
        return queryOne("select * from meeting_room where room_no=?", this::roomFrom, roomNo)
                .orElseThrow(() -> new IllegalArgumentException("会议室不存在。"));
    }

    public AdminStaff findStaff(String staffNo) {
        staffNo = code(staffNo, "工号", 20);
        return queryOne("select * from admin_staff where staff_no=?", this::staffFrom, staffNo)
                .orElseThrow(() -> new IllegalArgumentException("人员不存在。"));
    }

    public Reservation findReservation(String reservationId) {
        reservationId = code(reservationId, "预约编号", 20);
        return queryOne("select * from reservation where reservation_id=?", this::reservationFrom, reservationId)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在。"));
    }

    private Department departmentFrom(ResultSet rs) throws SQLException {
        return new Department(rs.getString("department_id"), rs.getString("department_name"), rs.getString("description"));
    }

    private AdminStaff staffFrom(ResultSet rs) throws SQLException {
        return new AdminStaff(rs.getString("staff_no"), rs.getString("staff_name"), rs.getString("gender"),
                rs.getString("department_id"), rs.getString("title"), rs.getString("phone"),
                rs.getString("login_password"), roleOf(rs.getString("role")));
    }

    private MeetingRoom roomFrom(ResultSet rs) throws SQLException {
        return new MeetingRoom(rs.getString("room_no"), rs.getString("room_name"), rs.getString("location"),
                rs.getInt("capacity"), rs.getString("equipment"));
    }

    private Reservation reservationFrom(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation(rs.getString("reservation_id"), rs.getString("subject"),
                rs.getString("department_id"), rs.getString("applicant_no"), rs.getString("room_no"),
                rs.getTimestamp("start_time").toLocalDateTime(), rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getInt("attendee_count"), rs.getString("description"));
        reservation.setStatus(statusOf(rs.getString("status")));
        return reservation;
    }

    private ConfirmationLog confirmationFrom(ResultSet rs) throws SQLException {
        return new ConfirmationLog(rs.getString("confirmation_id"), rs.getString("reservation_id"),
                rs.getString("confirmer_no"), statusOf(rs.getString("status")), rs.getString("opinion"),
                rs.getTimestamp("confirmation_time").toLocalDateTime());
    }

    private Participant participantFrom(ResultSet rs) throws SQLException {
        return new Participant(rs.getString("record_id"), rs.getString("reservation_id"),
                rs.getString("staff_no"), rs.getBoolean("checked_in"));
    }

    private Role roleOf(String value) {
        return Role.valueOf(value);
    }

    private ConfirmationStatus statusOf(String value) {
        return ConfirmationStatus.valueOf(value);
    }

    private String nextId(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    private String code(String value, String field, int maxLength) {
        value = text(value, field, maxLength);
        if (!value.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException(field + "只能包含字母、数字、下划线或横线。");
        }
        return value;
    }

    private String text(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + "不能为空。");
        }
        value = value.trim();
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + "长度不能超过 " + maxLength + " 个字符。");
        }
        rejectControlChars(value, field);
        return value;
    }

    private String optionalText(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        value = value.trim();
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + "长度不能超过 " + maxLength + " 个字符。");
        }
        rejectControlChars(value, field);
        return value;
    }

    private String phone(String value) {
        value = optionalText(value, "联系电话", 30);
        if (!value.isEmpty() && !value.matches("[0-9+\\- ]+")) {
            throw new IllegalArgumentException("联系电话只能包含数字、加号、横线和空格。");
        }
        return value;
    }

    private String gender(String value) {
        value = text(value, "性别", 10);
        if (!value.equals("男") && !value.equals("女")) {
            throw new IllegalArgumentException("性别只能选择男或女。");
        }
        return value;
    }

    private void requireRange(int value, String field, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + "必须在 " + min + " 到 " + max + " 之间。");
        }
    }

    private void rejectControlChars(String value, String field) {
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isISOControl(ch) && ch != '\n' && ch != '\r' && ch != '\t') {
                throw new IllegalArgumentException(field + "不能包含不可见控制字符。");
            }
        }
    }

    private void execute(String sql, Object... args) {
        try (Connection conn = SQLHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            bind(statement, args);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    private <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... args) {
        List<T> list = new ArrayList<>();
        try (Connection conn = SQLHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            bind(statement, args);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("数据库查询失败：" + e.getMessage(), e);
        }
        return list;
    }

    private <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... args) {
        List<T> list = queryList(sql, mapper, args);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    private void bind(PreparedStatement statement, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }
    }

    private interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
