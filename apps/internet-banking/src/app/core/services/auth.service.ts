import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  obterToken(): string | null {
    return null;
  }

  obterUsuario(): { nome: string; agencia: string; conta: string } {
    return {
      nome: 'João da Silva',
      agencia: '0001',
      conta: '12345-6',
    };
  }
}
