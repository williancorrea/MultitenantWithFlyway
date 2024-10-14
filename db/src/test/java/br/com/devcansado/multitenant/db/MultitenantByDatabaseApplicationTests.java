package br.com.devcansado.multitenant.db;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.devcansado.multitenant.db.client.Client;
import br.com.devcansado.multitenant.db.client.ClientRepository;
import br.com.devcansado.multitenant.db.config.TenantIdentifierResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
class MultitenantByDatabaseApplicationTests {

  public static final String TENANT_PIVOTAL = "TENANT_PIVOTAL";
  public static final String TENANT_VMWARE = "TENANT_VMWARE";
  @Autowired
  ClientRepository clientRepository;

  @Autowired
  TransactionTemplate txTemplate;

  @Autowired
  TenantIdentifierResolver currentTenant;

  @Test
  void saveAndLoadClient() {
    createClient(TENANT_PIVOTAL, "Adam");
    createClient(TENANT_VMWARE, "Eve");

    currentTenant.setCurrentTenant(TENANT_VMWARE);
    assertThat(clientRepository.findAll())
        .extracting(Client::getName)
        .containsExactly("Eve");

    currentTenant.setCurrentTenant(TENANT_PIVOTAL);
    assertThat(clientRepository.findAll())
        .extracting(Client::getName)
        .containsExactly("Adam");
  }

  private Client createClient(String schema, String name) {

    currentTenant.setCurrentTenant(schema);
    Client adam = txTemplate.execute(tx -> clientRepository.save(new Client(name)));

    assertThat(adam.getId()).isNotNull();
    return adam;
  }
}
