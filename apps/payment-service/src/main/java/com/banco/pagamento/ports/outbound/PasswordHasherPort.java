package com.banco.pagamento.ports.outbound;

public interface PasswordHasherPort {

    String hash(String senha);

    boolean matches(String senha, String senhaHash);
}
