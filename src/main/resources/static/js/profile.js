document.addEventListener('DOMContentLoaded', function() {
    const friendLogin = document.body.dataset.friendlogin;
    const messageButton = document.getElementById('message-button');
    const avatarPNG = document.getElementById('avatar-image');
    if (messageButton) {
        messageButton.addEventListener('click', function(event) {
            event.preventDefault();

            // Проверяем, есть ли логин друга
            if (!friendLogin || friendLogin.trim() === '') {
                alert(friendLogin);
                return;
            }

            // Отправляем запрос на создание чата
            fetch('/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'name=' + encodeURIComponent(friendLogin)
            })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                } else if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('Ошибка при создании чата');
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Произошла ошибка при создании чата');
            });
        });
    }
     const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {
            document.body.setAttribute('data-theme', savedTheme);
            if(savedTheme.includes('light') && !avatarPNG.getAttribute('src').includes('user'))
                avatarPNG.setAttribute('src', '/images/profile-black.png')
        }
});