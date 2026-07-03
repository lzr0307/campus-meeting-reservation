CREATE DATABASE IF NOT EXISTS meetingdb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE meetingdb;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS confirmation_log;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS meeting_room;
DROP TABLE IF EXISTS admin_staff;
DROP TABLE IF EXISTS department;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE department (
    department_id VARCHAR(20) PRIMARY KEY,
    department_name VARCHAR(80) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_staff (
    staff_no VARCHAR(20) PRIMARY KEY,
    staff_name VARCHAR(80) NOT NULL,
    gender VARCHAR(10),
    department_id VARCHAR(20) NOT NULL,
    title VARCHAR(80),
    phone VARCHAR(30),
    login_password VARCHAR(80) NOT NULL,
    role VARCHAR(30) NOT NULL,
    CONSTRAINT fk_staff_department
        FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE meeting_room (
    room_no VARCHAR(20) PRIMARY KEY,
    room_name VARCHAR(80) NOT NULL,
    location VARCHAR(120),
    capacity INT NOT NULL,
    equipment VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reservation (
    reservation_id VARCHAR(20) PRIMARY KEY,
    subject VARCHAR(120) NOT NULL,
    department_id VARCHAR(20) NOT NULL,
    applicant_no VARCHAR(20) NOT NULL,
    room_no VARCHAR(20) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    attendee_count INT NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_reservation_department
        FOREIGN KEY (department_id) REFERENCES department(department_id),
    CONSTRAINT fk_reservation_applicant
        FOREIGN KEY (applicant_no) REFERENCES admin_staff(staff_no),
    CONSTRAINT fk_reservation_room
        FOREIGN KEY (room_no) REFERENCES meeting_room(room_no),
    CONSTRAINT ck_reservation_time CHECK (start_time < end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE confirmation_log (
    confirmation_id VARCHAR(20) PRIMARY KEY,
    reservation_id VARCHAR(20) NOT NULL,
    confirmer_no VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    opinion VARCHAR(500),
    confirmation_time DATETIME NOT NULL,
    CONSTRAINT fk_confirmation_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
    CONSTRAINT fk_confirmation_staff
        FOREIGN KEY (confirmer_no) REFERENCES admin_staff(staff_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE participant (
    record_id VARCHAR(20) PRIMARY KEY,
    reservation_id VARCHAR(20) NOT NULL,
    staff_no VARCHAR(20) NOT NULL,
    checked_in BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_participant_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
    CONSTRAINT fk_participant_staff
        FOREIGN KEY (staff_no) REFERENCES admin_staff(staff_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO department(department_id, department_name, description) VALUES
('D001', '教务处', '教学运行与会议协调'),
('D002', '科研处', '科研项目管理'),
('D003', '后勤处', '校园后勤保障');

INSERT INTO admin_staff(staff_no, staff_name, gender, department_id, title, phone, login_password, role) VALUES
('A001', '系统管理员', '男', 'D001', '管理员', '13800000001', '123456', 'SYSTEM_ADMIN'),
('A002', '会议室管理员', '女', 'D003', '场地管理员', '13800000002', '123456', 'ROOM_MANAGER'),
('A003', '行政老师', '女', 'D002', '行政秘书', '13800000003', '123456', 'STAFF');

INSERT INTO meeting_room(room_no, room_name, location, capacity, equipment) VALUES
('R101', '第一会议室', '行政楼101', 30, '投影仪、音响、白板'),
('R202', '学术报告厅', '图文中心202', 120, '投影仪、音响、录播设备');
