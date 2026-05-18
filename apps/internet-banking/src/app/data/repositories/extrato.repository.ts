import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, delay, of } from 'rxjs';
import { FiltroExtrato, Transacao, Paginacao } from '../../domain/models/extrato.model';
import { environment } from '../../../environments/environment';

export interface ExtratoPageResponse {
  content: Transacao[];
  totalElements: number;
  number: number;
  size: number;
  totalPages: number;
}

@Injectable({ providedIn: 'root' })
export class ExtratoRepository {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/extrato`;

  buscar(filtro: FiltroExtrato): Observable<ExtratoPageResponse> {
    if (environment.usarMock) {
      return this.mockExtrato(filtro);
    }

    let params = new HttpParams()
      .set('pagina', filtro.pagina.toString())
      .set('tamanho', filtro.tamanhoPagina.toString());

    if (filtro.tipo !== 'TODOS') params = params.set('tipo', filtro.tipo);
    if (filtro.dataInicio) params = params.set('dataInicio', filtro.dataInicio.toISOString());
    if (filtro.dataFim) params = params.set('dataFim', filtro.dataFim.toISOString());

    return this.http.get<ExtratoPageResponse>(this.baseUrl, { params });
  }

  private mockExtrato(filtro: FiltroExtrato): Observable<ExtratoPageResponse> {
    const todasTransacoes: Transacao[] = MOCK_TRANSACOES.filter(t => {
      if (filtro.tipo !== 'TODOS' && t.tipo !== filtro.tipo) return false;
      return true;
    });

    const inicio = filtro.pagina * filtro.tamanhoPagina;
    const fim = inicio + filtro.tamanhoPagina;
    const content = todasTransacoes.slice(inicio, fim);

    const response: ExtratoPageResponse = {
      content,
      totalElements: todasTransacoes.length,
      number: filtro.pagina,
      size: filtro.tamanhoPagina,
      totalPages: Math.ceil(todasTransacoes.length / filtro.tamanhoPagina),
    };

    return of(response).pipe(delay(800));
  }
}

const MOCK_TRANSACOES: Transacao[] = [
  { id: 't-001', tipo: 'DEBITO',  descricao: 'Pagamento Boleto – Empresa ABC',     valor: 250.00,   dataHora: '2026-05-15T10:30:00', saldoApos: 4750.00, categoria: 'Boleto' },
  { id: 't-002', tipo: 'CREDITO', descricao: 'TED Recebida – Maria Souza',          valor: 1200.00,  dataHora: '2026-05-14T16:45:00', saldoApos: 5000.00, categoria: 'Transferência' },
  { id: 't-003', tipo: 'DEBITO',  descricao: 'Débito Automático – Energia Elétrica', valor: 187.50, dataHora: '2026-05-13T08:00:00', saldoApos: 3800.00, categoria: 'Débito Automático' },
  { id: 't-004', tipo: 'DEBITO',  descricao: 'PIX – Carlos Ferreira',               valor: 500.00,   dataHora: '2026-05-12T14:22:00', saldoApos: 3987.50, categoria: 'PIX' },
  { id: 't-005', tipo: 'CREDITO', descricao: 'Salário',                             valor: 8500.00,  dataHora: '2026-05-10T07:00:00', saldoApos: 4487.50, categoria: 'Crédito' },
  { id: 't-006', tipo: 'DEBITO',  descricao: 'Pagamento Boleto – Condomínio',       valor: 850.00,   dataHora: '2026-05-09T11:15:00', saldoApos: -4012.50, categoria: 'Boleto' },
  { id: 't-007', tipo: 'DEBITO',  descricao: 'Tarifa Manutenção de Conta',          valor: 29.90,    dataHora: '2026-05-07T00:00:00', saldoApos: -3162.50, categoria: 'Tarifa' },
  { id: 't-008', tipo: 'CREDITO', descricao: 'Rendimento Poupança',                 valor: 45.30,    dataHora: '2026-05-05T00:01:00', saldoApos: -3132.60, categoria: 'Rendimento' },
  { id: 't-009', tipo: 'DEBITO',  descricao: 'Compra Débito – Supermercado XYZ',   valor: 312.75,   dataHora: '2026-05-03T19:40:00', saldoApos: -3177.90, categoria: 'Compra' },
  { id: 't-010', tipo: 'DEBITO',  descricao: 'PIX – Restaurante Bom Sabor',        valor: 89.00,    dataHora: '2026-05-02T13:05:00', saldoApos: -2865.15, categoria: 'PIX' },
  { id: 't-011', tipo: 'CREDITO', descricao: 'Transferência Recebida',              valor: 300.00,   dataHora: '2026-05-01T09:30:00', saldoApos: -2776.15, categoria: 'Transferência' },
  { id: 't-012', tipo: 'DEBITO',  descricao: 'Débito Automático – Internet',        valor: 129.90,   dataHora: '2026-04-30T06:00:00', saldoApos: -3076.15, categoria: 'Débito Automático' },
];
