import { ApplicationConfig, ErrorHandler, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { provideRouter, withViewTransitions } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore } from '@ngrx/store';

import { APP_ROUTES } from './app.routes';
import { idempotencyInterceptor } from './core/interceptors/idempotency.interceptor';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { GlobalErrorHandler } from './core/handlers/global-error.handler';

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

    provideStore(),

    { provide: LOCALE_ID, useValue: 'pt-BR' },

    { provide: ErrorHandler, useClass: GlobalErrorHandler },
  ],
};
