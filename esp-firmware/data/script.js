function attachClickEventToAvailableSSIDItem(item) {
    item.addEventListener("click", function () {
        const ssidName = this.querySelector("#name-ssid").textContent;
        document.getElementById("ssid").value = ssidName;
        const passwordValue = this.querySelector("#password-ssid").textContent;
        if (passwordValue === "secure") {
            document.getElementById("password").style.display = "block";
        } else {
            document.getElementById("password").style.display = "none";
        }
    });
}

function fetchInformation() {
    fetch("/information")
        .then(response => response.json())
        .then(data => {
            document.getElementById("ssid-json").textContent = data.ssid || "No info";
            document.getElementById("ssid").textContent = data.ssid || "No info";
            document.getElementById("ip-json").textContent = data.ip || "No info";
            document.getElementById("device-key-json").textContent = data.device || "No info";
            document.getElementById("server-ip-json").textContent = data.server_ip || "No info";
        })
        .catch(error => {
            console.error('Error fetching connected SSID:', error);
        });
}

function fetchAvailablesSSID() {
    fetch("/availables-ssid")
        .then(response => response.json())
        .then(data => {

            const ulList = document.querySelector('.lista');
            ulList.innerHTML = '';  // Clear the current list
            data.forEach(ssidData => {
                const li = document.createElement('li');
                li.innerHTML = `
                <div class="ssid">
                    <span>🛜</span>
                    <span id="name-ssid">${ssidData.name}</span>
                </div>
                <div class="pass">
                    <span>🔒</span>
                    <span id="password-ssid">${ssidData.hasPassword ? 'secure' : 'open'}</span>
                </div>
                <div class="signal">
                    <span>📶</span>
                    <span>${ssidData.signalStrength}</span>
                </div>
            `;
                ulList.appendChild(li);
                attachClickEventToAvailableSSIDItem(li);
            });
        })
        .catch(error => {
            console.error('Error fetching SSIDs:', error);
        });
}




document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('save');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(form);

        fetch('/save', {
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(data => {
                // W tym miejscu możesz wyświetlić odpowiedź serwera, np. komunikat o pomyślnym zapisie
                console.log(data);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});


document.addEventListener("DOMContentLoaded", function () {
    fetchInformation();
    fetchAvailablesSSID();

});