import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ],
  template: `
    <section class="grid min-h-screen bg-slate-950 text-white lg:grid-cols-[0.95fr_1.05fr]">
      <div class="flex flex-col justify-between border-r border-white/10 px-8 py-8">
        <div class="flex items-center gap-4">
          <div class="grid h-12 w-12 place-items-center rounded-lg bg-teal-500">
            <mat-icon aria-hidden="true">account_balance</mat-icon>
          </div>
          <div>
            <p class="text-sm font-semibold uppercase tracking-[0.18em] text-teal-200">Banco Pagamento</p>
            <h1 class="text-xl font-semibold">Internet Banking</h1>
          </div>
        </div>

        <div class="max-w-xl py-16">
          <p class="text-sm font-semibold uppercase tracking-[0.18em] text-teal-200">Acesso seguro</p>
          <h2 class="mt-4 text-4xl font-bold leading-tight">Controle sua conta corporativa com autenticação protegida.</h2>
          <p class="mt-5 text-base leading-7 text-slate-300">
            Consulte extratos, valide boletos e confirme pagamentos usando sessão individual vinculada à sua conta.
          </p>
        </div>

        <p class="text-sm text-slate-400">JWT, BCrypt e autorização por conta ativa.</p>
      </div>

      <div class="flex items-center justify-center bg-slate-100 px-6 py-10 text-slate-900">
        <form
          [formGroup]="form"
          (ngSubmit)="entrar()"
          class="w-full max-w-md rounded-lg border border-slate-200 bg-white p-7 shadow-card"
        >
          <div class="mb-6">
            <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Login</p>
            <h2 class="mt-2 text-2xl font-bold">Entrar na conta</h2>
          </div>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>E-mail</mat-label>
            <input matInput formControlName="email" type="email" autocomplete="email" />
            <mat-icon matSuffix aria-hidden="true">mail</mat-icon>
            @if (form.get('email')?.invalid && form.get('email')?.touched) {
              <mat-error>Informe um e-mail válido.</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Senha</mat-label>
            <input matInput formControlName="senha" [type]="mostrarSenha ? 'text' : 'password'" autocomplete="current-password" />
            <button mat-icon-button matSuffix type="button" (click)="mostrarSenha = !mostrarSenha" aria-label="Alternar visibilidade da senha">
              <mat-icon aria-hidden="true">{{ mostrarSenha ? 'visibility_off' : 'visibility' }}</mat-icon>
            </button>
            @if (form.get('senha')?.invalid && form.get('senha')?.touched) {
              <mat-error>Informe sua senha.</mat-error>
            }
          </mat-form-field>

          <button mat-flat-button color="primary" class="mt-2 h-12 w-full !rounded-lg" [disabled]="form.invalid || carregando">
            <span class="inline-flex items-center gap-2">
              <mat-icon aria-hidden="true">login</mat-icon>
              {{ carregando ? 'Entrando...' : 'Entrar' }}
            </span>
          </button>

          <div class="mt-5 rounded-lg bg-slate-50 px-4 py-3 text-sm text-slate-600">
            Demo: <strong>joao&#64;bancopagamento.com</strong> / <strong>Senha&#64;123</strong>
          </div>

          <p class="mt-6 text-center text-sm text-slate-600">
            Ainda não tem acesso?
            <a routerLink="/cadastro" class="font-semibold text-teal-700 hover:text-teal-900">Criar conta</a>
          </p>
        </form>
      </div>
    </section>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected mostrarSenha = false;
  protected carregando = false;

  protected form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]],
  });

  protected entrar(): void {
    if (this.form.invalid || this.carregando) {
      this.form.markAllAsTouched();
      return;
    }

    this.carregando = true;
    this.authService.login({
      email: this.form.value.email!,
      senha: this.form.value.senha!,
    }).subscribe({
      next: () => {
        this.router.navigateByUrl('/extrato')
          .finally(() => this.carregando = false);
      },
      error: () => this.carregando = false,
    });
  }
}
