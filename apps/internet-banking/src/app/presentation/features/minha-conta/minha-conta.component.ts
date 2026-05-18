import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';
import { CepService } from '../../../core/services/cep.service';
import { MinhaConta } from '../../../domain/models/auth.model';

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  template: `
    <section class="space-y-6">
      <div>
        <p class="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">Dados cadastrais</p>
        <h1 class="mt-2 text-2xl font-bold text-slate-900">Minha conta</h1>
        <p class="mt-2 text-sm text-slate-600">Consulte seus dados bancários e mantenha o cadastro atualizado.</p>
      </div>

      @if (conta(); as dados) {
        <div class="grid gap-4 lg:grid-cols-3">
          <div class="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
            <p class="text-sm font-semibold text-slate-500">Saldo atual</p>
            <p class="mt-3 text-3xl font-bold text-blue-700">{{ dados.saldo | currency:'BRL':'symbol':'1.2-2' }}</p>
          </div>
          <div class="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
            <p class="text-sm font-semibold text-slate-500">Conta</p>
            <p class="mt-3 text-2xl font-bold text-slate-900">Ag {{ dados.agencia }} | CC {{ dados.conta }}</p>
          </div>
          <div class="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
            <p class="text-sm font-semibold text-slate-500">CPF</p>
            <p class="mt-3 text-2xl font-bold text-slate-900">{{ formatarCpfTexto(dados.cpf) }}</p>
          </div>
        </div>
      }

      <form [formGroup]="form" (ngSubmit)="salvar()" class="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
        <div class="grid gap-4 lg:grid-cols-2">
          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Nome</mat-label>
            <input matInput formControlName="nome" autocomplete="name" />
            <mat-icon matSuffix aria-hidden="true">person</mat-icon>
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
        </div>

        <div class="mt-2 grid gap-4 lg:grid-cols-[0.7fr_1.3fr_0.7fr]">
          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>CEP</mat-label>
            <input matInput formControlName="cep" inputmode="numeric" maxlength="9" autocomplete="postal-code" (input)="formatarCep()" (blur)="buscarCep()" />
            <button mat-icon-button matSuffix type="button" (click)="buscarCep()" aria-label="Buscar CEP">
              <mat-icon aria-hidden="true">{{ buscandoCep() ? 'hourglass_top' : 'search' }}</mat-icon>
            </button>
            @if (form.get('cep')?.invalid && form.get('cep')?.touched) {
              <mat-error>CEP deve conter 8 dígitos.</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Logradouro</mat-label>
            <input matInput formControlName="logradouro" autocomplete="address-line1" />
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Número</mat-label>
            <input matInput formControlName="numero" autocomplete="address-line2" />
          </mat-form-field>
        </div>

        <div class="mt-2 grid gap-4 lg:grid-cols-3">
          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Complemento</mat-label>
            <input matInput formControlName="complemento" />
          </mat-form-field>

          <mat-form-field appearance="outline" class="banking-field w-full">
            <mat-label>Bairro</mat-label>
            <input matInput formControlName="bairro" autocomplete="address-level3" />
          </mat-form-field>

          <div class="grid gap-4 sm:grid-cols-[1fr_0.5fr]">
            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>Cidade</mat-label>
              <input matInput formControlName="cidade" autocomplete="address-level2" />
            </mat-form-field>

            <mat-form-field appearance="outline" class="banking-field w-full">
              <mat-label>UF</mat-label>
              <input matInput formControlName="uf" maxlength="2" autocomplete="address-level1" />
            </mat-form-field>
          </div>
        </div>

        <div class="mt-4 flex justify-end">
          <button mat-flat-button color="primary" type="submit" class="h-12 min-w-44 !rounded-lg" [disabled]="form.invalid || salvando()">
            <span class="inline-flex items-center gap-2">
              <mat-icon aria-hidden="true">save</mat-icon>
              {{ salvando() ? 'Salvando...' : 'Salvar alterações' }}
            </span>
          </button>
        </div>
      </form>
    </section>
  `,
})
export class MinhaContaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly cepService = inject(CepService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly conta = signal<MinhaConta | null>(null);
  protected readonly buscandoCep = signal(false);
  protected readonly salvando = signal(false);

  protected readonly form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(160)]],
    cep: ['', [Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],
    logradouro: ['', [Validators.required, Validators.maxLength(140)]],
    numero: ['', [Validators.required, Validators.maxLength(20)]],
    complemento: [''],
    bairro: ['', [Validators.required, Validators.maxLength(80)]],
    cidade: ['', [Validators.required, Validators.maxLength(80)]],
    uf: ['', [Validators.required, Validators.pattern(/^[A-Za-z]{2}$/)]],
  });

  constructor() {
    this.authService.minhaConta().subscribe({
      next: conta => {
        this.conta.set(conta);
        this.form.patchValue({
          nome: conta.nome,
          email: conta.email,
          cep: this.formatarCepTexto(conta.endereco.cep),
          logradouro: conta.endereco.logradouro,
          numero: conta.endereco.numero,
          complemento: conta.endereco.complemento ?? '',
          bairro: conta.endereco.bairro,
          cidade: conta.endereco.cidade,
          uf: conta.endereco.uf,
        });
      },
    });
  }

  protected buscarCep(): void {
    const cep = this.form.value.cep?.replace(/\D/g, '') ?? '';
    if (cep.length !== 8 || this.buscandoCep()) {
      return;
    }
    this.buscandoCep.set(true);
    this.cepService.buscar(cep).subscribe({
      next: endereco => {
        this.form.patchValue({
          cep: this.formatarCepTexto(cep),
          logradouro: endereco.logradouro,
          bairro: endereco.bairro,
          cidade: endereco.localidade,
          uf: endereco.uf,
          complemento: this.form.value.complemento || endereco.complemento,
        });
        this.buscandoCep.set(false);
      },
      error: () => {
        this.buscandoCep.set(false);
        this.snackBar.open('Não foi possível consultar o CEP.', 'Fechar', { duration: 4000 });
      },
    });
  }

  protected salvar(): void {
    if (this.form.invalid || this.salvando()) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.salvando.set(true);
    this.authService.atualizarMinhaConta({
      nome: value.nome!,
      email: value.email!,
      endereco: {
        cep: value.cep!.replace(/\D/g, ''),
        logradouro: value.logradouro!,
        numero: value.numero!,
        complemento: value.complemento ?? '',
        bairro: value.bairro!,
        cidade: value.cidade!,
        uf: value.uf!.toUpperCase(),
      },
    }).subscribe({
      next: conta => {
        this.conta.set(conta);
        this.salvando.set(false);
        this.snackBar.open('Dados atualizados com sucesso.', 'Fechar', { duration: 3500 });
      },
      error: () => {
        this.salvando.set(false);
        this.snackBar.open('Não foi possível salvar os dados.', 'Fechar', { duration: 4500 });
      },
    });
  }

  protected formatarCep(): void {
    const digits = this.form.value.cep?.replace(/\D/g, '').slice(0, 8) ?? '';
    this.form.get('cep')?.setValue(this.formatarCepTexto(digits), { emitEvent: false });
  }

  protected formatarCpfTexto(cpf: string): string {
    return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
  }

  private formatarCepTexto(cep: string): string {
    return cep.replace(/\D/g, '').replace(/^(\d{5})(\d{1,3})$/, '$1-$2');
  }
}
