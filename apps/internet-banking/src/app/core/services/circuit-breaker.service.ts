import { Injectable, computed, signal } from '@angular/core';

type EstadoCircuito = 'FECHADO' | 'ABERTO' | 'SEMI_ABERTO';

@Injectable({ providedIn: 'root' })
export class CircuitBreakerService {
  private readonly LIMIAR_FALHAS = 5;
  private readonly JANELA_TEMPO_MS = 60_000;
  private readonly TEMPO_RECUPERACAO_MS = 30_000;

  private readonly _falhas = signal<number[]>([]);
  private readonly _estado = signal<EstadoCircuito>('FECHADO');
  private readonly _abrioEm = signal<number | null>(null);

  readonly estado = this._estado.asReadonly();
  readonly circuitoAberto = computed(() => this._estado() === 'ABERTO');

  registrarFalha(): void {
    const agora = Date.now();
    const falhasNaJanela = this._falhas().filter(t => agora - t < this.JANELA_TEMPO_MS);
    falhasNaJanela.push(agora);
    this._falhas.set(falhasNaJanela);

    if (falhasNaJanela.length >= this.LIMIAR_FALHAS) {
      this._estado.set('ABERTO');
      this._abrioEm.set(agora);
    }
  }

  registrarSucesso(): void {
    this._falhas.set([]);
    this._estado.set('FECHADO');
    this._abrioEm.set(null);
  }

  verificarTransicao(): void {
    const abrioEm = this._abrioEm();
    if (this._estado() === 'ABERTO' && abrioEm !== null) {
      if (Date.now() - abrioEm > this.TEMPO_RECUPERACAO_MS) {
        this._estado.set('SEMI_ABERTO');
      }
    }
  }

  tentarNovamente(): void {
    this.verificarTransicao();
    if (this._estado() === 'SEMI_ABERTO' || this._estado() === 'ABERTO') {
      this._falhas.set([]);
      this._estado.set('FECHADO');
      this._abrioEm.set(null);
    }
  }
}
