import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { FiltroExtrato, Transacao, Paginacao } from '../../domain/models/extrato.model';

export const ExtratoActions = createActionGroup({
  source: 'Extrato',
  events: {
    'Carregar Extrato':       props<{ filtro: FiltroExtrato }>(),
    'Carregar Extrato Sucesso': props<{ transacoes: Transacao[]; paginacao: Paginacao }>(),
    'Carregar Extrato Falha':  props<{ erro: string }>(),
    'Atualizar Filtros':      props<{ filtro: FiltroExtrato }>(),
    'Resetar Estado':         emptyProps(),
  },
});
