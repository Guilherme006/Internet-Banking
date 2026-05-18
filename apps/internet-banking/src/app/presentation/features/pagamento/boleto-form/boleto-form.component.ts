import {
  Component, Input, Output, EventEmitter, OnInit, OnChanges,
  SimpleChanges, inject
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Boleto } from '../../../../domain/models/boleto.model';
import { PagamentoRequest, StatusPagamento } from '../../../../domain/models/pagamento.model';
import { validarBoleto } from '../../../../domain/validators/boleto.validator';

@Component({
  selector: 'app-boleto-form',
  standalone: true,
  imports: [
    ReactiveFormsModule, CurrencyPipe, DatePipe,
    MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatProgressSpinnerModule, MatDividerModule,
  ],
  template: `
    <form
      [formGroup]="form"
      (ngSubmit)="onConfirmar()"
      novalidate
      aria-label="Formulário de pagamento de boleto"
    >
            <mat-form-field appearance="outline" class="w-full mb-2 banking-field">
        <mat-label>Código de barras ou linha digitável</mat-label>
        <input
          matInput
          formControlName="codigoBarra"
          placeholder="Digite ou cole o código de barras..."
          inputmode="numeric"
          autocomplete="off"
          aria-describedby="codigoBarra-hint codigoBarra-error"
          (paste)="onPaste($event)"
        />
        <mat-icon matSuffix aria-hidden="true" class="text-slate-400">qr_code</mat-icon>
        @if (form.get('codigoBarra')?.valid && !validacaoAtiva) {
          <mat-icon matSuffix aria-hidden="true" class="text-emerald-500">check_circle</mat-icon>
        }
        <mat-hint id="codigoBarra-hint">
          44 dígitos para código de barras ou 47/48 dígitos para linha digitável
        </mat-hint>
        @if (form.get('codigoBarra')?.invalid && form.get('codigoBarra')?.touched) {
          <mat-error id="codigoBarra-error" role="alert">
            {{ getErroCampo('codigoBarra') }}
          </mat-error>
        }
      </mat-form-field>

            <button
        mat-flat-button
        color="primary"
        type="button"
        (click)="onBuscarBoleto()"
        [disabled]="form.get('codigoBarra')?.invalid || validacaoAtiva"
        class="h-12 w-full !rounded-lg mb-6"
        [attr.aria-busy]="validacaoAtiva"
      >
        @if (validacaoAtiva) {
          <mat-spinner diameter="20" class="inline-block mr-2" aria-hidden="true" />
          <span>Validando boleto...</span>
        } @else {
          <span class="inline-flex items-center justify-center gap-2">
            <mat-icon aria-hidden="true" class="!h-5 !w-5 !text-[20px]">search</mat-icon>
            <span>Consultar Boleto</span>
          </span>
        }
      </button>

            @if (boleto) {
        <div
          role="region"
          aria-label="Dados do boleto consultado"
          class="bg-slate-50 rounded-xl border border-slate-200 p-5 mb-5"
        >
          <div class="flex items-center gap-2 mb-4">
            <mat-icon aria-hidden="true" class="text-blue-600">receipt</mat-icon>
            <h3 class="text-sm font-semibold text-slate-700">Dados do Boleto</h3>
          </div>

          <dl class="grid grid-cols-2 gap-y-3 text-sm">
            <dt class="text-slate-500 font-medium">Beneficiário</dt>
            <dd class="text-slate-800 font-semibold">{{ boleto.beneficiario }}</dd>

            <dt class="text-slate-500 font-medium">Vencimento</dt>
            <dd [class]="isVencido() ? 'text-red-600 font-semibold' : 'text-slate-800 font-semibold'">
              {{ boleto.dataVencimento | date:'dd/MM/yyyy' }}
              @if (isVencido()) {
                <span class="ml-1 text-xs bg-red-100 text-red-700 px-2 py-0.5 rounded-full" aria-label="Boleto vencido">
                  Vencido
                </span>
              }
            </dd>

            <dt class="text-slate-500 font-medium">Valor</dt>
            <dd class="text-lg font-bold text-slate-900">
              {{ boleto.valor | currency:'BRL':'symbol':'1.2-2' }}
            </dd>
          </dl>

          <mat-divider class="my-4" />

                    <mat-form-field appearance="outline" class="w-full banking-field">
            <mat-label>Conta debitada</mat-label>
            <input
              matInput
              formControlName="numeroConta"
              placeholder="Ex: 12345-6"
              aria-label="Número da conta a ser debitada"
              aria-describedby="conta-error"
            />
            <mat-icon matSuffix aria-hidden="true" class="text-slate-400">account_balance</mat-icon>
            @if (form.get('numeroConta')?.invalid && form.get('numeroConta')?.touched) {
              <mat-error id="conta-error" role="alert">
                {{ getErroCampo('numeroConta') }}
              </mat-error>
            }
          </mat-form-field>

                    <button
            mat-flat-button
            color="primary"
            type="submit"
            [disabled]="form.invalid || processando"
            class="h-12 w-full !rounded-lg !mt-2 !bg-emerald-600 hover:!bg-emerald-700"
            [attr.aria-busy]="processando"
            aria-label="Confirmar pagamento do boleto"
          >
            @if (processando) {
              <mat-spinner diameter="20" class="inline-block mr-2" aria-hidden="true" />
              <span>Processando pagamento...</span>
            } @else {
              <span class="inline-flex items-center justify-center gap-2">
                <mat-icon aria-hidden="true" class="!h-5 !w-5 !text-[20px]">lock</mat-icon>
                <span>Confirmar Pagamento de {{ boleto.valor | currency:'BRL' }}</span>
              </span>
            }
          </button>

                    <p class="text-xs text-slate-400 text-center mt-3 flex items-center justify-center gap-1">
            <mat-icon aria-hidden="true" class="!text-xs">security</mat-icon>
            Conexão segura. Duplo clique protegido por chave de idempotência.
          </p>
        </div>
      }
    </form>
  `,
})
export class BoletoFormComponent implements OnChanges {
  @Input() boleto: Boleto | null = null;
  @Input() validacaoAtiva = false;
  @Input() processando = false;
  @Input() numeroConta = '';
  @Output() buscarBoleto = new EventEmitter<string>();
  @Output() confirmarPagamento = new EventEmitter<PagamentoRequest>();

  private fb = inject(FormBuilder);

  form = this.fb.group({
    codigoBarra: ['', [
      Validators.required,
      (control: AbstractControl): ValidationErrors | null => {
        const val: string = control.value ?? '';
        const digits = val.replace(/\D/g, '');
        if (digits.length === 0) return null;
        const result = validarBoleto(digits);
        return result.valido ? null : { boletoInvalido: result.mensagem };
      },
    ]],
    numeroConta: ['', [
      Validators.required,
      Validators.pattern(/^\d{5}-\d$/),
    ]],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['numeroConta']?.currentValue) {
      this.form.patchValue({ numeroConta: this.numeroConta });
    }
  }

  onBuscarBoleto(): void {
    const codigo = this.form.value.codigoBarra?.replace(/\D/g, '') ?? '';
    if (codigo) this.buscarBoleto.emit(codigo);
  }

  onConfirmar(): void {
    if (this.form.valid && this.boleto) {
      this.confirmarPagamento.emit({
        numeroConta: this.form.value.numeroConta!,
        codigoBarra: this.boleto.codigoBarra,
      });
    }
  }

  onPaste(event: ClipboardEvent): void {
    const texto = event.clipboardData?.getData('text') ?? '';
    const somenteDigitos = texto.replace(/\D/g, '');
    if (somenteDigitos.length >= 44) {
      event.preventDefault();
      this.form.patchValue({ codigoBarra: somenteDigitos });
    }
  }

  isVencido(): boolean {
    if (!this.boleto?.dataVencimento) return false;
    return new Date(this.boleto.dataVencimento) < new Date();
  }

  getErroCampo(campo: string): string {
    const control = this.form.get(campo);
    if (!control?.errors) return '';
    if (control.errors['required']) return 'Campo obrigatório.';
    if (control.errors['pattern']) return 'Formato inválido. Use: 12345-6';
    if (control.errors['boletoInvalido']) return control.errors['boletoInvalido'];
    return 'Valor inválido.';
  }
}
