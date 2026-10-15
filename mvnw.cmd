@echo off
set "DIR=%~dp0"
if exist "%DIR%.tools\jdk17.0.20_12" (
    set "JAVA_HOME=%DIR%.tools\jdk17.0.20_12"
    set "PATH=%DIR%.tools\jdk17.0.20_12\bin;%DIR%.tools\apache-maven-3.9.6\bin;%PATH%"
    call "%DIR%.tools\apache-maven-3.9.6\bin\mvn.cmd" %*
) else (
    call mvn %*
)
