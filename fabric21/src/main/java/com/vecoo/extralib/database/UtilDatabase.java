package com.vecoo.extralib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class UtilDatabase {
    private static HikariDataSource dataSource;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static void init(String type, String address, String database, String username, String password, String prefix, int maxPoolSize, int minimumIdle, long maxLifeTime, long keepaliveTime, long connectionTimeout, boolean useSSL) {
        HikariConfig config = new HikariConfig();

        String normalizedType = type.toLowerCase();

        try {
            switch (normalizedType) {
                case "mysql": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MySQL driver not found", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mysql://" + address + "/" + database + ssl);
                    break;
                }

                case "mariadb": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mariadb.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MariaDB driver not found", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mariadb://" + address + "/" + database + ssl);
                    break;
                }

                case "postgresql": {
                    try {
                        Class.forName("com.vecoo.extralib.shade.postgresql.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("PostgreSQL driver not found", e);
                    }

                    String ssl = useSSL ? "" : "?sslmode=disable";

                    config.setJdbcUrl("jdbc:postgresql://" + address + "/" + database + ssl);
                    break;
                }

                default: {
                    throw new IllegalStateException("[ExtraLib] Unsupported database type: " + type);
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
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("[ExtraLib] Database not initialized.");
        }

        return dataSource;
    }
    public static boolean isDataSourceInitialized() {
        return dataSource != null;
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }

        EXECUTOR.shutdown();
    }

    public static void async(Runnable task) {
        EXECUTOR.execute(task);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, EXECUTOR);
    }

    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, EXECUTOR);
    }
}