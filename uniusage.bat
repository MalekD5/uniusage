@echo off

setlocal

set "JAR_PATH=app\build\libs\UniUsage.jar"

echo %JAR_PATH%

IF NOT EXIST "%JAR_PATH%" (
    echo UniUsage.jar not found. Building the project...
    call "%~dp0gradlew.bat" build
    if errorlevel 1 (
        echo Build failed. Exiting.
        exit /b 1
    )
)


java -jar "%JAR_PATH%" %*

endlocal