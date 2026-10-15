/**
 * IRONCORE — Chart.js Custom Visualizations
 * Dark Obsidian + Electric Lime aesthetic
 */

function initIronCharts(memberLabels, memberData, revenueLabels, revenueData) {
    // 1. Revenue Chart
    const revenueCanvas = document.getElementById('revenueChart');
    if (revenueCanvas && window.Chart) {
        const ctx = revenueCanvas.getContext('2d');
        const gradient = ctx.createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(204, 255, 0, 0.35)');
        gradient.addColorStop(1, 'rgba(204, 255, 0, 0.0)');

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: revenueLabels,
                datasets: [{
                    label: 'Monthly Revenue (₹)',
                    data: revenueData,
                    borderColor: '#CCFF00',
                    borderWidth: 2.5,
                    pointBackgroundColor: '#CCFF00',
                    pointBorderColor: '#080B10',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    fill: true,
                    backgroundColor: gradient,
                    tension: 0.35
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#151C2C',
                        titleColor: '#F8FAFC',
                        bodyColor: '#CCFF00',
                        borderColor: 'rgba(255, 255, 255, 0.1)',
                        borderWidth: 1,
                        padding: 12,
                        displayColors: false,
                        callbacks: {
                            label: function(context) {
                                return '₹ ' + Number(context.parsed.y).toLocaleString('en-IN');
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { color: 'rgba(255, 255, 255, 0.04)' },
                        ticks: { color: '#64748B', font: { family: 'Plus Jakarta Sans', size: 11 } }
                    },
                    y: {
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: {
                            color: '#64748B',
                            font: { family: 'Plus Jakarta Sans', size: 11 },
                            callback: function(val) {
                                return '₹' + (val >= 1000 ? (val / 1000) + 'k' : val);
                            }
                        }
                    }
                }
            }
        });
    }

    // 2. Member Registrations Bar Chart
    const growthCanvas = document.getElementById('growthChart');
    if (growthCanvas && window.Chart) {
        const ctx2 = growthCanvas.getContext('2d');
        new Chart(ctx2, {
            type: 'bar',
            data: {
                labels: memberLabels,
                datasets: [{
                    label: 'New Registrations',
                    data: memberData,
                    backgroundColor: '#10B981',
                    borderRadius: 6,
                    barThickness: 24,
                    hoverBackgroundColor: '#34D399'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#151C2C',
                        titleColor: '#F8FAFC',
                        bodyColor: '#10B981',
                        borderColor: 'rgba(255, 255, 255, 0.1)',
                        borderWidth: 1,
                        padding: 10,
                        displayColors: false
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { color: '#64748B', font: { family: 'Plus Jakarta Sans', size: 11 } }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: { stepSize: 1, color: '#64748B', font: { family: 'Plus Jakarta Sans', size: 11 } }
                    }
                }
            }
        });
    }
}
