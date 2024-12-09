import {Component, inject} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {SidemenuComponent} from "./components/sidemenu/sidemenu.component";
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {HttpClient} from '@angular/common/http';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n', '.json');
}

@Component({
    selector: 'app-root',
    imports: [
        RouterOutlet,
        SidemenuComponent,
        TranslateModule
    ],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent {
  private translate = inject(TranslateService);

  constructor() {
    this.translate.addLangs(['da', 'en']);
    this.translate.setDefaultLang('en');
    this.translate.use('en');
  }

  title = 'EcoStruxure';
}
