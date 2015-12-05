package com.accounted4.assetmanager.config;

import java.io.ObjectStreamException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Support multi-tenancy via SCHEMA. Intercepts connection usage and performs a "SET SESSION <schema>" on
 * the connection chaining it on for actual usage.
 *
 * @author gheinze
 */
@Component
public class AssetManagerMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    private static final long serialVersionUID = 1L;

    private transient DataSource dataSource;

    @Inject
    public AssetManagerMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }


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
        try {
            setSchemaTo(connection, NO_TENANT);
        } catch(HibernateException he) {
            LoggerFactory
                    .getLogger(AssetManagerMultiTenantConnectionProvider.class)
                    .warn("Failed to revert schema for connection when closing connection", he);
        }
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


    private static final String SET_SCHEMA_TO = "SELECT set_schema_to(?)";

    private void setSchemaTo(Connection connection, String tenantIdentifier) {
        try (PreparedStatement pStmt = connection.prepareStatement(SET_SCHEMA_TO)) {
            pStmt.setString(1, tenantIdentifier);
            pStmt.execute();
        } catch (SQLException ex) {
            throw new HibernateException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", ex);
        }
    }


    /*
     * TODO: This class needs to be serializable and hence cannot have a DataStource as a member field.
     * To circumvent this, an attempt is made to read the DataSource from the application context directly.
    */
    private void readObjectNoData() throws ObjectStreamException {
        dataSource = ApplicationContextUtil.getApplicationContext().getBean(DataSource.class);
    }

}
