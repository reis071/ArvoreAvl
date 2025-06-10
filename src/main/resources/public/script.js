const apiUrl = 'http://localhost:4567';
let network = null;
const nodes = new vis.DataSet([]);
const edges = new vis.DataSet([]);
const messageArea = document.getElementById('messageArea');

document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('avlNetwork');
    const data = {
        nodes: nodes,
        edges: edges,
    };
    // CÓDIGO CORRIGIDO
    const options = {
        layout: {
            hierarchical: {
                direction: "UD",
                sortMethod: "directed",
                nodeSpacing: 200,
                levelSeparation: 150,
            }
        },
        physics: { enabled: true }, // <-- AJUSTE FEITO
        nodes: {
            shape: 'circle',
            size: 35,
            font: { size: 16, face: 'arial', multi: 'html', align: 'center' },
            borderWidth: 2,
            shadow: true,
            color: { background: '#97C2FC', border: '#2B7CE9' }
        },
        edges: {
            arrows: { to: { enabled: true, scaleFactor: 0.5 } },
            smooth: { enabled: true, type: "cubicBezier", roundness: 0.5 },
            color: "#2B7CE9",
            width: 2,
            shadow: true
        }
    };

    network = new vis.Network(container, data, options);
    fetchTree();
});

function showMessage(text, isError = false) {
    messageArea.textContent = text;
    messageArea.className = isError ? 'error' : '';
    setTimeout(() => messageArea.textContent = '', 5000);
}

async function fetchTree() {
    try {
        const response = await fetch(`${apiUrl}/tree`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        updateVisualization(data);
    } catch (error) {
        console.error('Erro ao buscar árvore:', error);
        showMessage('Erro ao conectar com o servidor.', true);
    }
}

async function sendData(endpoint, value) {
    messageArea.textContent = '';
    try {
        const response = await fetch(`${apiUrl}/${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ value: value }),
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || `HTTP error! status: ${response.status}`);
        }
        if (data.message) {
            showMessage(data.message);
        }
        updateVisualization(data);
    } catch (error) {
        console.error(`Erro em ${endpoint}:`, error);
        showMessage(error.message || `Erro ao ${endpoint} nó.`, true);
    }
}

function updateVisualization(data) {
    nodes.clear();
    edges.clear();

    if (data.nodes) {
        nodes.add(data.nodes);
    }

    if (data.edges) {
        edges.add(data.edges);
    }

    network.fit();

    document.getElementById('rotationsInsert').textContent = data.rotationCountInsert || 0;
    document.getElementById('rotationsDelete').textContent = data.rotationCountDelete || 0;
}

function handleInput(actionFn) {
    const valueInput = document.getElementById('nodeValue');
    const valueStr = valueInput.value;

    if (valueStr === "") {
        showMessage("Por favor, insira um valor.", true);
        return;
    }

    const value = parseInt(valueStr, 10);

    if (isNaN(value)) {
        showMessage("Valor inválido. Insira um número inteiro.", true);
        valueInput.value = '';
        return;
    }

    actionFn(value);
    valueInput.value = '';
}

function insertNode() {
    handleInput(value => sendData('insert', value));
}

function deleteNode() {
    handleInput(value => sendData('delete', value));
}

async function searchNode() {
    handleInput(async value => {
        messageArea.textContent = '';
        try {
            const response = await fetch(`${apiUrl}/search/${value}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const result = await response.json();
            if (result.found) {
                showMessage(`Valor ${result.value} encontrado na árvore.`);
            } else {
                showMessage(`Valor ${result.value} NÃO encontrado na árvore.`, true);
            }
        } catch (error) {
            console.error('Erro ao buscar nó:', error);
            showMessage('Erro ao buscar nó.', true);
        }
    });
}

async function resetTree() {
    messageArea.textContent = '';
    try {
        const response = await fetch(`${apiUrl}/reset`, { method: 'POST' });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        updateVisualization(data);
        showMessage("Árvore resetada.");
    } catch (error) {
        console.error('Erro ao resetar árvore:', error);
        showMessage('Erro ao resetar árvore.', true);
    }
}