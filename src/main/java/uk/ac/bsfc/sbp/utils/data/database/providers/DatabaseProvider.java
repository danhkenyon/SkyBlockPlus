package uk.ac.bsfc.sbp.utils.data.database.providers;

import com.zaxxer.hikari.HikariConfig;

public interface DatabaseProvider {
    HikariConfig createConfig();
    String getName();
}