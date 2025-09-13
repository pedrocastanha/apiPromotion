package org.example.domain.user;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.example.domain.company.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();
    private static final Gson GSON = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${chat.api.url}")
    private String chatApiUrl;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(Long.valueOf(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCampaignMessage(User user, String prompt) {
        logger.info(messageSource.getMessage("user.campaign.message.start", new Object[]{user.getId()}, Locale.getDefault()));

        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalStateException(messageSource.getMessage("user.campaign.noCompany", new Object[]{user.getId()}, Locale.getDefault()));
        }

        UserRecord.ChatApiRequest apiRequest = new UserRecord.ChatApiRequest(
          prompt,
          company.getName(),
          company.getType()
        );
        String requestJson = GSON.toJson(apiRequest);

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(chatApiUrl))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestJson))
          .timeout(Duration.ofSeconds(30))
          .build();

        try {
            logger.debug(messageSource.getMessage("user.chat.api.request", new Object[]{chatApiUrl}, Locale.getDefault()));
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error(messageSource.getMessage("user.chat.api.failure", new Object[]{response.statusCode(), response.body()}, Locale.getDefault()));
                throw new RuntimeException(messageSource.getMessage("user.chat.api.error", new Object[]{response.statusCode()}, Locale.getDefault()));
            }

            UserRecord.ChatApiResponse apiResponse = GSON.fromJson(response.body(), UserRecord.ChatApiResponse.class);
            if (apiResponse == null || apiResponse.response() == null) {
                logger.warn(messageSource.getMessage("user.chat.api.invalidResponse", null, Locale.getDefault()));
                throw new IllegalStateException(messageSource.getMessage("user.chat.api.invalid", null, Locale.getDefault()));
            }

            logger.info(messageSource.getMessage("user.campaign.message.success", new Object[]{user.getId()}, Locale.getDefault()));
            return apiResponse.response();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(messageSource.getMessage("user.chat.api.communicationFailure", new Object[]{user.getId(), e.getMessage()}, Locale.getDefault()));
            throw new RuntimeException(messageSource.getMessage("user.chat.api.communicationError", null, Locale.getDefault()), e);
        }
    }
}