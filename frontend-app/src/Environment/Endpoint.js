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

export {EndpointMicroservice, EndpointAuthentication, EndpointDashboard, EndpointStreaming, EndpointUpload};