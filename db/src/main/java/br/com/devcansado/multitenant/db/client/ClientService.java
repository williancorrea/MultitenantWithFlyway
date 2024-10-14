package br.com.devcansado.multitenant.db.client;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public List<Client> findAll() {
    return this.clientRepository.findAll();
  }

  public Client save(Client client) {
    return this.clientRepository.save(client);
  }

}