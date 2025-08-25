package edu.studyapp.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    /**
     * Конструктор, который инициализирует путь для хранения файлов.
     * Путь можно задать через application.properties с помощью @Value
     */
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        // Базовый путь - папка 'uploads' в текущей рабочей директории
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        logger.info("Директория для файлов: {}", this.fileStorageLocation);
        try {
            // Создаем директорию, если она не существует
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов.", ex);
        }
    }

    /**
     * Загружает файл как ресурс по относительному пути.
     *
     * @param relativeFilePath относительный путь к файлу (например, "literature/spring-book.pdf")
     * @return Resource объект для работы с файлом
     * @throws RuntimeException если файл не найден или недоступен
     */
    public Resource loadFileAsResource(String relativeFilePath) {
        try {
            // Очищаем путь от небезопасных символов
            String fileName = StringUtils.cleanPath(relativeFilePath);
            logger.info("{}___fileName", fileName);
            // Собираем полный путь к файлу
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            logger.info("{}___filePath", filePath);

            // Проверяем, что итоговый путь все еще находится в корневой директории (защита от path traversal атак)
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Недопустимый путь к файлу: " + fileName);
            }

            // Создаем ресурс из файла
            Resource resource = new UrlResource(filePath.toUri());

            // Проверяем существование и доступность файла
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                logger.info("{}___filePath", filePath);
                throw new RuntimeException("Файл не найден или невозможно прочитать: " + fileName);
            }

        } catch (MalformedURLException ex) {
            throw new RuntimeException("Ошибка при формировании URL для файла: " + relativeFilePath, ex);
        }
    }
    public String determineContentType(Resource resource) {
        // Простая реализация - можно улучшить
        String filename = resource.getFilename();
        if (filename == null) {
            return "application/octet-stream";
        }

        if (filename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (filename.endsWith(".zip")) {
            return "application/zip";
        } else if (filename.endsWith(".txt")) {
            return "application/txt";
        } else if (filename.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else {
            return "application/octet-stream"; // стандартный тип для бинарных файлов
        }
    }
}
