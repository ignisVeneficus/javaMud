/**
 * javascript server file for communication with the mud server
 */
	var webSocket = new WebSocket('ws://localhost:8080/javaMud3/server');

    webSocket.onerror = function(event) {
        onError(event)
    };

    webSocket.onopen = function(event) {
        onOpen(event)
    };

    webSocket.onmessage = function(event) {
        onMessage(event)
    };

    function onMessage(event) {
        console.debug(event.data);
        handleEvent(JSON.parse(event.data));

    }

    function onOpen(event) {
        //document.getElementById('messages').innerHTML = 'Now Connection established';
    }

    function onError(event) {
        alert(event.data);
    }

    function send(text) {
    	console.debug(text);
        webSocket.send(text);
        //return false;
    }
