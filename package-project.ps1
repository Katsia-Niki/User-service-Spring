# Скрипт для упаковки проекта в ZIP-архив (Windows PowerShell)

Write-Host "Упаковка проекта homework4..." -ForegroundColor Green

# Исключаем ненужные папки и файлы
$exclude = @('target', '.git', '.idea', 'out', '*.iml', '.classpath', '.project', '.settings')

# Создаем временную папку
$tempDir = "homework4-temp"
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Path $tempDir | Out-Null

# Копируем нужные файлы и папки
Write-Host "Копирование файлов..." -ForegroundColor Yellow
Copy-Item -Path "src" -Destination "$tempDir\src" -Recurse -Force
Copy-Item -Path "pom.xml" -Destination "$tempDir\pom.xml" -Force
Copy-Item -Path "README.md" -Destination "$tempDir\README.md" -Force -ErrorAction SilentlyContinue

# Создаем ZIP-архив
$zipFile = "homework4.zip"
if (Test-Path $zipFile) {
    Remove-Item -Force $zipFile
}

Write-Host "Создание ZIP-архива..." -ForegroundColor Yellow
Compress-Archive -Path "$tempDir\*" -DestinationPath $zipFile -Force

# Удаляем временную папку
Remove-Item -Recurse -Force $tempDir

Write-Host "Готово! Архив создан: $zipFile" -ForegroundColor Green
Write-Host "Размер архива: $((Get-Item $zipFile).Length / 1MB) MB" -ForegroundColor Cyan

