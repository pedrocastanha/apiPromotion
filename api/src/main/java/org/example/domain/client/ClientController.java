package org.example.domain.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/create")
    public Client createClient(@RequestBody ClientRecord.clientDTO dto) {
        return clientService.createClient(dto);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/import-clients")
    public ResponseEntity<String> importClients(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId
    ) {
        clientService.importClientsCSV(file, userId);
        return ResponseEntity.ok("Importação realizada com sucesso.");
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PatchMapping("/edit-client/{id}")
    public ResponseEntity<ClientRecord.clientListDTO> editClient(
      @PathVariable Integer id,
      @Valid @RequestBody ClientRecord.updateClientDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Integer id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.ok("Client deleted with sucess.");
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

}
