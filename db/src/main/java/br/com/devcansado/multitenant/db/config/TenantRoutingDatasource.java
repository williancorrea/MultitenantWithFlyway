package br.com.devcansado.multitenant.db.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component
public class TenantRoutingDatasource extends AbstractRoutingDataSource {

  @Autowired
  private TenantIdentifierResolver tenantIdentifierResolver;


  TenantRoutingDatasource() {

    setDefaultTargetDataSource(createEmbeddedDatabase("default"));

    HashMap<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("TENANT_VMWARE", createEmbeddedDatabase("TENANT_VMWARE"));
    targetDataSources.put("TENANT_PIVOTAL", createEmbeddedDatabase("TENANT_PIVOTAL"));
    targetDataSources.put("TENANT_CUSTOM", createMariaDBDataSource());

    setTargetDataSources(targetDataSources);
  }

  @Override
  protected String determineCurrentLookupKey() {
    return tenantIdentifierResolver.resolveCurrentTenantIdentifier();
  }

  private EmbeddedDatabase createEmbeddedDatabase(String name) {

    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .setName(name).addScript("manual-schema.sql")
        .build();
  }

  private DataSource createMariaDBDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
    dataSource.setUrl("jdbc:mariadb://localhost:3306/TENANT_CUSTOM?useSSL=false&serverTimezone=UTC");
    dataSource.setUsername("tenant");
    dataSource.setPassword("tenant");

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("manual-schema.sql"));
    populator.execute(dataSource);

    return dataSource;
  }

}