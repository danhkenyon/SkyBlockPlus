package uk.ac.bsfc.sbp.utils.data.database.providers;

import com.zaxxer.hikari.HikariConfig;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;

public class PostgreSQL implements DatabaseProvider {

    @Override
    public HikariConfig createConfig() {
        DatabaseConfig config = DatabaseConfig.getInstance();

        HikariConfig hikari = new HikariConfig();
        hikari.setDriverClassName(config.getDriver());
        hikari.setJdbcUrl(config.getUrl());
        hikari.setUsername(config.getUser());
        hikari.setPassword(config.getPassword());
        hikari.setMaximumPoolSize(config.getMaxPoolSize());
        hikari.setAutoCommit(false);

        hikari.addDataSourceProperty("reWriteBatchedInserts", "true");
        hikari.addDataSourceProperty("stringtype", "unspecified");

        return hikari;
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }
}