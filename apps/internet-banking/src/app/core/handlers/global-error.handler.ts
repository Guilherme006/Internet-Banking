import { ErrorHandler, Injectable, inject } from '@angular/core';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  private notification = inject(NotificationService);

  handleError(error: unknown): void {
    const mensagem = this.extrairMensagem(error);
    console.error('[GlobalErrorHandler] Erro não tratado:', error);
    this.notification.erro(mensagem);
  }

  private extrairMensagem(error: unknown): string {
    if (error instanceof Error) {
      if (error.message.includes('NG02100')) {
        return 'Não foi possível formatar uma informação da tela. Recarregue a página.';
      }
      return 'Ocorreu um erro inesperado. Recarregue a página.';
    }
    if (typeof error === 'string') return error;
    return 'Erro inesperado. Por favor, recarregue a página.';
  }
}
