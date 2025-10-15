/**
 * javascript server file for communication with the mud server
 */
	var webSocket = new WebSocket("ws"+(location.protocol=="https:"?"s":"")+"://"+location.host+"/javaMud3/server");

    webSocket.onerror = function(event) {
        onError(event)
    };

    webSocket.onopen = function(event) {
        onOpen(event)
    };

    webSocket.onclose = function(event) {
        onError(event)
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
    	console.debug(event);
        alert(event.data);
    }

    function send(text) {
    	console.debug(text);
        webSocket.send(text);
        //return false;
    }
