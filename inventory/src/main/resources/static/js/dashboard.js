API.requireAuth();

let revenueChart;

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("refreshDashboard").addEventListener("click", loadDashboard);
    loadDashboard();
});

async function loadDashboard() {
    try {
        const stats = await API.request("/api/dashboard/stats");
        document.getElementById("totalProducts").textContent = stats.totalProducts;
        document.getElementById("totalSales").textContent = stats.totalSales;
        document.getElementById("totalCustomers").textContent = stats.totalCustomers;
        document.getElementById("lowStockCount").textContent = stats.lowStockCount;
        renderTransactions(stats.recentTransactions || []);
        renderRevenueChart(stats.revenueLabels || [], stats.revenueData || []);
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function renderTransactions(transactions) {
    const body = document.getElementById("recentTransactions");
    if (!transactions.length) {
        body.innerHTML = UI.emptyRow(4, "No recent transactions");
        return;
    }

    body.innerHTML = transactions.map((sale) => `
        <tr>
            <td>#${sale.saleId}</td>
            <td>${API.escapeHtml(sale.customerName)}</td>
            <td>${API.money(sale.totalAmount)}</td>
            <td>${API.dateTime(sale.saleDate)}</td>
        </tr>
    `).join("");
}

function renderRevenueChart(labels, values) {
    const canvas = document.getElementById("revenueChart");
    if (!window.Chart || !canvas) {
        return;
    }

    if (revenueChart) {
        revenueChart.destroy();
    }

    revenueChart = new Chart(canvas, {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Revenue",
                data: values.map(Number),
                borderColor: "#1769e0",
                backgroundColor: "rgba(23, 105, 224, 0.14)",
                fill: true,
                tension: 0.36,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: (value) => API.money(value)
                    }
                }
            }
        }
    });
}