// static/js/scripts.js
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

// Get the modal
var modal = document.getElementById("productModal");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

// Add event listeners to product links to open the modal with product details
document.querySelectorAll('.product-link').forEach(link => {
    link.addEventListener('click', () => {
        const productId = link.getAttribute('data-product-id');
        // Fetch product details using productId
        fetch(`/api/products/${productId}`)
            .then(response => response.json())
            .then(product => {
                document.getElementById('modalProductName').textContent = product.name;
                document.getElementById('modalProductDescription').textContent = product.description;
                document.getElementById('modalProductPrice').textContent = product.price;
                document.getElementById('modalProductStock').textContent = product.stock;
                document.getElementById('modalProductCategory').textContent = product.category;
                document.getElementById('modalAddToCartButton').setAttribute('data-product-id', product.id);
                modal.style.display = "block";
            });
    });
});

// Add to cart from modal
document.getElementById('modalAddToCartButton').addEventListener('click', () => {
    const productId = document.getElementById('modalAddToCartButton').getAttribute('data-product-id');
    const memberId = 1; // You should replace this with the actual member ID
    const quantity = 1; // Default quantity
    fetch(`/cart/add?memberId=${memberId}&productId=${productId}&quantity=${quantity}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert('Product added to cart');
            modal.style.display = "none";
        } else {
            alert('Failed to add product to cart');
        }
    });
});

// Add to cart directly from the table
document.querySelectorAll('.cart-button').forEach(button => {
    button.addEventListener('click', () => {
        const productId = button.getAttribute('data-product-id');
        const memberId = 1; // You should replace this with the actual member ID
        const quantity = 1; // Default quantity
        fetch(`/cart/add?memberId=${memberId}&productId=${productId}&quantity=${quantity}`, {
            method: 'POST'
        }).then(response => {
            if (response.ok) {
                alert('Product added to cart');
            } else {
                alert('Failed to add product to cart');
            }
        });
    });
});
