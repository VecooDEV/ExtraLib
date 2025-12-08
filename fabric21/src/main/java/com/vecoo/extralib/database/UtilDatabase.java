package com.vecoo.extralib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class UtilDatabase {
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
     * @param keepaliveTime     how often the pool checks for idle connections (ms)
     * @param connectionTimeout maximum wait time when obtaining a connection (ms)
     * @param useSSL            whether SSL should be used for the connection
     * @param threadPool        size of the async executor thread pool
     * @throws RuntimeException if database driver is missing or configuration fails
     */
    public UtilDatabase(@NotNull String type, @NotNull String address, @NotNull String database, @NotNull String username,
                        @NotNull String password, @NotNull String prefix, int maxPoolSize, int minimumIdle, long maxLifeTime,
                        long keepaliveTime, long connectionTimeout, boolean useSSL, int threadPool) {
        HikariConfig config = new HikariConfig();
        String normalizedType = type.toLowerCase();

        try {
            switch (normalizedType) {
                case "mysql" -> {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MySQL driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mysql://" + address + "/" + database + ssl);
                }

                case "mariadb" -> {
                    try {
                        Class.forName("com.vecoo.extralib.shade.mariadb.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("MariaDB driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?useSSL=false";

                    config.setJdbcUrl("jdbc:mariadb://" + address + "/" + database + ssl);
                }

                case "postgresql" -> {
                    try {
                        Class.forName("com.vecoo.extralib.shade.postgresql.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("PostgreSQL driver not found.", e);
                    }

                    String ssl = useSSL ? "" : "?sslmode=disable";

                    config.setJdbcUrl("jdbc:postgresql://" + address + "/" + database + ssl);
                }

                default -> throw new IllegalStateException("Unsupported database type.");
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

    /**
     * Returns the initialized {@link DataSource}.
     *
     * @return the active data source
     * @throws IllegalStateException if the data source is not initialized
     */
    @NotNull
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            throw new IllegalStateException("Database not initialized.");
        }

        return this.dataSource;
    }

    /**
     * Checks whether the HikariCP data source has been successfully initialized.
     *
     * @return {@code true} if the data source is initialized, otherwise {@code false}
     */
    public boolean isDataSourceInitialized() {
        return this.dataSource != null;
    }

    /**
     * Closes the data source and shuts down the executor service.
     * Should be called when the plugin or server is shutting down.
     */
    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }

        this.executor.shutdown();
    }

    /**
     * Runs the given task asynchronously using the internal executor service.
     *
     * @param task the task to execute asynchronously
     */
    public void async(@NotNull Runnable task) {
        this.executor.execute(task);
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