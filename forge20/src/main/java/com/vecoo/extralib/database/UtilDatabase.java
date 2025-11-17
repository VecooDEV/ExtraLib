package com.vecoo.extralib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class UtilDatabase {
    private final HikariDataSource dataSource;
    private final ExecutorService executor;

    public UtilDatabase(@NotNull String type, @NotNull String address, @NotNull String database, @NotNull String username,
                        @NotNull String password, @NotNull String prefix, int maxPoolSize, int minimumIdle, long maxLifeTime,
                        long keepaliveTime, long connectionTimeout, boolean useSSL, int threadPool) {
        HikariConfig config = new HikariConfig();

        String normalizedType = type.toLowerCase();

        try {
            switch (normalizedType) {
                case "mysql": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MySQL driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mysql://" + address + "/" + database + ssl);
                    break;
                }

                case "mariadb": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mariadb.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MariaDB driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mariadb://" + address + "/" + database + ssl);
                    break;
                }

                case "postgresql": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.postgresql.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("PostgreSQL driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?sslmode=disable";

                    config.setJdbcUrl("jdbc:postgresql://" + address + "/" + database + ssl);
                    break;
                }

                default: {
                    throw new IllegalStateException("Unsupported database type: " + type);
                }
            }

            config.setUsername(username);
            config.setPassword(password);
            config.setPoolName(prefix);
            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minimumIdle);
            config.setMaxLifetime(maxLifeTime);
            config.setKeepaliveTime(keepaliveTime);
            config.setConnectionTimeout(connectionTimeout);

            this.dataSource = new HikariDataSource(config);
            this.executor = Executors.newFixedThreadPool(threadPool);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            throw new IllegalStateException("Database not initialized.");
        }

        return this.dataSource;
    }

    public boolean isDataSourceInitialized() {
        return this.dataSource != null;
    }

    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }

        this.executor.shutdown();
    }

    public void async(@NotNull Runnable task) {
        this.executor.execute(task);
    }

    @NotNull
    public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, this.executor);
    }

    @NotNull
    public CompletableFuture<Void> runAsync(@NotNull Runnable task) {
        return CompletableFuture.runAsync(task, this.executor);
    }
}