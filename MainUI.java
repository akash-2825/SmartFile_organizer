package ui;

import core.*;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class MainUI {

    private File selectedFolder;
    private ArrayList<String> autoFolders = new ArrayList<>();

    public void show(Stage stage) {

        FileOrganizer organizer = new FileOrganizer();
        AutoOrganizer auto = new AutoOrganizer();

        // 🔷 TITLE
        Label title = new Label("SMART FILE ORGANISER");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Separator line = new Separator();

        // 🔷 STATUS
        Label status = new Label("Status: Stopped");
        status.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // 🔷 PATH
        Label path = new Label("No folder selected");
        path.setStyle("-fx-text-fill: white;");

        // 🔷 AUTO LABEL
        Label autoLabel = new Label("Auto Organize");
        autoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label autoPaths = new Label("No folders added");
        autoPaths.setStyle("-fx-text-fill: lightgray;");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(false);

        // 🔷 BUTTONS
        Button undoBtn = new Button("Undo");
        Button redoBtn = new Button("Redo");

        undoBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");
        redoBtn.setStyle("-fx-background-color: #fb8c00; -fx-text-fill: white;");

        Button selectBtn = new Button("Select Folder");
        Button clearBtn = new Button("Clear");

        Button organizeBtn = new Button("Organize Files");
        organizeBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white;");

        Button addAutoBtn = new Button("Add Auto Folder");
        Button clearAutoBtn = new Button("Clear Auto Folders");

        ToggleButton autoToggle = new ToggleButton("OFF");
        autoToggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        // ================= LOGIC (UNCHANGED) =================

        selectBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File folder = chooser.showDialog(stage);

            if (folder != null) {
                selectedFolder = folder;
                path.setText(folder.getAbsolutePath());
            }
        });

        clearBtn.setOnAction(e -> {
            selectedFolder = null;
            path.setText("No folder selected");
        });

        organizeBtn.setOnAction(e -> {
            if (selectedFolder == null) {
                new Alert(Alert.AlertType.WARNING, "Select folder first!").showAndWait();
                return;
            }

            progress.setVisible(true);

            new Thread(() -> {
                organizer.organize(selectedFolder.getAbsolutePath());

                Platform.runLater(() -> {
                    progress.setVisible(false);
                    new Alert(Alert.AlertType.INFORMATION, "Files Organized!").showAndWait();
                });
            }).start();
        });

        undoBtn.setOnAction(e -> {
            progress.setVisible(true);

            new Thread(() -> {
                organizer.undo();

                Platform.runLater(() -> {
                    progress.setVisible(false);
                    new Alert(Alert.AlertType.INFORMATION, "Undo Completed!").showAndWait();
                });
            }).start();
        });

        redoBtn.setOnAction(e -> {
            progress.setVisible(true);

            new Thread(() -> {
                organizer.redo();

                Platform.runLater(() -> {
                    progress.setVisible(false);
                    new Alert(Alert.AlertType.INFORMATION, "Redo Completed!").showAndWait();
                });
            }).start();
        });

        addAutoBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File folder = chooser.showDialog(stage);

            if (folder != null) {
                autoFolders.add(folder.getAbsolutePath());
                autoPaths.setText(String.join("\n", autoFolders));
            }
        });

        clearAutoBtn.setOnAction(e -> {
            autoFolders.clear();
            autoPaths.setText("No folders added");
        });

        autoToggle.setOnAction(e -> {

            if (autoToggle.isSelected()) {

                autoToggle.setText("ON");
                autoToggle.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                status.setText("Status: Running");
                status.setStyle("-fx-text-fill: lightgreen;");

                if (autoFolders.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Add auto folders first!").showAndWait();
                    autoToggle.setSelected(false);
                    return;
                }

                progress.setVisible(true);

                for (String folder : autoFolders) {

                    new Thread(() -> {
                        organizer.organize(folder);
                    }).start();

                    auto.startWatching(folder, organizer);
                }

                progress.setVisible(false);

            } else {

                autoToggle.setText("OFF");
                autoToggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                status.setText("Status: Stopped");
                status.setStyle("-fx-text-fill: red;");

                auto.stopWatching(organizer);

                new Alert(Alert.AlertType.INFORMATION, "Auto Organize Stopped").showAndWait();
            }
        });

        // ================= UI STRUCTURE =================

        HBox topBar = new HBox(10, undoBtn, redoBtn);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        VBox manualBox = new VBox(10,
                selectBtn,
                clearBtn,
                path,
                organizeBtn
        );
        manualBox.setAlignment(Pos.CENTER);

        VBox autoBox = new VBox(10,
                autoLabel,
                addAutoBtn,
                clearAutoBtn,
                autoPaths,
                autoToggle,
                status
        );
        autoBox.setAlignment(Pos.CENTER);

        HBox mainContent = new HBox(60, manualBox, autoBox);
        mainContent.setAlignment(Pos.CENTER);

        VBox root = new VBox(20,
                title,
                line,
                topBar,
                mainContent,
                progress
        );

        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0b1f3a; -fx-padding: 20;");

        Scene scene = new Scene(root, 750, 550);

        stage.setScene(scene);
        stage.setTitle("Smart File Organizer");
        stage.show();
    }
}