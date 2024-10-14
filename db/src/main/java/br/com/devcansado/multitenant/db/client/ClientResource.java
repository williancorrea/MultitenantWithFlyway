package br.com.devcansado.multitenant.db.client;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientsByDatabase")
public class ClientResource {

  private final ClientService clientService;

  public ClientResource(ClientService clientService) {
    this.clientService = clientService;
  }

  @RequestMapping
  public List<Client> findAll() {
    return clientService.findAll();
  }

  @PostMapping
  public Client save(@RequestBody Client client) {
    return clientService.save(client);
  }

}