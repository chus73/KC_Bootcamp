import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CustosAppPaths } from './common/constants/app.constants';

const routes: Routes = [
  { path: CustosAppPaths.ROOT,  loadChildren:  './main/main.module#MainModule' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
