/**
 * IRONCORE — Smart Gym Management System
 * Core UI Scripting & Micro-Interactions
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Lucide Icons
    if (window.lucide) {
        window.lucide.createIcons();
    }

    // 2. Mobile Sidebar Toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    const appSidebar = document.querySelector('.app-sidebar');
    if (sidebarToggle && appSidebar) {
        sidebarToggle.addEventListener('click', () => {
            appSidebar.classList.toggle('open');
        });
    }

    // Close sidebar on background click for mobile
    document.addEventListener('click', (e) => {
        if (window.innerWidth <= 1024 && appSidebar && appSidebar.classList.contains('open')) {
            if (!appSidebar.contains(e.target) && !sidebarToggle.contains(e.target)) {
                appSidebar.classList.remove('open');
            }
        }
    });

    // 3. Modal System
    window.openModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('open');
            document.body.style.overflow = 'hidden';
            if (window.lucide) window.lucide.createIcons();
        }
    };

    window.closeModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('open');
            document.body.style.overflow = '';
        }
    };

    // Close modal on escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.iron-modal-backdrop.open').forEach(modal => {
                modal.classList.remove('open');
            });
            document.body.style.overflow = '';
        }
    });

    // Close modal on backdrop click
    document.querySelectorAll('.iron-modal-backdrop').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('open');
                document.body.style.overflow = '';
            }
        });
    });

    // 4. Quick Search Focus Shortcut (Ctrl+K or Command+K)
    document.addEventListener('keydown', (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
            e.preventDefault();
            const searchInput = document.getElementById('globalSearchInput');
            if (searchInput) {
                searchInput.focus();
                searchInput.select();
            }
        }
    });

    // 5. Auto dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.iron-alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});
