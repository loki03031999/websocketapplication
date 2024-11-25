'use strict';
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUser = null;
let selectedUserId = null;

function connect(event) {

    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }

    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    //register the connected user
    stompClient.send('/app/user.addUser', 
        {},
        JSON.stringify({nickname: nickname, fullName: fullname, status: 'ONLINE'})
        );
    //find and display the connected users:
    findAndDisplayConnectedUser().then();
}

async function findAndDisplayConnectedUser() {
    const connectedUserResponse = await fetch('/users');
    let connectedUsers = await connectedUserResponse.json();
    connectedUsers = connectedUsers.filter(user => user.nickname != nickname);
    const connectedUsersList = document.querySelector('#connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            //add a seperator
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listitem = document.createElement('li');
    listitem.classList.add('user-item');
    listitem.id = user.nickname;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.fullName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullName;

    const receviedMsgs = document.createElement('span');
    receviedMsgs.textContent = '0';
    receviedMsgs.classList.add('nbs-msg', 'hidden');

    listitem.appendChild(userImage);
    listitem.appendChild(usernameSpan);
    listitem.appendChild(receviedMsgs);

    listitem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listitem);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach( item => {
        item.classList.remove('active');
    })

    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';

    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;

}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    }
    else {
        messageContainer.classList.add('receiver');
    }

    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

function onError() {

}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date()
        };
        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageContent);
    }

    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

async function onMessageReceived(payload) {
    await findAndDisplayConnectedUser();
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    }
    else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.senderId}`);

    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }

}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);