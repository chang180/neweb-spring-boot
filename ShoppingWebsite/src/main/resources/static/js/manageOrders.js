document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('.update-status-button').forEach(button => {
        button.addEventListener('click', () => {
            const orderId = button.getAttribute('data-order-id');
            const selectElement = document.querySelector(`select[data-order-id="${orderId}"]`);
            const status = selectElement.value;

            fetch(`/orders/updateStatus?orderId=${orderId}&status=${status}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    alert('Order status updated');
                } else {
                    alert('Failed to update order status');
                }
            })
            .catch(error => {
                console.error('Error updating order status:', error);
                alert('Error updating order status');
            });
        });
    });

    document.getElementById('backToHomeButton').addEventListener('click', () => {
        window.location.href = '/';
    });
});
