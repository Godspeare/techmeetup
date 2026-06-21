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

If your database URL, username, or password is different, set these environment variables before running:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/techmeetup?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password
```

I did not hardcode your password in the source. Put it in your run configuration or shell environment.

For Docker or Render, set the same `SPRING_DATASOURCE_*` values in the service environment. Render also provides the `PORT` variable, and the app now binds to it automatically.

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

## Docker

The backend includes a root-level `Dockerfile` for Render and local container runs.

Build the image:

```bash
docker build -t techmeetup .
```

Run it locally:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL='jdbc:mysql://host.docker.internal:3306/techmeetup?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  techmeetup
```

If you are connecting to a managed MySQL provider, replace the datasource URL and credentials with that provider's values.
On Linux, use your database host directly or add `--add-host=host.docker.internal:host-gateway` to the `docker run` command.

## Frontend template

Files:

- [Frontend_Template/index.html](Frontend_Template/index.html)
- [Frontend_Template/style.css](Frontend_Template/style.css)
- [Frontend_Template/script.js](Frontend_Template/script.js)

`script.js` starts with:

```js
const BACKEND_URL = "http://localhost:8080";
```

Change that to your deployed Render URL when you go live.

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
