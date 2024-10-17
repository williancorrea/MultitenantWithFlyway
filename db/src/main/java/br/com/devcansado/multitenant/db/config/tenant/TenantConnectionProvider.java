package br.com.devcansado.multitenant.db.config.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TenantConnectionProvider implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

  @SuppressWarnings("java:S1948")
  private final DataSource dataSource;

  public TenantConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

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
  public boolean isUnwrappableAs(@NonNull Class<?> aClass) {
    return false;
  }

  @Override
  public <T> T unwrap(@NonNull Class<T> aClass) {
    throw new UnsupportedOperationException("Can't unwrap this.");
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
  }

  @Override
  public Connection getConnection(String tenantIdentifier) throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
    connection.close();
  }

}