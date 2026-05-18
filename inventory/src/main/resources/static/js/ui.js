const UI = (() => {
    function toast(message, type = "success") {
        let container = document.querySelector(".toast-container");
        if (!container) {
            container = document.createElement("div");
            container.className = "toast-container";
            document.body.appendChild(container);
        }

        const item = document.createElement("div");
        item.className = `toast ${type}`;
        item.textContent = message;
        container.appendChild(item);
        setTimeout(() => item.remove(), 3600);
    }

    function openModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.add("open");
        }
    }

    function closeModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.remove("open");
        }
    }

    function setLoading(button, isLoading, label = "Processing") {
        if (!button) {
            return;
        }
        if (isLoading) {
            button.dataset.originalText = button.innerHTML;
            button.disabled = true;
            button.textContent = label;
        } else {
            button.disabled = false;
            if (button.dataset.originalText) {
                button.innerHTML = button.dataset.originalText;
            }
            if (window.lucide) {
                window.lucide.createIcons();
            }
        }
    }

    function emptyRow(colspan, message) {
        return `<tr><td colspan="${colspan}" class="empty-state">${API.escapeHtml(message)}</td></tr>`;
    }

    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll("[data-modal-open]").forEach((button) => {
            button.addEventListener("click", () => openModal(button.dataset.modalOpen));
        });

        document.querySelectorAll("[data-modal-close]").forEach((button) => {
            button.addEventListener("click", () => closeModal(button.dataset.modalClose));
        });

        document.querySelectorAll(".modal-backdrop").forEach((backdrop) => {
            backdrop.addEventListener("click", (event) => {
                if (event.target === backdrop) {
                    backdrop.classList.remove("open");
                }
            });
        });
    });

    return {
        toast,
        openModal,
        closeModal,
        setLoading,
        emptyRow
    };
})();