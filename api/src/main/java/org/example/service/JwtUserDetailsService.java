package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Good practice for read operations
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Check if user is active
        if (!user.isAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        // Create GrantedAuthority based on the user's role
        // Spring Security typically expects roles prefixed with "ROLE_"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        // Return Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Use email as username
                user.getSenha(), // Encoded password
                user.isAtivo(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities // User's authorities/roles
        );
    }

    // Optional: Method to load UserDetails by ID (useful for JWT validation)
    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
         User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o ID: " + id));

         if (!user.isAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + id);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getSenha(),
                user.isAtivo(),
                true,
                true,
                true,
                authorities
        );
    }
}

