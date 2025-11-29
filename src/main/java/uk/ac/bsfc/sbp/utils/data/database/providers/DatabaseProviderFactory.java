package uk.ac.bsfc.sbp.utils.data.database.providers;

import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseType;

public final class DatabaseProviderFactory {

    public static DatabaseProvider create(DatabaseConfig config) {
        DatabaseType type = config.getType();

        return switch (type) {
            case SQLITE -> new SQLite();
            case MARIADB -> new MariaDB();
            case MYSQL -> new MySQL();
            case POSTGRES -> new PostgreSQL();
        };
    }
}