package org.example.domain.user;

import org.example.domain.campaign.CampaignRecord;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    CampaignRecord.CampaignMessageResponse generateCampaignMessage(User user, String prompt);
}