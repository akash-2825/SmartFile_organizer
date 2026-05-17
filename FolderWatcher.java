package core;

import java.io.File;
import java.nio.file.*;

public class FolderWatcher {

    private boolean running = true;
    private String folderPath;
    private FileOrganizer organizer;
    private WatchService watchService;

    public FolderWatcher(String folderPath, FileOrganizer organizer) {
        this.folderPath = folderPath;
        this.organizer = organizer;
    }

    public void start() {

        new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();

                Path path = Paths.get(folderPath);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                while (running) {

                    WatchKey key;

                    try {
                        key = watchService.take();
                    } catch (ClosedWatchServiceException e) {
                        break;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {

                        if (!running) break;

                        Thread.sleep(500);

                        File file = new File(folderPath + "\\" + event.context());

                        if (file.exists()) {
                            organizer.organize(folderPath);
                        }
                    }

                    key.reset();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopWatching(FileOrganizer organizer) {

        running = false;

        try {
            if (watchService != null) watchService.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        organizer.stop();
    }
}