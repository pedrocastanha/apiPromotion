package org.example.service;

import org.apache.el.stream.Optional;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.model.Clinica;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.ClinicaRepository;
import org.example.repository.UserRepository;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user, UserRole role, BigInteger clinicaId) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Usuário com email " + user.getEmail() + " já existe.");
        }

        user.setSenha(passwordEncoder.encode(user.getSenha()));
        user.setRole(role);
        user.setAtivo(true);

        if (clinicaId != null && (role == UserRole.DONO_CLINICA || role == UserRole.ATENDENTE || role == UserRole.PROFISSIONAL)) {
            Clinica clinica = clinicaRepository.findById(clinicaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com ID: " + clinicaId));
            user.setClinica(clinica);
        } else if (role == UserRole.PACIENTE) {
             if (clinicaId != null) {
                 Clinica clinica = clinicaRepository.findById(clinicaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com ID: " + clinicaId));
                 user.setClinica(clinica);
             }
        } else if (role == UserRole.ADMIN) {
            user.setClinica(null);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(BigInteger id) { // Changed Long to UUID
        return userRepository.findById(id);
    }

    public User getUserById(BigInteger id) { // Convenience method with exception
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    // --- User Update Methods --- 

    @Transactional
    public User updateUserProfile(BigInteger userId, User updatedData) {
        User existingUser = getUserById(userId);

        if (updatedData.getNome() != null) {
            existingUser.setNome(updatedData.getNome());
        }
        if (updatedData.getTelefone() != null) {
            existingUser.setTelefone(updatedData.getTelefone());
        }
        if (updatedData.getCpf() != null) {
             existingUser.setCpf(updatedData.getCpf());
        }
        if (updatedData.getDataNascimento() != null) {
             existingUser.setDataNascimento(updatedData.getDataNascimento());
        }
        // Update address fields
        if (updatedData.getEnderecoLogradouro() != null) existingUser.setEnderecoLogradouro(updatedData.getEnderecoLogradouro());
        if (updatedData.getEnderecoNumero() != null) existingUser.setEnderecoNumero(updatedData.getEnderecoNumero());
        if (updatedData.getEnderecoComplemento() != null) existingUser.setEnderecoComplemento(updatedData.getEnderecoComplemento());
        if (updatedData.getEnderecoBairro() != null) existingUser.setEnderecoBairro(updatedData.getEnderecoBairro());
        if (updatedData.getEnderecoCidade() != null) existingUser.setEnderecoCidade(updatedData.getEnderecoCidade());
        if (updatedData.getEnderecoUf() != null) existingUser.setEnderecoUf(updatedData.getEnderecoUf());
        if (updatedData.getEnderecoCep() != null) existingUser.setEnderecoCep(updatedData.getEnderecoCep());

        // Persist changes
        return userRepository.save(existingUser);
    }

    @Transactional
    public void changePassword(BigInteger userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        // Encode and set new password
        user.setSenha(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // --- User Activation/Deactivation --- 

    @Transactional
    public void setUserActiveStatus(BigInteger userId, boolean isActive) {
        User user = getUserById(userId);
        user.setAtivo(isActive);
        userRepository.save(user);
    }

    // --- Email Verification --- 
    // (Placeholder - requires token generation/validation logic)
    @Transactional
    public void setEmailVerified(BigInteger userId) {
         User user = getUserById(userId);
         user.setEmailVerificado(true);
         user.setTokenVerificacaoEmail(null); // Clear token after verification
         userRepository.save(user);
    }

    // Add methods for password reset flow (generate token, validate token, reset password)

}

