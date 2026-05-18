import {
  Component, Input, Output, EventEmitter, OnInit, inject
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FiltroExtrato, TipoTransacao, filtroExtratoInicial } from '../../../../domain/models/extrato.model';

@Component({
  selector: 'app-extrato-filtros',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatDatepickerModule, MatButtonModule, MatIconModule, MatTooltipModule,
  ],
  template: `
    <form
      [formGroup]="form"
      (ngSubmit)="aplicarFiltros()"
      class="bg-white rounded-xl border border-slate-200 p-5 mb-5 shadow-card"
      role="search"
      aria-label="Filtros do extrato"
    >
      <div class="grid grid-cols-1 gap-4 lg:grid-cols-[minmax(180px,1fr)_minmax(180px,1fr)_minmax(180px,1fr)_auto_auto] lg:items-start">

                <mat-form-field appearance="outline" class="w-full">
          <mat-label>Tipo</mat-label>
          <mat-select
            formControlName="tipo"
            aria-label="Filtrar por tipo de transação"
            (selectionChange)="onTipoChange($event)"
          >
            <mat-option value="TODOS">Todos</mat-option>
            <mat-option value="CREDITO">Créditos</mat-option>
            <mat-option value="DEBITO">Débitos</mat-option>
          </mat-select>
        </mat-form-field>

                <mat-form-field appearance="outline" class="w-full">
          <mat-label>De</mat-label>
          <input
            matInput
            [matDatepicker]="pickerInicio"
            formControlName="dataInicio"
            aria-label="Data de início do período"
          />
          <mat-datepicker-toggle matIconSuffix [for]="pickerInicio" />
          <mat-datepicker #pickerInicio />
        </mat-form-field>

                <mat-form-field appearance="outline" class="w-full">
          <mat-label>Até</mat-label>
          <input
            matInput
            [matDatepicker]="pickerFim"
            formControlName="dataFim"
            aria-label="Data de fim do período"
          />
          <mat-datepicker-toggle matIconSuffix [for]="pickerFim" />
          <mat-datepicker #pickerFim />
        </mat-form-field>

                <div class="flex gap-2 lg:pt-0">
          <button
            mat-flat-button
            color="primary"
            type="submit"
            class="h-14 min-w-36 !rounded-lg"
            aria-label="Aplicar filtros"
          >
            <span class="inline-flex items-center justify-center gap-2">
              <mat-icon aria-hidden="true" class="!h-5 !w-5 !text-[20px]">filter_list</mat-icon>
              <span>Filtrar</span>
            </span>
          </button>
        </div>

        <div class="flex lg:pt-0">
          <button
            mat-stroked-button
            type="button"
            (click)="limparFiltros()"
            class="h-14 !min-w-14 !rounded-lg !px-0"
            aria-label="Limpar filtros"
            matTooltip="Limpar filtros"
          >
            <mat-icon aria-hidden="true" class="!m-0 !h-5 !w-5 !text-[20px]">close</mat-icon>
          </button>
        </div>
      </div>
    </form>
  `,
})
export class ExtratoFiltrosComponent implements OnInit {
  @Input() filtros: FiltroExtrato | null = null;
  @Output() filtrosChange = new EventEmitter<FiltroExtrato>();

  private fb = inject(FormBuilder);

  form = this.fb.group({
    tipo: ['TODOS' as TipoTransacao | 'TODOS'],
    dataInicio: [null as Date | null],
    dataFim: [null as Date | null],
  });

  ngOnInit(): void {
    if (this.filtros) {
      this.form.patchValue({
        tipo: this.filtros.tipo,
        dataInicio: this.filtros.dataInicio,
        dataFim: this.filtros.dataFim,
      });
    }
  }

  aplicarFiltros(): void {
    const v = this.form.value;
    this.filtrosChange.emit({
      tipo: (v.tipo ?? 'TODOS') as TipoTransacao | 'TODOS',
      dataInicio: v.dataInicio ?? null,
      dataFim: v.dataFim ?? null,
      pagina: 0,
      tamanhoPagina: 10,
    });
  }

  onTipoChange(event: MatSelectChange): void {
    const v = this.form.value;
    this.filtrosChange.emit({
      tipo: event.value as TipoTransacao | 'TODOS',
      dataInicio: v.dataInicio ?? null,
      dataFim: v.dataFim ?? null,
      pagina: 0,
      tamanhoPagina: this.filtros?.tamanhoPagina ?? 10,
    });
  }

  limparFiltros(): void {
    this.form.reset({ tipo: 'TODOS', dataInicio: null, dataFim: null });
    this.filtrosChange.emit(filtroExtratoInicial);
  }
}
