import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ViaCepResponse } from '../../domain/models/auth.model';

@Injectable({ providedIn: 'root' })
export class CepService {
  private readonly http = inject(HttpClient);

  buscar(cep: string): Observable<ViaCepResponse> {
    const digits = cep.replace(/\D/g, '');
    return this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${digits}/json/`)
      .pipe(
        map(response => {
          if (response.erro) {
            throw new Error('CEP não encontrado.');
          }
          return response;
        })
      );
  }
}
