API.requireAuth();

let saleProducts = [];
let cart = [];

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("cartForm").addEventListener("submit", addCartItem);
    document.getElementById("cartTableBody").addEventListener("click", removeCartItem);
    document.getElementById("generateInvoice").addEventListener("click", generateInvoice);
    document.getElementById("reloadSales").addEventListener("click", loadSales);
    loadProducts();
    loadSales();
    renderCart();
});

async function loadProducts() {
    try {
        saleProducts = await API.request("/api/products");
        const select = document.getElementById("saleProduct");
        const available = saleProducts.filter((product) => product.quantity > 0);
        select.innerHTML = available.length
            ? available.map((product) => `<option value="${product.productId}">${API.escapeHtml(product.name)} - ${API.money(product.price)} (${product.quantity} in stock)</option>`).join("")
            : "<option value=''>No products in stock</option>";
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

async function loadSales() {
    try {
        const sales = await API.request("/api/sales");
        renderSales(sales || []);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function addCartItem(event) {
    event.preventDefault();
    const productId = Number(document.getElementById("saleProduct").value);
    const quantity = Number(document.getElementById("saleQuantity").value);
    const product = saleProducts.find((item) => item.productId === productId);

    if (!product || quantity < 1) {
        UI.toast("Select a product and valid quantity", "error");
        return;
    }

    const existing = cart.find((item) => item.productId === productId);
    const requestedQuantity = (existing?.quantity || 0) + quantity;
    if (requestedQuantity > product.quantity) {
        UI.toast(`Only ${product.quantity} units available for ${product.name}`, "error");
        return;
    }

    if (existing) {
        existing.quantity = requestedQuantity;
        existing.lineTotal = existing.quantity * existing.price;
    } else {
        cart.push({
            productId: product.productId,
            name: product.name,
            quantity,
            price: Number(product.price),
            lineTotal: quantity * Number(product.price)
        });
    }

    document.getElementById("saleQuantity").value = 1;
    renderCart();
}

function removeCartItem(event) {
    const button = event.target.closest("button[data-remove]");
    if (!button) {
        return;
    }
    cart = cart.filter((item) => item.productId !== Number(button.dataset.remove));
    renderCart();
}

function renderCart() {
    const body = document.getElementById("cartTableBody");
    const total = cart.reduce((sum, item) => sum + item.lineTotal, 0);
    document.getElementById("cartTotal").textContent = API.money(total);

    if (!cart.length) {
        body.innerHTML = UI.emptyRow(5, "Cart is empty");
        return;
    }

    body.innerHTML = cart.map((item) => `
        <tr>
            <td>${API.escapeHtml(item.name)}</td>
            <td>${item.quantity}</td>
            <td>${API.money(item.price)}</td>
            <td>${API.money(item.lineTotal)}</td>
            <td><button class="btn btn-danger btn-icon" type="button" data-remove="${item.productId}" title="Remove"><i data-lucide="x"></i></button></td>
        </tr>
    `).join("");
    if (window.lucide) {
        window.lucide.createIcons();
    }
}

async function generateInvoice() {
    const button = document.getElementById("generateInvoice");
    const customerName = document.getElementById("customerName").value.trim();
    if (!customerName) {
        UI.toast("Customer name is required", "error");
        return;
    }
    if (!cart.length) {
        UI.toast("Add at least one product to the cart", "error");
        return;
    }

    try {
        UI.setLoading(button, true, "Saving invoice");
        const sale = await API.request("/api/sales", {
            method: "POST",
            body: JSON.stringify({
                customerName,
                items: cart.map((item) => ({ productId: item.productId, quantity: item.quantity }))
            })
        });
        UI.toast("Invoice generated");
        showInvoice(sale);
        cart = [];
        document.getElementById("customerName").value = "";
        renderCart();
        await loadProducts();
        await loadSales();
    } catch (error) {
        UI.toast(error.message, "error");
    } finally {
        UI.setLoading(button, false);
    }
}

function showInvoice(sale) {
    const body = document.getElementById("invoiceBody");
    body.innerHTML = `
        <div class="invoice-line"><strong>Invoice</strong><span>#${sale.saleId}</span></div>
        <div class="invoice-line"><strong>Customer</strong><span>${API.escapeHtml(sale.customerName)}</span></div>
        <div class="invoice-line"><strong>Date</strong><span>${API.dateTime(sale.saleDate)}</span></div>
        ${(sale.items || []).map((item) => `
            <div class="invoice-line">
                <span>${API.escapeHtml(item.productName)} x ${item.quantity}</span>
                <strong>${API.money(item.lineTotal)}</strong>
            </div>
        `).join("")}
        <div class="total-strip"><span>Total</span><strong>${API.money(sale.totalAmount)}</strong></div>
    `;
    UI.openModal("invoiceModal");
}

function renderSales(sales) {
    const body = document.getElementById("salesTableBody");
    if (!sales.length) {
        body.innerHTML = UI.emptyRow(4, "No sales records found");
        return;
    }

    body.innerHTML = sales.map((sale) => `
        <tr>
            <td>#${sale.saleId}</td>
            <td>${API.escapeHtml(sale.customerName)}</td>
            <td>${API.money(sale.totalAmount)}</td>
            <td>${API.dateTime(sale.saleDate)}</td>
        </tr>
    `).join("");
}