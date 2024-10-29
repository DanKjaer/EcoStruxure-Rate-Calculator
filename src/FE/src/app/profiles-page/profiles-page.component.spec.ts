import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilesPageComponent } from './profiles-page.component';

describe('ProfilesPageComponent', () => {
  let component: ProfilesPageComponent;
  let fixture: ComponentFixture<ProfilesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfilesPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfilesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
