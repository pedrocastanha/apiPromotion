package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClinicaConfig {
    @Value("${clinica.endereco:Rua Exemplo, 123}")
    private String endereco;
    
    @Value("${clinica.cidade:São Paulo}")
    private String cidade;
    
    @Value("${clinica.cep:01234-567}")
    private String cep;
    
    @Value("${clinica.complemento:}")
    private String complemento;
    
    public String getEnderecoCompleto() {
        return String.format("%s, %s, CEP %s %s", 
            endereco, cidade, cep, complemento != null && !complemento.isEmpty() ? complemento : "");
    }
}
