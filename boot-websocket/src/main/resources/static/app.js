var url = "http://localhost:8080/websocket"
var socket = new SockJS(url);
var stompClient = Stomp.over(socket);

var user = $("#user").val()
console.log($("#user").val())
var headers = {
    username: user,
    password: user
};

console.log("start connect server...")
stompClient.connect(headers, function (frame) {
    console.log("connect success !!!")
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/greetings', function (message) {
        showGreeting(JSON.parse(message.body).content);
    });
    stompClient.subscribe("/topic/subscribe", function (message) {
        console.log(message.body)
        $("#datetime").text(message.body)
    });
    stompClient.subscribe("/user/queue/notify", function (message) {
        showGreeting(JSON.parse(message.body).content);
    });
    stompClient.subscribe("/user/queue/errors", function (message) {
        alert("Error " + message.body);
    });

});

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function sendUser() {
    stompClient.send("/app/helloToUser", {}, JSON.stringify({'name': $("#name").val()}))
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
    $("#send1").click(function () {
        sendUser()
    })
});
