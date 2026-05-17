package core;

import java.util.HashMap;

public class AutoOrganizer {

    private HashMap<String, FolderWatcher> watchers = new HashMap<>();

    public void startWatching(String folderPath, FileOrganizer organizer) {

        if (watchers.containsKey(folderPath)) return;

        FolderWatcher watcher = new FolderWatcher(folderPath, organizer);
        watchers.put(folderPath, watcher);
        watcher.start();
    }

    public void stopWatching(FileOrganizer organizer) {

        for (FolderWatcher watcher : watchers.values()) {
            watcher.stopWatching(organizer);
        }

        watchers.clear();
    }
}