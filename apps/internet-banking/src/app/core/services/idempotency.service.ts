import { Injectable, signal } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';

@Injectable({ providedIn: 'root' })
export class IdempotencyService {
  private readonly _chaveAtual = signal<string>(this.gerarNovaChave());

  obterChaveAtual(): string {
    return this._chaveAtual();
  }

    resetar(): string {
    const novaChave = this.gerarNovaChave();
    this._chaveAtual.set(novaChave);
    return novaChave;
  }

  private gerarNovaChave(): string {
    return uuidv4();
  }
}
