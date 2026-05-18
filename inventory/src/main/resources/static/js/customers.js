API.requireAuth();

let customers = [];

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("openCustomerModal").addEventListener("click", () => {
        document.getElementById("customerForm").reset();
        UI.openModal("customerModal");
    });
    document.getElementById("customerForm").addEventListener("submit", saveCustomer);
    document.getElementById("customerTableBody").addEventListener("click", handleCustomerAction);

    let searchTimer;
    document.getElementById("customerSearch").addEventListener("input", (event) => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => loadCustomers(event.target.value), 220);
    });

    loadCustomers();
});

async function loadCustomers(query = "") {
    try {
        const endpoint = query.trim()
            ? `/api/customers/search?query=${encodeURIComponent(query.trim())}`
            : "/api/customers";
        customers = await API.request(endpoint);
        renderCustomers(customers);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function renderCustomers(items) {
    const body = document.getElementById("customerTableBody");
    if (!items.length) {
        body.innerHTML = UI.emptyRow(5, "No customers found");
        return;
    }

    body.innerHTML = items.map((customer) => `
        <tr>
            <td>${customer.customerId}</td>
            <td>${API.escapeHtml(customer.name)}</td>
            <td>${API.escapeHtml(customer.phone || "-")}</td>
            <td>${API.escapeHtml(customer.address || "-")}</td>
            <td>
                <div class="actions">
                    <button class="btn btn-secondary" type="button" data-action="history" data-id="${customer.customerId}">
                        <i data-lucide="history"></i>
                        History
                    </button>
                    <button class="btn btn-danger btn-icon" type="button" title="Delete" data-action="delete" data-id="${customer.customerId}">
                        <i data-lucide="trash-2"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join("");
    if (window.lucide) {
        window.lucide.createIcons();
    }
}

async function saveCustomer(event) {
    event.preventDefault();
    const button = document.getElementById("saveCustomerButton");
    const payload = {
        name: document.getElementById("customerNameInput").value.trim(),
        phone: document.getElementById("customerPhoneInput").value.trim(),
        address: document.getElementById("customerAddressInput").value.trim()
    };

    try {
        UI.setLoading(button, true, "Saving");
        await API.request("/api/customers", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        UI.closeModal("customerModal");
        UI.toast("Customer added");
        await loadCustomers(document.getElementById("customerSearch").value);
    } catch (error) {
        UI.toast(error.message, "error");
    } finally {
        UI.setLoading(button, false);
    }
}

async function handleCustomerAction(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }
    const id = Number(button.dataset.id);
    const customer = customers.find((item) => item.customerId === id);

    if (button.dataset.action === "delete" && customer) {
        if (!confirm(`Delete ${customer.name}?`)) {
            return;
        }
        try {
            await API.request(`/api/customers/${id}`, { method: "DELETE" });
            UI.toast("Customer deleted");
            await loadCustomers(document.getElementById("customerSearch").value);
        } catch (error) {
            UI.toast(error.message, "error");
        }
    }

    if (button.dataset.action === "history" && customer) {
        await loadHistory(customer);
    }
}

async function loadHistory(customer) {
    try {
        const history = await API.request(`/api/customers/${customer.customerId}/history`);
        document.getElementById("historyTitle").textContent = `${customer.name} Purchase History`;
        const body = document.getElementById("historyTableBody");
        if (!history.length) {
            body.innerHTML = UI.emptyRow(3, "No purchase history found");
        } else {
            body.innerHTML = history.map((sale) => `
                <tr>
                    <td>#${sale.saleId}</td>
                    <td>${API.money(sale.totalAmount)}</td>
                    <td>${API.dateTime(sale.saleDate)}</td>
                </tr>
            `).join("");
        }
        UI.openModal("historyModal");
    } catch (error) {
        UI.toast(error.message, "error");
    }
}