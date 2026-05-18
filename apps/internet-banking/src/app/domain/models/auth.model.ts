export interface EnderecoCadastro {
  readonly cep: string;
  readonly logradouro: string;
  readonly numero: string;
  readonly complemento?: string;
  readonly bairro: string;
  readonly cidade: string;
  readonly uf: string;
}

export interface CadastroRequest {
  readonly nome: string;
  readonly email: string;
  readonly cpf: string;
  readonly senha: string;
  readonly endereco: EnderecoCadastro;
}

export interface LoginRequest {
  readonly email: string;
  readonly senha: string;
}

export interface AuthUser {
  readonly id: number;
  readonly nome: string;
  readonly email: string;
  readonly cpf: string;
  readonly agencia: string;
  readonly conta: string;
}

export interface AuthResponse {
  readonly token: string;
  readonly tipo: string;
  readonly expiraEmSegundos: number;
  readonly usuario: AuthUser;
}

export interface ViaCepResponse {
  readonly cep: string;
  readonly logradouro: string;
  readonly complemento: string;
  readonly bairro: string;
  readonly localidade: string;
  readonly uf: string;
  readonly erro?: boolean;
}
