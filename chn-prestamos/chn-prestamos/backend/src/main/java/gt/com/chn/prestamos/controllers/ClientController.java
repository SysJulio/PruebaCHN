package gt.com.chn.prestamos.controllers;

import gt.com.chn.prestamos.common.NotFoundException;
import gt.com.chn.prestamos.models.Client;
import gt.com.chn.prestamos.repositories.ClientRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * (Crear, Consultar, Actualizar y Eliminar) para la entidad Cliente.
 * 
 * URL base: /api/clients
 */


@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {
    private final ClientRepository clients;

    public ClientController(ClientRepository clients) {
        this.clients = clients;
    }

    /**
     * Obtiene la lista completa de clientes.
     *
     * Método: GET /api/clients
     *
     * @return Lista de clientes registrados
     */
    
    @GetMapping
    public List<Client> list() {
        return clients.findAll();
    }

   /**
     * Obtiene un cliente por su identificador.
     *
     * Método: GET /api/clients/{id}
     *
     * @param id Identificador del cliente
     * @return Cliente encontrado
     */
   
    @GetMapping("/{id}")
    public Client get(@PathVariable Long id) {
        return findClient(id);
    }

    
      /**
     * Crea un nuevo cliente.
     *
     * Método: POST /api/clients
     *
     * @param client Datos del cliente a registrar
     * @return Cliente creado
     */
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@Valid @RequestBody Client client) {
        client.setId(null);
        return clients.save(client);
    }

    /**
     * Actualiza la información de un cliente existente.
     *
     * Método: PUT /api/clients/{id}
     *
     * @param id Identificador del cliente
     * @param input Nuevos datos del cliente
     * @return Cliente actualizado
     */
    
    @PutMapping("/{id}")
    public Client update(@PathVariable Long id, @Valid @RequestBody Client input) {
        Client client = findClient(id);
        client.setFirstName(input.getFirstName());
        client.setLastName(input.getLastName());
        client.setIdentificationNumber(input.getIdentificationNumber());
        client.setBirthDate(input.getBirthDate());
        client.setAddress(input.getAddress());
        client.setEmail(input.getEmail());
        client.setPhone(input.getPhone());
        return clients.save(client);
    }

   /**
     * Elimina un cliente por su ID.
     *
     * Método: DELETE /api/clients/{id}
     *
     * @param id Identificador del cliente
     */
   
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clients.delete(findClient(id));
    }
 /**
     * Método auxiliar para buscar un cliente por ID.
     *
     * Si el cliente no existe, lanza una excepción personalizada.
     *
     * @param id Identificador del cliente
     * @return Cliente encontrado
     * @throws NotFoundException cuando el cliente no existe
     */

    
    private Client findClient(Long id) {
        return clients.findById(id).orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }
}
