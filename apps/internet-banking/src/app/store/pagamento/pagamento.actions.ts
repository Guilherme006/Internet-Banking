import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { Boleto } from '../../domain/models/boleto.model';
import { Comprovante, PagamentoRequest } from '../../domain/models/pagamento.model';

export const PagamentoActions = createActionGroup({
  source: 'Pagamento',
  events: {
    'Buscar Boleto':          props<{ codigoBarra: string }>(),
    'Buscar Boleto Sucesso':  props<{ boleto: Boleto }>(),
    'Buscar Boleto Falha':    props<{ erro: string }>(),
    'Confirmar Pagamento':    props<{ request: PagamentoRequest }>(),
    'Pagamento Sucesso':      props<{ comprovante: Comprovante }>(),
    'Pagamento Falha':        props<{ erro: string }>(),
    'Resetar Pagamento':      emptyProps(),
  },
});
