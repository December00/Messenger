document.addEventListener('DOMContentLoaded', function() {
    let stompClient = null;
    const currentChatId = document.getElementById('send-button')?.dataset.chatid || null;
    const messagesContainer = document.querySelector('.chat-messages');
    const userId = document.body.dataset.userid;
    console.log("Ваш текущий Id-пользователя: " + userId)
    const addButton = document.getElementById('add-button');
    const addForm = document.getElementById('add-friend-form');
    const submitButton = document.getElementById('submit-add-friend');
    const usernameInput = document.getElementById('friend-username');
    const sendButton = document.getElementById('send-button');
    const messageInput = document.getElementById('message-input');

    // Подключение к WebSocket
    function connect() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({},
            () => onConnect(),
            (error) => onError(error)
        );
    }

    // Успешное подключение
    function onConnect() {
        console.log("WebSocket connected!");

        // Подписка на сообщения текущего чата
        if (currentChatId) {
            const chatDestination = `/topic/chat.${currentChatId}`;
            stompClient.subscribe(chatDestination, (message) => {
                try {
                    const msg = JSON.parse(message.body);
                    displayMessage(msg);
                } catch (e) {
                    console.error("Error parsing message:", e);
                }
            });
        }
    const updateDestination = `/queue/chat.updates.user.${userId}`;


    stompClient.subscribe(updateDestination, (message) => {
            try {
                const msg = JSON.parse(message.body);
                updateChatList(msg);
            } catch (e) {
                console.error("Error parsing chat update:", e);
            }
        });
    }

    function displayMessage(message) {
        if (!messagesContainer) return;

        const isOutgoing = message.senderId == userId;
        const messageElement = document.createElement('div');

        messageElement.className = isOutgoing ? 'message message-outgoing' : 'message message-incoming';
        messageElement.innerHTML = `
            <div class="message-content">${message.content}</div>
            <div class="message-time">
                ${new Date(message.timestamp).toLocaleString('ru-RU', {
                    hour: '2-digit',
                    minute: '2-digit'
                })}
            </div>
        `;

        messagesContainer.appendChild(messageElement);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function updateChatList(message) {
        const chatList = document.querySelector('.friends-list');
        let chatItem = document.querySelector(`#chat-${message.chat.id}`);

        if (!chatItem) {
            chatItem = createNewChatItem(message);
            chatList.prepend(chatItem);
        } else {
            const lastMessage = chatItem.querySelector('.friend-last-message');
            const lastTime = chatItem.querySelector('.friend-last-time');

            if (lastMessage) lastMessage.textContent = message.content;
            if (lastTime) lastTime.textContent = new Date(message.timestamp)
                .toLocaleTimeString('ru-RU', {hour: '2-digit', minute: '2-digit'});

            chatList.prepend(chatItem);

            if (message.chat.id != currentChatId) {
                chatItem.classList.add('new-message');
                setTimeout(() => chatItem.classList.remove('new-message'), 5000);
            }
        }
    }

    function createNewChatItem(message) {
        const li = document.createElement('li');
        li.className = 'friend-item new-message';
        li.id = `chat-${message.chat.id}`;

        const chatName = 'Новый собеседник';

        li.innerHTML = `
            <div class="friend-avatar"></div>
            <div class="friend-info">
                <div class="friend-header">
                    <span class="friend-name">${chatName}</span>
                    <span class="friend-last-time">
                        ${new Date(message.timestamp).toLocaleTimeString('ru-RU', {hour: '2-digit', minute: '2-digit'})}
                    </span>
                </div>
                <div class="friend-last-message">${message.content}</div>
            </div>
        `;

        li.addEventListener('click', function() {
            window.location.href = `/chat/${message.chat.id}`;
        });

        setTimeout(() => li.classList.remove('new-message'), 5000);

        return li;
    }
    // Ошибка подключения
    function onError(error) {
        console.error("WebSocket error:", error);
        setTimeout(connect, 5000);
    }

    function sendMessage() {
        const messageText = messageInput.value.trim();

        if (!messageText) {
            alert("Введите сообщение!");
            return;
        }

        if (!currentChatId) {
            alert("Выберите чат!");
            return;
        }

        if (!stompClient || !stompClient.connected) {
            alert("Соединение не установлено. Попробуйте обновить страницу.");
            return;
        }

        const message = {
            chat: { id: parseInt(currentChatId) },
            senderId: parseInt(userId),
            content: messageText,
            timestamp: new Date().toISOString()
        };

        stompClient.send("/app/chat", {}, JSON.stringify(message));
        messageInput.value = "";
    }


    // Показать/скрыть форму при клике на "+"
   addButton?.addEventListener('click', function(e) {
       e.stopPropagation(); // Предотвращаем всплытие события
       addForm.style.display = addForm.style.display === 'block' ? 'none' : 'block';
   });
    // Предотвращаем всплытие событий кликов по форме
    addForm?.addEventListener('click', function(e) {
        e.stopPropagation();
    });
    // Обработка отправки формы
    const addFriendForm = document.querySelector('#add-friend-form form');
    addFriendForm?.addEventListener('submit', function(e) {
        e.preventDefault();
        const username = usernameInput.value.trim();

        if (!username) {
            alert('Пожалуйста, введите имя пользователя или email');
            return;
        }

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
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Произошла ошибка при отправке запроса: ' + error.message);
        });
    });

    // Скрыть форму при клике вне её
    document.addEventListener('click', function(event) {
        if (addForm && !addForm.contains(event.target) && event.target !== addButton) {
            addForm.style.display = 'none';
        }
    });

    // Обработка отправки сообщения
    sendButton?.addEventListener('click', sendMessage);

    messageInput?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    window.addEventListener('beforeunload', () => {
        if (stompClient) stompClient.disconnect();
    });

    // Инициализация WebSocket
    connect();

});