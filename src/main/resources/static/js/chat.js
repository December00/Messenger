document.addEventListener('DOMContentLoaded', function() {
        const addButton = document.getElementById('add-button');
        const addForm = document.getElementById('add-friend-form');
        const submitButton = document.getElementById('submit-add-friend');
        const usernameInput = document.getElementById('friend-username');
        //объекты для отправки сообщений
        const sendButton = document.getElementById('send-button');
        const messageInput = document.getElementById('message-input');

        // Показать/скрыть форму при клике на "+"
        addButton.addEventListener('click', function() {
            if (addForm.style.display === 'block') {
                addForm.style.display = 'none';
            } else {
                addForm.style.display = 'block';
            }
        });

        // Обработка отправки формы
        submitButton.addEventListener('click', function() {
            const username = usernameInput.value.trim();

            if (!username) {
                alert('Пожалуйста, введите имя пользователя или email');
                return;
            }

            // Отправка POST-запроса на сервер
            fetch('/chat', {
               method: 'POST',
               headers: {
                   'Content-Type': 'application/x-www-form-urlencoded',
               },
               body: `username=${encodeURIComponent(username)}`
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка сети');
                }
                return response.json();
            })
            .then(data => {
                alert('Запрос на добавление отправлен: ' + username);
                usernameInput.value = '';
                addForm.style.display = 'none';

                // Здесь можно обновить список друзей, если нужно
                // updateFriendsList(data);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Произошла ошибка при отправке запроса');
            });
        });

        // Скрыть форму при клике вне её
        document.addEventListener('click', function(event) {
            if (!addForm.contains(event.target) && event.target !== addButton) {
                addForm.style.display = 'none';
            }
        });

        //обработка отправки сообщения
        sendButton.addEventListener('click', function() {
            const chatId = this.getAttribute('data-chatid');
            const message = messageInput.value.trim();

            if (!chatId || !message) {
                alert("Вы не выбрали чат или не ввели сообщение");
                return;
            }

            fetch(`/chat/${chatId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(message)}`
            })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                } else if (!response.ok) {
                    throw new Error('Ошибка сети');
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Произошла ошибка при отправке сообщения');
            });
        });

        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendButton.click();
            }
        });
    });