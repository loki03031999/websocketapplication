'use strict';
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-message');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUser = null;

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
    connectedUsers = connectedUsers.filter(user => user.nickName != nickname);
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
    listitem.id = user.nickName;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.fullName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullname;

    const receviedMsgs = document.createElement('span');
    receviedMsgs.textContent = '0';
    receviedMsgs.classList.add('nbs-msg', 'hidden');

    listitem.appendChild(userImage);
    listitem.appendChild(usernameSpan);
    listitem.appendChild(receviedMsgs);

    connectedUsersList.appendChild(listitem);
}

function onError() {

}

function onMessageReceived() {

}

usernameForm.addEventListener('submit', connect, true);
