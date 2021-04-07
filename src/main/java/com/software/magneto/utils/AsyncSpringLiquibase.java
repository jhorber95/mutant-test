package com.software.magneto.utils;

import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public class AsyncSpringLiquibase extends DataSourceClosingSpringLiquibase {

    public static final String DISABLED_MESSAGE = "Liquibase is disabled";
    public static final String STARTING_ASYNC_MESSAGE = "Starting Liquibase asynchronously, your database might not be ready at startup!";
    public static final String STARTING_SYNC_MESSAGE = "Starting Liquibase synchronously";
    public static final String STARTED_MESSAGE = "Liquibase has updated your database in {} ms";
    public static final String EXCEPTION_MESSAGE = "Liquibase could not start correctly, your database is NOT ready: {}";
    public static final long SLOWNESS_THRESHOLD = 5L;
    public static final String SLOWNESS_MESSAGE = "Warning, Liquibase took more than {} seconds to start up!";
    private final Logger logger = LoggerFactory.getLogger(AsyncSpringLiquibase.class);
    private final Executor executor;
    private final Environment env;

    public AsyncSpringLiquibase(Executor executor, Environment env) {
        this.executor = executor;
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        if (!this.env.acceptsProfiles(Profiles.of("no-liquibase"))) {
            if (this.env.acceptsProfiles(Profiles.of("dev|heroku"))) {
                try {
                    Connection connection = this.getDataSource().getConnection();

                    try {
                        this.executor.execute(() -> {
                            try {
                                this.logger.warn(STARTING_ASYNC_MESSAGE);
                                this.initDb();
                            } catch (LiquibaseException var2) {
                                this.logger.error(EXCEPTION_MESSAGE, var2.getMessage(), var2);
                            }

                        });
                    } catch (Throwable var5) {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Throwable var4) {
                                var5.addSuppressed(var4);
                            }
                        }

                        throw var5;
                    }

                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException var6) {
                    this.logger.error(EXCEPTION_MESSAGE, var6.getMessage(), var6);
                }
            } else {
                this.logger.debug(STARTING_ASYNC_MESSAGE);
                this.initDb();
            }
        } else {
            this.logger.debug(DISABLED_MESSAGE);
        }

    }

    protected void initDb() throws LiquibaseException {
        StopWatch watch = new StopWatch();
        watch.start();
        super.afterPropertiesSet();
        watch.stop();
        this.logger.debug(STARTED_MESSAGE, watch.getTotalTimeMillis());
        if (watch.getTotalTimeMillis() > 5000L) {
            this.logger.warn(SLOWNESS_MESSAGE, 5L);
        }

    }
}
