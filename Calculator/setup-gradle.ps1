$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$wrapperDirectory = Join-Path $PSScriptRoot 'gradle\wrapper'
$wrapperJar = Join-Path $wrapperDirectory 'gradle-wrapper.jar'
$expectedHash = '498495120a03b9a6ab5d155f5de3c8f0d986a449153702fb80fc80e134484f17'
if (Test-Path $wrapperJar) {
    if ((Get-FileHash $wrapperJar -Algorithm SHA256).Hash.ToLower() -eq $expectedHash) {
        Write-Host 'Gradle Wrapper sudah siap.'
        exit 0
    }
    throw 'Checksum wrapper yang sudah ada tidak cocok. Periksa file gradle-wrapper.jar.'
}
$downloadTarget = Join-Path $wrapperDirectory 'gradle-wrapper.jar.download'
try {
    Write-Host 'Mengunduh Gradle Wrapper resmi 8.9...'
    Invoke-WebRequest -UseBasicParsing -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar' -OutFile $downloadTarget
    if ((Get-FileHash $downloadTarget -Algorithm SHA256).Hash.ToLower() -ne $expectedHash) {
        throw 'Checksum unduhan tidak cocok. Unduhan tidak digunakan.'
    }
    Move-Item -LiteralPath $downloadTarget -Destination $wrapperJar
    Write-Host 'Siap. Buka folder Calculator melalui Android Studio > Open.'
} finally {
    if (Test-Path $downloadTarget) { Remove-Item -LiteralPath $downloadTarget }
}
