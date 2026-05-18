import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, AuthUser, CadastroRequest, LoginRequest } from '../../domain/models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly usuarioKey = 'internet-banking.usuario';
  private readonly usuarioSignal = signal<AuthUser | null>(this.carregarUsuario());

  readonly autenticado = computed(() => !!this.usuarioSignal());
  readonly usuarioAtual = computed(() => this.usuarioSignal());

  obterUsuario(): AuthUser {
    const usuario = this.usuarioSignal();
    if (!usuario) {
      return {
        id: 0,
        nome: 'Usuário',
        email: '',
        cpf: '',
        agencia: '0000',
        conta: '00000-0',
      };
    }
    return usuario;
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request, { withCredentials: true })
      .pipe(tap(response => this.salvarSessao(response)));
  }

  cadastrar(request: CadastroRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/cadastro`, request, { withCredentials: true })
      .pipe(tap(response => this.salvarSessao(response)));
  }

  refresh(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, {}, { withCredentials: true })
      .pipe(tap(response => this.salvarSessao(response)));
  }

  logout(): void {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.limparSessaoLocal(),
      error: () => this.limparSessaoLocal(),
    });
  }

  limparSessaoLocal(): void {
    localStorage.removeItem(this.usuarioKey);
    this.usuarioSignal.set(null);
  }

  private salvarSessao(response: AuthResponse): void {
    localStorage.setItem(this.usuarioKey, JSON.stringify(response.usuario));
    this.usuarioSignal.set(response.usuario);
  }

  private carregarUsuario(): AuthUser | null {
    const raw = localStorage.getItem(this.usuarioKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      localStorage.removeItem(this.usuarioKey);
      return null;
    }
  }
}
