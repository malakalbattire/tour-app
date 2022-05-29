import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { TripsComponent } from './trips/trips.component';
import { ProfileComponent } from './profile/profile.component';
import { RegisterComponent } from './register/register.component';
import { CreateTripsComponent } from './create-trips/create-trips.component';

const routes: Routes = [
  {path : '',component: LoginComponent},
  {path:'home' , component: HomeComponent},
  {path:'login' , component: LoginComponent},
  {path:'trips' , component: TripsComponent},
  {path:'profile' , component: ProfileComponent},
  {path:'register' , component: RegisterComponent},
  {path:'create-trips' , component: CreateTripsComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

