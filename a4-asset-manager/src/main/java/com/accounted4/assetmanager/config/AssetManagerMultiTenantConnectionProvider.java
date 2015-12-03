package com.accounted4.assetmanager.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Support multi-tenancy via SCHEMA. Intercepts connection usage and performs a "SET SESSION <schema>" on
 * the connection chaining it on for actual usage.
 * 
 * @author gheinze
 */
@Component
public class AssetManagerMultiTenantConnectionProvider implements MultiTenantConnectionProvider {


    @Autowired private DataSource dataSource;


    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        setSchemaTo(connection, tenantIdentifier);
        return connection;
    }


    private static final String NO_TENANT = "public";

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        setSchemaTo(connection, NO_TENANT);
        connection.close();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        releaseAnyConnection(connection);
    }


    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class type) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }


    private static final String SET_SCHEMA_FORMAT = "SET SCHEMA '%s'";

    private void setSchemaTo(Connection connection, String tenantIdentifier) {
        try {
            connection.createStatement().execute(String.format(SET_SCHEMA_FORMAT, tenantIdentifier));
        } catch (SQLException ex) {
            throw new HibernateException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", ex);
        }
    }


}
