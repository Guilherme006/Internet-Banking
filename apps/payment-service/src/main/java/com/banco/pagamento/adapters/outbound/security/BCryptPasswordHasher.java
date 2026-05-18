package com.banco.pagamento.adapters.outbound.security;

import com.banco.pagamento.ports.outbound.PasswordHasherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher implements PasswordHasherPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String senha) {
        return passwordEncoder.encode(senha);
    }

    @Override
    public boolean matches(String senha, String senhaHash) {
        return passwordEncoder.matches(senha, senhaHash);
    }
}
