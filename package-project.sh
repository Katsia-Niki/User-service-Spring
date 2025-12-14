#!/bin/bash
# Скрипт для упаковки проекта в ZIP-архив (Linux/Mac)

echo "Упаковка проекта homework4..."

# Создаем ZIP-архив, исключая ненужные папки
zip -r homework4.zip \
    src \
    pom.xml \
    README.md \
    -x "*/target/*" \
    "*/.git/*" \
    "*/.idea/*" \
    "*/out/*" \
    "*.iml" \
    "*/.classpath" \
    "*/.project" \
    "*/.settings/*"

echo "Готово! Архив создан: homework4.zip"
echo "Размер архива: $(du -h homework4.zip | cut -f1)"

