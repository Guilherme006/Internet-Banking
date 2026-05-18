import { MatDatepickerIntl } from '@angular/material/datepicker';
import { MatPaginatorIntl } from '@angular/material/paginator';

export function criarPaginatorIntl(): MatPaginatorIntl {
  const intl = new MatPaginatorIntl();

  intl.itemsPerPageLabel = 'Itens por página';
  intl.nextPageLabel = 'Próxima página';
  intl.previousPageLabel = 'Página anterior';
  intl.firstPageLabel = 'Primeira página';
  intl.lastPageLabel = 'Última página';
  intl.getRangeLabel = (page: number, pageSize: number, length: number): string => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }

    const startIndex = page * pageSize;
    const endIndex = Math.min(startIndex + pageSize, length);
    return `${startIndex + 1} - ${endIndex} de ${length}`;
  };

  return intl;
}

export function criarDatepickerIntl(): MatDatepickerIntl {
  const intl = new MatDatepickerIntl();

  intl.calendarLabel = 'Calendário';
  intl.openCalendarLabel = 'Abrir calendário';
  intl.prevMonthLabel = 'Mês anterior';
  intl.nextMonthLabel = 'Próximo mês';
  intl.prevYearLabel = 'Ano anterior';
  intl.nextYearLabel = 'Próximo ano';
  intl.prevMultiYearLabel = 'Período anterior';
  intl.nextMultiYearLabel = 'Próximo período';
  intl.switchToMonthViewLabel = 'Escolher data';
  intl.switchToMultiYearViewLabel = 'Escolher mês e ano';
  intl.closeCalendarLabel = 'Fechar calendário';

  return intl;
}
