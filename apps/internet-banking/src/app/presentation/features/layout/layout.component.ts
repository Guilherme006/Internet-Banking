import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { filter } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { CircuitBreakerService } from '../../../core/services/circuit-breaker.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatIconModule, MatTooltipModule, MatSnackBarModule, MatMenuModule],
  template: `
      <div class="min-h-screen bg-[#f4f7fb] text-slate-900 lg:flex">
      <a
        href="#main-content"
        class="sr-only focus:not-sr-only focus:fixed focus:left-4 focus:top-4
               focus:z-50 focus:rounded-md focus:bg-slate-950 focus:px-4 focus:py-2
               focus:text-sm focus:font-semibold focus:text-white"
      >
        Ir para o conteúdo principal
      </a>

      <aside
        class="flex border-b border-slate-200 bg-slate-950 text-white lg:fixed lg:inset-y-0 lg:left-0
               lg:w-72 lg:flex-col lg:border-b-0 lg:border-r lg:border-slate-800"
        role="navigation"
        aria-label="Menu principal"
      >
        <div class="flex min-w-0 flex-1 items-center gap-4 px-5 py-4 lg:flex-none lg:border-b lg:border-slate-800 lg:px-6 lg:py-6">
          <div class="grid h-11 w-11 shrink-0 place-items-center rounded-lg bg-teal-500 text-white shadow-sm">
            <mat-icon aria-hidden="true">account_balance</mat-icon>
          </div>
          <div class="min-w-0">
            <p class="truncate text-sm font-semibold uppercase tracking-[0.14em] text-teal-200">Banco Pagamento</p>
            <h1 class="truncate text-lg font-semibold leading-tight text-white">Internet Banking</h1>
          </div>
        </div>

        <div class="hidden px-6 py-5 lg:block">
          <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">Conta ativa</p>
          <div class="mt-3 rounded-lg border border-slate-800 bg-white/[0.04] p-4">
            <div class="flex items-center gap-3">
              <div class="grid h-10 w-10 place-items-center rounded-full bg-slate-800 text-sm font-semibold text-teal-200">
                {{ iniciaisUsuario }}
              </div>
              <div class="min-w-0">
                <p class="truncate text-sm font-semibold text-white">{{ usuario.nome }}</p>
                <p class="mt-0.5 text-xs text-slate-400">Ag {{ usuario.agencia }} | CC {{ usuario.conta }}</p>
              </div>
            </div>
          </div>
        </div>

        <nav class="flex items-center gap-2 px-3 py-3 lg:flex-1 lg:flex-col lg:items-stretch lg:px-4 lg:py-2" role="menubar">
          <a
            routerLink="/extrato"
            routerLinkActive="bg-white text-slate-950 shadow-sm"
            [routerLinkActiveOptions]="{ exact: false }"
            class="group inline-flex min-h-11 items-center gap-3 rounded-lg px-3 text-sm font-semibold
                   text-slate-300 transition hover:bg-white/10 hover:text-white focus-visible:outline-white
                   lg:w-full"
            role="menuitem"
            [attr.aria-current]="isActive('/extrato') ? 'page' : null"
          >
            <mat-icon aria-hidden="true" class="!text-[20px] text-teal-300 group-[.bg-white]:text-teal-700">receipt_long</mat-icon>
            <span class="hidden sm:inline">Extrato</span>
          </a>

          <a
            routerLink="/pagamento"
            routerLinkActive="bg-white text-slate-950 shadow-sm"
            [routerLinkActiveOptions]="{ exact: false }"
            class="group inline-flex min-h-11 items-center gap-3 rounded-lg px-3 text-sm font-semibold
                   text-slate-300 transition hover:bg-white/10 hover:text-white focus-visible:outline-white
                   lg:w-full"
            role="menuitem"
            [attr.aria-current]="isActive('/pagamento') ? 'page' : null"
          >
            <mat-icon aria-hidden="true" class="!text-[20px] text-teal-300 group-[.bg-white]:text-teal-700">qr_code_scanner</mat-icon>
            <span class="hidden sm:inline">Pagar boleto</span>
          </a>

        </nav>

        <div class="hidden border-t border-slate-800 px-6 py-5 lg:block">
          @if (circuitBreaker.circuitoAberto()) {
            <div
              role="alert"
              class="rounded-lg border border-amber-400/30 bg-amber-400/10 px-3 py-2 text-xs font-medium text-amber-200"
              aria-live="polite"
            >
              <div class="flex items-center gap-2">
                <mat-icon aria-hidden="true" class="!text-base">warning_amber</mat-icon>
                Serviços com atenção
              </div>
            </div>
          } @else {
            <div class="rounded-lg border border-emerald-400/20 bg-emerald-400/10 px-3 py-2 text-xs font-medium text-emerald-200">
              <div class="flex items-center gap-2">
                <span class="h-2 w-2 rounded-full bg-emerald-300"></span>
                Conexão segura
              </div>
            </div>
          }
        </div>
      </aside>

      <div class="min-h-screen w-full lg:flex-1 lg:pl-72">
        <header class="sticky top-0 z-30 w-full border-b border-slate-200 bg-white/95 backdrop-blur">
          <div class="flex min-h-16 items-center justify-between gap-4 px-5 lg:px-8">
            <div>
              <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Dashboard corporativo</p>
              <p class="mt-0.5 text-sm text-slate-600">Controle operacional de conta, extrato e pagamentos</p>
            </div>
            <div class="hidden items-center gap-3 sm:flex">
              <div class="text-right">
                <p class="text-sm font-semibold text-slate-900">{{ usuario.nome }}</p>
                <p class="text-xs text-slate-500">Ag {{ usuario.agencia }} | CC {{ usuario.conta }}</p>
              </div>
              <button
                type="button"
                class="grid h-9 w-9 place-items-center rounded-full bg-slate-100 text-sm font-semibold text-slate-700
                       transition hover:bg-slate-200 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2
                       focus-visible:outline-teal-600"
                matTooltip="Abrir menu do usuário"
                aria-label="Abrir menu do usuário"
                [matMenuTriggerFor]="userMenu"
              >
                {{ iniciaisUsuario }}
              </button>
              <mat-menu #userMenu="matMenu" xPosition="before">
                <button type="button" mat-menu-item routerLink="/minha-conta">
                  <mat-icon aria-hidden="true">manage_accounts</mat-icon>
                  <span>Minha conta</span>
                </button>
                <button type="button" mat-menu-item (click)="sair()">
                  <mat-icon aria-hidden="true">logout</mat-icon>
                  <span>Sair</span>
                </button>
              </mat-menu>
            </div>
          </div>
        </header>

        <main class="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 lg:px-8 lg:py-8" id="main-content" tabindex="-1">
          <router-outlet />
        </main>
      </div>
      </div>
  `,
})
export class LayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected circuitBreaker = inject(CircuitBreakerService);
  protected readonly currentUrl = signal(this.router.url);

  constructor() {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(event => this.currentUrl.set(event.urlAfterRedirects));
  }

  protected get usuario() {
    return this.authService.obterUsuario();
  }

  protected get iniciaisUsuario(): string {
    return this.usuario.nome
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(parte => parte.charAt(0).toUpperCase())
      .join('');
  }

  protected isActive(path: string): boolean {
    return this.currentUrl().startsWith(path);
  }

  protected sair(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
