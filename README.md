# 校内会议预约与排程管理系统

Java final homework: campus meeting reservation and scheduling management system.

Java Swing 图形界面课程设计项目，按题目二实现。代码已按官方参考项目 `itemsMIS` 的分层格式重新编排：

- `entity`：实体类
- `dao`：业务访问与管理逻辑
- `dbutil`：JDBC 数据库连接工具
- `view`：Swing 图形界面与备用控制台界面

## 功能

- 系统管理员：部门管理、会议室管理、行政人员录入、重置密码、设置会议室管理员、预约记录筛选、统计报表、导出会议室使用记录、修改密码。
- 会议室管理员：待确认预约表格查询、确认或驳回预约、确认历史查询、个人信息修改、修改密码。
- 行政人员：按日期查询会议室状态、通过表单选择会议室和起止时间提交预约、表格查看我的预约、审批前撤销预约、参会签到管理、修改密码。
- 管理界面：部门、会议室、行政人员、预约记录等模块均采用表格展示；可维护数据支持直接点击单元格编辑，完成后点击“保存表格修改”写回。
- 数据持久化：使用 MySQL 数据库，通过 JDBC 读写数据。
- 输入安全：数据库访问使用 `PreparedStatement` 防 SQL 注入；业务层统一限制编号格式、字段长度、电话格式、容量/人数范围，并拒绝不可见控制字符。
- 数据库设计：见 `database/schema.sql`。

## 数据库准备

1. 安装并启动 MySQL。
2. 将 MySQL JDBC 驱动 `mysql-connector-j-*.jar` 放入项目根目录的 `lib/` 文件夹。
3. 使用 MySQL 客户端执行 `database/schema.sql`，脚本会创建 `meetingdb` 数据库、数据表和初始账号。
4. 如本机 MySQL 用户名或密码不同，修改 [SQLHelper.java](dbutil/SQLHelper.java) 中的连接配置：

```java
public static String url = "jdbc:mysql://127.0.0.1:3306/meetingdb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
public static String user = "root";
public static String pwd = "123456";
```

## 运行

Windows 下可直接双击项目根目录的 `run.bat` 启动图形界面，或在 PowerShell 中执行：

```powershell
.\run.bat
```

`run.bat` 会自动编译源码并启动 Swing 图形界面系统。

## 美化界面

项目支持可选的 FlatLaf 外观库。将 `flatlaf-*.jar` 放入 `lib/` 文件夹后，程序启动时会自动启用更现代的 Swing 外观；如果没有该 jar，会自动回退到 Nimbus 或系统默认外观。

手动编译运行：

```powershell
javac -encoding UTF-8 -cp "lib\*" -d out entity\*.java dao\*.java dbutil\*.java view\*.java
java -cp "out;lib\*" coursePractice.meetingMIS.view.SwingApp
```

## 初始账号

| 角色 | 工号 | 密码 |
| --- | --- | --- |
| 系统管理员 | A001 | 123456 |
| 会议室管理员 | A002 | 123456 |
| 行政人员 | A003 | 123456 |

## 目录

```text
entity/
dao/
dbutil/
view/
lib/mysql-connector-j-*.jar
lib/flatlaf-*.jar
database/schema.sql
run.bat
```
