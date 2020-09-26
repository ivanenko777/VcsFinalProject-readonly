# Repair Service App (Vilnius Codind School Final Project)
  > Buitinės technikos ir elektronikos serviso webapp'sas

## Technologies
* Java 14
* Spring Framework
  * Spring Security - WebSecurityConfigurerAdapter, UserDetails, UserDetailsService
* SQL (MySQL)
* Bootstrap

## Processes
WIP [#2][i2]

## Modules
* web-externalApp
* web-internalApp


### Module: web-externalApp
#### Pages
* [ ] Welcome page
* [x] Registration page -> [#1][i1]
  * [x] Validation (fields not empty, email unique, passwords match) -> [#4][i4]
* [x] Login -> [#1][i1]
* [x] Logoff -> [#1][i1]
* [ ] Reset password
* [ ] Profile / Settings (change Personal info, password)
* [ ] Repairs
  * [x] List -> [#5][i5]
  * [x] New (with status: pending) -> [#5][i5]
  * [x] Delete (only mine repairs and with status: pending) -> [#5][i5]
  * [ ] View
  * [ ] Confirm repair (if need confirm order)


### Module: web-internalApp
#### Roles
* ADMIN
* MANAGER
* TECHNICAN

#### Pages

[i1]: https://github.com/ivanevla/VCS_final-project/pull/1
[i2]: https://github.com/ivanevla/VCS_final-project/issues/2
[i4]: https://github.com/ivanevla/VCS_final-project/pull/4
[i5]: https://github.com/ivanevla/VCS_final-project/pull/5
