import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Comprovante } from '../../../../domain/models/pagamento.model';

@Component({
  selector: 'app-comprovante',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, MatButtonModule, MatIconModule, MatDividerModule],
  template: `
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="comprovante-titulo"
      class="flex flex-col items-center"
    >
            <div class="w-20 h-20 rounded-full bg-emerald-50 flex items-center justify-center mb-5 mt-2 animate-bounce">
        <mat-icon aria-hidden="true" class="text-emerald-500 !text-5xl !w-12 !h-12">check_circle</mat-icon>
      </div>

      <h2
        id="comprovante-titulo"
        class="text-xl font-bold text-slate-900 mb-1"
        tabindex="-1"
        #tituloRef
      >
        {{ comprovante.reprocessado ? 'Pagamento Já Processado' : 'Pagamento Realizado!' }}
      </h2>

      @if (comprovante.reprocessado) {
        <p class="text-sm text-amber-600 bg-amber-50 px-3 py-1.5 rounded-full mb-4">
          Este boleto já havia sido pago anteriormente. O comprovante original foi retornado.
        </p>
      } @else {
        <p class="text-sm text-slate-500 mb-6">Seu boleto foi pago com sucesso.</p>
      }

            <div
        class="w-full bg-slate-50 rounded-xl border border-slate-200 p-5 text-sm mb-5"
        aria-label="Detalhes do comprovante"
      >
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-slate-500">Nº do Comprovante</dt>
            <dd class="font-mono font-semibold text-slate-800 text-xs">{{ comprovante.transacaoId }}</dd>
          </div>
          <mat-divider />
          <div class="flex justify-between">
            <dt class="text-slate-500">Data e Hora</dt>
            <dd class="font-semibold text-slate-800">
              {{ comprovante.dataHora | date:'dd/MM/yyyy HH:mm:ss' }}
            </dd>
          </div>
          <mat-divider />
          <div class="flex justify-between">
            <dt class="text-slate-500">Conta Debitada</dt>
            <dd class="font-semibold text-slate-800">{{ comprovante.numeroConta }}</dd>
          </div>
          <mat-divider />
          <div class="flex justify-between items-center">
            <dt class="text-slate-500">Valor Pago</dt>
            <dd class="text-lg font-bold text-emerald-600">
              {{ comprovante.valorDebitado | currency:'BRL':'symbol':'1.2-2' }}
            </dd>
          </div>
          <mat-divider />
          <div class="flex justify-between">
            <dt class="text-slate-500">Status</dt>
            <dd>
              <span class="px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700">
                {{ comprovante.status }}
              </span>
            </dd>
          </div>
        </dl>
      </div>

            <div class="flex gap-3 w-full">
        <button
          mat-stroked-button
          type="button"
          class="flex-1 !rounded-xl"
          (click)="novoPagamento.emit()"
          aria-label="Realizar novo pagamento"
        >
          <mat-icon aria-hidden="true">add</mat-icon>
          Novo Pagamento
        </button>
        <button
          mat-flat-button
          color="primary"
          type="button"
          class="flex-1 !rounded-xl"
          (click)="imprimirComprovante()"
          aria-label="Imprimir comprovante"
        >
          <mat-icon aria-hidden="true">print</mat-icon>
          Imprimir
        </button>
      </div>
    </div>
  `,
})
export class ComprovanteComponent {
  @Input({ required: true }) comprovante!: Comprovante;
  @Output() novoPagamento = new EventEmitter<void>();

  protected imprimirComprovante(): void {
    window.print();
  }
}
