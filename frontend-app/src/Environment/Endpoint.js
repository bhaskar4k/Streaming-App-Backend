let EndpointMicroservice = {
    authentication : "http://localhost:8090",
    dashboard : "http://localhost:8091",
    streaming : "http://localhost:8092",
    upload : "http://localhost:8093",
}

let EndpointAuthentication = {
    do_signup : "/authentication/do_signup",
    do_login : "/authentication/do_login",
}

let EndpointDashboard = {
    dashboard : "/dashboard/temp",
}

let EndpointStreaming = {
    streaming : "/streaming/temp",
}

let EndpointUpload = {
    upload : "/upload/temp",
}

let EndpointWebsocket = {
    authentication_websocket: "ws://localhost:8090",
    get_websocket_emit : "/authentication-websocket",

    get_logout_emit : "/topic/logout",
    get_broadcast_emit : "/topic/broadcast",
    
    emit_data : "/app/send-message",
}

export {EndpointMicroservice, EndpointAuthentication, EndpointDashboard, EndpointStreaming, EndpointUpload, EndpointWebsocket};