package org.example.domain.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
public class ClientController {
    private final ClientService clientService;
    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.CREATED)
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
        return ResponseEntity.ok("Client import completed successfully.");
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PatchMapping("/edit-client/{id}")
    public ResponseEntity<ClientRecord.clientListDTO> editClient(
      @PathVariable Integer id,
      @Valid @RequestBody ClientRecord.updateClientDTO dto) {
        ClientRecord.clientListDTO updatedClient = clientService.updateClient(id, dto);
        return ResponseEntity.ok(updatedClient);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable @Positive(message="Client ID must be positive") Integer id, Locale locale) {
        try {
            clientService.deleteClient(id);
            String successMessage = messageSource.getMessage("client.deleted", null, locale);
            return ResponseEntity.ok(successMessage);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

}
