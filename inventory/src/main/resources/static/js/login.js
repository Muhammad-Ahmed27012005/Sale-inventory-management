document.addEventListener("DOMContentLoaded", () => {
  if (API.getToken()) {
    window.location.href = "/dashboard";
    return;
  }

  const form = document.getElementById("loginForm");
  const button = document.getElementById("loginButton");
  const message = document.getElementById("loginMessage");

  if (!form) return;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    message.textContent = "";

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const rememberMe = document.getElementById("rememberMe")?.checked || false;

    if (!username || !password) {
      message.textContent = "Username and password are required.";
      return;
    }

    try {
      UI.setLoading(button, true, "Logging in...");
      const authResponse = await API.request("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ username, password, rememberMe }),
      });
      API.setSession(authResponse, rememberMe);
      UI.toast("Login successful! Redirecting...");
      setTimeout(() => {
        window.location.href = "/dashboard";
      }, 500);
    } catch (error) {
      message.textContent = error.message || "Login failed. Please try again.";
      UI.toast(error.message, "error");
    } finally {
      UI.setLoading(button, false);
    }
  });
});
