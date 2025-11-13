const statusText = document.getElementById("statusText");
const etaText = document.getElementById("etaText");
const progressBar = document.getElementById("progressBar");

const steps = [
  { status: "Order Picked", eta: "20 minutes", progress: 30 },
  { status: "Out for Delivery", eta: "10 minutes", progress: 70 },
  { status: "Delivered", eta: "0 minutes", progress: 100 }
];

let current = 0;

function updateStatus() {
  if (current < steps.length) {
    const step = steps[current];
    statusText.textContent = step.status;
    etaText.textContent = step.eta;
    progressBar.style.width = `${step.progress}%`;
    current++;
  } else {
    clearInterval(interval);
  }
}

function cancelDelivery() {
  const confirmCancel = confirm("Are you sure you want to cancel the delivery?");
  if (confirmCancel) {
    statusText.textContent = "Cancelled by User";
    etaText.textContent = "-";
    progressBar.style.width = "0%";
    alert("Your delivery has been cancelled.");
    clearInterval(interval);
  }
}

const interval = setInterval(updateStatus, 5000);
updateStatus(); // load first step
