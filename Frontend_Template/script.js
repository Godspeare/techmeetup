const BACKEND_URL = "http://localhost:8080";

const eventsBody = document.getElementById("eventsBody");
const statusEl = document.getElementById("status");
const emptyStateEl = document.getElementById("emptyState");
const refreshButton = document.getElementById("refreshButton");

refreshButton.addEventListener("click", () => loadEvents());
window.addEventListener("DOMContentLoaded", () => loadEvents());

function setStatus(message, state = "") {
    statusEl.textContent = message;
    statusEl.className = state ? `status ${state}` : "status";
}

async function loadEvents(options = {}) {
    const { silent = false } = options;

    if (!silent) {
        setStatus("Loading events...");
    }

    refreshButton.disabled = true;

    try {
        const response = await fetch(`${BACKEND_URL}/api/events`);

        if (!response.ok) {
            throw new Error(`Unable to load events (${response.status})`);
        }

        const events = await response.json();
        renderEvents(events);

        if (!silent) {
            setStatus(events.length ? "Events loaded." : "No events are available right now.");
        }
    } catch (error) {
        renderEvents([]);
        setStatus(error.message || "Unable to load events.", "is-error");
    } finally {
        refreshButton.disabled = false;
    }
}

function renderEvents(events) {
    eventsBody.innerHTML = "";
    emptyStateEl.hidden = events.length > 0;

    events.forEach((event) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>
                <div class="event-title">${escapeHtml(event.title)}</div>
            </td>
            <td>${escapeHtml(event.date)}</td>
            <td>
                <div class="event-location">${escapeHtml(event.location)}</div>
            </td>
            <td>
                <span class="ticket-count ${event.availableTickets <= 0 ? "is-low" : ""}">
                    ${event.availableTickets}
                </span>
            </td>
            <td>
                <button
                    type="button"
                    class="primary-button"
                    ${event.availableTickets <= 0 ? "disabled" : ""}
                    data-event-id="${event.id}"
                >
                    ${event.availableTickets <= 0 ? "Sold out" : "Register"}
                </button>
            </td>
        `;

        const button = row.querySelector("button");
        if (button) {
            button.addEventListener("click", () => registerForEvent(event.id, button));
        }

        eventsBody.appendChild(row);
    });
}

async function registerForEvent(eventId, button) {
    const originalText = button.textContent;
    button.disabled = true;
    button.textContent = "Working...";

    try {
        const response = await fetch(`${BACKEND_URL}/api/events/${eventId}/register`, {
            method: "POST"
        });

        const payload = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(payload.message || "Registration failed.");
        }

        setStatus(payload.message || "Registration successful.", "is-success");
        await loadEvents({ silent: true });
    } catch (error) {
        setStatus(error.message || "Registration failed.", "is-error");
    } finally {
        if (document.contains(button)) {
            button.disabled = false;
            button.textContent = originalText;
        }
    }
}

function escapeHtml(value) {
    const element = document.createElement("div");
    element.textContent = value ?? "";
    return element.innerHTML;
}
