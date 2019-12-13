var websocket = null;

window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
    // Aqui meter /Meta2/ws
    connect('wss://' + window.location.host + '/Meta2/meta2/ws');
    document.getElementById("chat").focus();
}

function connect(host) { // connect to the host websocket
    if ('WebSocket' in window)
        websocket = new WebSocket(host);
    else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
    else {
        console.log('Get a real browser which supports WebSocket.');
        return;
    }

    websocket.onopen    = onOpen; // set the 4 event listeners below
    websocket.onclose   = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror   = onError;
}

function onOpen(event) {
    console.log('Connected to ' + window.location.host + '.');
    console.log(window.location.pathname + window.location.search);
    /*
    document.getElementById('chat').onkeydown = function(key) {
        if (key.code === 'Enter')
            doSend(); // call doSend() on enter key press
    };
    */
}

function onClose(event) {
    console.log('WebSocket closed (code ' + event.code + ').');
    //document.getElementById('chat').onkeydown = null;
}

function onMessage(message) { // print the received message
    if(message.data == "You have been promoted to admin!") {
        console.log('Promoting to admin toast');
        M.toast({html: 'You have been promoted to admin! Refresh the page.',displayLength: 20000});
    }
        console.log(message.data);
}

function onError(event) {
    console.log('WebSocket error.');
    //document.getElementById('chat').onkeydown = null;
}

function doSend() {
    var message = document.getElementById('chat').value;
    if (message != '')
        websocket.send(message); // send the message to the server
    //document.getElementById('chat').value = '';
}

/*
function writeToHistory(text) {
    var history = document.getElementById('history');
    var line = document.createElement('p');
    line.style.wordWrap = 'break-word';
    line.innerHTML = text;
    history.appendChild(line);
    history.scrollTop = history.scrollHeight;
}*/