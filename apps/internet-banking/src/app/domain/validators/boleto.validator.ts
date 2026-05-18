import { ValidacaoBoleto } from '../models/boleto.model';

export function validarBoleto(input: string): ValidacaoBoleto {
  const digits = input.replace(/\D/g, '');

  if ([44, 47, 48].includes(digits.length)) {
    return {
      valido: true,
      mensagem: 'Boleto pronto para consulta.',
      codigoFormatado: digits,
    };
  }

  return {
    valido: false,
    mensagem: `Formato inválido. São necessários 44, 47 ou 48 dígitos (recebido: ${digits.length}).`,
  };
}

export function formatarCodigoBarra(codigo: string): string {
  const d = codigo.replace(/\D/g, '');
  if (d.length === 44) {
    return `${d.substring(0, 5)} ${d.substring(5, 15)} ${d.substring(15, 25)} ${d.substring(25, 35)} ${d.substring(35, 44)}`;
  }
  return codigo;
}
