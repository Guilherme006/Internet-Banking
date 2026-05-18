import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private snackBar = inject(MatSnackBar);

  sucesso(mensagem: string): void {
    this.snackBar.open(mensagem, 'Fechar', {
      duration: 5000,
      panelClass: ['snack-sucesso'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  erro(mensagem: string): void {
    this.snackBar.open(mensagem, 'Fechar', {
      duration: 8000,
      panelClass: ['snack-erro'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  aviso(mensagem: string): void {
    this.snackBar.open(mensagem, 'Fechar', {
      duration: 6000,
      panelClass: ['snack-aviso'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }
}
