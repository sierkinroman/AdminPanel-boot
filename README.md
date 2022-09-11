# AdminPanel-boot

The application has two roles: **Admin** and **User**.

**Admin** has access to **adminPanel**, where he can manage all users and create new ones.
**User** can create and manage his own account.

On starting application, the database is initialized with default accounts.

### Some features
- Last enabled Admin can't delete himself
- Last enabled Admin can't change self role to non Admin
- Last enabled Admin can't disable self
- After an authorized user deletes himself, he is automatically logged out
- If Admin changes self role to non Admin, he doesn't have access to adminPanel

## Requirements
- JDK 8

## Run application
- Open terminal in project's root directory
- `./mvnw spring-boot:run`

or run the `main` method from `AdminPanelBootApplication` class.

## View application in browser
To view application, visit http://localhost:8080/

To log in as *Admin*:<br/>
username and password: **admin**

To log in as *default User*:<br/>
username and password: **user1** or (**user10-user49**, user1-user9 is blocked)

Or to log in your *own account* type your credentials.

## Stopping application
To stop application press `Ctrl + C` in terminal.

## Technology stack
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- H2 database
- JUnit
- Lombok
- Logback
- Thymeleaf
- HTML, CSS, JavaScript