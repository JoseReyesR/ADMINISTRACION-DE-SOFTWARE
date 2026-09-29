// ==========================================
// TOTTUS ADMIN DASHBOARD CONTROLLER (v2.7 Responsive)
// ==========================================

let allCases = []; 
let currentEditCode = null; 
let statusChartInstance = null; // Instancia del gráfico

// Base de Datos de Clientes (Espejo de app.js)
const usersDB = {
    "12345678": { name: "Juan Carlos", lastName: "Pérez Lopez", email: "juan.perez@hotmail.com", phone: "912345678" },
    "87654321": { name: "Ana María", lastName: "Gómez Ruiz", email: "ana.gomez@yahoo.com", phone: "998877665" },
    "11223344": { name: "Carlos Alberto", lastName: "Sánchez Diaz", email: "carlos.sanchez@gmail.com", phone: "955443322" },
    "11111111": { name: "María Alejandra", lastName: "Torres", email: "maria.torres@gmail.com", phone: "987654321" }
};

document.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
    showAdminView('dashboard');
});

// GESTIÓN DE VISTAS (SPA)
function showAdminView(viewName) {
    document.querySelectorAll('.admin-view').forEach(el => el.classList.add('hidden'));
    const view = document.getElementById(`view-${viewName}`);
    if(view) view.classList.remove('hidden');

    const titles = { 'dashboard': 'Resumen de Operaciones', 'inbox': 'Bandeja de Entrada', 'clients': 'Gestión de Clientes' };
    document.getElementById('page-title').innerText = titles[viewName] || 'Panel Admin';

    ['dashboard', 'inbox', 'clients'].forEach(btn => {
        const el = document.getElementById(`btn-${btn}`);
        if(el) {
            el.classList.remove('bg-slate-700', 'text-white');
            el.classList.add('text-gray-400');
        }
    });
    const activeBtn = document.getElementById(`btn-${viewName}`);
    if(activeBtn) {
        activeBtn.classList.remove('text-gray-400');
        activeBtn.classList.add('bg-slate-700', 'text-white');
    }

    if(viewName === 'clients') renderClients();
}

// LÓGICA DE DATOS
function loadDashboardData() {
    const storedData = localStorage.getItem('casesHistory');
    allCases = storedData ? JSON.parse(storedData).reverse() : [];
    renderTable(allCases);
    updateKPIs();
    updateChart(); // Actualizar Gráfico
}

// --- LOGICA DEL GRÁFICO (Chart.js) ---
function updateChart() {
    const ctx = document.getElementById('casesDoughnutChart');
    if(!ctx) return;

    // 1. Calcular Datos Reales
    const resolvedCount = allCases.filter(c => c.status === 'resolved').length;
    const expiredCount = allCases.filter(c => c.status === 'expired').length;
    
    // Sumamos 'pending' y 'analysis' para el estado "En Proceso" (Amarillo)
    const processCount = allCases.filter(c => c.status === 'pending' || c.status === 'analysis').length;

    // 2. Destruir gráfico previo si existe (Evitar bugs)
    if (statusChartInstance) {
        statusChartInstance.destroy();
    }

    // 3. Crear Nuevo Gráfico
    statusChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Resueltos', 'Vencidos', 'En Proceso'], // Etiquetas
            datasets: [{
                data: [resolvedCount, expiredCount, processCount],
                backgroundColor: [
                    '#22c55e', // Verde (Resuelto)
                    '#ef4444', // Rojo (Vencido)
                    '#fbbf24'  // Amarillo (En Proceso)
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%', // Grosor
            plugins: {
                legend: { display: false } // Usamos leyenda manual
            }
        }
    });
}

// --- RENDERIZAR TABLA PRINCIPAL ---
function renderTable(data) {
    const tbody = document.getElementById('adminTableBody');
    tbody.innerHTML = "";
    if(data.length === 0) { tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-gray-500">No hay casos registrados</td></tr>'; return; }

    data.forEach(c => {
        const priority = calculatePriority(c.reason);
        const priorityClass = getPriorityClass(priority);
        const statusConfig = getStatusConfig(c.status);
        const dniDisplay = c.dni ? c.dni : '<span class="text-gray-300">---</span>';

        tbody.innerHTML += `
            <tr class="hover:bg-gray-50 transition">
                <td class="px-6 py-4 font-mono text-gray-600 font-bold">${c.code}</td>
                <td class="px-6 py-4 font-mono text-gray-500 font-medium">${dniDisplay}</td>
                <td class="px-6 py-4 text-gray-500">${c.date}</td>
                <td class="px-6 py-4"><span class="block text-gray-800 font-medium truncate w-48" title="${c.reason}">${c.reason}</span></td>
                <td class="px-6 py-4"><span class="prio-badge ${priorityClass}">${priority}</span></td>
                <td class="px-6 py-4"><div class="flex items-center"><span class="status-dot ${statusConfig.colorClass}"></span><span class="text-gray-700 capitalize">${statusConfig.label}</span></div></td>
                <td class="px-6 py-4"><button onclick="openEditModal('${c.code}')" class="text-blue-600 hover:text-blue-800 hover:bg-blue-100 p-2 rounded transition"><i class="fas fa-edit"></i></button></td>
            </tr>`;
    });
}

// --- RENDERIZAR TABLA CLIENTES ---
function renderClients() {
    const tbody = document.getElementById('clientsTableBody');
    if(!tbody) return;
    tbody.innerHTML = "";
    Object.keys(usersDB).forEach(dni => {
        const user = usersDB[dni];
        const caseCount = allCases.filter(c => c.dni === dni).length;
        tbody.innerHTML += `
            <tr class="hover:bg-gray-50 transition">
                <td class="px-6 py-4 font-bold text-gray-700">${user.name} ${user.lastName}</td>
                <td class="px-6 py-4 font-mono text-gray-500">${dni}</td>
                <td class="px-6 py-4 text-blue-600">${user.email}</td>
                <td class="px-6 py-4"><span class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full font-bold text-xs">${caseCount} Casos</span></td>
                <td class="px-6 py-4"><span class="text-green-600 font-bold text-xs uppercase bg-green-50 px-2 py-1 rounded border border-green-200">Activo</span></td>
            </tr>`;
    });
}

// --- UTILIDADES ---
function updateKPIs() {
    document.getElementById('kpi-total').innerText = allCases.length;
    document.getElementById('kpi-pending').innerText = allCases.filter(c => c.status === 'pending' || c.status === 'analysis').length;
    document.getElementById('kpi-urgent').innerText = allCases.filter(c => calculatePriority(c.reason) === 'ALTA').length;
    document.getElementById('kpi-resolved').innerText = allCases.filter(c => c.status === 'resolved').length;
}

function calculatePriority(reasonText) {
    const r = reasonText.toLowerCase();
    if (r.includes('vencido') || r.includes('cobro') || r.includes('delivery')) return 'ALTA';
    if (r.includes('mala atención') || r.includes('precio')) return 'MEDIA';
    return 'BAJA';
}

function getPriorityClass(prio) { return prio === 'ALTA' ? 'prio-high' : prio === 'MEDIA' ? 'prio-medium' : 'prio-low'; }
function getStatusConfig(status) {
    switch(status) {
        case 'pending': return { label: 'En Proceso', colorClass: 'status-pending' };
        case 'analysis': return { label: 'En Análisis', colorClass: 'status-analysis' };
        case 'resolved': return { label: 'Resuelto', colorClass: 'status-resolved' };
        case 'expired': return { label: 'Vencido', colorClass: 'status-expired' };
        case 'rejected': return { label: 'Rechazado', colorClass: 'status-rejected' }; 
        default: return { label: status, colorClass: 'bg-gray-400' };
    }
}

function openEditModal(code) {
    const caseItem = allCases.find(c => c.code === code);
    if(!caseItem) return;
    currentEditCode = code;
    document.getElementById('modalCode').innerText = caseItem.code;
    document.getElementById('modalReason').innerText = caseItem.reason;
    document.getElementById('modalStatusSelect').value = caseItem.status === 'expired' ? 'pending' : caseItem.status; 
    document.getElementById('editModal').classList.remove('hidden');
}

function closeModal() { document.getElementById('editModal').classList.add('hidden'); currentEditCode = null; }

function saveCaseChanges() {
    if(!currentEditCode) return;
    const newStatus = document.getElementById('modalStatusSelect').value;
    const caseIndex = allCases.findIndex(c => c.code === currentEditCode);
    if(caseIndex !== -1) {
        allCases[caseIndex].status = newStatus;
        localStorage.setItem('casesHistory', JSON.stringify([...allCases].reverse()));
        alert(`Caso ${currentEditCode} actualizado.`);
        closeModal();
        loadDashboardData(); 
    }
}

function searchTable() {
    const filter = document.getElementById('searchInput').value.toLowerCase();
    renderTable(allCases.filter(c => c.code.toLowerCase().includes(filter) || c.reason.toLowerCase().includes(filter) || (c.dni && c.dni.includes(filter))));
}

// --- FUNCIONES RESPONSIVE (NUEVO) ---
function toggleAdminSidebar() {
    const sidebar = document.getElementById('admin-sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    
    // Si tiene la clase hidden en móvil (translate negativo), se la quitamos para mostrarlo
    if (sidebar.classList.contains('-translate-x-full')) {
        // ABRIR MENU
        sidebar.classList.remove('-translate-x-full');
        overlay.classList.remove('hidden');
    } else {
        // CERRAR MENU
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
    }
}