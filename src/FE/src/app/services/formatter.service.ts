import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FormatterService {

  constructor() { }

  /**
   * Formats a date to the format 'dd/MM/yyyy'
   * @param date
   */
  formatDate(date: Date): string {
    const formatter = new Intl.DateTimeFormat('en-GB', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });

    const dateToFormat = new Date(date);
    return formatter.format(dateToFormat);
  }

  /**
   * Formats a date to the format 'dd/MM/yyyy HH:mm'
   * @param date
   */
  formatDateTime(date: Date): string {
    const formatter = new Intl.DateTimeFormat('en-GB', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });

    const dateToFormat = new Date(date);
    return formatter.format(dateToFormat);
  }
}
