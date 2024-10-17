package br.com.devcansado.multitenant.db.config.tenant;

import java.util.HashMap;
import br.com.devcansado.multitenant.db.config.properties.PropertiesConnectionDatabase;
import br.com.devcansado.multitenant.db.config.properties.PropertiesTenantNames;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component
public class TenantRoutingDatasource extends AbstractRoutingDataSource {


  private TenantIdentifierResolver tenantIdentifierResolver;
  private PropertiesConnectionDatabase propertiesConnectionDatabase;

  public TenantRoutingDatasource(TenantIdentifierResolver tenantIdentifierResolver,
                                 PropertiesTenantNames propertiesTenantNames,
                                 PropertiesConnectionDatabase propertiesConnectionDatabase) {
    this.tenantIdentifierResolver = tenantIdentifierResolver;
    this.propertiesConnectionDatabase = propertiesConnectionDatabase;

//    setDefaultTargetDataSource(createEmbeddedDatabase("default"));

    HashMap<Object, Object> targetDataSources = new HashMap<>();

    if (propertiesTenantNames != null) {
      propertiesTenantNames.getTenants()
          .forEach(tenant -> targetDataSources.put(tenant, createMariaDBDataSource(tenant)));
    }
    setTargetDataSources(targetDataSources);
  }

  @Override
  protected String determineCurrentLookupKey() {
    return tenantIdentifierResolver.resolveCurrentTenantIdentifier();
  }

  private EmbeddedDatabase createEmbeddedDatabase(String name) {

    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .setName(name)
        .build();
  }

  private DataSource createMariaDBDataSource(String database) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(propertiesConnectionDatabase.getDriver());
    dataSource.setUrl(propertiesConnectionDatabase.getUrl().replace("{{TENANT}}", database));
    dataSource.setUsername(propertiesConnectionDatabase.getUsername());
    dataSource.setPassword(propertiesConnectionDatabase.getPassword());

//    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//    populator.addScript(new ClassPathResource("manual-schema.sql"));
//    populator.execute(dataSource);

    flywayMigration(dataSource);

    return dataSource;
  }

  private void flywayMigration(DriverManagerDataSource dataSource) {
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .baselineOnMigrate(true)
        .locations("classpath:db/migration")
        .load();
    flyway.migrate();
  }

}