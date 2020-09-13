var websocket = null;

window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
    // Aqui meter /Meta2/ws
    connect('wss://' + window.location.host + '/Meta2/meta2/ws');
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
    console.log(window.location.pathname);
    if(window.location.pathname == "/Meta2/rtsView.action")
        doSend("inRTS");
}

function onClose(event) {
    console.log('WebSocket closed (code ' + event.code + ').');
    websocket.close();
}

function onMessage(message) { // print the received message
    console.log(message.data);
    if(message.data == "You have been promoted to admin!") {
        console.log('Promoting to admin toast');
        M.toast({html: 'You have been promoted to admin! Refresh the page.',displayLength: 20000});
    } else {
        if(window.location.pathname == "/Meta2/rtsView.action") {
            console.log("RTS UPDATE HERE");
            let parameters = message.data.split(";;");
            updatePage(parameters);
        }
    }
}

function updatePage(parameters) {
    console.log('Params:',parameters);
    var mostRelevant = parameters.slice(2,12);
    var mostSearched = parameters.slice(12,22);
    var multicastServers = new Array();
    for(let i = 22,j = 0; i < parameters.length; i++,j++) {
        multicastServers[j] = 'IP: ' + parameters[i++].split('|||')[1] + ' PORT: ' + parameters[i].split('|||')[1];
    }

    for(let i = 1; i <= 10; i++) {
        $('#mostRelevant-'+i).html(i + ". " + mostRelevant[i-1].split('|||')[1]);
        $('#mostSearched-'+i).html(i + ". " + mostSearched[i-1].split('|||')[1]);
    }

    $('#multicastServersContainer').empty();
    $('#multicastServersContainer').append('<li class="collection-header"><h6><b>Active multicast servers</b></h6></li>');
    $()
    for(let i = 0; i < multicastServers.length; i++) {
        let theId = 'multicastServers-'+(i+1);
        $('#multicastServersContainer').append('<li id=' + theId + ' class="collection-item">' + multicastServers[i] + '</li>');
    }
}

function onError(event) {
    console.log('WebSocket error.');
    websocket.close();
}

function doSend(msg) {
    if (msg != '')
        websocket.send(msg); // send the message to the server
}