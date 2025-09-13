package org.example.domain.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.exception.CustomExceptions;
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
    public Client createClient(@Valid @RequestBody ClientRecord.clientDTO dto) {
        try {
            return clientService.createClient(dto);
        } catch (Exception e) {
            throw new CustomExceptions.InvalidDataException("Erro ao criar cliente: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/import-clients")
    public ResponseEntity<String> importClients(
      @RequestParam("file") MultipartFile file,
      @RequestParam("userId") Long userId
    ) {
        try {
            if (file == null || file.isEmpty()) {
                throw new CustomExceptions.FileUploadException("Arquivo de importação não fornecido ou vazio");
            }
            clientService.importClientsCSV(file, userId);
            return ResponseEntity.ok(messageSource.getMessage("client.deleted", null, Locale.getDefault()));
        } catch (CustomExceptions.FileUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomExceptions.FileUploadException("Erro ao importar clientes: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/edit-client/{id}")
    public ResponseEntity<ClientRecord.clientListDTO> editClient(
      @PathVariable Integer id,
      @Valid @RequestBody ClientRecord.updateClientDTO dto
    ) {
        try {
            ClientRecord.clientListDTO updatedClient = clientService.updateClient(id, dto);
            if (updatedClient == null) {
                throw new CustomExceptions.ResourceNotFoundException("Cliente", "ID", id);
            }
            return ResponseEntity.ok(updatedClient);
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomExceptions.InvalidDataException("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClient(
      @PathVariable @Positive(message = "Client ID must be positive") Integer id,
      Locale locale
    ) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.ok(messageSource.getMessage("client.deleted", null, locale));
        } catch (Exception e) {
            throw new CustomExceptions.ResourceNotFoundException("Cliente", "ID", id);
        }
    }
}