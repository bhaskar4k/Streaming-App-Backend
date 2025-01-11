export async function get_ip_address() {
    try {
        let response = await fetch("https://api.ipify.org/?format=json", {
            method: 'GET',
        });

        let res = await response.json();

        return res.ip;
    } catch {
        console.log("Internal server error");
    }

    return null;
}

export function logout(navigate) {
    localStorage.removeItem("JWT");
    redirect_to_login(navigate);
}

export function redirect_to_login(navigate){
    setTimeout(() => {
        navigate(`/login`);
    }, 500);
}

export function redirect_to_home(navigate){
    navigate(`/home`);
}