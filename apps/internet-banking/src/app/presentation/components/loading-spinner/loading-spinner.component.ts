import { Component, Input } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [MatProgressBarModule],
  template: `
    <div
      role="status"
      [attr.aria-label]="mensagem"
      class="flex flex-col items-center gap-4 py-12"
    >
      <div class="relative w-16 h-16">
        <div class="w-16 h-16 border-4 border-blue-100 rounded-full"></div>
        <div class="absolute inset-0 w-16 h-16 border-4 border-blue-600 rounded-full border-t-transparent animate-spin"></div>
      </div>
      <p class="text-slate-500 text-sm animate-pulse">{{ mensagem }}</p>
    </div>
  `,
})
export class LoadingSpinnerComponent {
  @Input() mensagem = 'Carregando...';
}
