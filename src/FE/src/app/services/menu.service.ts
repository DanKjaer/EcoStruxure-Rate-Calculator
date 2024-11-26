import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private isMenuOpenSource = new BehaviorSubject<boolean>(true);
  isMenuOpen$ = this.isMenuOpenSource.asObservable();

  toggleMenu() {
    this.isMenuOpenSource.next(!this.isMenuOpenSource.value);
  }
}
