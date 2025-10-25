package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.SBReflectionUtils;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DatabaseTable<T> {
    protected final SBDatabase database = SBDatabase.getInstance();
    protected final String tableName;
    protected final String primaryKey;

    protected DatabaseTable(String tableName) {
        this.tableName = tableName;
        this.primaryKey = this.findPrimaryKey();
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
                "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?;",
                value
        );
    }

    public abstract T mapRow(Map<String, Object> row);
    public abstract void ensureTableExists();

    public String getPrimaryKey() {
        return primaryKey;
    }
    public String getTableName() {
        return tableName;
    }

    public static List<DatabaseTable<?>> getAllTables() {
        List<DatabaseTable<?>> tables = new ArrayList<>();

        for (Class<?> clazz : SBReflectionUtils.find("uk.ac.bsfc.sbp.utils.data.database", DatabaseTable.class)) {
            SBLogger.info("&eFound class: &b"+clazz.getSimpleName());
            try {
                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    SBLogger.info("&b"+clazz.getSimpleName()+" &eis a valid DatabaseTable implementation.");
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    if (instance instanceof DatabaseTable) {
                        tables.add((DatabaseTable<?>) instance);
                        SBLogger.info("&b"+instance+" &eadded to table list.");
                    }
                }
            } catch (Exception e) {
                SBLogger.err("&cFailed to instantiate class &b" + clazz.getSimpleName() + "&c: " + e.getMessage());
            }
        }

        SBLogger.info("[Database] &aFound &b"+tables.size()+" &aTable.");
        for (DatabaseTable<?> table : tables) {
            SBLogger.info("[Database] &a-| Table: &b"+table.getTableName());
        }
        return tables;
    }

}
