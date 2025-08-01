package org.example.domain.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/create-client")
    public Client createClient(@RequestBody ClientRecord.createClientDTO dto) {
        return clientService.createClient(dto);
    }
}
