package uk.ac.bsfc.sbp.utils.data.database;

public enum DatabaseType {
    SQLITE("org.sqlite.JDBC"),
    MARIADB("org.mariadb.jdbc.Driver"),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    POSTGRES("org.postgresql.Driver");

    private final String driver;
    DatabaseType(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public static DatabaseType fromString(String value) {
        return DatabaseType.valueOf(value.toUpperCase());
    }
}