# TechMeetup

TechMeetup is a small decoupled event registration system:

- Spring Boot backend exposes a REST API.
- MySQL stores events and ticket counts.
- The frontend is a plain HTML/CSS/JS app that talks to the backend with `fetch()`.

## Backend

Files:

- [TechmeetupApplication.java](src/main/java/com/techmeetup/TechmeetupApplication.java)
- [Event.java](src/main/java/com/techmeetup/event/Event.java)
- [EventRepository.java](src/main/java/com/techmeetup/event/EventRepository.java)
- [EventController.java](src/main/java/com/techmeetup/event/EventController.java)
- [application.properties](src/main/resources/application.properties)

### API

- `GET /api/events` returns all events.
- `POST /api/events/{id}/register` reduces `availableTickets` by 1 if tickets are still available.

### Database

The app expects MySQL to be running locally by default:

- host: `localhost`
- port: `3306`
- database: `techmeetup`
- username: `root`
- password: set `SPRING_DATASOURCE_PASSWORD`

If your database name or username is different, set these environment variables before running:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/techmeetup?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password
```

I did not hardcode your password in the source. Put it in your run configuration or shell environment.

### Run locally

Using Gradle:

```bash
./gradlew bootRun
```

Using Maven:

```bash
./mvnw spring-boot:run
```

The server listens on `http://localhost:8080` by default.

## Frontend template

Files:

- [Frontend_Template/index.html](Frontend_Template/index.html)
- [Frontend_Template/style.css](Frontend_Template/style.css)
- [Frontend_Template/script.js](Frontend_Template/script.js)

`script.js` starts with:

```js
const BACKEND_URL = "http://localhost:8080";
```

Change that to your deployed Railway URL when you go live.

### Local frontend test

Serve the `Frontend_Template` folder with any static server, then open it in a browser.
For example:

```bash
python3 -m http.server 5500 -d Frontend_Template
```

Then browse to `http://localhost:5500`.

## How it works

1. The backend starts and connects to MySQL.
2. If the `events` table is empty, the app inserts sample events.
3. The frontend loads `GET /api/events` and renders the list.
4. Clicking `Register` sends `POST /api/events/{id}/register`.
5. The backend checks tickets, subtracts 1, saves the row, and returns JSON.
6. The frontend refreshes the list so the new ticket count appears immediately.
