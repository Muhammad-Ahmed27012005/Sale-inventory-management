API.requireAuth();

let reportsChart;
let latestDaily;
let latestMonthly;
let latestTopProducts = [];

document.addEventListener("DOMContentLoaded", () => {
    const today = new Date();
    document.getElementById("dailyDate").value = today.toISOString().slice(0, 10);
    document.getElementById("monthlyDate").value = today.toISOString().slice(0, 7);
    document.getElementById("dailyDate").addEventListener("change", loadReports);
    document.getElementById("monthlyDate").addEventListener("change", loadReports);
    document.getElementById("refreshReports").addEventListener("click", loadReports);
    document.getElementById("exportReport").addEventListener("click", exportReport);
    loadReports();
});

async function loadReports() {
    try {
        const dailyDate = document.getElementById("dailyDate").value;
        const monthlyDate = document.getElementById("monthlyDate").value;
        const [daily, monthly, topProducts] = await Promise.all([
            API.request(`/api/reports/daily?date=${encodeURIComponent(dailyDate)}`),
            API.request(`/api/reports/monthly?month=${encodeURIComponent(monthlyDate)}`),
            API.request("/api/reports/top-products")
        ]);

        latestDaily = daily;
        latestMonthly = monthly;
        latestTopProducts = topProducts || [];
        renderReportCards();
        renderTopProducts();
        renderReportsChart();
    } catch (error) {
        UI.toast(error.message, "error");
    }
}

function renderReportCards() {
    document.getElementById("dailySalesCount").textContent = `${latestDaily.totalSales} sales`;
    document.getElementById("dailyRevenue").textContent = API.money(latestDaily.totalRevenue);
    document.getElementById("monthlySalesCount").textContent = `${latestMonthly.totalSales} sales`;
    document.getElementById("monthlyRevenue").textContent = API.money(latestMonthly.totalRevenue);
}

function renderTopProducts() {
    const body = document.getElementById("topProductsTableBody");
    if (!latestTopProducts.length) {
        body.innerHTML = UI.emptyRow(4, "No best-selling products yet");
        return;
    }

    body.innerHTML = latestTopProducts.map((product, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>${API.escapeHtml(product.productName)}</td>
            <td>${product.quantitySold}</td>
            <td>${API.money(product.revenue)}</td>
        </tr>
    `).join("");
}

function renderReportsChart() {
    const canvas = document.getElementById("reportsChart");
    if (!window.Chart || !canvas) {
        return;
    }
    if (reportsChart) {
        reportsChart.destroy();
    }

    reportsChart = new Chart(canvas, {
        type: "bar",
        data: {
            labels: ["Daily Revenue", "Monthly Revenue"],
            datasets: [{
                data: [Number(latestDaily.totalRevenue || 0), Number(latestMonthly.totalRevenue || 0)],
                backgroundColor: ["#1769e0", "#168a57"],
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { callback: (value) => API.money(value) }
                }
            }
        }
    });
}

function exportReport() {
    const rows = [
        ["Report", "Period", "Total Sales", "Total Revenue"],
        ["Daily", latestDaily?.period || "", latestDaily?.totalSales || 0, latestDaily?.totalRevenue || 0],
        ["Monthly", latestMonthly?.period || "", latestMonthly?.totalSales || 0, latestMonthly?.totalRevenue || 0],
        [],
        ["Rank", "Product", "Quantity Sold", "Revenue"],
        ...latestTopProducts.map((product, index) => [
            index + 1,
            product.productName,
            product.quantitySold,
            product.revenue
        ])
    ];
    const csv = rows.map((row) => row.map((value) => `"${String(value ?? "").replaceAll('"', '""')}"`).join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "sales-inventory-report.csv";
    link.click();
    URL.revokeObjectURL(link.href);
}