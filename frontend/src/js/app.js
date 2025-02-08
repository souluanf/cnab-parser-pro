document.addEventListener("DOMContentLoaded", async function () {
    console.log("Aplicação CNAB Parser carregada.");
    verificarAutenticacao();
    await carregarNavbar();
    await configurarUpload();
    await carregarTransacoes();
    await carregarSaldosLojas();
});
const API_URL = "http://localhost:8080/api/v1/auth";


async function obterToken() {
    let token = localStorage.getItem("access_token");
    let expiresIn = localStorage.getItem("expires_in");

    if (!token || Date.now() >= expiresIn) {
        return await refreshToken();
    }

    return token;
}

function verificarAutenticacao() {
    const token = localStorage.getItem("access_token");
    const isLoginPage = window.location.pathname.includes("login");

    if (!token && !isLoginPage) {
        window.location.href = "/login";
    }
}

async function refreshToken() {
    const refreshToken = localStorage.getItem("refresh_token");

    if (!refreshToken) {
        logout();
        return null;
    }

    try {
        const response = await fetch(`${API_URL}/refresh-token`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${refreshToken}`
            }
        });

        if (!response.ok) throw new Error("Erro ao renovar token");

        const data = await response.json();
        armazenarTokens(data);
        return data.access_token;

    } catch (error) {
        console.error("Erro ao renovar token:", error);
        logout();
        return null;
    }
}

async function carregarTransacoesPorLoja() {
    const storeNameInput = document.getElementById("storeName");
    const tabela = document.getElementById("storeTransactionsTable");
    const tableContainer = document.getElementById("storeTableContainer");

    if (!storeNameInput.value.trim()) {
        alert("Por favor, digite o nome da loja.");
        return;
    }

    const storeName = encodeURIComponent(storeNameInput.value.trim());

    try {
        const token = await obterToken();
        if (!token) return;

        const response = await fetch(`http://localhost:8080/api/v1/transactions/store/${storeName}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (!response.ok) throw new Error("Erro ao buscar transações");

        const transacoes = await response.json();
        tabela.innerHTML = "";

        if (transacoes.length === 0) {
            tableContainer.style.display = "none";
            alert("Nenhuma transação encontrada para essa loja.");
            return;
        }

        transacoes.forEach(tx => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${tx.id}</td>
                <td>${tx.description}</td>
                <td>${tx.date}</td>
                <td>R$ ${tx.value.toFixed(2)}</td>
                <td>${tx.cpf}</td>
                <td>${tx.card}</td>
                <td>${tx.hour}</td>
                <td>${tx.storeOwner}</td>
            `;
            tabela.appendChild(row);
        });

        tableContainer.style.display = "block";

    } catch (error) {
        console.error("Erro ao carregar transações:", error);
        alert("Erro ao buscar transações.");
    }
}


function armazenarTokens(data) {
    localStorage.setItem("access_token", data.access_token);
    localStorage.setItem("refresh_token", data.refresh_token);
    localStorage.setItem("expires_in", Date.now() + data.expires_in * 1000);
}

async function logout() {
    const refreshToken = localStorage.getItem("refresh_token");

    if (refreshToken) {
        await fetch(`${API_URL}/logout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${refreshToken}`
            }
        });
    }

    localStorage.clear();
    window.location.href = "/login";
}

async function login(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch(`${API_URL}/token`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) throw new Error("Usuário ou senha incorretos");

        const data = await response.json();
        armazenarTokens(data);
        window.location.href = "/home";

    } catch (error) {
        document.getElementById("loginMessage").innerText = "Erro: " + error.message;
    }
}

const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", login);
}

function carregarNavbar() {
    fetch("navbar.html")
        .then(response => response.text())
        .then(data => {
            document.getElementById("navbar-container").innerHTML = data;
            destacarPaginaAtiva();
        })
        .catch(error => console.error("Erro ao carregar a navbar:", error));
}

function destacarPaginaAtiva() {
    const paginaAtual = window.location.pathname.split("/").pop();
    const links = document.querySelectorAll(".nav-link");

    links.forEach(link => {
        if (link.getAttribute("href") === paginaAtual) {
            link.classList.add("active");
        }
    });
}

async function carregarTransacoesPorCPF() {
    const tabela = document.getElementById("cpfTransactionsTable");
    const tableContainer = document.getElementById("cpfTableContainer");
    const cpf = document.getElementById("cpf").value.trim();

    if (!cpf) {
        alert("Por favor, digite um CPF válido.");
        return;
    }

    try {
        const token = await obterToken();
        const response = await fetch(`http://localhost:8080/api/v1/transactions/cpf/${cpf}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });

        if (!response.ok) throw new Error("Erro ao buscar transações");

        const transacoes = await response.json();
        tabela.innerHTML = "";

        if (transacoes.length === 0) {
            tableContainer.style.display = "none";
            alert("Nenhuma transação encontrada para esse CPF.");
            return;
        }

        transacoes.forEach(tx => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${tx.id}</td>
                <td>${tx.description}</td>
                <td>${tx.date}</td>
                <td>${tx.hour}</td>
                <td>${tx.value.toFixed(2)}</td>
                <td>${tx.cpf}</td>
                <td>${tx.card}</td>
                <td>${tx.storeName}</td>
                <td>${tx.storeOwner}</td>
            `;
            tabela.appendChild(row);
        });
        tableContainer.style.display = "block";

    } catch (error) {
        console.error("Erro ao carregar transações:", error);
    }
}


function configurarUpload() {
    const uploadForm = document.getElementById("uploadForm");
    const fileInput = document.getElementById("fileInput");
    const fileNameDisplay = document.getElementById("fileName");
    const messageDiv = document.getElementById("message");

    if (!uploadForm || !fileInput || !fileNameDisplay || !messageDiv) return;

    fileInput.addEventListener("change", function () {
        fileNameDisplay.textContent = this.files.length > 0 ? this.files[0].name : "Nenhum arquivo selecionado";
    });

    uploadForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        if (!fileInput.files.length) {
            messageDiv.innerHTML = `<span class="error-message">Por favor, selecione um arquivo.</span>`;
            return;
        }

        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        try {
            const token = await obterToken();
            const response = await fetch("http://localhost:8080/api/v1/transactions/process-file", {
                method: "POST",
                body: formData,
                headers: { "Authorization": `Bearer ${token}` }
            });

            const responseData = await response.json();

            if (!response.ok) {
                if (response.status === 400 && responseData.detail === "Erro ao processar o arquivo CNAB") {
                    mostrarErrosProcessamento(responseData.stacktrace);
                } else {
                    throw new Error(responseData.detail || "Erro desconhecido ao processar o arquivo.");
                }
            } else {
                messageDiv.innerHTML = `<span class="success-message">Arquivo enviado com sucesso!</span>`;
            }
        } catch (error) {
            messageDiv.innerHTML = `<span class="error-message">Erro ao enviar arquivo: ${error.message}</span>`;
            console.error("Erro:", error);
        }
    });
}

function mostrarErrosProcessamento(stacktrace) {
    const messageDiv = document.getElementById("message");
    const uploadForm = document.getElementById("uploadForm");
    if (!messageDiv || !uploadForm) return;
    uploadForm.style.display = "none";
    let erroHTML = `
        <span class="error-message">Ocorreu um erro ao processar o arquivo:</span>
        <ul class="error-list">
    `;
    stacktrace.forEach(error => {
        erroHTML += `
            <li class="error-item">
                <strong>Linha ${error.linha}:</strong> ${error.content} <br>
                <em>Erro:</em> ${error.reason}
            </li>
        `;
    });
    erroHTML += `</ul>
    <button id="clearErrors" class="clear-errors">🧹 Limpar Erros</button>`;
    messageDiv.innerHTML = erroHTML;
    document.getElementById("clearErrors").addEventListener("click", function () {
        messageDiv.innerHTML = "";
        uploadForm.style.display = "flex";
    });
}

async function carregarTransacoes() {
    const tabela = document.getElementById("transactionsTableBody");
    if (!tabela) return;

    try {
        const token = await obterToken();
        const response = await fetch("http://localhost:8080/api/v1/transactions", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });

        if (!response.ok) throw new Error("Erro ao buscar transações");

        const transacoes = await response.json();
        tabela.innerHTML = "";

        transacoes.forEach(tx => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${tx.id}</td>
                <td>${tx.description}</td>
                <td>${tx.date}</td>
                <td>${tx.value.toFixed(2)}</td>
                <td>${tx.cpf}</td>
                <td>${tx.card}</td>
                <td>${tx.hour}</td>
                <td>${tx.storeOwner}</td>
                <td>${tx.storeName}</td>
            `;
            tabela.appendChild(row);
        });
    } catch (error) {
        console.error("Erro ao carregar transações:", error);
    }
}

async function carregarSaldosLojas() {
    const tabela = document.getElementById("balancesTable");
    tabela.innerHTML = "";

    try {
        const token = localStorage.getItem("access_token");
        if (!token) {
            window.location.href = "/login";
            return;
        }

        const response = await fetch("http://localhost:8080/api/v1/transactions/store/balance", {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error("Erro ao buscar saldos das lojas");

        const saldos = await response.json();

        if (saldos.length === 0) {
            tabela.innerHTML = "<tr><td colspan='3'>Nenhum saldo encontrado</td></tr>";
            return;
        }

        saldos.forEach(store => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${store.storeName}</td>
                <td>${store.storeOwner}</td>
                <td>R$ ${store.totalBalance.toFixed(2)}</td>
            `;
            tabela.appendChild(row);
        });

    } catch (error) {
        console.error("Erro ao carregar saldos das lojas:", error);
        tabela.innerHTML = "<tr><td colspan='3'>Erro ao carregar os dados</td></tr>";
    }
}

document.getElementById("searchStore").addEventListener("click", carregarTransacoesPorLoja);

