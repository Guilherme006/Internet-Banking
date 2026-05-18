import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { IdempotencyService } from '../services/idempotency.service';

export const idempotencyInterceptor: HttpInterceptorFn = (req, next) => {
  const idempotencyService = inject(IdempotencyService);
  const isPagamentoBoleto = req.method === 'POST' && req.url.includes('/pagamentos/boletos');

  if (!isPagamentoBoleto) {
    return next(req);
  }

  const chave = idempotencyService.obterChaveAtual();
  const reqComChave = req.clone({
    setHeaders: { 'X-Idempotency-Key': chave },
  });

  return next(reqComChave);
};
