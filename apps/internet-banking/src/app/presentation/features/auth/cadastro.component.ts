import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../../core/services/auth.service';
import { CepService } from '../../../core/services/cep.service';

@Component({
  selector: 'app-cadastro',
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
    <section class="min-h-screen bg-slate-100 px-4 py-8 text-slate-900">
      <form
        [formGroup]="form"
        (ngSubmit)="cadastrar()"
        class="mx-auto w-full max-w-5xl rounded-lg border border-slate-200 bg-white p-7 shadow-card"
      >
        <div class="mb-7 flex flex-col gap-4 border-b border-slate-200 pb-6 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Cadastro completo</p>
            <h1 class="mt-2 text-2xl font-bold">Abrir conta corporativa</h1>
          </div>
          <a routerLink="/login" class="text-sm font-semibold text-teal-700 hover:text-teal-900">Já tenho acesso</a>
        </div>

        <div class="grid gap-4 lg:grid-cols-2">
          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Nome completo</mat-label>
            <input matInput formControlName="nome" autocomplete="name" />
            <mat-icon matSuffix aria-hidden="true">badge</mat-icon>
            @if (form.get('nome')?.invalid && form.get('nome')?.touched) {
              <mat-error>Informe pelo menos 3 caracteres.</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>E-mail</mat-label>
            <input matInput formControlName="email" type="email" autocomplete="email" />
            <mat-icon matSuffix aria-hidden="true">mail</mat-icon>
            @if (form.get('email')?.invalid && form.get('email')?.touched) {
              <mat-error>Informe um e-mail válido.</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>CPF</mat-label>
            <input matInput formControlName="cpf" inputmode="numeric" maxlength="14" autocomplete="off" (input)="formatarCpf()" />
            <mat-icon matSuffix aria-hidden="true">fingerprint</mat-icon>
            @if (form.get('cpf')?.invalid && form.get('cpf')?.touched) {
              <mat-error>{{ erroCampo('cpf') }}</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Senha</mat-label>
            <input matInput formControlName="senha" type="password" autocomplete="new-password" />
            <mat-icon matSuffix aria-hidden="true">lock</mat-icon>
            @if (form.get('senha')?.invalid && form.get('senha')?.touched) {
              <mat-error>{{ erroCampo('senha') }}</mat-error>
            }
          </mat-form-field>
        </div>

        <div class="mt-7">
          <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Endereço</p>
          <div class="mt-4 grid gap-4 lg:grid-cols-[0.8fr_1.2fr]">
            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>CEP</mat-label>
              <input matInput formControlName="cep" inputmode="numeric" maxlength="9" autocomplete="postal-code" (input)="formatarCep()" (blur)="buscarCep()" />
              <button mat-icon-button matSuffix type="button" (click)="buscarCep()" aria-label="Buscar CEP">
                <mat-icon aria-hidden="true">{{ buscandoCep ? 'hourglass_top' : 'search' }}</mat-icon>
              </button>
              @if (form.get('cep')?.invalid && form.get('cep')?.touched) {
                <mat-error>{{ erroCampo('cep') }}</mat-error>
              }
            </mat-form-field>

            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>Logradouro</mat-label>
              <input matInput formControlName="logradouro" autocomplete="address-line1" />
            </mat-form-field>
          </div>

          <div class="grid gap-4 lg:grid-cols-[0.55fr_1fr_1fr_0.45fr]">
            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>Número</mat-label>
              <input matInput formControlName="numero" autocomplete="address-line2" />
            </mat-form-field>

            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>Complemento</mat-label>
              <input matInput formControlName="complemento" />
            </mat-form-field>

            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>Bairro</mat-label>
              <input matInput formControlName="bairro" />
            </mat-form-field>

            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>UF</mat-label>
              <input matInput formControlName="uf" maxlength="2" />
            </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Cidade</mat-label>
            <input matInput formControlName="cidade" autocomplete="address-level2" />
          </mat-form-field>
        </div>

        <button mat-flat-button color="primary" class="mt-4 h-12 w-full !rounded-lg sm:w-auto sm:px-8" [disabled]="form.invalid || carregando">
          <span class="inline-flex items-center gap-2">
            <mat-icon aria-hidden="true">person_add</mat-icon>
            {{ carregando ? 'Criando conta...' : 'Criar conta' }}
          </span>
        </button>
      </form>
    </section>
  `,
})
export class CadastroComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly cepService = inject(CepService);
  private readonly router = inject(Router);

  protected carregando = false;
  protected buscandoCep = false;

  protected form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(160)]],
    cpf: ['', [Validators.required, cpfValidator]],
    senha: ['', [Validators.required, Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,72}$/)]],
    cep: ['', [Validators.required, cepValidator]],
    logradouro: ['', [Validators.required, Validators.maxLength(140)]],
    numero: ['', [Validators.required, Validators.maxLength(20)]],
    complemento: [''],
    bairro: ['', [Validators.required, Validators.maxLength(80)]],
    cidade: ['', [Validators.required, Validators.maxLength(80)]],
    uf: ['', [Validators.required, Validators.pattern(/^[A-Z]{2}$/)]],
  });

  protected buscarCep(): void {
    const cep = this.form.value.cep?.replace(/\D/g, '') ?? '';
    if (cep.length !== 8 || this.buscandoCep) {
      return;
    }

    this.buscandoCep = true;
    this.cepService.buscar(cep).subscribe({
      next: endereco => {
        this.form.patchValue({
          cep,
          logradouro: endereco.logradouro,
          complemento: endereco.complemento,
          bairro: endereco.bairro,
          cidade: endereco.localidade,
          uf: endereco.uf,
        });
        this.buscandoCep = false;
      },
      error: () => this.buscandoCep = false,
    });
  }

  protected cadastrar(): void {
    if (this.form.invalid || this.carregando) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.carregando = true;
    this.authService.cadastrar({
      nome: value.nome!,
      email: value.email!,
      cpf: value.cpf!.replace(/\D/g, ''),
      senha: value.senha!,
      endereco: {
        cep: value.cep!.replace(/\D/g, ''),
        logradouro: value.logradouro!,
        numero: value.numero!,
        complemento: value.complemento ?? '',
        bairro: value.bairro!,
        cidade: value.cidade!,
        uf: value.uf!,
      },
    }).subscribe({
      next: () => {
        this.router.navigateByUrl('/extrato')
          .finally(() => this.carregando = false);
      },
      error: () => this.carregando = false,
    });
  }

  protected formatarCpf(): void {
    const digits = this.form.value.cpf?.replace(/\D/g, '').slice(0, 11) ?? '';
    const formatted = digits
      .replace(/^(\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
    this.form.get('cpf')?.setValue(formatted, { emitEvent: false });
  }

  protected formatarCep(): void {
    const digits = this.form.value.cep?.replace(/\D/g, '').slice(0, 8) ?? '';
    const formatted = digits.replace(/^(\d{5})(\d)/, '$1-$2');
    this.form.get('cep')?.setValue(formatted, { emitEvent: false });
  }

  protected erroCampo(campo: 'cpf' | 'senha' | 'cep'): string {
    const control = this.form.get(campo);
    if (!control?.errors) return '';
    if (control.errors['required']) return 'Campo obrigatório.';
    if (campo === 'cpf') return 'CPF inválido. Verifique os 11 dígitos.';
    if (campo === 'cep') return 'CEP deve conter 8 dígitos.';
    return 'Use 8 a 72 caracteres com maiúscula, minúscula, número e símbolo.';
  }
}

function cpfValidator(control: AbstractControl): ValidationErrors | null {
  const cpf = String(control.value ?? '').replace(/\D/g, '');
  if (cpf.length !== 11 || new Set(cpf).size === 1) {
    return { cpf: true };
  }

  const digit = (size: number): number => {
    let sum = 0;
    for (let index = 0; index < size; index++) {
      sum += Number(cpf[index]) * (size + 1 - index);
    }
    const rest = sum % 11;
    return rest < 2 ? 0 : 11 - rest;
  };

  return digit(9) === Number(cpf[9]) && digit(10) === Number(cpf[10])
    ? null
    : { cpf: true };
}

function cepValidator(control: AbstractControl): ValidationErrors | null {
  const cep = String(control.value ?? '').replace(/\D/g, '');
  return cep.length === 8 ? null : { cep: true };
}
