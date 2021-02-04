# Repair Service App (Vilnius Coding School Final Project)
  > Home appliances and electronics service webapp

## Technologies
* Java 14
* Spring Framework 2.3.4
  * Spring Security - WebSecurityConfigurerAdapter, UserDetails, UserDetailsService
  * Mail - JavaMailSender, SimpleMailMessage
  * Validation
* Thymeleaf 3.0.11
    * plugins: 
      * thymeleaf-layout-dialect 2.5.1
      * thymeleaf-extras-springsecurity5 3.0.4
* MySQL 5.8
* Docker 20.10.0
  * Docker compose (Compose file format 3.7)
  * [MailHog](https://github.com/mailhog/MailHog) 1.0.1
* Bootstrap 5.0.0-beta1 -> [#12][i12]

## Try it out with [Docker](https://www.docker.com/)
You need Docker installed.

```
# Run app:
docker-compose up -d
```

Open in browser:
- External app http://localhost:8081/
  - username: k1@demo.lt
  - password: a
- Internal app http://localhost:8082/
  - username: admin@demo.lt
  - password: a
- MailHog http://localhost:8025/

```
# Stop app:
docker-compose down --volumes
```

[comment]: <> (## Processes)

[comment]: <> (WIP [#2][i2])

## Modules
* components (Entities, Repositories, Services, Exceptions, EmailTemplates)
* web-externalApp (Controllers, Services, Dto, Auth, DataLoader)
* web-internalApp (Controllers, Services, Dto, Auth, PdfGenerator, Dataloader)


### Module: web-externalApp
#### Pages
* [ ] Welcome page
* [x] Registration page -> [#1][i1]
  * [x] Validation (fields not empty, email unique, passwords match) -> [#4][i4]
  * [x] Account verification (email + token) -> [#8][i8]
  * [x] Account activation (token: notfound, expired, valid) -> [#8][i8]
* [x] Login -> [#1][i1]
* [x] Logoff -> [#1][i1]
* [x] Remember password -> [#9][i9]
* [x] Reset password -> [#9][i9]
* [ ] Profile / Settings (change Personal info, password)
* [x] Repairs
  * [x] List -> [#5][i5]
  * [x] New (with status: pending) -> [#5][i5]
    * [x] Validation (fields not empty) -> [#5][i5]
  * [x] Delete (only mine repairs and with status: pending) -> [#5][i5]
  * [x] View -> [#5][i5]
  * [x] Confirm repair (if need confirm order)


### Module: web-internalApp
#### Roles
  > -> [#17][i17]
* ADMIN
* MANAGER
* TECHNICAN

#### Pages
* [x] Login -> [#15][i15]
* [x] Logoff -> [#15][i15]
* [x] Remember password + email-> [#15][i15]
* [x] Reset password -> [#15][i15]
* [x] Employees management -> [#17][i17]
  * [x] View, Create + email, Update, Manage roles -> [#17][i17]
* [x] Customers management -> [#18][i18]
  * [x] View, Create, Update -> [#18][i18]


[i1]: https://github.com/ivanevla/VCS_final-project/pull/1
[i2]: https://github.com/ivanevla/VCS_final-project/issues/2
[i4]: https://github.com/ivanevla/VCS_final-project/pull/4
[i5]: https://github.com/ivanevla/VCS_final-project/pull/5
[i8]: https://github.com/ivanevla/VCS_final-project/pull/8
[i9]: https://github.com/ivanevla/VCS_final-project/pull/9
[i12]: https://github.com/ivanevla/VCS_final-project/pull/12
[i15]: https://github.com/ivanevla/VCS_final-project/pull/15
[i17]: https://github.com/ivanevla/VCS_final-project/pull/17
[i18]: https://github.com/ivanevla/VCS_final-project/pull/18
