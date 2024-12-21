export async function get_ip_address(){
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