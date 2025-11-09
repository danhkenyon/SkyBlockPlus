package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.SBReflectionUtils;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class DatabaseTable<T> {
    protected final SBDatabase database = SBDatabase.getInstance();
    protected final String tableName;
    protected final String primaryKey;
    protected final int weight;

    protected DatabaseTable(String tableName) {
        this(tableName, 0);
    }
    protected DatabaseTable(String tableName, int weight) {
        this.tableName = tableName;
        this.primaryKey = this.findPrimaryKey();

        this.weight = weight;
    }

    private String findPrimaryKey() {
        try {
            List<Map<String, Object>> result = SBDatabase.query(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                            "WHERE TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY' LIMIT 1;",
                    tableName
            );
            if (!result.isEmpty()) {
                Object value = result.getFirst().get("COLUMN_NAME");
                return value != null ? value.toString() : "id";
            }
        } catch (Exception ignored) {}
        return "id";
    }

    public T getRow(Object value) {
        List<Map<String, Object>> results = SBDatabase.query(
                "SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ? LIMIT 1;",
                value
        );
        return results.isEmpty() ? null : mapRow(results.getFirst());
    }
    public boolean exists(String fieldName, Object value) {
        List<Map<String, Object>> results = SBDatabase.getInstance().getExecutor().query(
                "SELECT 1 FROM " + this.getTableName() + " WHERE " + fieldName + " = ? LIMIT 1;",
                value
        );
        return !results.isEmpty();
    }
    public T getRow(String column, Object value) {
        List<Map<String, Object>> results = SBDatabase.query(
                "SELECT * FROM " + tableName + " WHERE " + column + " = ? LIMIT 1;",
                value
        );
        return results.isEmpty() ? null : mapRow(results.getFirst());
    }
    public List<String> getColumnValues(String columnName) {
        return SBDatabase.getAllColumnValues(tableName, columnName);
    }
    public List<T> getRows() {
        return SBDatabase.query("SELECT * FROM " + tableName + ";")
                .stream().map(this::mapRow).toList();
    }
    public List<T> getRows(String column, Object value) {
        return SBDatabase.query(
                "SELECT * FROM " + tableName + " WHERE " + column + " = ?;",
                value
        ).stream().map(this::mapRow).toList();
    }
    public int delete(Object value) {
        return SBDatabase.update(
                "DELETE FROM " + this.tableName + " WHERE " + this.primaryKey + " = ?;",
                value
        );
    }

    public abstract T mapRow(Map<String, Object> row);
    public abstract void ensureTableExists();

    public String getPrimaryKey() {
        return this.primaryKey;
    }
    public String getTableName() {
        return this.tableName;
    }
    public int getWeight() {
        return this.weight;
    }

    public static List<DatabaseTable<?>> getAllTables() {
        List<DatabaseTable<?>> tables = new ArrayList<>();

        for (Class<?> clazz : SBReflectionUtils.find("uk.ac.bsfc.sbp.utils.data.database", DatabaseTable.class)) {
            try {
                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    if (instance instanceof DatabaseTable<?> table) {
                        tables.add(table);
                    }
                }
            } catch (Exception e) {
                SBLogger.err("<red>Failed to instantiate class <aqua>" + clazz.getSimpleName() + "<red>: " + e.getMessage());
            }
        }

        tables.sort(Comparator.comparingInt(DatabaseTable::getWeight));

        SBLogger.info("[Database] <green>Found " + tables.size() + " <green>Table(s).");
        for (DatabaseTable<?> table : tables) {
            SBLogger.info("[Database] <green>-| Table: <aqua>" + table.getTableName() + " <gray>(Weight: " + table.getWeight() + ")");
        }

        return tables;
    }
}
