document.addEventListener('DOMContentLoaded', function() {
    const showRegisterLink = document.getElementById('show-register');
    const showLoginLink = document.getElementById('show-login');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    showRegisterLink.addEventListener('click', () => showForm('register'));
    showLoginLink.addEventListener('click', () => showForm('login'));

    function showForm(formType) {
        loginForm.classList.remove('active');
        registerForm.classList.remove('active');

        if (formType === 'login') {
            loginForm.classList.add('active');
        } else {
            registerForm.classList.add('active');
        }
    }

    const successMessage = document.querySelector('.alert-success');
    if (successMessage && successMessage.textContent.includes('Регистрация')) {
        showForm('login');
    }
});