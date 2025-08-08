package org.example.domain.client;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClientService {
    Client createClient(ClientRecord.createClientDTO dto);

    void importClientsCSV(MultipartFile file, Long userId);

    List<ClientRecord.ClientListDTO> getClientsByUserId(Long userId);
}
