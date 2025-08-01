package org.example.domain.client;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.User;
import org.example.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Client createClient(ClientRecord.createClientDTO dto) {
        User user = userRepository.findById(dto.user_id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Client client = Client.builder()
                .name(dto.name())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .product(dto.product())
                .amount(dto.amount())
                .active(dto.active())
                .lastPurchase(dto.lastPurchase())
                .createdAt(Timestamp.from(Instant.now()))
                .user(user)
                .build();

        return clientRepository.save(client);
    }
}
