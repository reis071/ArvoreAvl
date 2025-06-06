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
    const options = {
        layout: {
            hierarchical: {
                direction: "UD", // Up-Down
                sortMethod: "directed",
                nodeSpacing: 120,
                levelSeparation: 120,
                parentCentralization: true,
                blockShifting: true,
                edgeMinimization: true
            }
        },
        edges: {
            arrows: 'to',
            smooth: {
                type: 'straightCross',
                roundness: 0
            },
            color: "#2B7CE9",
            width: 2
        },
        nodes: {
            fixed: true,
            shape: 'circle',
            size: 30,
            color: {
                background: '#97C2FC',
                border: '#2B7CE9',
                highlight: {
                    background: '#D2E5FF',
                    border: '#2B7CE9'
                }
            },
            margin: 10,
            font: {
                size: 16,
                color: '#333',
                face: 'arial',
                align: 'center',
                multi: true
            },
            borderWidth: 2
        },
        physics: false,
        interaction: {
            dragNodes: false,
            dragView: true,
            zoomView: true,
            hover: true
        }
    };
    network = new vis.Network(container, data, options);
    fetchTree(); // Carrega a árvore inicial (vazia)
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
    messageArea.textContent = ''; // Limpa mensagens anteriores
    try {
        const response = await fetch(`${apiUrl}/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ value: parseInt(value) }),
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({error: "Erro desconhecido"}));
            throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
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
    if (data && data.nodes) {
        nodes.add(data.nodes);
    }
    if (data && data.edges) {
        edges.add(data.edges);
    }

    // Para o layout hierárquico funcionar melhor, é bom re-aplicar opções ou forçar um redraw
    // network.setOptions(network.options); // Reaplicar opções pode ajudar
    network.fit(); // Ajusta a visualização

    document.getElementById('rotationsInsert').textContent = data.rotationCountInsert || 0;
    document.getElementById('rotationsDelete').textContent = data.rotationCountDelete || 0;
}

function insertNode() {
    const value = document.getElementById('nodeValue').value;
    if (value === "") {
        showMessage("Por favor, insira um valor.", true);
        return;
    }
    sendData('insert', value);
    document.getElementById('nodeValue').value = '';
}

function deleteNode() {
    const value = document.getElementById('nodeValue').value;
    if (value === "") {
        showMessage("Por favor, insira um valor para remover.", true);
        return;
    }
    sendData('delete', value);
    document.getElementById('nodeValue').value = '';
}

async function searchNode() {
    const value = document.getElementById('nodeValue').value;
    if (value === "") {
        showMessage("Por favor, insira um valor para buscar.", true);
        return;
    }
    messageArea.textContent = '';
    try {
        const response = await fetch(`${apiUrl}/search/${value}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const result = await response.json();
        if (result.found) {
            showMessage(`Valor ${result.value} encontrado na árvore.`);
            // Opcional: destacar o nó. Vis.js permite selecionar nós.
            // nodes.update([{id: /*id do nó encontrado*/, color: {background: 'lime'}}]);
            // Precisaria de uma forma de mapear valor para id do nó aqui.
            // Por simplicidade, apenas informamos que foi encontrado.
        } else {
            showMessage(`Valor ${result.value} NÃO encontrado na árvore.`, true);
        }
    } catch (error) {
        console.error('Erro ao buscar nó:', error);
        showMessage('Erro ao buscar nó.', true);
    }
    document.getElementById('nodeValue').value = '';
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