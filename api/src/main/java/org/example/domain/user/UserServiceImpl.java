package org.example.domain.user;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.example.domain.company.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder( )
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
    public String sendChatMessage(Integer userId, String message) {
        User user = userRepository.findById(Long.valueOf(userId))
          .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + userId));

        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalStateException("O usuário com ID " + userId + " não possui uma empresa associada.");
        }

        UserRecord.ChatApiRequest apiRequest = new UserRecord.ChatApiRequest(
          company.getName(),
          company.getType(),
          message
        );
        String requestJson = GSON.toJson(apiRequest);

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(chatApiUrl))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestJson))
          .timeout(Duration.ofSeconds(20))
          .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Falha na API de Chat. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("Erro na comunicação com o serviço de chat: status " + response.statusCode());
            }

            UserRecord.ChatApiResponse apiResponse = GSON.fromJson(response.body(), UserRecord.ChatApiResponse.class);
            if (apiResponse == null || apiResponse.response() == null) {
                logger.warn("API de Chat retornou uma resposta válida (200 OK) mas com corpo nulo ou inválido.");
                throw new IllegalStateException("Resposta inválida recebida do serviço de chat.");
            }
            return apiResponse.response();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Falha de comunicação com a API de Chat para o usuário {}: {}", userId, e.getMessage());
            throw new RuntimeException("Falha de comunicação com o serviço de chat.", e);
        }
    }
}