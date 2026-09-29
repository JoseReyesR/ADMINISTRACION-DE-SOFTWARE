// ==========================================
// TOTTUS APP LOGIC (v2.3 - FULL)
// ==========================================

let currentStep = 1;
let userData = {};

// --- BASE DE DATOS DE CLIENTES ---

const usersDB = {
    "12345678": { name: "Juan Carlos", lastName: "Pérez Lopez", email: "juan.perez@hotmail.com", phone: "912345678" },
    "87654321": { name: "Ana María", lastName: "Gómez Ruiz", email: "ana.gomez@yahoo.com", phone: "998877665" },
    "11223344": { name: "Carlos Alberto", lastName: "Sánchez Diaz", email: "carlos.sanchez@gmail.com", phone: "955443322" },
    "11111111": { name: "María Alejandra", lastName: "Torres", email: "maria.torres@gmail.com", phone: "987654321" }
};
// --- CONFIGURACIÓN DE CATEGORÍAS ---
const claimOptions = {
    reclamo: [
        { value: 'high', text: 'Cobro equivocado en caja' },
        { value: 'high', text: 'Precio en góndola distinto al cobrado' },
        { value: 'medium', text: 'Promoción no realizada' },
        { value: 'medium', text: 'Cobro doble del producto' },
        { value: 'high', text: 'Producto defectuoso, mal estado o vencido' },
        { value: 'high', text: 'Producto distinto al solicitado' },
        { value: 'medium', text: 'Falta de producto en el pedido' },
        { value: 'low', text: 'Producto con empaque dañado' }
    ],
    queja: [
        { value: 'medium', text: 'Demora excesiva en la atención' },
        { value: 'medium', text: 'Mala atención del personal' },
        { value: 'low', text: 'Información incorrecta' },
        { value: 'high', text: 'Delivery no llegó o llegó incompleto' }
    ]
};

// --- DATA PRECARGADA PARA PRUEBAS ---
const demoCases = [
    { code: "REQ-2025-889", dni: "12345678", date: "26/11/2025", reason: "[Reclamo] Tienda - Cobro equivocado", status: "pending" },
    { code: "REQ-2025-750", dni: "87654321", date: "20/11/2025", reason: "[Queja] Web - Delivery no llegó", status: "analysis" },
    { code: "REQ-2025-402", dni: "11223344", date: "15/11/2025", reason: "[Reclamo] Tienda - Producto vencido", status: "resolved" },
    { code: "REQ-2025-101", dni: "45892100", date: "01/10/2025", reason: "[Reclamo] Web - Falta producto", status: "expired" }
];

// --- INICIALIZACIÓN ---
document.addEventListener('DOMContentLoaded', () => {
    // 1. Inicializar Categorías
    if(document.getElementById('categorySelect')) {
        updateCategories();
    }
    
    // 2. Inicializar "Base de Datos" en LocalStorage si está vacía
    if (!localStorage.getItem('casesHistory')) {
        console.log("Inicializando Data Demo...");
        localStorage.setItem('casesHistory', JSON.stringify(demoCases));
    }
});

// --- FUNCIONES DE NAVEGACIÓN (WIZARD) ---

// [RNF03] Avanzar Paso (Aquí estaba el problema posible)
function nextStep(step) {
    // Validaciones antes de avanzar del Paso 1 al 2
    if (step === 2) {
        if (!document.getElementById('dniInput').value) {
            alert("Por favor ingresa tu DNI primero.");
            return;
        }
    }

    // Ocultar paso actual
    document.getElementById(`step-${currentStep}`).classList.remove('active');
    
    // Actualizar indicador actual a "completado"
    const currentInd = document.getElementById(`ind-${currentStep}`);
    currentInd.classList.remove('step-active');
    currentInd.classList.add('step-completed');
    currentInd.innerHTML = '<i class="fas fa-check"></i>';

    // Avanzar contador
    currentStep = step;
    
    // Mostrar nuevo paso
    document.getElementById(`step-${currentStep}`).classList.add('active');

    // Actualizar nuevo indicador a "activo"
    const newInd = document.getElementById(`ind-${currentStep}`);
    newInd.classList.remove('step-inactive');
    newInd.classList.add('step-active');
    if(step < 4) newInd.innerText = step;
}

// Retroceder Paso
function prevStep(step) {
    document.getElementById(`step-${currentStep}`).classList.remove('active');
    
    const currentInd = document.getElementById(`ind-${currentStep}`);
    currentInd.classList.remove('step-active');
    currentInd.classList.add('step-inactive');
    
    currentStep = step;
    document.getElementById(`step-${currentStep}`).classList.add('active');

    const prevInd = document.getElementById(`ind-${currentStep}`);
    prevInd.classList.remove('step-completed');
    prevInd.classList.add('step-active');
    prevInd.innerText = step;
}

// --- GESTIÓN DE VISTAS (SPA) ---
function showView(viewName) {
    document.querySelectorAll('.view-section').forEach(el => el.classList.add('hidden'));
    document.getElementById(`view-${viewName}`).classList.remove('hidden');
    
    if(viewName === 'my-cases') loadMyCases();
    if(viewName === 'wizard') resetWizard();
}

function resetWizard() {
    currentStep = 1;
    document.getElementById('claimForm').reset();
    document.getElementById('filePreview').innerHTML = "";
    document.getElementById('userFoundMsg').classList.add('hidden');
    document.getElementById('store-selector').classList.remove('hidden');
    document.getElementById('order-selector').classList.add('hidden');
    document.getElementById('priorityHint').classList.add('hidden');
    
    document.querySelectorAll('.form-step').forEach(step => step.classList.remove('active'));
    document.getElementById('step-1').classList.add('active');
    
    const ind1 = document.getElementById('ind-1');
    ind1.className = "step-indicator step-active w-10 h-10 rounded-full flex items-center justify-center border-2 font-bold shadow-sm mx-auto";
    ind1.innerHTML = "1";
    for(let i=2; i<=4; i++) {
        const ind = document.getElementById(`ind-${i}`);
        ind.className = "step-indicator step-inactive w-10 h-10 rounded-full flex items-center justify-center border-2 font-bold shadow-sm mx-auto";
        if (i < 4) ind.innerText = i;
        if (i === 4) ind.innerHTML = '<i class="fas fa-check"></i>';
    }
    
    // Reiniciar categorías
    if(document.getElementById('categorySelect')) {
        const radioReclamo = document.querySelector('input[name="claimType"][value="reclamo"]');
        if(radioReclamo) radioReclamo.checked = true;
        updateCategories();
    }
}

// --- VALIDACIONES Y FORMULARIOS ---

// [RF02] Validación DNI
// [RF02] Validación DNI (Lógica: Cliente Existente vs Nuevo)
function validateUser() {
    const dniInput = document.getElementById('dniInput');
    const dni = dniInput.value;
    const btnText = document.getElementById('btnTextValidar');
    const loader = document.getElementById('loaderValidar');
    const successMsg = document.getElementById('userFoundMsg');
    
    // Referencias a los campos que vamos a bloquear/desbloquear
    const nameInput = document.getElementById('nameInput');
    const lastNameInput = document.getElementById('lastNameInput');
    const emailInput = document.getElementById('emailInput');
    const phoneInput = document.getElementById('phoneInput');

    if(dni.length < 8) { alert("Por favor ingresa un DNI válido (8 dígitos)"); return; }

    // UI Loading state
    btnText.classList.add('hidden');
    loader.classList.remove('hidden');
    successMsg.classList.add('hidden'); // Ocultar mensaje previo

    setTimeout(() => {
        loader.classList.add('hidden');
        btnText.classList.remove('hidden');
        btnText.innerText = "Validado";
        
        const foundUser = usersDB[dni]; // Buscar DNI exacto (Sin fallback)

        if (foundUser) {
            // --- CASO 1: CLIENTE REGISTRADO ---
            // Llenar datos
            nameInput.value = foundUser.name;
            lastNameInput.value = foundUser.lastName;
            emailInput.value = foundUser.email;
            phoneInput.value = foundUser.phone;
            
            // BLOQUEAR campos (Readonly) para que no se editen por error
            nameInput.setAttribute('readonly', true);
            lastNameInput.setAttribute('readonly', true);
            // Estilo visual de "bloqueado" (gris)
            nameInput.classList.add('bg-gray-50');
            lastNameInput.classList.add('bg-gray-50');

            // Mensaje de Éxito (Verde)
            successMsg.classList.remove('hidden');
            successMsg.className = "text-xs text-tottus-green mt-1 font-bold fade-in";
            successMsg.innerHTML = `<i class="fas fa-check-circle"></i> ¡Hola ${foundUser.name.split(' ')[0]}! Tus datos han sido cargados.`;
            
            userData = foundUser;
            
        } else {
            // --- CASO 2: CLIENTE NUEVO (NO ENCONTRADO) ---
            // Limpiar campos para permitir escritura limpia
            nameInput.value = "";
            lastNameInput.value = "";
            emailInput.value = "";
            phoneInput.value = "";
            
            // DESBLOQUEAR campos (Quitar Readonly)
            nameInput.removeAttribute('readonly');
            lastNameInput.removeAttribute('readonly');
            // Quitar estilo gris (hacerlos blancos editables)
            nameInput.classList.remove('bg-gray-50');
            lastNameInput.classList.remove('bg-gray-50');
            
            // Poner el foco en el nombre para invitar a escribir
            nameInput.focus();

            // Mensaje Creativo/Informativo (Azul)
            successMsg.classList.remove('hidden');
            successMsg.className = "text-xs text-blue-600 mt-1 font-bold fade-in";
            successMsg.innerHTML = `<i class="fas fa-user-plus"></i> DNI no registrado. ¡Bienvenido! Por favor completa tus datos manualmente.`;
            
            userData = {}; // Limpiar memoria
        }

    }, 800); 
}

// Actualizar Categorías (Reclamo vs Queja)
function updateCategories() {
    const type = document.querySelector('input[name="claimType"]:checked').value;
    const select = document.getElementById('categorySelect');
    
    select.innerHTML = "";
    
    claimOptions[type].forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.value;
        option.text = opt.text;
        select.appendChild(option);
    });

    document.getElementById('priorityHint').classList.add('hidden');
    select.dispatchEvent(new Event('change'));
}

// Listener para cambio de prioridad
document.getElementById('categorySelect').addEventListener('change', function(e) {
    const hint = document.getElementById('priorityHint');
    if(e.target.value === 'high') hint.classList.remove('hidden');
    else hint.classList.add('hidden');
});

// Toggle Tienda/Web
function toggleChannel(channel) {
    const storeSelector = document.getElementById('store-selector');
    const orderSelector = document.getElementById('order-selector');

    if (channel === 'tienda') {
        storeSelector.classList.remove('hidden');
        orderSelector.classList.add('hidden');
    } else {
        storeSelector.classList.add('hidden');
        orderSelector.classList.remove('hidden');
    }
}

// Manejo de Archivos
function handleFileSelect(input) {
    const previewDiv = document.getElementById('filePreview');
    previewDiv.innerHTML = "";
    
    if (input.files && input.files.length > 0) {
        if(input.files[0].size > 10 * 1024 * 1024) {
            alert("El archivo excede los 10MB");
            return;
        }

        for (let i = 0; i < input.files.length; i++) {
            const file = input.files[i];
            const fileEl = document.createElement('div');
            fileEl.className = "flex items-center justify-between bg-gray-50 p-3 rounded border border-gray-200 text-sm animate-pulse";
            fileEl.innerHTML = `
                <div class="flex items-center gap-2">
                    <i class="fas fa-file-image text-gray-500"></i>
                    <span class="text-gray-700 font-medium truncate max-w-xs">${file.name}</span>
                </div>
                <i class="fas fa-check-circle text-tottus-green"></i>
            `;
            previewDiv.appendChild(fileEl);
            setTimeout(() => fileEl.classList.remove('animate-pulse'), 500);
        }
    }
}

// [RF08] Enviar Formulario
function submitForm() {
    const date = new Date();
    const year = date.getFullYear();
    const randomNum = Math.floor(Math.random() * 10000); 
    const randomCode = `REQ-${year}-${randomNum}`;
    
    // Capturar datos
    const dniVal = document.getElementById('dniInput').value; 
    const tipoVal = document.querySelector('input[name="claimType"]:checked').value;
    const canalVal = document.querySelector('input[name="channel"]:checked').value;
    const select = document.getElementById('categorySelect');
    const motivoTexto = select.options[select.selectedIndex].text;
    
    const tipoTexto = tipoVal === 'queja' ? 'Queja' : 'Reclamo';
    const canalTexto = canalVal === 'tienda' ? 'Tienda' : 'Web';
    
    const titleEl = document.getElementById('successTitle');
    titleEl.innerText = tipoVal === 'queja' ? "¡Queja Registrada!" : "¡Reclamo Registrado!";
    
    document.getElementById('generatedCode').innerText = randomCode;
    document.getElementById('confirmEmail').innerText = document.getElementById('emailInput').value || "tu correo";

    const newCase = {
        code: randomCode,
        dni: dniVal, 
        date: date.toLocaleDateString(),
        reason: `[${tipoTexto}] ${canalTexto} - ${motivoTexto}`, 
        status: "pending"
    };
    
    let history = JSON.parse(localStorage.getItem('casesHistory')) || [];
    history.push(newCase);
    localStorage.setItem('casesHistory', JSON.stringify(history));

    nextStep(4);
}

// --- CONSULTAS Y TABLAS ---

// Cargar Mis Casos
function loadMyCases() {
    const tbody = document.getElementById('casesTableBody');
    tbody.innerHTML = "";

    // Combinar Demo + LocalStorage
    let userCases = JSON.parse(localStorage.getItem('casesHistory')) || [];
    
    // Usamos Map para evitar duplicados visuales entre demo y local
    const allCasesMap = new Map();
    demoCases.forEach(c => allCasesMap.set(c.code, c));
    userCases.forEach(c => allCasesMap.set(c.code, c));
    
    const allCases = Array.from(allCasesMap.values()).reverse();

    allCases.forEach(c => {
        let statusBadge = '';
        switch(c.status) {
            case 'pending': statusBadge = '<span class="badge badge-pending"><i class="fas fa-clock"></i> En Proceso</span>'; break;
            case 'analysis': statusBadge = '<span class="badge badge-analysis" style="background:#dbeafe;color:#1e40af"><i class="fas fa-search"></i> En Análisis</span>'; break;
            case 'resolved': statusBadge = '<span class="badge badge-resolved"><i class="fas fa-check"></i> Resuelto</span>'; break;
            case 'expired': statusBadge = '<span class="badge badge-expired"><i class="fas fa-times-circle"></i> Vencido</span>'; break;
            case 'rejected': statusBadge = '<span class="badge" style="background:#f3f4f6;color:#374151"><i class="fas fa-ban"></i> Rechazado</span>'; break;
            default: statusBadge = '<span class="badge badge-pending">Pendiente</span>';
        }

        const row = `
            <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                <td class="py-4 px-2 font-bold text-gray-700 text-xs md:text-sm">${c.code}</td>
                <td class="py-4 px-2 text-gray-500 text-xs md:text-sm">${c.date}</td>
                <td class="py-4 px-2 text-xs md:text-sm max-w-[150px] truncate" title="${c.reason}">${c.reason}</td>
                <td class="py-4 px-2">${statusBadge}</td>
                <td class="py-4 px-2">
                    <button class="text-tottus-green hover:text-white hover:bg-tottus-green border border-tottus-green px-3 py-1 rounded-full text-xs font-bold transition">
                        Ver
                    </button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

// Búsqueda Invitado


function searchGuestCase() {
    const inputDni = document.getElementById('guestDni').value;
    const inputCode = document.getElementById('guestCode').value.trim();
    const resultDiv = document.getElementById('guestResult');
    const errorDiv = document.getElementById('guestError');
    
    resultDiv.classList.add('hidden');
    if(errorDiv) errorDiv.classList.add('hidden');

    if(!inputDni || !inputCode) { alert("Completa ambos campos"); return; }

    const history = JSON.parse(localStorage.getItem('casesHistory')) || [];
    const allHistory = [...demoCases, ...history];
    const match = allHistory.find(c => c.code === inputCode && c.dni === inputDni);

    if (match) {
        let colorClass = "text-gray-500", bgClass = "bg-gray-500", statusLabel = "Desconocido", message = "", width = "0%", icon = '<i class="fas fa-question-circle"></i>';

        switch(match.status) {
            case 'pending': colorClass="text-yellow-600"; bgClass="bg-yellow-500"; statusLabel="En Proceso"; message="Tu caso ha sido recibido."; width="25%"; icon='<i class="fas fa-clock text-yellow-500"></i>'; break;
            case 'analysis': colorClass="text-blue-600"; bgClass="bg-blue-600"; statusLabel="En Análisis"; message="Un especialista revisa tu evidencia."; width="60%"; icon='<i class="fas fa-search text-blue-600"></i>'; break;
            case 'resolved': colorClass="text-green-600"; bgClass="bg-green-600"; statusLabel="Resuelto"; message="¡Tu caso ha sido aprobado!"; width="100%"; icon='<i class="fas fa-check-circle text-green-600"></i>'; break;
            
            
            case 'rejected': 
                colorClass="text-gray-600"; 
                bgClass="bg-gray-600"; 
                statusLabel="Rechazado"; // Antes decía Cerrado/Rechazado
                message="El caso no procedió. Revisa tu correo."; 
                width="100%"; 
                icon='<i class="fas fa-ban text-gray-600"></i>'; 
                break;
            // --------------------------

            case 'expired': colorClass="text-red-600"; bgClass="bg-red-600"; statusLabel="Vencido"; message="El tiempo expiró. Escalando."; width="100%"; icon='<i class="fas fa-exclamation-circle text-red-600"></i>'; break;
        }

        document.getElementById('guestStatusIcon').innerHTML = icon;
        const statusText = document.getElementById('guestStatusText');
        statusText.innerText = statusLabel;
        statusText.className = `font-bold ${colorClass}`;
        document.getElementById('guestMessage').innerText = message;
        const bar = document.getElementById('guestProgressBar');
        bar.className = `${bgClass} h-2.5 rounded-full transition-all duration-1000`;
        setTimeout(() => { bar.style.width = width; }, 100);

        if(document.getElementById('guestDate')) document.getElementById('guestDate').innerText = match.date;
        if(document.getElementById('guestReason')) document.getElementById('guestReason').innerText = match.reason;

        resultDiv.classList.remove('hidden');
    } else {
        if(errorDiv) errorDiv.classList.remove('hidden');
        else alert("No se encontró el caso con esos datos.");
    }
}


// --- MENÚS Y ACCESIBILIDAD ---

function toggleMobileMenu() {
    const nav = document.getElementById('main-nav');
    if (window.innerWidth < 768) { 
        nav.classList.toggle('hidden');
    }
}
document.getElementById('mobile-menu-btn').addEventListener('click', toggleMobileMenu);

// Accesibilidad
function toggleAccMenu() {
    const menu = document.getElementById('acc-menu');
    const triggerBtn = document.getElementById('acc-trigger-btn');
    menu.classList.toggle('hidden');
    if (!menu.classList.contains('hidden')) {
        triggerBtn.setAttribute('aria-expanded', 'true');
        document.getElementById('btn-acc-contrast').focus(); 
    } else {
        triggerBtn.setAttribute('aria-expanded', 'false');
        triggerBtn.focus(); 
    }
}

function toggleAccFeature(feature) {
    const body = document.body;
    const html = document.documentElement; 
    const btn = document.getElementById(`btn-acc-${feature}`);
    let isActive = false;

    switch(feature) {
        case 'contrast': isActive = body.classList.toggle('acc-contrast'); break;
        case 'text': isActive = html.classList.toggle('acc-text-big'); break;
        case 'links': isActive = body.classList.toggle('acc-links'); break;
        case 'spacing': isActive = body.classList.toggle('acc-spacing'); break;
        case 'noani': isActive = body.classList.toggle('acc-no-ani'); break;
        case 'grayscale': isActive = body.classList.toggle('acc-grayscale'); break;
    }

    if (isActive) {
        btn.classList.add('acc-btn-active');
        btn.setAttribute('aria-pressed', 'true');
    } else {
        btn.classList.remove('acc-btn-active');
        btn.setAttribute('aria-pressed', 'false');
    }
}

function resetAccessibility() {
    document.body.classList.remove('acc-contrast', 'acc-links', 'acc-spacing', 'acc-no-ani', 'acc-grayscale');
    document.documentElement.classList.remove('acc-text-big');
    document.querySelectorAll('#acc-menu button[id^="btn-acc-"]').forEach(btn => {
        btn.classList.remove('acc-btn-active');
        btn.setAttribute('aria-pressed', 'false');
    });
}

document.addEventListener('click', function(event) {
    const menu = document.getElementById('acc-menu');
    const triggerBtn = document.getElementById('acc-trigger-btn');
    if (menu && !menu.classList.contains('hidden') && !menu.contains(event.target) && !triggerBtn.contains(event.target)) {
        toggleAccMenu();
    }
});