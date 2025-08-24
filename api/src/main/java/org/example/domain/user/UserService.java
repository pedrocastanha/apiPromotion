package org.example.domain.user;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    String generateCampaignMessage(User user, String prompt);
}