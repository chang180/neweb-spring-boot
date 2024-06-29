let memberId;

// Fetch current user information on page load
fetch('/auth/currentUser')
    .then(response => response.json())
    .then(data => {
        memberId = data.id; // 假設後端返回的數據格式包含用戶ID
        console.log(`Current member ID: ${memberId}`);
        updateCartModal();
    })
    .catch(error => {
        console.error('Error fetching current user:', error);
    });

function logout() {
    fetch('/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            window.location.href = '/';
        } else {
            alert('Logout failed');
        }
    });
}

// Get the product modal and cart modal
var productModal = document.getElementById("productModal");
var cartModal = document.getElementById("cartModal");

// Get the <span> elements that close the modals
var productSpan = document.getElementsByClassName("close")[0];
var cartSpan = document.getElementsByClassName("close-cart")[0];

// When the user clicks on <span> (x), close the modals
productSpan.onclick = function() {
    productModal.style.display = "none";
}
cartSpan.onclick = function() {
    cartModal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == productModal) {
        productModal.style.display = "none";
    } else if (event.target == cartModal) {
        cartModal.style.display = "none";
    }
}

// Add event listeners to details buttons to open the modal with product details
document.querySelectorAll('.details-button').forEach(button => {
    button.addEventListener('click', () => {
        const productId = button.getAttribute('data-product-id');
        console.log(`Fetching details for product ID: ${productId}`);

        // Reset quantity input field
        document.getElementById('modalProductQuantity').value = 1;
        document.getElementById('modalRemoveFromCartButton').style.display = "none";

        // Fetch product details using productId
        fetch(`/api/products/${productId}`)
            .then(response => response.json())
            .then(product => {
                console.log('Product details:', product);
                document.getElementById('modalProductName').textContent = product.name;
                document.getElementById('modalProductDescription').textContent = product.description;
                document.getElementById('modalProductPrice').textContent = product.price;
                document.getElementById('modalProductStock').textContent = product.stock;
                document.getElementById('modalProductCategory').textContent = product.category;

                // Check if the product is already in the cart
                fetch(`/cart/check?productId=${productId}&memberId=${memberId}`)
                    .then(response => response.json())
                    .then(cartItem => {
                        console.log('Cart item:', cartItem);
                        if (cartItem) {
                            document.getElementById('modalProductQuantity').value = cartItem.quantity;
                            document.getElementById('modalRemoveFromCartButton').style.display = "block";
                        } else {
                            document.getElementById('modalProductQuantity').value = 1;
                            document.getElementById('modalRemoveFromCartButton').style.display = "none";
                        }
                    });

                document.getElementById('modalAddToCartButton').setAttribute('data-product-id', product.id);
                document.getElementById('modalRemoveFromCartButton').setAttribute('data-product-id', product.id);
                productModal.style.display = "block";
            }).catch(error => {
                console.error('Error fetching product details:', error);
            });
    });
});

// Add to cart from modal
document.getElementById('modalAddToCartButton').addEventListener('click', () => {
    const productId = document.getElementById('modalAddToCartButton').getAttribute('data-product-id');
    const quantity = document.getElementById('modalProductQuantity').value;
    console.log(`Adding product ID: ${productId} with quantity: ${quantity} to cart for member ID: ${memberId}`);

    fetch(`/cart/add?memberId=${memberId}&productId=${productId}&quantity=${quantity}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert('Product added to cart');
            productModal.style.display = "none";
            updateCartModal();
        } else {
            alert('Failed to add product to cart');
        }
    }).catch(error => {
        console.error('Error adding product to cart:', error);
    });
});

// Remove from cart from modal
document.getElementById('modalRemoveFromCartButton').addEventListener('click', () => {
    const productId = document.getElementById('modalRemoveFromCartButton').getAttribute('data-product-id');
    console.log(`Removing product ID: ${productId} from cart for member ID: ${memberId}`);

    fetch(`/cart/remove?memberId=${memberId}&productId=${productId}`, {
        method: 'DELETE'
    }).then(response => {
        if (response.ok) {
            alert('Product removed from cart');
            productModal.style.display = "none";
            updateCartModal();
        } else {
            alert('Failed to remove product from cart');
        }
    }).catch(error => {
        console.error('Error removing product from cart:', error);
    });
});

// Show cart modal
function showCart() {
    console.log('Showing cart modal');
    updateCartModal();
    cartModal.style.display = "block";
}

// Update cart modal content
function updateCartModal() {
    console.log('Updating cart modal');
    fetch(`/cart/view?memberId=${memberId}`, { 
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(response => response.json())
    .then(cartItems => {
        console.log('Cart items:', cartItems);
        const cartItemsContainer = document.getElementById('cartItems');
        cartItemsContainer.innerHTML = '';
        let total = 0;
        cartItems.forEach(item => {
            if (item.price !== undefined && item.quantity !== undefined) {
                const subtotal = item.price * item.quantity;
                total += subtotal;
                cartItemsContainer.innerHTML += `
                    <tr>
                        <td>${item.name}</td>
                        <td>${item.quantity}</td>
                        <td>${item.price}</td>
                        <td>${subtotal}</td>
                    </tr>
                `;
            } else {
                console.error('Error: item properties are undefined', item);
            }
        });
        document.getElementById('cartTotal').textContent = total;
    }).catch(error => {
        console.error('Error fetching cart items:', error);
        alert('Failed to load cart items.');
    });
}

// Clear cart
document.getElementById('clearCartButton').addEventListener('click', () => {
    if (confirm('Are you sure you want to clear the cart?')) {
        console.log(`Clearing cart for member ID: ${memberId}`);
        fetch(`/cart/clear?memberId=${memberId}`, { 
            method: 'DELETE'
        }).then(response => {
            if (response.ok) {
                alert('Cart cleared');
                updateCartModal();
            } else {
                alert('Failed to clear cart');
            }
        }).catch(error => {
            console.error('Error clearing cart:', error);
        });
    }
});

// Checkout button
document.getElementById("checkoutButton").addEventListener("click", function() {
                
        // 發送POST請求到 /orders/checkout
        fetch(`/orders/checkout?memberId=${memberId}`, {
            method: "POST"
        }).then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("Checkout failed");
            }
        }).then(message => {
            alert(message);
            // 清空購物車UI
            // 你的清空購物車UI的代碼
        }).catch(error => {
            alert(error.message);
        });
    });

// Navigate to orders page
function goToOrders(memberId) {
    window.location.href = '/orders?memberId=' + memberId;
}

// Cancel order
function cancelOrder(orderId) {
    fetch('/orders/cancel?orderId=' + orderId, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            window.location.href = '/orders?memberId=' + memberId;
        } else {
            alert('Failed to cancel order');
        }
    });
}

// Initialize cart modal
updateCartModal();

// Ensure that goToOrders and cancelOrder are set after DOM is fully loaded
document.addEventListener("DOMContentLoaded", () => {
    const backToOrdersButton = document.querySelector('button[onclick^="goToOrders"]');
    const cancelOrderButton = document.querySelector('button[onclick^="cancelOrder"]');
    
    if (backToOrdersButton) {
        backToOrdersButton.onclick = () => goToOrders(memberId);
    }
    
    if (cancelOrderButton) {
        const orderId = cancelOrderButton.getAttribute('onclick').match(/\d+/)[0];
        cancelOrderButton.onclick = () => cancelOrder(orderId);
    }
});
