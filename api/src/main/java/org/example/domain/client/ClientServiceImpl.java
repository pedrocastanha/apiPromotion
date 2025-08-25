package org.example.domain.client;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.User;
import org.example.domain.user.UserRepository;
import org.example.exception.ClientNotFoundException;
import org.example.service.CSV.CsvHeaderAliasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
   private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
   private final ClientRepository clientRepository;
   private final UserRepository userRepository;
   private final CsvHeaderAliasService headerAliasService;
   private final ClientMapper clientMapper;
   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

   @Override
   public Client createClient(ClientRecord.clientDTO dto) {
      User user = userRepository.findById(dto.user_id())
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.user_id()));
      Client client = Client.builder()
        .name(dto.name())
        .email(dto.email())
        .phoneNumber(dto.phoneNumber())
        .product(dto.product())
        .amount(dto.amount())
        .active(true)
        .lastPurchase(dto.lastPurchase())
        .createdAt(Timestamp.from(Instant.now()))
        .user(user)
        .build();

      Client savedClient = clientRepository.save(client);
      log.info("Successfully created client with ID: {} for user ID: {}", savedClient.getId(), dto.user_id());
      return savedClient;
   }

   @Override
   public void importClientsCSV(MultipartFile file, Long userId) {
      User userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

      try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
         String[] header = reader.readNext();
         List<Client> clientsToSave = new ArrayList<>();
         int lineNumber = 2;

         if (header == null) {
            throw new RuntimeException("CSV empty or without header.");
         }

         Map<String, Integer> columnIndex = IntStream.range(0, header.length)
           .boxed()
           .collect(Collectors.toMap(
             i -> headerAliasService.resolveCanonical(header[i]),
             i -> i,
             (existentValue, newValue) -> newValue
           ));

         List<String> missing = headerAliasService.requiredFields().stream()
           .filter(f -> !columnIndex.containsKey(f))
           .toList();
         if (!missing.isEmpty())
            throw new RuntimeException("Missing required fields: " + missing);

         String[] line;
         while ((line = reader.readNext()) != null) {
            try {
               LocalDate lastPurshaseDate = LocalDate.parse(line[columnIndex.get("lastpurchase")], dateFormatter);
               ClientRecord.importClientsDTO dto = ClientRecord.importClientsDTO.builder()
                 .name(line[columnIndex.get("name")])
                 .phoneNumber(line[columnIndex.get("phonenumber")])
                 .product(line[columnIndex.get("product")])
                 .amount(new BigDecimal(line[columnIndex.get("amount")]))
                 .lastPurchase(lastPurshaseDate)
                 .email(columnIndex.containsKey("email") && line[columnIndex.get("email")].isBlank() ? null : line[columnIndex.get("email")])
                 .build();

               Client newClient = new Client(dto, userEntity);
               newClient.setActive(true);
               clientsToSave.add(newClient);

            } catch (Exception e) {
               throw new RuntimeException("Error processing the line " + lineNumber + ": " + Arrays.toString(line), e);
            }
            lineNumber++;
         }
         clientRepository.saveAll(clientsToSave);
         log.info("Successfully imported {} clients from CSV for user ID: {}", clientsToSave.size(), userId);
      } catch (Exception e) {
         log.error("Failed to import CSV for user ID: {}. Error: {}", userId, e.getMessage());
         throw new RuntimeException("Failed to import CSV file: " + e.getMessage(), e);
      }
   }

   @Override
   @Transactional(readOnly = true)
   public List<ClientRecord.clientListDTO> getClientsByUserId(Long userId){
      return clientRepository.findAllAsDTOByUserId(userId);
   }

   @Override
   @Transactional
   public ClientRecord.clientListDTO updateClient(Integer id, ClientRecord.updateClientDTO dto) {
      Client entity = clientRepository.findById(id)
        .orElseThrow(() -> {
           log.warn("Update attempt failed. Client ID: {} not found.", id);
           return new ClientNotFoundException(id);
        });

      clientMapper.updateClientFromDto(dto, entity);
      log.info("Client ID: {} updated with success.", id);

      return clientMapper.toClientListDTO(entity);
   }

   @Override
   @Transactional
   public void deleteClient(Integer id) {
      if (!clientRepository.existsById(id)) {
         log.warn("Deletion attempt failed. Client ID: {} not found.", id);
         throw new ClientNotFoundException(id);
      }
      clientRepository.deleteById(id);
      log.info("Client ID: {} deleted.", id);
   }
}
