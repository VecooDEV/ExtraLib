package com.vecoo.extralib.sql;

import com.vecoo.extralib.ExtraLib;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Utility class that provides a simple abstraction over a HikariCP-based database connection pool
 * and asynchronous execution helpers.
 *
 * <p>This class initializes a {@link HikariDataSource} based on the provided database type
 * (MySQL, MariaDB, PostgreSQL). It also manages a fixed thread pool for executing asynchronous
 * database queries or tasks.</p>
 *
 * <p>Supported database types:
 * <ul>
 *   <li>"mysql"</li>
 *   <li>"mariadb"</li>
 *   <li>"postgresql"</li>
 * </ul>
 * </p>
 *
 * <p>The database connection is initialized during construction, and can be accessed via
 * {@link #getDataSource()}. The executor service is exposed through helper methods for running
 * tasks asynchronously.</p>
 */
public class DatabaseUtil {
    private final HikariDataSource dataSource;
    private final ExecutorService executor;

    /**
     * Creates and initializes the database connection pool using the provided parameters.
     *
     * @param type              the database type ("mysql", "mariadb", "postgresql")
     * @param address           database host and port (e.g. "localhost:3306")
     * @param database          database name
     * @param username          database username
     * @param password          database password
     * @param prefix            the connection pool name
     * @param maxPoolSize       maximum number of active connections in the pool
     * @param minimumIdle       minimum number of idle connections in the pool
     * @param maxLifeTime       maximum lifetime of a connection before it is recycled (ms)
     * @param keepaliveTime     interval for keeping idle connections alive (ms)
     * @param connectionTimeout maximum wait time when obtaining a connection (ms)
     * @param useSSL            whether SSL should be used for the connection
     * @param threadPool        size of the async executor thread pool
     * @throws RuntimeException if database driver is missing or configuration fails
     */
    public DatabaseUtil(@NotNull String type, @NotNull String address, @NotNull String database, @NotNull String username,
                        @NotNull String password, @NotNull String prefix, int maxPoolSize, int minimumIdle, long maxLifeTime,
                        long keepaliveTime, long connectionTimeout, boolean useSSL, int threadPool) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("maxPoolSize must be greater than 0.");
        }

        if (minimumIdle < 0 || minimumIdle > maxPoolSize) {
            throw new IllegalArgumentException("minimumIdle must be between 0 and maxPoolSize.");
        }

        if (threadPool <= 0) {
            throw new IllegalArgumentException("threadPool must be greater than 0.");
        }

        if (keepaliveTime > 0 && keepaliveTime >= maxLifeTime) {
            throw new IllegalArgumentException("keepaliveTime must be less than maxLifeTime.");
        }

        HikariConfig config = new HikariConfig();
        String normalizedType = type.toLowerCase(Locale.ROOT).trim();

        switch (normalizedType) {
            case "mysql" -> {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");

                config.setJdbcUrl("jdbc:mysql://" + address + "/" + database);

                config.addDataSourceProperty("sslMode", useSSL ? "VERIFY_IDENTITY" : "DISABLED");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("useServerPrepStmts", "true");
            }

            case "mariadb" -> {
                config.setDriverClassName("org.mariadb.jdbc.Driver");

                config.setJdbcUrl("jdbc:mariadb://" + address + "/" + database);

                config.addDataSourceProperty("sslMode", useSSL ? "verify-full" : "disable");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("useServerPrepStmts", "true");
            }

            case "postgresql" -> {
                config.setDriverClassName("org.postgresql.Driver");

                config.setJdbcUrl("jdbc:postgresql://" + address + "/" + database);

                config.addDataSourceProperty("sslmode", useSSL ? "verify-full" : "disable");
                config.addDataSourceProperty("tcpKeepAlive", "true");
            }

            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
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
    }

    /**
     * Returns the initialized {@link DataSource}.
     *
     * @return the active data source
     * @throws IllegalStateException if the data source is not initialized
     */
    @NotNull
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Closes the data source and shuts down the executor service.
     * Should be called when the plugin or server is shutting down.
     */
    public void close() {
        this.executor.shutdown();

        try {
            if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();

                if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    ExtraLib.getLogger().warn("Database executor did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            this.dataSource.close();
        }
    }

    /**
     * Runs the given task asynchronously using the internal executor service.
     *
     * @param task the task to execute asynchronously
     */
    public void async(@NotNull Runnable task) {
        runAsync(task).exceptionally(throwable -> {
            ExtraLib.getLogger().error("An error occurred while executing asynchronous database task.", throwable);

            return null;
        });
    }

    /**
     * Submits a task that returns a value asynchronously.
     *
     * @param task the supplier providing the result
     * @param <T>  the type of result returned by the task
     * @return a {@link CompletableFuture} representing the asynchronous computation
     */
    @NotNull
    public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, this.executor);
    }

    /**
     * Runs a task asynchronously that does not return a value.
     *
     * @param task the runnable task to execute
     * @return a {@link CompletableFuture} that completes once the task is finished
     */
    @NotNull
    public CompletableFuture<Void> runAsync(@NotNull Runnable task) {
        return CompletableFuture.runAsync(task, this.executor);
    }
}