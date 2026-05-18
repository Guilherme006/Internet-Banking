import { ValidacaoBoleto } from '../models/boleto.model';

export function validarBoleto(input: string): ValidacaoBoleto {
  const digits = input.replace(/\D/g, '');

  if (digits.length === 47) {
    return validarLinhaDigitavel(digits);
  }

  if (digits.length === 44) {
    return validarCodigoBarra(digits);
  }

  return {
    valido: false,
    mensagem: `Formato inválido. São necessários 44 ou 47 dígitos (recebido: ${digits.length}).`,
  };
}

function validarLinhaDigitavel(d: string): ValidacaoBoleto {
  const campo1SemDv = d.substring(0, 9);
  const dvCampo1 = parseInt(d[9]);
  if (calcModulo10(campo1SemDv) !== dvCampo1) {
    return { valido: false, mensagem: 'Dígito verificador inválido no campo 1 do boleto.' };
  }

  const campo2SemDv = d.substring(10, 20);
  const dvCampo2 = parseInt(d[20]);
  if (calcModulo10(campo2SemDv) !== dvCampo2) {
    return { valido: false, mensagem: 'Dígito verificador inválido no campo 2 do boleto.' };
  }

  const campo3SemDv = d.substring(21, 31);
  const dvCampo3 = parseInt(d[31]);
  if (calcModulo10(campo3SemDv) !== dvCampo3) {
    return { valido: false, mensagem: 'Dígito verificador inválido no campo 3 do boleto.' };
  }

  const codigoBarra = linhaDigitavelParaCodigoBarra(d);
  return {
    valido: true,
    mensagem: 'Boleto válido.',
    codigoFormatado: codigoBarra,
  };
}

function validarCodigoBarra(d: string): ValidacaoBoleto {
  const dvGeral = parseInt(d[4]);
  const codigoSemDv = d.substring(0, 4) + d.substring(5);
  const dvCalculado = calcModulo11(codigoSemDv);

  if (dvCalculado !== dvGeral) {
    return { valido: false, mensagem: 'Dígito verificador geral inválido no código de barras.' };
  }

  return {
    valido: true,
    mensagem: 'Código de barras válido.',
    codigoFormatado: d,
  };
}

function calcModulo10(campo: string): number {
  let soma = 0;
  let multiplicador = 2;

  for (let i = campo.length - 1; i >= 0; i--) {
    let resultado = parseInt(campo[i]) * multiplicador;
    if (resultado > 9) resultado -= 9;
    soma += resultado;
    multiplicador = multiplicador === 2 ? 1 : 2;
  }

  const resto = soma % 10;
  return resto === 0 ? 0 : 10 - resto;
}

function calcModulo11(campo: string): number {
  let soma = 0;
  let multiplicador = 2;

  for (let i = campo.length - 1; i >= 0; i--) {
    soma += parseInt(campo[i]) * multiplicador;
    multiplicador = multiplicador === 9 ? 2 : multiplicador + 1;
  }

  const resto = soma % 11;
  return resto === 0 || resto === 1 ? 1 : 11 - resto;
}

function linhaDigitavelParaCodigoBarra(d: string): string {
  return (
    d.substring(0, 4)  +
    d.substring(32, 33) +
    d.substring(33, 47) +
    d.substring(4, 9)  +
    d.substring(10, 20) +
    d.substring(21, 31)
  );
}

export function formatarCodigoBarra(codigo: string): string {
  const d = codigo.replace(/\D/g, '');
  if (d.length === 44) {
    return `${d.substring(0, 5)} ${d.substring(5, 15)} ${d.substring(15, 25)} ${d.substring(25, 35)} ${d.substring(35, 44)}`;
  }
  return codigo;
}
