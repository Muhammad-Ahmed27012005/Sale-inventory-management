document.addEventListener("DOMContentLoaded", () => {
    if (API.getToken()) {
        window.location.href = "/dashboard";
        return;
    }

    const form = document.getElementById("loginForm");
    const button = document.getElementById("loginButton");
    const message = document.getElementById("loginMessage");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        message.textContent = "";

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;
        const rememberMe = document.getElementById("rememberMe").checked;

        if (!username || !password) {
            message.textContent = "Enter both username and password.";
            return;
        }

        try {
            UI.setLoading(button, true, "Signing in");
            const authResponse = await API.request("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ username, password, rememberMe })
            });
            API.setSession(authResponse, rememberMe);
            window.location.href = "/dashboard";
        } catch (error) {
            message.textContent = error.message;
            UI.toast(error.message, "error");
        } finally {
            UI.setLoading(button, false);
        }
    });
});