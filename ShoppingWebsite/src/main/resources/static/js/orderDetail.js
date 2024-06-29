document.addEventListener("DOMContentLoaded", () => {
    const backToOrdersButton = document.getElementById('backToOrdersButton');
    const cancelOrderButton = document.getElementById('cancelOrderButton');

    if (backToOrdersButton) {
        backToOrdersButton.onclick = () => {
            window.location.href = '/orders?memberId=' + memberId;
        };
    }
    
    if (cancelOrderButton) {
        cancelOrderButton.onclick = () => {
            fetch('/orders/cancel?orderId=' + orderId, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    window.location.href = '/orders?memberId=' + memberId;
                } else {
                    alert('Failed to cancel order');
                }
            });
        };
    }
});
