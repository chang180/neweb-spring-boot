document.addEventListener("DOMContentLoaded", () => {
    const homeButton = document.getElementById('homeButton');
    homeButton.onclick = () => goToHome();
});

function goToHome() {
    window.location.href = '/';
}
