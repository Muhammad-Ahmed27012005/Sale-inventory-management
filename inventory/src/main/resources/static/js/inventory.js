API.requireAuth();

let inventory = [];

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("showAllStock").addEventListener("click", () => loadInventory(false));
    document.getElementById("showLowStock").addEventListener("click", () => loadInventory(true));
    document.getElementById("inventoryTableBody").addEventListener("click", handleInventoryAction);
    document.getElementById("stockForm").addEventListener("submit", updateStock);
    loadInventory(false);
});

async function loadInventory(lowStockOnly) {
    try {
        inventory = await API.request(lowStockOnly ? "/api/inventory/low-stock" : "/api/inventory");
        document.getElementById("inventoryTitle").textContent = lowStockOnly ? "Low Stock Alerts" : "Current Stock";
        document.getElementById("inventoryCount").textContent = `${inventory.length} items`;
        renderInventory(inventory);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function renderInventory(items) {
    const body = document.getElementById("inventoryTableBody");
    if (!items.length) {
        body.innerHTML = UI.emptyRow(6, "No inventory records found");
        return;
    }

    body.innerHTML = items.map((item) => `
        <tr>
            <td>${API.escapeHtml(item.name)}</td>
            <td>${API.escapeHtml(item.category)}</td>
            <td>${API.money(item.price)}</td>
            <td>${item.quantity}</td>
            <td>${statusBadge(item.status)}</td>
            <td>
                <button class="btn btn-secondary" type="button" data-action="stock" data-id="${item.productId}">
                    <i data-lucide="sliders-horizontal"></i>
                    Update
                </button>
            </td>
        </tr>
    `).join("");
    if (window.lucide) {
        window.lucide.createIcons();
    }
}

function statusBadge(status) {
    const classes = {
        IN_STOCK: "badge-success",
        LOW_STOCK: "badge-warning",
        OUT_OF_STOCK: "badge-danger"
    };
    const labels = {
        IN_STOCK: "In Stock",
        LOW_STOCK: "Low Stock",
        OUT_OF_STOCK: "Out of Stock"
    };
    return `<span class="badge ${classes[status] || "badge-warning"}">${labels[status] || status}</span>`;
}

function handleInventoryAction(event) {
    const button = event.target.closest("button[data-action='stock']");
    if (!button) {
        return;
    }
    const item = inventory.find((entry) => entry.productId === Number(button.dataset.id));
    if (!item) {
        return;
    }
    document.getElementById("stockProductId").value = item.productId;
    document.getElementById("stockProductName").value = item.name;
    document.getElementById("stockQuantity").value = item.quantity;
    UI.openModal("stockModal");
}

async function updateStock(event) {
    event.preventDefault();
    const button = document.getElementById("saveStockButton");
    const id = document.getElementById("stockProductId").value;
    const quantity = Number(document.getElementById("stockQuantity").value);

    try {
        UI.setLoading(button, true, "Updating");
        await API.request(`/api/inventory/update/${id}`, {
            method: "PUT",
            body: JSON.stringify({ quantity })
        });
        UI.closeModal("stockModal");
        UI.toast("Stock updated");
        await loadInventory(false);
    } catch (error) {
        UI.toast(error.message, "error");
    } finally {
        UI.setLoading(button, false);
    }
}