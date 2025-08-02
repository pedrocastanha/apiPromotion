package org.example.domain.client;

import org.springframework.web.multipart.MultipartFile;

public interface ClientService {
    Client createClient(ClientRecord.createClientDTO dto);

    void importClientsCSV(MultipartFile file, Long userId);
}
