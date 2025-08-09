package org.example.domain.client;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClientService {
    Client createClient(ClientRecord.clientDTO dto);

    void importClientsCSV(MultipartFile file, Long userId);

    List<ClientRecord.clientListDTO> getClientsByUserId(Long userId);

    ClientRecord.clientListDTO updateClient(Integer id, ClientRecord.updateClientDTO dto);
}
