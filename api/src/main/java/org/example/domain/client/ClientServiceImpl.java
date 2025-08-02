package org.example.domain.client;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.User;
import org.example.domain.user.UserRepository;
import org.example.service.CSV.CsvHeaderAliasService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
   private final ClientRepository clientRepository;
   private final UserRepository userRepository;
   private final CsvHeaderAliasService headerAliasService;
   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

   @Override
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

   @Override
   public void importClientsCSV(MultipartFile file, Long userId) {
      User userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário ID:    " + userId + " não encontrado."));

      try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
         String[] header = reader.readNext();
         List<Client> clientsToSave = new ArrayList<>();
         int lineNumber = 2;

         if (header == null) {
            throw new RuntimeException("CSV está vazio ou sem cabeçalho.");
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
            throw new RuntimeException("Campos obrigatórios ausentes: " + missing);

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
               clientsToSave.add(newClient);

            } catch (Exception e) {
               throw new RuntimeException("Erro ao processar linha " + lineNumber + ": " + Arrays.toString(line), e);
            }
            lineNumber++;
         }
         clientRepository.saveAll(clientsToSave);

      } catch (Exception e) {
         throw new RuntimeException("Erro ao importar o CSV: " + e.getMessage(), e);
      }
   }
}
