package coursePractice.meetingMIS.view;

import coursePractice.meetingMIS.dao.MeetingService;
import coursePractice.meetingMIS.entity.AdminStaff;
import coursePractice.meetingMIS.entity.ConfirmationStatus;
import coursePractice.meetingMIS.entity.Department;
import coursePractice.meetingMIS.entity.MeetingRoom;
import coursePractice.meetingMIS.entity.Participant;
import coursePractice.meetingMIS.entity.Reservation;
import coursePractice.meetingMIS.entity.Role;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SwingUI {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MeetingService service;
    private JFrame frame;

    public SwingUI(MeetingService service) {
        this.service = service;
    }

    public void start() {
        SwingUtilities.invokeLater(this::showLogin);
    }

    private void showLogin() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("校内会议预约与排程管理系统");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(620, 340);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("校内会议预约与排程管理系统", JLabel.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 30));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        JTextField staffNo = new JTextField();
        JPasswordField password = new JPasswordField();
        staffNo.setColumns(18);
        password.setColumns(18);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.gridx = 0;
        form.add(markLabel("工号：", new Color(0xE8F1FF), new Color(0x2B5CAA)), gbc);
        gbc.gridx = 1;
        form.add(staffNo, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        form.add(markLabel("密码：", new Color(0xFFF3D6), new Color(0x8A5A00)), gbc);
        gbc.gridx = 1;
        form.add(password, gbc);
        JButton login = new JButton("登录");
        JButton exit = new JButton("退出");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.add(login);
        actions.add(exit);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        form.add(actions, gbc);
        root.add(form, BorderLayout.CENTER);

        login.addActionListener(e -> service.login(staffNo.getText().trim(), new String(password.getPassword()))
                .ifPresentOrElse(this::showHome, () -> message("账号或密码错误。")));
        exit.addActionListener(e -> {
            frame.dispose();
        });

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private void showHome(AdminStaff user) {
        frame.setTitle("校内会议预约与排程管理系统 - " + user.getRole().getLabel());
        JPanel root = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel(user.getName() + "（" + user.getRole().getLabel() + "）", JLabel.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0, 2, 10, 10));
        if (user.getRole() == Role.SYSTEM_ADMIN) {
            adminButtons(menu, user);
        } else if (user.getRole() == Role.ROOM_MANAGER) {
            managerButtons(menu, user);
        } else {
            staffButtons(menu, user);
        }
        root.add(menu, BorderLayout.CENTER);

        JButton logout = new JButton("退出登录");
        logout.addActionListener(e -> {
            showLogin();
        });
        root.add(logout, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private void adminButtons(JPanel menu, AdminStaff user) {
        addButton(menu, "部门管理", this::departmentTable);
        addButton(menu, "会议室管理", this::roomTable);
        addButton(menu, "行政人员管理", this::staffTable);
        addButton(menu, "设置会议室管理员", () -> run(() -> service.setRoomManager(ask("指定工号"))));
        addButton(menu, "预约记录管理", this::reservationTable);
        addButton(menu, "统计报表", this::showStatistics);
        addButton(menu, "导出使用记录", () -> run(() -> {
            Path file = Path.of("reports", "meeting-room-usage.txt");
            service.exportUsageRecords(file);
            message("已导出：" + file.toAbsolutePath());
        }));
        addButton(menu, "修改密码", () -> changePassword(user));
    }

    private void managerButtons(JPanel menu, AdminStaff user) {
        addButton(menu, "待确认预约/排程", () -> pendingReservationTable(user));
        addButton(menu, "确认历史查询", () -> showList("确认历史", service.confirmationLogs()));
        addButton(menu, "个人信息修改", () -> personalInfoTable(user));
        addButton(menu, "修改密码", () -> changePassword(user));
    }

    private void staffButtons(JPanel menu, AdminStaff user) {
        addButton(menu, "会议室查询", this::queryRoomAvailability);
        addButton(menu, "提交预约", () -> submitReservation(user));
        addButton(menu, "我的预约", () -> myReservationTable(user));
        addButton(menu, "撤销预约", () -> run(() -> service.cancelReservation(ask("预约编号"), user.getStaffNo())));
        addButton(menu, "签到管理", () -> checkInDialog(user));
        addButton(menu, "修改密码", () -> changePassword(user));
    }

    private void departmentTable() {
        DefaultTableModel model = editableModel(new int[] { 0, 1 }, "序号", "部门编号", "部门名称", "部门简介");
        JTable table = new JTable(model);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            model.setRowCount(0);
            int index = 1;
            for (Department d : service.departments()) {
                model.addRow(new Object[] { index++, d.getId(), d.getName(), d.getDescription() });
            }
        };
        refresh[0].run();
        JDialog dialog = tableDialog("部门管理", table,
                button("新增",
                        () -> runAndRefresh(() -> service.addDepartment(ask("部门编号"), ask("部门名称"), ask("部门简介")),
                                refresh[0])),
                button("保存表格修改", () -> runAndRefresh(() -> saveDepartmentRows(table, model), refresh[0])),
                button("删除选中",
                        () -> runAndRefresh(() -> service.deleteDepartment(selectedValue(table, 1)), refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void roomTable() {
        DefaultTableModel model = editableModel(new int[] { 0 }, "会议室编号", "会议室名称", "位置", "容量", "设备");
        JTable table = new JTable(model);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            model.setRowCount(0);
            for (MeetingRoom r : service.rooms()) {
                model.addRow(new Object[] { r.getRoomNo(), r.getName(), r.getLocation(), r.getCapacity(),
                        r.getEquipment() });
            }
        };
        refresh[0].run();
        JDialog dialog = tableDialog("会议室管理", table,
                button("新增", () -> runAndRefresh(() -> addRoomByForm(), refresh[0])),
                button("保存表格修改", () -> runAndRefresh(() -> saveRoomRows(table, model), refresh[0])),
                button("删除选中", () -> runAndRefresh(() -> service.deleteRoom(selectedValue(table, 0)), refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void staffTable() {
        DefaultTableModel model = editableModel(new int[] { 0 }, "工号", "姓名", "性别", "部门", "职务", "电话", "角色");
        JTable table = new JTable(model);
        installStaffEditors(table);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            model.setRowCount(0);
            for (AdminStaff s : service.staff()) {
                model.addRow(new Object[] { s.getStaffNo(), s.getName(), s.getGender(), s.getDepartmentId(),
                        s.getTitle(), s.getPhone(), s.getRole().getLabel() });
            }
        };
        refresh[0].run();
        JDialog dialog = tableDialog("行政人员管理", table,
                button("录入人员", () -> runAndRefresh(() -> addStaffByForm(), refresh[0])),
                button("保存表格修改", () -> runAndRefresh(() -> saveStaffRows(table, model), refresh[0])),
                button("重置密码",
                        () -> runAndRefresh(() -> service.resetPassword(selectedValue(table, 0), ask("新密码")),
                                refresh[0])),
                button("设为会议室管理员",
                        () -> runAndRefresh(() -> service.setRoomManager(selectedValue(table, 0)), refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void reservationTable() {
        DefaultTableModel model = reservationModel();
        JTable table = new JTable(model);
        Runnable refresh = () -> fillReservations(model, service.reservations());
        refresh.run();
        JDialog dialog = tableDialog("预约记录管理", table,
                button("按条件筛选", () -> run(() -> {
                    String dateText = askOptional("日期 yyyy-MM-dd（可空）");
                    LocalDate date = dateText.isBlank() ? null : LocalDate.parse(dateText, DATE);
                    fillReservations(model,
                            service.filterReservations(date, askOptional("部门编号（可空）"), askOptional("会议室编号（可空）")));
                })),
                button("查看全部", refresh));
        dialog.setVisible(true);
    }

    private void pendingReservationTable(AdminStaff manager) {
        DefaultTableModel model = reservationModel();
        JTable table = new JTable(model);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> fillReservations(model, service.reservations().stream()
                .filter(r -> r.getStatus() == ConfirmationStatus.PENDING).toList());
        refresh[0].run();
        JDialog dialog = tableDialog("待确认预约/排程", table,
                button("通过选中", () -> runAndRefresh(() -> service.approveReservation(selectedValue(table, 0),
                        manager.getStaffNo(), true, ask("确认意见")), refresh[0])),
                button("驳回选中", () -> runAndRefresh(() -> service.approveReservation(selectedValue(table, 0),
                        manager.getStaffNo(), false, ask("驳回意见")), refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void myReservationTable(AdminStaff user) {
        DefaultTableModel model = reservationModel();
        JTable table = new JTable(model);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> fillReservations(model, service.reservations().stream()
                .filter(r -> r.getApplicantNo().equalsIgnoreCase(user.getStaffNo())).toList());
        refresh[0].run();
        JDialog dialog = tableDialog("我的预约", table,
                button("复制预约编号", () -> copyToClipboard(selectedValue(table, 0))),
                button("撤销选中",
                        () -> runAndRefresh(() -> service.cancelReservation(selectedValue(table, 0), user.getStaffNo()),
                                refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void personalInfoTable(AdminStaff user) {
        DefaultTableModel model = editableModel(new int[] { 0, 6 }, "工号", "姓名", "性别", "部门", "职务", "电话", "角色");
        JTable table = new JTable(model);
        installPersonalEditors(table);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            model.setRowCount(0);
            AdminStaff current = service.findStaff(user.getStaffNo());
            model.addRow(new Object[] { current.getStaffNo(), current.getName(), current.getGender(),
                    current.getDepartmentId(), current.getTitle(), current.getPhone(), current.getRole().getLabel() });
        };
        refresh[0].run();
        JDialog dialog = tableDialog("个人信息修改", table,
                button("保存表格修改", () -> runAndRefresh(() -> savePersonalRow(table, model, user), refresh[0])),
                button("修改密码", () -> changePassword(user)),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void installStaffEditors(JTable table) {
        table.getColumnModel().getColumn(2)
                .setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "男", "女" })));
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(departmentBox()));
        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox<>(
                new String[] { Role.SYSTEM_ADMIN.getLabel(), Role.ROOM_MANAGER.getLabel(), Role.STAFF.getLabel() })));
    }

    private void installPersonalEditors(JTable table) {
        table.getColumnModel().getColumn(2)
                .setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "男", "女" })));
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(departmentBox()));
    }

    private JComboBox<String> departmentBox() {
        JComboBox<String> box = new JComboBox<>();
        for (Department department : service.departments()) {
            box.addItem(department.getId());
        }
        return box;
    }

    private void addStaffByForm() {
        JTextField staffNo = new JTextField(18);
        JTextField name = new JTextField(18);
        JComboBox<String> gender = new JComboBox<>(new String[] { "男", "女" });
        JComboBox<String> department = departmentBox();
        JTextField title = new JTextField(18);
        JTextField phone = new JTextField(18);
        JPasswordField password = new JPasswordField(18);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("工号："));
        form.add(staffNo);
        form.add(new JLabel("姓名："));
        form.add(name);
        form.add(new JLabel("性别："));
        form.add(gender);
        form.add(new JLabel("部门："));
        form.add(department);
        form.add(new JLabel("职务："));
        form.add(title);
        form.add(new JLabel("电话："));
        form.add(phone);
        form.add(new JLabel("初始密码："));
        form.add(password);

        int result = JOptionPane.showConfirmDialog(frame, form, "录入行政人员",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        service.addStaff(requireText(staffNo.getText(), "工号"),
                requireText(name.getText(), "姓名"),
                String.valueOf(gender.getSelectedItem()),
                String.valueOf(department.getSelectedItem()),
                title.getText().trim(),
                phone.getText().trim(),
                requireText(new String(password.getPassword()), "初始密码"),
                Role.STAFF);
    }

    private void addRoomByForm() {
        JTextField roomNo = new JTextField(18);
        JTextField name = new JTextField(18);
        JTextField location = new JTextField(18);
        JSpinner capacity = new JSpinner(new SpinnerNumberModel(30, 1, 10000, 1));
        JTextField equipment = new JTextField(18);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("会议室编号："));
        form.add(roomNo);
        form.add(new JLabel("会议室名称："));
        form.add(name);
        form.add(new JLabel("位置："));
        form.add(location);
        form.add(new JLabel("容量："));
        form.add(capacity);
        form.add(new JLabel("设备："));
        form.add(equipment);

        int result = JOptionPane.showConfirmDialog(frame, form, "新增会议室",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        service.addRoom(requireText(roomNo.getText(), "会议室编号"),
                requireText(name.getText(), "会议室名称"),
                location.getText().trim(),
                (Integer) capacity.getValue(),
                equipment.getText().trim());
    }

    private void fillReservations(DefaultTableModel model, List<Reservation> reservations) {
        model.setRowCount(0);
        for (Reservation r : reservations) {
            model.addRow(new Object[] { r.getReservationId(), r.getDepartmentId(), r.getApplicantNo(), r.getRoomNo(),
                    r.getStartTime(), r.getEndTime(), r.getStatus().getLabel(), r.toString() });
        }
    }

    private DefaultTableModel reservationModel() {
        return model("预约编号", "部门", "申请人", "会议室", "开始时间", "结束时间", "状态", "详情");
    }

    private void submitReservation(AdminStaff user) {
        JTextField subject = new JTextField(24);
        JComboBox<String> roomBox = new JComboBox<>();
        for (MeetingRoom room : service.rooms()) {
            roomBox.addItem(room.getRoomNo() + " - " + room.getName());
        }

        Date now = new Date();
        JSpinner startTime = new JSpinner(new SpinnerDateModel(now, null, null, java.util.Calendar.MINUTE));
        JSpinner endTime = new JSpinner(
                new SpinnerDateModel(new Date(now.getTime() + 60 * 60 * 1000), null, null, java.util.Calendar.MINUTE));
        startTime.setEditor(new JSpinner.DateEditor(startTime, "yyyy-MM-dd HH:mm"));
        endTime.setEditor(new JSpinner.DateEditor(endTime, "yyyy-MM-dd HH:mm"));

        JSpinner attendeeCount = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        JTextArea description = new JTextArea(4, 24);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("会议主题："));
        form.add(subject);
        form.add(new JLabel("会议室："));
        form.add(roomBox);
        form.add(new JLabel("开始时间："));
        form.add(startTime);
        form.add(new JLabel("结束时间："));
        form.add(endTime);
        form.add(new JLabel("参会人数："));
        form.add(attendeeCount);
        form.add(new JLabel("会议说明："));
        form.add(new JScrollPane(description));

        int result = JOptionPane.showConfirmDialog(frame, form, "提交会议预约",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        run(() -> {
            String roomValue = String.valueOf(roomBox.getSelectedItem());
            String roomNo = roomValue.split(" - ", 2)[0];
            Reservation reservation = service.submitReservation(user, requireText(subject.getText(), "会议主题"), roomNo,
                    toLocalDateTime((Date) startTime.getValue()),
                    toLocalDateTime((Date) endTime.getValue()),
                    (Integer) attendeeCount.getValue(), description.getText().trim());
            message("预约已提交，编号：" + reservation.getReservationId());
        });
    }

    private void queryRoomAvailability() {
        Date now = new Date();
        JSpinner queryDate = new JSpinner(new SpinnerDateModel(now, null, null, java.util.Calendar.DAY_OF_MONTH));
        queryDate.setEditor(new JSpinner.DateEditor(queryDate, "yyyy-MM-dd"));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("查询日期："));
        form.add(queryDate);

        int result = JOptionPane.showConfirmDialog(frame, form, "会议室查询",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        run(() -> {
            LocalDate date = toLocalDate((Date) queryDate.getValue());
            StringBuilder text = new StringBuilder("会议室列表：\n");
            service.availableRooms(date).forEach(room -> text.append(room).append('\n'));
            text.append("\n当日预约：\n");
            service.reservationsOnDate(date).forEach(r -> text.append(r).append('\n'));
            showText("会议室空闲状态", text.toString());
        });
    }

    private void checkInDialog(AdminStaff user) {
        DefaultTableModel model = model("记录编号", "预约编号", "参会人工号", "参会人姓名", "签到状态");
        JTable table = new JTable(model);
        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> fillParticipantRows(model, user);
        refresh[0].run();

        JDialog dialog = tableDialog("签到管理", table,
                button("添加参会人员", () -> runAndRefresh(() -> addParticipantByForm(user), refresh[0])),
                button("签到选中", () -> runAndRefresh(() -> service.checkIn(selectedValue(table, 0)), refresh[0])),
                button("刷新", refresh[0]));
        dialog.setVisible(true);
    }

    private void fillParticipantRows(DefaultTableModel model, AdminStaff user) {
        model.setRowCount(0);
        for (Participant participant : service.participants()) {
            try {
                Reservation reservation = service.findReservation(participant.getReservationId());
                if (!reservation.getDepartmentId().trim().equalsIgnoreCase(user.getDepartmentId().trim())) {
                    continue;
                }
                AdminStaff staff = service.findStaff(participant.getStaffNo());
                model.addRow(new Object[] { participant.getRecordId(), participant.getReservationId(),
                        participant.getStaffNo(), staff.getName(), participant.isCheckedIn() ? "已签到" : "未签到" });
            } catch (RuntimeException ignored) {
                // Ignore stale participant rows whose reservation or staff record no longer exists.
            }
        }
    }

    private void addParticipantByForm(AdminStaff user) {
        JComboBox<String> reservationBox = new JComboBox<>();
        List<Reservation> departmentReservations = service.reservations().stream()
                .filter(r -> r.getDepartmentId().trim().equalsIgnoreCase(user.getDepartmentId().trim()))
                .filter(r -> r.getStatus() == ConfirmationStatus.APPROVED)
                .toList();
        if (departmentReservations.isEmpty()) {
            departmentReservations = service.reservations().stream()
                    .filter(r -> r.getDepartmentId().trim().equalsIgnoreCase(user.getDepartmentId().trim()))
                    .toList();
        }
        for (Reservation reservation : departmentReservations) {
            reservationBox.addItem(reservation.getReservationId() + " - " + reservation.getRoomNo() + " - "
                    + reservation.getStartTime());
        }

        JComboBox<String> staffBox = new JComboBox<>();
        for (AdminStaff staff : service.staff()) {
            if (staff.getDepartmentId().trim().equalsIgnoreCase(user.getDepartmentId().trim())) {
                staffBox.addItem(staff.getStaffNo() + " - " + staff.getName());
            }
        }

        if (reservationBox.getItemCount() == 0) {
            throw new IllegalArgumentException("当前部门暂无可添加参会人员的预约。");
        }
        if (staffBox.getItemCount() == 0) {
            throw new IllegalArgumentException("当前部门暂无可选择的行政人员。");
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("预约："));
        form.add(reservationBox);
        form.add(new JLabel("参会人员："));
        form.add(staffBox);

        int result = JOptionPane.showConfirmDialog(frame, form, "添加参会人员",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String reservationId = String.valueOf(reservationBox.getSelectedItem()).split(" - ", 2)[0];
        String staffNo = String.valueOf(staffBox.getSelectedItem()).split(" - ", 2)[0];
        service.addParticipant(reservationId, staffNo);
    }

    private void showStatistics() {
        StringBuilder text = new StringBuilder();
        appendMap(text, "会议室使用次数", service.roomUsageCount());
        appendMap(text, "\n部门会议次数", service.departmentMeetingCount());
        showText("统计报表", text.toString());
    }

    private void changePassword(AdminStaff user) {
        try {
            String oldPassword = ask("原密码");
            if (service.login(user.getStaffNo(), oldPassword).isEmpty()) {
                message("原密码错误。");
                return;
            }
            String newPassword = ask("新密码");
            run(() -> service.changePassword(user.getStaffNo(), oldPassword, newPassword));
        } catch (RuntimeException e) {
            message("操作失败：" + e.getMessage());
        }
    }

    private DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel editableModel(int[] lockedColumns, String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                for (int lockedColumn : lockedColumns) {
                    if (lockedColumn == column) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private void saveDepartmentRows(JTable table, DefaultTableModel model) {
        stopEditing(table);
        for (int row = 0; row < model.getRowCount(); row++) {
            service.updateDepartment(cell(model, row, 1), cell(model, row, 2), cell(model, row, 3));
        }
    }

    private void saveRoomRows(JTable table, DefaultTableModel model) {
        stopEditing(table);
        for (int row = 0; row < model.getRowCount(); row++) {
            service.updateRoom(cell(model, row, 0), cell(model, row, 1), cell(model, row, 2),
                    Integer.parseInt(cell(model, row, 3)), cell(model, row, 4));
        }
    }

    private void saveStaffRows(JTable table, DefaultTableModel model) {
        stopEditing(table);
        for (int row = 0; row < model.getRowCount(); row++) {
            service.updateStaff(cell(model, row, 0), cell(model, row, 1), cell(model, row, 2),
                    cell(model, row, 3), cell(model, row, 4), cell(model, row, 5), roleFromText(cell(model, row, 6)));
        }
    }

    private void savePersonalRow(JTable table, DefaultTableModel model, AdminStaff user) {
        stopEditing(table);
        service.updateStaff(user.getStaffNo(), cell(model, 0, 1), cell(model, 0, 2),
                cell(model, 0, 3), cell(model, 0, 4), cell(model, 0, 5), user.getRole());
    }

    private String cell(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("表格第 " + (row + 1) + " 行第 " + (column + 1) + " 列不能为空。");
        }
        return value.toString().trim();
    }

    private Role roleFromText(String text) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text) || role.getLabel().equals(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("角色只能填写：系统管理员、会议室管理员、行政人员。");
    }

    private void stopEditing(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private JDialog tableDialog(String title, JTable table, JButton... buttons) {
        JDialog dialog = new JDialog(frame, title, false);
        dialog.setSize(980, 560);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout(8, 8));
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        table.setRowHeight(28);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : buttons) {
            tools.add(button);
        }
        JButton back = new JButton("返回上一步");
        back.addActionListener(e -> dialog.dispose());
        tools.add(back);
        dialog.add(tools, BorderLayout.SOUTH);
        return dialog;
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    private String selectedValue(JTable table, int column) {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new IllegalArgumentException("请先选中表格中的一行。");
        }
        int modelRow = table.convertRowIndexToModel(row);
        return String.valueOf(table.getModel().getValueAt(modelRow, column));
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private JLabel markLabel(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return label;
    }

    private String ask(String prompt) {
        String value = JOptionPane.showInputDialog(frame, prompt + "：");
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("输入不能为空。");
        }
        return value.trim();
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空。");
        }
        return value.trim();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    private void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        message("已复制预约编号：" + text);
    }

    private String askOptional(String prompt) {
        String value = JOptionPane.showInputDialog(frame, prompt + "：");
        return value == null ? "" : value.trim();
    }

    private void appendMap(StringBuilder builder, String title, Map<String, Long> map) {
        builder.append(title).append("：\n");
        if (map.isEmpty()) {
            builder.append("暂无数据\n");
            return;
        }
        map.forEach((key, value) -> builder.append(key).append(" -> ").append(value).append(" 次\n"));
    }

    private void showList(String title, Iterable<?> items) {
        StringBuilder builder = new StringBuilder();
        boolean empty = true;
        for (Object item : items) {
            builder.append(item).append('\n');
            empty = false;
        }
        showText(title, empty ? "暂无数据。" : builder.toString());
    }

    private void showText(String title, String text) {
        JTextArea area = new JTextArea(text, 20, 80);
        area.setEditable(false);
        area.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(frame, new JScrollPane(area), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(frame, text);
    }

    private void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            message("操作失败：" + e.getMessage());
        }
    }

    private void runAndRefresh(Runnable action, Runnable refresh) {
        run(action);
        refresh.run();
    }

}
