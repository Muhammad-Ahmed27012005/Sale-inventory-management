document.addEventListener("DOMContentLoaded", () => {
  if (API.getToken()) {
    window.location.href = "/dashboard";
    return;
  }

  const form = document.getElementById("signupForm");
  const button = document.getElementById("signupButton");
  const message = document.getElementById("signupMessage");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    message.textContent = "";

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (!username || !password || !confirmPassword) {
      message.textContent = "All fields are required.";
      return;
    }
    if (password.length < 6) {
      message.textContent = "Password must be at least 6 characters.";
      return;
    }
    if (password !== confirmPassword) {
      message.textContent = "Passwords do not match.";
      return;
    }

    try {
      UI.setLoading(button, true, "Creating");
      const authResponse = await API.request("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ username, password, confirmPassword }),
      });
      API.setSession(authResponse, false);
      UI.toast("Account created");
      window.location.href = "/dashboard";
    } catch (error) {
      message.textContent = error.message;
      UI.toast(error.message, "error");
    } finally {
      UI.setLoading(button, false);
    }
  });
});
