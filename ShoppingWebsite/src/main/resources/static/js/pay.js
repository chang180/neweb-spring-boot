let memberId;
let userEmail;

// Fetch current user information on page load
fetch('/member/currentUser')
    .then(response => response.json())
    .then(data => {
        memberId = data.id; // 假設後端返回的數據格式包含用戶ID
        userEmail = data.email; // 假設後端返回的數據格式包含用戶的電子郵件
        console.log(`Current member ID: ${memberId}, email: ${userEmail}`);
        updateCartModal();
    })
    .catch(error => {
        console.error('Error fetching current user:', error);
    });

// Checkout button
document.getElementById("checkoutButton").addEventListener("click", function() {
    fetch(`/orders/checkout?memberId=${memberId}&email=${userEmail}`, {
        method: "POST"
    }).then(response => {
        if (response.ok) {
            return response.text();
        } else {
            throw new Error("Checkout failed");
        }
    }).then(url => {
        window.location.href = url; // 跳轉到訂單詳細頁面
    }).catch(error => {
        alert(error.message);
    });
});
