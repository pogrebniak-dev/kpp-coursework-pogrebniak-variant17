package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private Observation readTestObservation() throws IOException {
        Path file = Path.of("sample-data", "field_observations.csv");
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        if (lines.size() < 2) {
            throw new IllegalArgumentException("CSV-файл не містить рядків даних");
        }

        String line = lines.get(1); // індекс 0 — заголовок, індекс 1 — перший рядок даних
        String[] parts = line.split(";", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Некоректна кількість стовпців у CSV: " + parts.length);
        }

        long fieldId = Long.parseLong(parts[0].trim());
        String obsDate = parts[1].trim();
        double f1 = Double.parseDouble(parts[2].trim());
        double f2 = Double.parseDouble(parts[3].trim());
        double f3 = Double.parseDouble(parts[4].trim());

        return new Observation(null, fieldId, obsDate, f1, f2, f3);
    }
    private final HttpMonitoringClient api = new HttpMonitoringClient();

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private static final Path CSV_PATH = Path.of("sample-data", "field_observations.csv");

    private final TableView<Field> fieldTable = new TableView<>();
    private final TableView<Observation> obsTable = new TableView<>();
    private final TableView<Problem> problemTable = new TableView<>();
    private final TableView<TreatmentAction> actionTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
                new Tab("Поля", createFieldsTab()),
                new Tab("Спостереження", createObservationsTab()),
                new Tab("Проблеми", createProblemsTab()),
                new Tab("Заходи обробки", createActionsTab())
        );

        refreshAll();

        Scene scene = new Scene(tabs, 960, 620);
        stage.setTitle("Моніторинг стану полів та посівів (Клієнт)");
        stage.setScene(scene);
        stage.show();
    }

    // --- 1. Вкладка "Поля" ---
    private BorderPane createFieldsTab() {
        fieldTable.getColumns().addAll(
                col("ID", "id", 60),
                col("Назва поля", "name", 260),
                col("Площа (га)", "area", 140),
                col("Культура", "cropType", 220)
        );

        TextField nameField = new TextField();
        TextField areaField = new TextField();
        TextField cropField = new TextField();

        fieldTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                areaField.setText(Double.toString(selected.getArea()));
                cropField.setText(selected.getCropType());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            api.addField(
                    nameField.getText(),
                    Double.parseDouble(areaField.getText().trim()),
                    cropField.getText()
            );
            refreshFields();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Field selected = requireSelected(fieldTable);
            api.updateField(
                    selected.getId(),
                    nameField.getText(),
                    Double.parseDouble(areaField.getText().trim()),
                    cropField.getText()
            );
            refreshFields();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Field selected = requireSelected(fieldTable);
            api.deleteField(selected.getId());
            refreshFields();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::refreshFields));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Назва поля:"), nameField);
        form.addRow(1, new Label("Площа (га):"), areaField);
        form.addRow(2, new Label("Культура:"), cropField);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn);
        VBox bottom = new VBox(10, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(fieldTable);
        pane.setBottom(bottom);
        pane.setPadding(new Insets(10));
        return pane;
    }
    // --- 2. Вкладка "Спостереження" ---
    private BorderPane createObservationsTab() {
        obsTable.getColumns().addAll(
                col("ID", "id", 60),
                col("ID поля", "fieldId", 90),
                col("Дата", "obsDate", 140),
                col("F1 (ураження, %)", "f1", 150),
                col("F2 (тяжкість)", "f2", 140),
                col("F3 (чутливість)", "f3", 150)
        );

        TextField fieldIdField = new TextField();
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        TextField f1Field = new TextField();
        TextField f2Field = new TextField();
        TextField f3Field = new TextField();

        obsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                fieldIdField.setText(Long.toString(selected.getFieldId()));
                dateField.setText(selected.getObsDate());
                f1Field.setText(Double.toString(selected.getF1()));
                f2Field.setText(Double.toString(selected.getF2()));
                f3Field.setText(Double.toString(selected.getF3()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            api.addObservation(
                    Long.parseLong(fieldIdField.getText().trim()),
                    dateField.getText().trim(),
                    Double.parseDouble(f1Field.getText().trim()),
                    Double.parseDouble(f2Field.getText().trim()),
                    Double.parseDouble(f3Field.getText().trim())
            );
            refreshObservations();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Observation selected = requireSelected(obsTable);
            api.updateObservation(
                    selected.getId(),
                    Long.parseLong(fieldIdField.getText().trim()),
                    dateField.getText().trim(),
                    Double.parseDouble(f1Field.getText().trim()),
                    Double.parseDouble(f2Field.getText().trim()),
                    Double.parseDouble(f3Field.getText().trim())
            );
            refreshObservations();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Observation selected = requireSelected(obsTable);
            api.deleteObservation(selected.getId());
            refreshObservations();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::refreshObservations));

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        Label statusLabel = new Label("CSV не імпортовано");

        Button importCsvBtn = new Button("Імпортувати CSV");
        importCsvBtn.setOnAction(e -> {
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            progressBar.setProgress(0);

            ImportObservationsTask task = new ImportObservationsTask(CSV_PATH, api);
            progressBar.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());
            importCsvBtn.setDisable(true);

            task.setOnSucceeded(ev -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                int importedCount = task.getValue() == null ? 0 : task.getValue();
                progressBar.setProgress(importedCount > 0 ? 1.0 : 0.0);
                statusLabel.setText("Готово. Імпортовано: " + importedCount);
                importCsvBtn.setDisable(false);
                refreshObservations();
            });

            task.setOnFailed(ev -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                progressBar.setProgress(0);
                Throwable ex = task.getException();
                String message = (ex == null || ex.getMessage() == null || ex.getMessage().isBlank())
                        ? "Помилка під час імпорту CSV"
                        : ex.getMessage();
                LOG.log(Level.WARNING, "Помилка фонового CSV-імпорту: " + message, ex);
                statusLabel.setText("Помилка: " + message);
                importCsvBtn.setDisable(false);
                showError(message);
            });

            task.setOnCancelled(ev -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                statusLabel.setText("Імпорт скасовано; частина записів могла бути збережена");
                importCsvBtn.setDisable(false);
                refreshObservations();
            });

            Thread worker = new Thread(task, "csv-import-observations");
            worker.setDaemon(true);
            worker.start();
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID поля:"), fieldIdField, new Label("F1 (0..100):"), f1Field);
        form.addRow(1, new Label("Дата спостереження:"), dateField, new Label("F2 (0..100):"), f2Field);
        form.addRow(2, new Label(""), new Label(""), new Label("F3 (0..100):"), f3Field);

        HBox crudButtons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn);
        HBox importBar = new HBox(10, importCsvBtn, progressBar, statusLabel);
        importBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox bottom = new VBox(10, form, crudButtons, importBar);
        bottom.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(obsTable);
        pane.setBottom(bottom);
        pane.setPadding(new Insets(10));
        return pane;
    }

    // --- 3. Вкладка "Проблеми" (життєвий цикл та інтегральний показник) ---
    private BorderPane createProblemsTab() {
        problemTable.getColumns().addAll(
                col("ID", "id", 60),
                col("ID спостереження", "observationId", 150),
                col("Інтегральний показник", "riskIndex", 180),
                col("Стан", "state", 200)
        );

        TextField obsIdField = new TextField();
        TextField riskDisplayField = new TextField();
        riskDisplayField.setEditable(false);
        riskDisplayField.setPromptText("Розраховується сервером");
        ComboBox<ProblemState> stateBox = new ComboBox<>(FXCollections.observableArrayList(ProblemState.values()));

        problemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                obsIdField.setText(Long.toString(selected.getObservationId()));
                riskDisplayField.setText(Double.toString(selected.getRiskIndex()));
                stateBox.setValue(selected.getState());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            api.addProblem(Long.parseLong(obsIdField.getText().trim()));
            refreshProblems();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            Problem selected = requireSelected(problemTable);
            api.updateProblem(selected.getId(), Long.parseLong(obsIdField.getText().trim()));
            refreshProblems();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            Problem selected = requireSelected(problemTable);
            api.deleteProblem(selected.getId());
            refreshProblems();
        }));

        Button stateBtn = new Button("Змінити стан");
        stateBtn.setOnAction(e -> safe(() -> {
            Problem selected = requireSelected(problemTable);
            ProblemState next = stateBox.getValue();
            if (next == null) {
                throw new IllegalArgumentException("Виберіть новий стан");
            }
            api.changeProblemState(selected.getId(), next);
            refreshProblems();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::refreshProblems));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID спостереження:"), obsIdField, new Label("Інтегральний показник:"), riskDisplayField);
        form.addRow(1, new Label("Новий стан:"), stateBox, new Label(""), stateBtn);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn);
        VBox bottom = new VBox(10, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(problemTable);
        pane.setBottom(bottom);
        pane.setPadding(new Insets(10));
        return pane;
    }

    // --- 4. Вкладка "Заходи обробки" ---
    private BorderPane createActionsTab() {
        actionTable.getColumns().addAll(
                col("ID", "id", 60),
                col("ID проблеми", "problemId", 120),
                col("Опис заходу", "description", 340),
                col("Планова дата", "plannedDate", 160)
        );

        TextField problemIdField = new TextField();
        TextField descField = new TextField();
        TextField plannedDateField = new TextField();
        plannedDateField.setPromptText("YYYY-MM-DD");

        actionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                problemIdField.setText(Long.toString(selected.getProblemId()));
                descField.setText(selected.getDescription());
                plannedDateField.setText(selected.getPlannedDate());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            api.addAction(
                    Long.parseLong(problemIdField.getText().trim()),
                    descField.getText(),
                    plannedDateField.getText().trim()
            );
            refreshActions();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            TreatmentAction selected = requireSelected(actionTable);
            api.updateAction(
                    selected.getId(),
                    Long.parseLong(problemIdField.getText().trim()),
                    descField.getText(),
                    plannedDateField.getText().trim()
            );
            refreshActions();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            TreatmentAction selected = requireSelected(actionTable);
            api.deleteAction(selected.getId());
            refreshActions();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> safe(this::refreshActions));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID проблеми:"), problemIdField);
        form.addRow(1, new Label("Опис заходу:"), descField);
        form.addRow(2, new Label("Планова дата:"), plannedDateField);

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn);
        VBox bottom = new VBox(10, form, buttons);
        bottom.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(actionTable);
        pane.setBottom(bottom);
        pane.setPadding(new Insets(10));
        return pane;
    }

    // --- Допоміжні методи ---
    private <S, T> TableColumn<S, T> col(String title, String property, double width) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private <T> T requireSelected(TableView<T> table) {
        T selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            throw new IllegalArgumentException("Виберіть запис");
        }
        return selected;
    }

    private void refreshFields() {
        fieldTable.setItems(FXCollections.observableArrayList(api.getFields()));
    }

    private void refreshObservations() {
        obsTable.setItems(FXCollections.observableArrayList(api.getObservations()));
    }

    private void refreshProblems() {
        problemTable.setItems(FXCollections.observableArrayList(api.getProblems()));
    }

    private void refreshActions() {
        actionTable.setItems(FXCollections.observableArrayList(api.getActions()));
    }

    private void refreshAll() {
        safe(() -> {
            refreshFields();
            refreshObservations();
            refreshProblems();
            refreshActions();
        });
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private void safe(ThrowingRunnable action) {
        try {
            action.run();
        } catch (NumberFormatException e) {
            showError("Перевірте правильність введених числових значень");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText((message == null || message.isBlank()) ? "Невідома помилка" : message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}