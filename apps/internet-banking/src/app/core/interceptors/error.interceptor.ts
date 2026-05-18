import { HttpInterceptorFn, HttpStatusCode } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { CircuitBreakerService } from '../services/circuit-breaker.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notification = inject(NotificationService);
  const circuitBreaker = inject(CircuitBreakerService);

  return next(req).pipe(
    catchError(error => {
      const status: number = error.status;
      const detail: string = error.error?.detail ?? error.error?.message ?? 'Erro desconhecido';

      switch (status) {
        case HttpStatusCode.BadRequest:
          notification.erro(`Dados inválidos: ${detail}`);
          break;

        case HttpStatusCode.Unauthorized:
          notification.aviso('Sessão expirada. Faça login novamente.');
          break;

        case HttpStatusCode.NotFound:
          notification.erro(`Não encontrado: ${detail}`);
          break;

        case HttpStatusCode.Conflict:
          notification.aviso(`Operação duplicada: ${detail}`);
          break;

        case HttpStatusCode.UnprocessableEntity:
          notification.erro(`Operação não permitida: ${detail}`);
          break;

        case HttpStatusCode.ServiceUnavailable:
          circuitBreaker.registrarFalha();
          notification.erro('Serviço temporariamente indisponível. Tente novamente em instantes.');
          break;

        default:
          if (status >= 500) {
            circuitBreaker.registrarFalha();
            notification.erro('Erro interno do servidor. Nossa equipe foi notificada.');
          } else if (status === 0) {
            circuitBreaker.registrarFalha();
            notification.erro('Sem conexão com o servidor. Verifique sua internet.');
          }
      }

      return throwError(() => error);
    })
  );
};
