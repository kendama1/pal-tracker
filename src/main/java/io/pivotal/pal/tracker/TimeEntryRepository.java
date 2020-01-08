package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry any);

    TimeEntry find(long timeEntryId);

    TimeEntry update(long eq, TimeEntry any);

    List list();

    void delete(long timeEntryId);
}
