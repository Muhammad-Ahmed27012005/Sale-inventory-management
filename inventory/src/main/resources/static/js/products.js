API.requireAuth();

let products = [];

document.addEventListener("DOMContentLoaded", () => {
    const search = document.getElementById("productSearch");
    document.getElementById("openProductModal").addEventListener("click", openCreateModal);
    document.getElementById("productForm").addEventListener("submit", saveProduct);
    document.getElementById("undoDeleteButton").addEventListener("click", undoDelete);
    document.getElementById("productTableBody").addEventListener("click", handleTableAction);

    let searchTimer;
    search.addEventListener("input", () => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => loadProducts(search.value), 220);
    });

    loadProducts();
});

async function loadProducts(query = "") {
    try {
        const endpoint = query.trim()
            ? `/api/products/search?query=${encodeURIComponent(query.trim())}`
            : "/api/products";
        products = await API.request(endpoint);
        renderProducts(products);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function renderProducts(items) {
    const body = document.getElementById("productTableBody");
    if (!items.length) {
        body.innerHTML = UI.emptyRow(6, "No products found");
        return;
    }

    body.innerHTML = items.map((product) => `
        <tr>
            <td>${product.productId}</td>
            <td>${API.escapeHtml(product.name)}</td>
            <td>${API.escapeHtml(product.category)}</td>
            <td>${API.money(product.price)}</td>
            <td>${product.quantity}</td>
            <td>
                <div class="actions">
                    <button class="btn btn-secondary btn-icon" type="button" title="Edit" data-action="edit" data-id="${product.productId}">
                        <i data-lucide="pencil"></i>
                    </button>
                    <button class="btn btn-danger btn-icon" type="button" title="Delete" data-action="delete" data-id="${product.productId}">
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

function openCreateModal() {
    document.getElementById("productModalTitle").textContent = "Add Product";
    document.getElementById("productForm").reset();
    document.getElementById("productId").value = "";
    UI.openModal("productModal");
}

function openEditModal(product) {
    document.getElementById("productModalTitle").textContent = "Edit Product";
    document.getElementById("productId").value = product.productId;
    document.getElementById("productName").value = product.name;
    document.getElementById("productCategory").value = product.category;
    document.getElementById("productPrice").value = product.price;
    document.getElementById("productQuantity").value = product.quantity;
    UI.openModal("productModal");
}

async function saveProduct(event) {
    event.preventDefault();
    const button = document.getElementById("saveProductButton");
    const id = document.getElementById("productId").value;
    const payload = {
        name: document.getElementById("productName").value.trim(),
        category: document.getElementById("productCategory").value.trim(),
        price: Number(document.getElementById("productPrice").value),
        quantity: Number(document.getElementById("productQuantity").value)
    };

    try {
        UI.setLoading(button, true, "Saving");
        await API.request(id ? `/api/products/${id}` : "/api/products", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        UI.closeModal("productModal");
        UI.toast(id ? "Product updated" : "Product added");
        await loadProducts(document.getElementById("productSearch").value);
    } catch (error) {
        UI.toast(error.message, "error");
    } finally {
        UI.setLoading(button, false);
    }
}

async function handleTableAction(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }
    const id = Number(button.dataset.id);
    const product = products.find((item) => item.productId === id);
    if (button.dataset.action === "edit" && product) {
        openEditModal(product);
    }
    if (button.dataset.action === "delete" && product) {
        await deleteProduct(product);
    }
}

async function deleteProduct(product) {
    if (!confirm(`Delete ${product.name}?`)) {
        return;
    }
    try {
        await API.request(`/api/products/${product.productId}`, { method: "DELETE" });
        UI.toast("Product deleted. Use Undo Delete to restore it.");
        await loadProducts(document.getElementById("productSearch").value);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

async function undoDelete() {
    try {
        await API.request("/api/products/undo-delete", { method: "POST" });
        UI.toast("Last deleted product restored");
        await loadProducts(document.getElementById("productSearch").value);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}