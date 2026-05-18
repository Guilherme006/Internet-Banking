import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-error-state',
  standalone: true,
  imports: [MatButtonModule, MatIconModule],
  template: `
    <div
      role="alert"
      aria-live="assertive"
      class="flex flex-col items-center gap-5 py-14 px-6 text-center"
    >
      <div class="w-20 h-20 rounded-full bg-red-50 flex items-center justify-center">
        <mat-icon aria-hidden="true" class="text-red-500 !text-4xl !w-10 !h-10">
          error_outline
        </mat-icon>
      </div>

      <div>
        <h3 class="text-lg font-semibold text-slate-800 mb-1">Algo deu errado</h3>
        <p class="text-sm text-slate-500 max-w-xs">{{ mensagem }}</p>
      </div>

      @if (mostrarRetry) {
        <button
          mat-flat-button
          color="primary"
          (click)="retry.emit()"
          class="!rounded-full"
          aria-label="Tentar novamente"
        >
          <mat-icon aria-hidden="true">refresh</mat-icon>
          Tentar novamente
        </button>
      }
    </div>
  `,
})
export class ErrorStateComponent {
  @Input() mensagem = 'Ocorreu um erro inesperado. Por favor, tente novamente.';
  @Input() mostrarRetry = true;
  @Output() retry = new EventEmitter<void>();
}
