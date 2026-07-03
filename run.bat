@echo off
chcp 65001 >nul
setlocal

cd /d "%~dp0"

if not exist out mkdir out

echo Compiling Java source...
javac -encoding UTF-8 -cp "lib\*" -d out entity\*.java dao\*.java dbutil\*.java view\*.java

if errorlevel 1 (
  echo.
  echo Build failed. Please check whether JDK is installed and configured in PATH.
  pause
  exit /b 1
)

echo.
echo Starting system...
java -cp "out;lib\*" coursePractice.meetingMIS.view.SwingApp

echo.
pause
