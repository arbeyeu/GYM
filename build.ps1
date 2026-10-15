$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$env:JAVA_HOME = Join-Path $projectDir ".tools\jdk17.0.20_12"
$env:PATH = "$env:JAVA_HOME\bin;$projectDir\.tools\apache-maven-3.9.6\bin;$env:PATH"

Write-Host "Running Maven Clean Package..." -ForegroundColor Cyan
& "$projectDir\.tools\apache-maven-3.9.6\bin\mvn.cmd" clean package
