const API = (() => {
    const AUTH_KEY = "sim_auth_token";
    const USER_KEY = "sim_user";

    function getToken() {
        return localStorage.getItem(AUTH_KEY) || sessionStorage.getItem(AUTH_KEY);
    }

    function getUser() {
        const raw = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY);
        return raw ? JSON.parse(raw) : null;
    }

    function setSession(authResponse, remember) {
        clearSession();
        const store = remember ? localStorage : sessionStorage;
        store.setItem(AUTH_KEY, authResponse.token);
        store.setItem(USER_KEY, JSON.stringify({
            username: authResponse.username,
            role: authResponse.role
        }));
    }

    function clearSession() {
        localStorage.removeItem(AUTH_KEY);
        localStorage.removeItem(USER_KEY);
        sessionStorage.removeItem(AUTH_KEY);
        sessionStorage.removeItem(USER_KEY);
    }

    function logout(redirect = true) {
        clearSession();
        if (redirect) {
            window.location.href = "/";
        }
    }

    function requireAuth() {
        if (!getToken()) {
            window.location.href = "/";
        }
    }

    async function request(url, options = {}) {
        const headers = new Headers(options.headers || {});
        if (options.body && !headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }
        const token = getToken();
        if (token) {
            headers.set("Authorization", token);
        }

        const response = await fetch(url, { ...options, headers });
        const contentType = response.headers.get("content-type") || "";
        let payload = null;

        if (response.status !== 204) {
            payload = contentType.includes("application/json") ? await response.json() : await response.text();
        }

        if (response.status === 401) {
            logout(false);
            throw new Error("Session expired. Please log in again.");
        }

        if (!response.ok) {
            throw new Error(payload?.message || payload || `Request failed with status ${response.status}`);
        }

        return payload;
    }

    function money(value) {
        return new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD"
        }).format(Number(value || 0));
    }

    function dateTime(value) {
        if (!value) {
            return "-";
        }
        return new Intl.DateTimeFormat("en-US", {
            dateStyle: "medium",
            timeStyle: "short"
        }).format(new Date(value));
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    document.addEventListener("DOMContentLoaded", () => {
        const user = getUser();
        document.querySelectorAll("[data-username]").forEach((node) => {
            node.textContent = user ? `${user.username} (${user.role})` : "";
        });
        document.querySelectorAll("[data-logout]").forEach((button) => {
            button.addEventListener("click", () => logout(true));
        });
        document.querySelectorAll(".nav-link").forEach((link) => {
            if (link.getAttribute("href") === window.location.pathname) {
                link.classList.add("active");
            }
        });
        if (window.lucide) {
            window.lucide.createIcons();
        }
    });

    return {
        request,
        setSession,
        getUser,
        getToken,
        requireAuth,
        logout,
        money,
        dateTime,
        escapeHtml
    };
})();