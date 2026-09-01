@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "GRADLE_VERSION=8.1.1"
set "ROOT_DIR=%~dp0"
set "BOOT_DIR=%ROOT_DIR%.gradle-bootstrap"
set "GRADLE_HOME_LOCAL=%BOOT_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_ZIP=%BOOT_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
set "GRADLE_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

set "FOUND_JAVA_HOME="

for /d %%J in ("%USERPROFILE%\.jdks\*17*") do (
    if not defined FOUND_JAVA_HOME if exist "%%~fJ\bin\javac.exe" set "FOUND_JAVA_HOME=%%~fJ"
)

for /d %%J in ("C:\Program Files\Eclipse Adoptium\*17*") do (
    if not defined FOUND_JAVA_HOME if exist "%%~fJ\bin\javac.exe" set "FOUND_JAVA_HOME=%%~fJ"
)

for /d %%J in ("%LOCALAPPDATA%\Programs\Eclipse Adoptium\*17*") do (
    if not defined FOUND_JAVA_HOME if exist "%%~fJ\bin\javac.exe" set "FOUND_JAVA_HOME=%%~fJ"
)

for /d %%J in ("C:\Program Files\Java\*17*") do (
    if not defined FOUND_JAVA_HOME if exist "%%~fJ\bin\javac.exe" set "FOUND_JAVA_HOME=%%~fJ"
)

if not defined FOUND_JAVA_HOME if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "FOUND_JAVA_HOME=%JAVA_HOME%"

if not defined FOUND_JAVA_HOME (
    echo ERROR: A JDK 17 installation is required for Forge 1.20.1 development.
    echo Install/select JDK 17 in IntelliJ or set JAVA_HOME to a JDK 17 folder.
    echo Example: set JAVA_HOME=%%USERPROFILE%%\.jdks\temurin-17.x.x
    exit /b 1
)

set "JAVA_HOME=%FOUND_JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [bootstrap] Using Java from: %JAVA_HOME%

if not exist "%GRADLE_HOME_LOCAL%\bin\gradle.bat" (
    echo [bootstrap] Gradle %GRADLE_VERSION% not found. Downloading...
    if not exist "%BOOT_DIR%" mkdir "%BOOT_DIR%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%GRADLE_URL%' -OutFile '%GRADLE_ZIP%'"
    if errorlevel 1 exit /b !errorlevel!
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force -Path '%GRADLE_ZIP%' -DestinationPath '%BOOT_DIR%'"
    if errorlevel 1 exit /b !errorlevel!
)

call "%GRADLE_HOME_LOCAL%\bin\gradle.bat" %*
exit /b %errorlevel%
