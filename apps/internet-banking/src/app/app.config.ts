import { ApplicationConfig, ErrorHandler, LOCALE_ID, isDevMode } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { provideRouter, withViewTransitions } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideNativeDateAdapter } from '@angular/material/core';

import { APP_ROUTES } from './app.routes';
import { idempotencyInterceptor } from './core/interceptors/idempotency.interceptor';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { GlobalErrorHandler } from './core/handlers/global-error.handler';
import { extratoReducer } from './store/extrato/extrato.reducer';
import { pagamentoReducer } from './store/pagamento/pagamento.reducer';
import { ExtratoEffects } from './store/extrato/extrato.effects';
import { PagamentoEffects } from './store/pagamento/pagamento.effects';

registerLocaleData(localePt, 'pt-BR');

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES, withViewTransitions()),

    provideAnimationsAsync(),

    provideHttpClient(
      withInterceptors([
        authInterceptor,
        idempotencyInterceptor,
        errorInterceptor,
      ])
    ),

    provideStore({
      extrato:   extratoReducer,
      pagamento: pagamentoReducer,
    }),
    provideEffects([ExtratoEffects, PagamentoEffects]),
    provideStoreDevtools({ maxAge: 25, logOnly: !isDevMode() }),

    provideNativeDateAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
    { provide: LOCALE_ID, useValue: 'pt-BR' },

    { provide: ErrorHandler, useClass: GlobalErrorHandler },
  ],
};
