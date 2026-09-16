@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup-gradle.ps1"
if errorlevel 1 (
    echo.
    echo Persiapan gagal. Pastikan internet aktif dan github.com dapat diakses.
    pause
    exit /b 1
)
echo.
echo Selesai. Android Studio - Open - pilih folder Calculator ini.
pause
