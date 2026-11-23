package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportObservationsTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportObservationsTask.class.getName());
    private static final String EXPECTED_HEADER = "field_id;obs_date;f1;f2;f3";

    private final Path file;
    private final HttpMonitoringClient api;

    public ImportObservationsTask(Path file, HttpMonitoringClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        updateMessage("Читання CSV-файлу...");
        LOG.info("Початок CSV-імпорту: " + file.getFileName());

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("CSV-файл не знайдено: " + file.getFileName());
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }

        String actualHeader = stripBom(lines.get(0)).trim();
        if (!EXPECTED_HEADER.equals(actualHeader)) {
            throw new IllegalArgumentException(
                    "Некоректний заголовок CSV. Очікувалося: " + EXPECTED_HEADER
            );
        }

        List<String> dataLines = lines.subList(1, lines.size());
        long total = dataLines.stream().filter(line -> !line.isBlank()).count();

        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("Немає рядків для імпорту");
            LOG.info("CSV не містить рядків даних. Імпортовано: 0");
            return 0;
        }

        int processed = 0;
        int imported = 0;
        updateProgress(0, total);
        updateMessage("Опрацьовано 0 із " + total + "; імпортовано: 0");

        for (int i = 0; i < dataLines.size(); i++) {
            if (isCancelled()) {
                LOG.warning("CSV-імпорт скасовано користувачем після опрацювання " + processed + " із " + total + " рядків");
                break;
            }

            String rawLine = dataLines.get(i);
            int lineNumber = i + 2; // +1 бо заголовок на 1-му рядку, +1 бо індекс з 0

            if (rawLine.isBlank()) {
                continue;
            }

            String[] parts = rawLine.split(";", -1);
            if (parts.length != 5) {
                LOG.warning("Рядок " + lineNumber + " пропущено: некоректна кількість полів (" + parts.length + ")");
                processed++;
                updateProgress(processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано: " + imported);
                continue;
            }

            try {
                long fieldId = Long.parseLong(parts[0].trim());
                String obsDate = parts[1].trim();
                double f1 = Double.parseDouble(parts[2].trim());
                double f2 = Double.parseDouble(parts[3].trim());
                double f3 = Double.parseDouble(parts[4].trim());

                if (obsDate.isEmpty()) {
                    LOG.warning("Рядок " + lineNumber + " пропущено: порожня дата спостереження");
                    processed++;
                    updateProgress(processed, total);
                    updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано: " + imported);
                    continue;
                }

                if (!Double.isFinite(f1) || !Double.isFinite(f2) || !Double.isFinite(f3)) {
                    LOG.warning("Рядок " + lineNumber + " пропущено: некоректне числове значення фактора");
                    processed++;
                    updateProgress(processed, total);
                    updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано: " + imported);
                    continue;
                }

                try {
                    api.addObservation(fieldId, obsDate, f1, f2, f3);
                    imported++;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Помилка HTTP під час імпорту рядка " + lineNumber, e);
                    throw e;
                }
            } catch (NumberFormatException e) {
                LOG.warning("Рядок " + lineNumber + " пропущено: невірний формат числа (" + e.getMessage() + ")");
                processed++;
                updateProgress(processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано: " + imported);
                continue;
            }

            processed++;
            updateProgress(processed, total);
            updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано: " + imported);
        }

        LOG.info("Завершення CSV-імпорту. Опрацьовано: " + processed + ", імпортовано: " + imported);
        return imported;
    }

    private String stripBom(String text) {
        if (text != null && !text.isEmpty() && text.charAt(0) == '\uFEFF') {
            return text.substring(1);
        }
        return text;
    }
}
