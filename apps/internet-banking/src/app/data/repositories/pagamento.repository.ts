import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, delay, of, throwError } from 'rxjs';
import { PagamentoRequest, Comprovante } from '../../domain/models/pagamento.model';
import { Boleto } from '../../domain/models/boleto.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PagamentoRepository {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/pagamentos`;

  buscarDadosBoleto(codigoBarra: string): Observable<Boleto> {
    if (environment.usarMock) {
      return this.mockBuscarBoleto(codigoBarra);
    }
    return this.http.get<Boleto>(`${this.baseUrl}/boletos/${codigoBarra}`);
  }

  pagarBoleto(request: PagamentoRequest): Observable<Comprovante> {
    if (environment.usarMock) {
      return this.mockPagarBoleto(request);
    }
    return this.http.post<Comprovante>(`${this.baseUrl}/boletos`, request);
  }

  private mockBuscarBoleto(codigoBarra: string): Observable<Boleto> {
    const boleto = MOCK_BOLETOS.find(b => b.codigoBarra === codigoBarra);
    if (!boleto) {
      return throwError(() => ({
        status: 404,
        error: { detail: `Boleto não encontrado: ${codigoBarra}` },
      }));
    }
    return of(boleto).pipe(delay(600));
  }

  private mockPagarBoleto(request: PagamentoRequest): Observable<Comprovante> {
    const boleto = MOCK_BOLETOS.find(b => b.codigoBarra === request.codigoBarra);

    if (!boleto || boleto.status !== 'PENDENTE') {
      return throwError(() => ({
        status: 409,
        error: { detail: 'Boleto já se encontra com status PAGO.' },
      }));
    }

    const comprovante: Comprovante = {
      transacaoId: `TXN-${Date.now()}`,
      numeroConta: request.numeroConta,
      codigoBarra: request.codigoBarra,
      valorDebitado: boleto.valor,
      dataHora: new Date().toISOString(),
      status: 'APROVADO',
      reprocessado: false,
    };

    return of(comprovante).pipe(delay(1500));
  }
}

const MOCK_BOLETOS: Boleto[] = [
  {
    codigoBarra: '23793380896012340900901613951001291070001500000',
    beneficiario: 'Empresa ABC Ltda',
    pagador: 'João da Silva',
    valor: 250.00,
    dataVencimento: '2027-12-31',
    status: 'PENDENTE',
  },
  {
    codigoBarra: '34191090008001212904606855851177300000100000000',
    beneficiario: 'Empresa XYZ S.A.',
    pagador: 'Maria Souza',
    valor: 1500.00,
    dataVencimento: '2027-12-31',
    status: 'PENDENTE',
  },
  {
    codigoBarra: '75691122300001500001092310800004510935003795000',
    beneficiario: 'Condomínio Central',
    pagador: 'Carlos Ferreira',
    valor: 450.00,
    dataVencimento: '2027-12-31',
    status: 'PENDENTE',
  },
];
