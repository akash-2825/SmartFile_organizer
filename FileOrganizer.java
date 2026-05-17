package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class FileOrganizer {

    private HashMap<String, String> history = new HashMap<>();
    private HashMap<String, String> redoHistory = new HashMap<>();
    private volatile boolean stopRequested = false;

    public void organize(String folderPath) {

        stopRequested = false;
        redoHistory.clear();

        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null) return;

        for (File file : files) {

            if (stopRequested) break;

            if (file.isFile()) {

                String category = getCategory(file.getName().toLowerCase());

                File newDir = new File(folderPath + "\\" + category);
                if (!newDir.exists()) newDir.mkdir();

                File newFile = new File(newDir, file.getName());

                try {
                    history.put(newFile.getAbsolutePath(), file.getAbsolutePath());

                    Files.move(file.toPath(), newFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                    Thread.sleep(120);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createCategoryFolders(String folderPath) {

        String[] categories = {
                "Images", "Documents", "Videos",
                "Applications", "Archives", "Others"
        };

        for (String cat : categories) {
            File dir = new File(folderPath + "\\" + cat);
            if (!dir.exists()) dir.mkdir();
        }
    }

    public void stop() {
        stopRequested = true;
    }

    public void undo() {

        redoHistory.clear();

        for (String newPath : history.keySet()) {
            String oldPath = history.get(newPath);

            try {
                Files.move(new File(newPath).toPath(),
                        new File(oldPath).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                redoHistory.put(oldPath, newPath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        history.clear();
    }

    public void redo() {

        for (String oldPath : redoHistory.keySet()) {
            String newPath = redoHistory.get(oldPath);

            try {
                Files.move(new File(oldPath).toPath(),
                        new File(newPath).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                history.put(newPath, oldPath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        redoHistory.clear();
    }

    private String getCategory(String name) {

        if (name.endsWith(".jpg") || name.endsWith(".png"))
            return "Images";
        else if (name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".pptx"))
            return "Documents";
        else if (name.endsWith(".mp4") || name.endsWith(".mkv"))
            return "Videos";
        else if (name.endsWith(".exe") || name.endsWith(".msi"))
            return "Applications";
        else if (name.endsWith(".zip") || name.endsWith(".rar"))
            return "Archives";
        else
            return "Others";
    }
}