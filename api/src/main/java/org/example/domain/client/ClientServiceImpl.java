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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
   private final ClientRepository clientRepository;
   private final UserRepository userRepository;
   private final CsvHeaderAliasService headerAliasService;

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
      try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
         SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
         String[] line;
         int lineNumber = 2;
         String[] header = reader.readNext();

         if (header == null) {
            throw new RuntimeException("CSV está vazio ou sem cabeçalho.");
         }

         Map<String, Integer> columnIndex = new HashMap<>();
         for (int i = 0; i < header.length; i++) {
            String canonical = headerAliasService.resolveCanonical(header[i]);
            if (canonical != null) {
               columnIndex.put(canonical, i);
            }
         }

         List<String> missing = headerAliasService.requiredFields().stream()
           .filter(f -> !columnIndex.containsKey(f))
           .toList();
         if (!missing.isEmpty())
            throw new RuntimeException("Campos obrigatórios ausentes: " + missing);

         while ((line = reader.readNext()) != null) {
            try {
               ClientRecord.importClientsDTO dto = ClientRecord.importClientsDTO.builder()
                 .name(line[columnIndex.get("name")])
                 .phoneNumber(line[columnIndex.get("phonenumber")])
                 .product(line[columnIndex.get("product")])
                 .amount(new BigDecimal(line[columnIndex.get("amount")]))
                 .lastPurchase(formatter.parse(line[columnIndex.get("lastpurchase")]))
                 .email(columnIndex.containsKey("email") && line[columnIndex.get("email")].isBlank() ? null : line[columnIndex.get("email")])
                 .build();

               Client client = convertToEntity(dto, userId);
               clientRepository.save(client);

            } catch (Exception e) {
               throw new RuntimeException("Erro ao processar linha " + lineNumber + ": " + Arrays.toString(line), e);
            }
            lineNumber++;
         }
      } catch (Exception e) {
         throw new RuntimeException("Erro ao importar o CSV: " + e.getMessage(), e);
      }
   }

   private Client convertToEntity(ClientRecord.importClientsDTO dto, Long userId) {
      User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + userId));

      return Client.builder()
        .user(user)
        .name(dto.name())
        .phoneNumber(dto.phoneNumber())
        .product(dto.product())
        .amount(dto.amount())
        .lastPurchase(dto.lastPurchase())
        .email(dto.email())
        .build();
   }
}
