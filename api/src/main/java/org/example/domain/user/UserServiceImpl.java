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
    public String generateCampaignMessage(User user, String prompt) {
        logger.info("Iniciando geração de mensagem para campanha do usuário ID: {}", user.getId());

        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalStateException("{error.campaign.userHasNoCompany," + user.getId() + "}");
        }

        UserRecord.ChatApiRequest apiRequest = new UserRecord.ChatApiRequest(
          company.getName(),
          company.getType(),
          prompt
        );
        String requestJson = GSON.toJson(apiRequest);

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(chatApiUrl))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestJson))
          .timeout(Duration.ofSeconds(30))
          .build();

        try {
            logger.debug("Enviando requisição para a API de Chat no endereço: {}", chatApiUrl);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Falha na API de Chat. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("Erro na comunicação com o serviço de IA: status " + response.statusCode());
            }

            UserRecord.ChatApiResponse apiResponse = GSON.fromJson(response.body(), UserRecord.ChatApiResponse.class);
            if (apiResponse == null || apiResponse.response() == null) {
                logger.warn("API de Chat retornou uma resposta válida (200 OK) mas com corpo nulo ou inválido.");
                throw new IllegalStateException("Resposta inválida recebida do serviço de IA.");
            }

            logger.info("Mensagem gerada com sucesso pela IA para o usuário ID: {}", user.getId());
            return apiResponse.response();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Falha de comunicação com a API de IA para o usuário {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Falha de comunicação com o serviço de IA.", e);
        }
    }
}