export interface Boleto {
  readonly codigoBarra: string;
  readonly beneficiario: string;
  readonly pagador: string;
  readonly valor: number;
  readonly dataVencimento: string;
  readonly status: 'PENDENTE' | 'PAGO' | 'VENCIDO' | 'CANCELADO';
}

export interface ValidacaoBoleto {
  readonly valido: boolean;
  readonly mensagem: string;
  readonly codigoFormatado?: string;
}
