package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    ConcurrentHashMap<Long, TimeEntry> timeEntries = new ConcurrentHashMap();
    long entryNumber = 0;

    public TimeEntry create(TimeEntry timeEntry) {
        long id = ++entryNumber;
        TimeEntry createdTimeEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntries.put(id, createdTimeEntry);
        return createdTimeEntry;
    }

    public TimeEntry find(long timeEntryId) {
        return timeEntries.get(timeEntryId);
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntries.replace(id, new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours()));
        return find(id);
//        if (id <= timeEntries.size() && id > 0) {
//            timeEntry.setId(id);
//            timeEntries.put(id, timeEntry);
//            return find(id);
//        }
//        return null;
    }

    public List list() {
        List list = new ArrayList();
        timeEntries.forEach((k,v) -> list.add(v));
        return list;
    }

    public void delete(long timeEntryId) {
        timeEntries.remove(timeEntryId);
    }
}
