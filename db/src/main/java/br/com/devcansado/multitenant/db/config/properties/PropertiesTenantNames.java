package br.com.devcansado.multitenant.db.config.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.database")
public class PropertiesTenantNames {
  private List<String> tenants;

  public List<String> getTenants() {
    return tenants;
  }

  public void setTenants(List<String> tenants) {
    this.tenants = tenants;
  }
}