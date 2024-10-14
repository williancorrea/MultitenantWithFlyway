package br.com.devcansado.multitenant.db.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class NoOpConnectionProvider implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {

  @Autowired
  DataSource dataSource;

  @Override
  public Connection getAnyConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    connection.close();
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @Override
  public boolean isUnwrappableAs(Class<?> aClass) {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> aClass) {
    throw new UnsupportedOperationException("Can't unwrap this.");
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
  }

  @Override
  public Connection getConnection(Object tenantIdentifier) throws SQLException {
    return dataSource.getConnection();
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider#releaseConnection(java.lang.Object, java.sql.Connection)
   */
  @Override
  public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
    connection.close();
  }
}