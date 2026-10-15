param(
    [switch]$Build,
    [switch]$Dev,
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$RemainingArgs
)

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaHome = Join-Path $projectDir ".tools\jdk17.0.20_12"
$javaExe = Join-Path $javaHome "bin\java.exe"
$mvnCmd = Join-Path $projectDir ".tools\apache-maven-3.9.6\bin\mvn.cmd"
$jarFile = Join-Path $projectDir "target\ironcore-gym-1.0.0.jar"

if (Test-Path $javaHome) {
    $env:JAVA_HOME = $javaHome
    $env:PATH = "$javaHome\bin;$projectDir\.tools\apache-maven-3.9.6\bin;$env:PATH"
}

Write-Host "=================================================" -ForegroundColor Green
Write-Host " IRONCORE - Smart Gym Management System" -ForegroundColor Green
Write-Host " Starting Spring Boot Server on http://localhost:8080" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

if ($Build -or (-not (Test-Path $jarFile))) {
    Write-Host "Building project artifact with Maven..." -ForegroundColor Cyan
    & $mvnCmd clean package -DskipTests
}

if ($Dev) {
    Write-Host "Running in Maven Dev Mode..." -ForegroundColor Cyan
    & $mvnCmd spring-boot:run
} else {
    Write-Host "Launching IRONCORE with Java 17..." -ForegroundColor Cyan
    if (Test-Path $javaExe) {
        & $javaExe -jar $jarFile $RemainingArgs
    } else {
        java -jar $jarFile $RemainingArgs
    }
}
