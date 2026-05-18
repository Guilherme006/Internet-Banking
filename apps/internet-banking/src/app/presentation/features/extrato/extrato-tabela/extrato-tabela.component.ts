import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Paginacao, Transacao } from '../../../../domain/models/extrato.model';

@Component({
  selector: 'app-extrato-tabela',
  standalone: true,
  imports: [DatePipe, CurrencyPipe, MatTableModule, MatPaginatorModule, MatIconModule, MatChipsModule],
  template: `
    <div class="bg-white rounded-xl border border-slate-200 shadow-card overflow-hidden">

            <table
        mat-table
        [dataSource]="transacoes"
        [trackBy]="trackByTransacaoId"
        class="w-full"
        aria-label="Tabela de movimentações do extrato"
      >

                <ng-container matColumnDef="tipo">
          <th
            mat-header-cell
            *matHeaderCellDef
            scope="col"
            class="!text-xs !font-semibold !text-slate-500 !uppercase !tracking-wider !py-3"
          >
            Tipo
          </th>
          <td mat-cell *matCellDef="let transacao" class="!py-3.5">
            <div class="flex items-center gap-2">
              <div
                [class]="transacao.tipo === 'CREDITO'
                  ? 'inline-flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-emerald-50'
                  : 'inline-flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-red-50'"
                [attr.aria-label]="transacao.tipo === 'CREDITO' ? 'Crédito' : 'Débito'"
              >
                <mat-icon
                  [class]="transacao.tipo === 'CREDITO'
                    ? 'text-emerald-600 !flex !h-[18px] !w-[18px] !items-center !justify-center !text-[18px] !leading-[18px]'
                    : 'text-red-500 !flex !h-[18px] !w-[18px] !items-center !justify-center !text-[18px] !leading-[18px]'"
                  aria-hidden="true"
                >
                  {{ transacao.tipo === 'CREDITO' ? 'arrow_downward' : 'arrow_upward' }}
                </mat-icon>
              </div>
            </div>
          </td>
        </ng-container>

                <ng-container matColumnDef="descricao">
          <th mat-header-cell *matHeaderCellDef scope="col"
              class="!text-xs !font-semibold !text-slate-500 !uppercase !tracking-wider !py-3">
            Descrição
          </th>
          <td mat-cell *matCellDef="let transacao" class="!py-3.5">
            <div>
              <p class="text-sm font-medium text-slate-800">{{ transacao.descricao }}</p>
              @if (transacao.categoria) {
                <span class="text-xs text-slate-400">{{ transacao.categoria }}</span>
              }
            </div>
          </td>
        </ng-container>

                <ng-container matColumnDef="data">
          <th mat-header-cell *matHeaderCellDef scope="col"
              class="!text-xs !font-semibold !text-slate-500 !uppercase !tracking-wider !py-3">
            Data
          </th>
          <td mat-cell *matCellDef="let transacao" class="!py-3.5">
            <time
              [dateTime]="transacao.dataHora"
              class="text-sm text-slate-500"
            >
              {{ transacao.dataHora | date:'dd/MM/yyyy HH:mm' }}
            </time>
          </td>
        </ng-container>

                <ng-container matColumnDef="valor">
          <th mat-header-cell *matHeaderCellDef scope="col"
              class="!text-xs !font-semibold !text-slate-500 !uppercase !tracking-wider !py-3 !text-right">
            Valor
          </th>
          <td mat-cell *matCellDef="let transacao" class="!py-3.5 !text-right">
            <span
              [class]="transacao.tipo === 'CREDITO'
                ? 'text-sm font-semibold text-emerald-600'
                : 'text-sm font-semibold text-red-500'"
              [attr.aria-label]="(transacao.tipo === 'CREDITO' ? 'Crédito de ' : 'Débito de ') + (transacao.valor | currency:'BRL')"
            >
              {{ transacao.tipo === 'CREDITO' ? '+' : '-' }}
              {{ transacao.valor | currency:'BRL':'symbol':'1.2-2' }}
            </span>
          </td>
        </ng-container>

                <ng-container matColumnDef="saldo">
          <th mat-header-cell *matHeaderCellDef scope="col"
              class="!text-xs !font-semibold !text-slate-500 !uppercase !tracking-wider !py-3 !text-right">
            Saldo da conta
          </th>
          <td mat-cell *matCellDef="let transacao" class="!py-3.5 !text-right">
            <span
              [class]="transacao.saldoApos >= 0 ? 'text-sm text-slate-700' : 'text-sm text-red-500'"
              [attr.aria-label]="'Saldo da conta após a movimentação: ' + (transacao.saldoApos | currency:'BRL')"
            >
              {{ transacao.saldoApos | currency:'BRL':'symbol':'1.2-2' }}
            </span>
          </td>
        </ng-container>

                <tr mat-header-row *matHeaderRowDef="colunas" class="!bg-slate-50 border-b border-slate-200"></tr>
        <tr
          mat-row
          *matRowDef="let row; columns: colunas;"
          class="hover:bg-slate-50 transition-colors border-b border-slate-100 last:border-0"
          [attr.aria-label]="'Transação: ' + row.descricao"
        ></tr>

                <tr *matNoDataRow>
          <td [attr.colspan]="colunas.length" class="py-16 text-center">
            <p class="text-slate-400 text-sm">Nenhuma transação encontrada para o período selecionado.</p>
          </td>
        </tr>
      </table>

            @if (paginacao && paginacao.totalElementos > 0) {
        <mat-paginator
          [length]="paginacao.totalElementos"
          [pageSize]="paginacao.tamanhoPagina"
          [pageIndex]="paginacao.paginaAtual"
          [pageSizeOptions]="[10, 25, 50]"
          (page)="onPaginaChange($event)"
          showFirstLastButtons
          aria-label="Navegação de páginas do extrato"
          class="border-t border-slate-200"
        />
      }
    </div>
  `,
})
export class ExtratoTabelaComponent {
  @Input({ required: true }) transacoes: Transacao[] = [];
  @Input() paginacao: Paginacao | null = null;
  @Output() paginaChange = new EventEmitter<{ pagina: number; tamanho: number }>();

  protected readonly colunas = ['tipo', 'descricao', 'data', 'valor', 'saldo'];

    protected trackByTransacaoId(_index: number, transacao: Transacao): string {
    return transacao.id;
  }

  protected onPaginaChange(event: PageEvent): void {
    this.paginaChange.emit({ pagina: event.pageIndex, tamanho: event.pageSize });
  }
}
