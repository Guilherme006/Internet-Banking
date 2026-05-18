export type StatusPagamento = 'OCIOSO' | 'VALIDANDO' | 'PROCESSANDO' | 'APROVADO' | 'ERRO';

export interface PagamentoRequest {
  readonly numeroConta: string;
  readonly codigoBarra: string;
}

export interface Comprovante {
  readonly transacaoId: string;
  readonly numeroConta: string;
  readonly codigoBarra: string;
  readonly valorDebitado: number;
  readonly dataHora: string;
  readonly status: string;
  readonly reprocessado: boolean;
}
