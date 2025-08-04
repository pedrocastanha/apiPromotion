package org.example.service.CSV;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CsvHeaderAliasService {
    private final Map<String, String> aliases = Map.ofEntries(
            Map.entry("name", "name"),
            Map.entry("nome", "name"),
            Map.entry("cliente", "name"),
            Map.entry("phonenumber", "phonenumber"),
            Map.entry("telefone", "phonenumber"),
            Map.entry("celular", "phonenumber"),
            Map.entry("product", "product"),
            Map.entry("produto", "product"),
            Map.entry("amount", "amount"),
            Map.entry("valor", "amount"),
            Map.entry("preco", "amount"),
            Map.entry("preço", "amount"),
            Map.entry("lastpurchase", "lastpurchase"),
            Map.entry("datacompra", "lastpurchase"),
            Map.entry("ultimacompra", "lastpurchase"),
            Map.entry("email", "email")
    );

    private final Set<String> required = Set.of(
            "name", "phonenumber", "product", "amount", "lastpurchase"
    );

    private String normalize(String s) {
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.trim().toLowerCase().replace(" ", "");
    }

    public String resolveCanonical(String raw) {
        return aliases.get(normalize(raw));
    }

    public boolean isRequired(String canonical) {
        return required.contains(canonical);
    }

    public List<String> requiredFields() {
        return List.copyOf(required);
    }
}