// ===== Signup =====
document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("signupUsername").value;
  const password = document.getElementById("signupPassword").value;

  try {
    const response = await fetch("http://localhost:8081/users/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const message = await response.text();
    alert(message);
  } catch (error) {
    console.error("Signup error:", error);
    alert("Something went wrong while signing up.");
  }
});

// ===== Login =====
document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("loginUsername").value;
  const password = document.getElementById("loginPassword").value;

  try {
    const response = await fetch("http://localhost:8081/users/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const message = await response.text();
    alert(message);

    if (message.includes("Login successful")) {
      // example redirect after successful login
      window.location.href = "products.html";
    }

  } catch (error) {
    console.error("Login error:", error);
    alert("Something went wrong while logging in.");
  }
});
