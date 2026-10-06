document.addEventListener("DOMContentLoaded", () => {
  let cart = JSON.parse(localStorage.getItem("cart")) || [];

  const productCards = document.querySelectorAll('.product-card');

  productCards.forEach(card => {
    const addBtn = card.querySelector('.addBtn');
    const counterContainer = card.querySelector('.counterContainer');
    const countEl = card.querySelector('.count');
    const minusBtn = card.querySelector('.minusBtn');
    const plusBtn = card.querySelector('.plusBtn');

    let count = 1;

    addBtn.addEventListener('click', () => {
      addBtn.style.display = 'none';
      counterContainer.style.display = 'flex';
      counterContainer.style.justifyContent = 'center';
      count = 1;
      countEl.textContent = count;
      updateCart();
    });

    plusBtn.addEventListener('click', () => {
      count++;
      countEl.textContent = count;
      updateCart();
    });

    minusBtn.addEventListener('click', () => {
      count--;
      if (count <= 0) {
        counterContainer.style.display = 'none';
        addBtn.style.display = 'flex';
        removeFromCart();
        count = 1;
      } else {
        countEl.textContent = count;
        updateCart();
      }
    });

    function updateCart() {
      const name = card.dataset.name;
      const price = parseInt(card.dataset.price);
      const imageUrl = card.dataset.image;

      const existing = cart.find(p => p.name === name);
      if (existing) {
        existing.quantity = count;
      } else {
        cart.push({ name, price, imageUrl, quantity: count });
      }
      localStorage.setItem('cart', JSON.stringify(cart));
    }

    function removeFromCart() {
      const name = card.dataset.name;
      const index = cart.findIndex(p => p.name === name);
      if (index !== -1) {
        cart.splice(index, 1);
        localStorage.setItem('cart', JSON.stringify(cart));
      }
    }
  });
});
