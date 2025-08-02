package org.example.domain.client;

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
    public Client createClient(@RequestBody ClientRecord.createClientDTO dto) {
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
}
