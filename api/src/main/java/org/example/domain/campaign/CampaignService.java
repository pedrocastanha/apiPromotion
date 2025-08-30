package org.example.domain.campaign;

import org.example.domain.user.User;

public interface CampaignService {
   void scheduleCampaign(User user, CampaignRecord.SendCampaignRequest request);

}
