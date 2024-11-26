import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Currency} from '../models';
import {BehaviorSubject, firstValueFrom, tap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private currencySource = new BehaviorSubject<string>('EUR');
  currentCurrency$ = this.currencySource.asObservable();
  private currencies: { [key: string]: Currency } = {};
  private apiUrl = 'http://localhost:8080/api/currency';

  constructor(private http: HttpClient) {
    this.loadCurrencies();
  }

  //#region API calls
  /**
   * Gets all currencies from backend.
   */
  getCurrencies(): Promise<Currency[]> {
    return firstValueFrom(this.http.get<Currency[]>(`${this.apiUrl}`));
  }

  //#endregion

  /**
   * Loads currency data from backend.
   * @private
   */
  private loadCurrencies() {
    this.http.get<Currency[]>(`${this.apiUrl}`).pipe(
      tap(currencies => {
        currencies.forEach(currency => {
          this.currencies[currency.currencyCode] = currency;
        });
      })
    ).subscribe();
  }

  /**
   * Sets the currency in cache.
   * @param currency
   */
  setCurrency(currency: string) {
    this.currencySource.next(currency);
    localStorage.setItem('selectedCurrency', currency);
  }

  /**
   * Gets the symbol of the given currency.
   *
   * If there isn't a symbol 'n/a' is given instead.
   * @param currency
   */
  getSymbol(currency: string) {
    return this.currencies[currency].symbol || 'n/a';
  }

  /**
   * Converts one currency to another using EUR as an intermediary.
   *
   * So you can calculate from any currency to any other by first converting to EUR and then to the target currency.
   * @param amount
   * @param from
   * @param to
   */
  convert(amount: number, from: string, to: string): number {
    if(!this.currencies[from] || !this.currencies[to]){
      return amount;
    }
    const amountInEUR = amount / this.currencies[from].eurConversionRate;
    return amountInEUR * this.currencies[to].eurConversionRate;
  }

  /**
   * Updates the currencies in the database.
   * @param currencies
   */
  importCurrency(currencies: Currency[]) {
    return firstValueFrom(this.http.put<Currency>(`${this.apiUrl}/import`, currencies));
  }
}
