package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.security.Key;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;
    private KeyHolder keyHolder;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.keyHolder = new GeneratedKeyHolder();
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());
            return statement;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{timeEntryId},
                extractor);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update(connection -> {
            PreparedStatement psc = connection.prepareStatement("UPDATE time_entries " +
                    "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                    "WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
            psc.setLong(1, timeEntry.getProjectId());
            psc.setLong(2, timeEntry.getUserId());
            psc.setDate(3, Date.valueOf(timeEntry.getDate()));
            psc.setInt(4, timeEntry.getHours());
            psc.setLong(5, id);

            return psc;
        }, keyHolder);

        return find(id);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement psc = connection.prepareStatement("DELETE FROM time_entries WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
            psc.setLong(1, timeEntryId);

            return psc;
        }, keyHolder);
    }

    private RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
