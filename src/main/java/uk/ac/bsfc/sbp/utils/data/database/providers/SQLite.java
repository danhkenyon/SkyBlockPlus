package uk.ac.bsfc.sbp.utils.data.database.providers;

import com.zaxxer.hikari.HikariConfig;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;

public class SQLite implements DatabaseProvider {

    @Override
    public HikariConfig createConfig() {
        DatabaseConfig config = DatabaseConfig.getInstance();

        HikariConfig hikari = new HikariConfig();
        hikari.setDriverClassName(config.getDriver());
        hikari.setJdbcUrl(config.getUrl());
        hikari.setMaximumPoolSize(Math.max(1, config.getMaxPoolSize()));
        hikari.setAutoCommit(false);

        return hikari;
    }

    @Override
    public String getName() {
        return "SQLite";
    }
}