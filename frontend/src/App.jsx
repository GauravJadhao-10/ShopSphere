import { useEffect, useState } from 'react'

function App() {

  const [isLogin, setIsLogin] = useState(true)

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const [token, setToken] = useState(
    localStorage.getItem('token')
  )

  const [userId, setUserId] = useState(
    localStorage.getItem('userId')
  )

  const [cartId, setCartId] = useState(
    localStorage.getItem('cartId')
  )

  const [products, setProducts] = useState([])

  const [cartItems, setCartItems] = useState([])

  const [orders, setOrders] = useState([])

  const [showCart, setShowCart] = useState(false)

  const [showOrders, setShowOrders] = useState(false)

  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const [loading, setLoading] = useState(false)

  const [quantity, setQuantity] = useState(1)


  // Load products
  function loadProducts() {

    fetch('http://localhost:8080/products')
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to load products')
        }

        return response.json()
      })
      .then(data => {

        setProducts(data)
        setError('')
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Create or get cart
  function loadCart() {

    if (!token || !userId) {
      return
    }

    fetch(`http://localhost:8080/cart/create/${userId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to create cart')
        }

        return response.json()
      })
      .then(data => {

        localStorage.setItem('cartId', data.id)

        setCartId(data.id)

        loadCartItems(data.id)
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Load cart items
  function loadCartItems(id = cartId) {

    if (!id || !token) {
      return
    }

    fetch(`http://localhost:8080/cart/${id}/items`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to load cart')
        }

        return response.json()
      })
      .then(data => {

        setCartItems(data)
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Load user orders
  function loadOrders() {

    if (!userId || !token) {
      return
    }

    fetch(`http://localhost:8080/orders/user/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to load orders')
        }

        return response.json()
      })
      .then(data => {

        setOrders(data)
        setError('')
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Load products and cart after login
  useEffect(() => {

    loadProducts()

    if (token && userId) {
      loadCart()
    }

  }, [token, userId])


  // Login / Register
  function handleSubmit(event) {

    event.preventDefault()

    setMessage('')
    setError('')
    setLoading(true)

    if (isLogin) {

      fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email,
          password: password
        })
      })
        .then(response => {

          if (!response.ok) {
            throw new Error('Invalid email or password')
          }

          return response.json()
        })
        .then(data => {

          localStorage.setItem('token', data.token)
          localStorage.setItem('userId', data.userId)

          setToken(data.token)
          setUserId(data.userId)

          setMessage('Login successful')

          setEmail('')
          setPassword('')
        })
        .catch(error => {

          setError(error.message)
        })
        .finally(() => {

          setLoading(false)
        })

    } else {

      fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: name,
          email: email,
          password: password,
          role: 'USER'
        })
      })
        .then(response => {

          if (!response.ok) {
            throw new Error('Registration failed')
          }

          return response.json()
        })
        .then(() => {

          setMessage(
            'Registration successful. Please login.'
          )

          setIsLogin(true)

          setName('')
          setEmail('')
          setPassword('')
        })
        .catch(error => {

          setError(error.message)
        })
        .finally(() => {

          setLoading(false)
        })
    }
  }


  // Add product to cart
  function addToCart(productId) {

    if (!cartId) {
      setError('Cart is not ready yet')
      return
    }

    if (quantity <= 0) {
      setError('Quantity must be greater than 0')
      return
    }

    fetch(
      `http://localhost:8080/cart/${cartId}/items?productId=${productId}&quantity=${quantity}`,
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    )
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to add product to cart')
        }

        return response.json()
      })
      .then(() => {

        setMessage('Product added to cart')

        loadCartItems()
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Remove cart item
  function removeCartItem(itemId) {

    fetch(`http://localhost:8080/cart/items/${itemId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to remove cart item')
        }

        setMessage('Item removed from cart')

        loadCartItems()
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Place order
  function placeOrder() {

    if (!userId || !token) {
      setError('Please login first')
      return
    }

    if (!cartItems || cartItems.length === 0) {
      setError('Your cart is empty')
      return
    }

    setMessage('')
    setError('')

    fetch(`http://localhost:8080/orders/create/${userId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {

        if (!response.ok) {
          throw new Error('Failed to place order')
        }

        return response.json()
      })
      .then(order => {

        setMessage(
          `Order placed successfully. Order ID: ${order.id}`
        )

        loadCartItems()

        loadProducts()

        loadOrders()
      })
      .catch(error => {

        setError(error.message)
      })
  }


  // Logout
  function handleLogout() {

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('cartId')

    setToken(null)
    setUserId(null)
    setCartId(null)
    setCartItems([])
    setOrders([])

    setMessage('Logged out successfully')
  }


  return (
    <div>

      <h1>ShopSphere</h1>

      <p>Welcome to our online store</p>

      {message && <p>{message}</p>}

      {error && <p>{error}</p>}


      {!token ? (

        <div>

          <h2>
            {isLogin ? 'Login' : 'Register'}
          </h2>

          <form onSubmit={handleSubmit}>

            {!isLogin && (
              <div>

                <label>Name</label>

                <br />

                <input
                  type="text"
                  value={name}
                  onChange={event =>
                    setName(event.target.value)
                  }
                  required
                />

              </div>
            )}

            <div>

              <label>Email</label>

              <br />

              <input
                type="email"
                value={email}
                onChange={event =>
                  setEmail(event.target.value)
                }
                required
              />

            </div>

            <div>

              <label>Password</label>

              <br />

              <input
                type="password"
                value={password}
                onChange={event =>
                  setPassword(event.target.value)
                }
                required
              />

            </div>

            <br />

            <button type="submit">

              {loading
                ? 'Please wait...'
                : isLogin
                  ? 'Login'
                  : 'Register'}

            </button>

          </form>

          <br />

          <button
            onClick={() => {

              setIsLogin(!isLogin)

              setMessage('')
              setError('')

            }}
          >
            {isLogin
              ? 'Create an account'
              : 'Already have an account? Login'}
          </button>

        </div>

      ) : (

        <div>

          <p>You are logged in.</p>

          <button onClick={handleLogout}>
            Logout
          </button>

          <button
            onClick={() => {

              setShowCart(!showCart)

              if (!showCart) {
                loadCartItems()
              }

            }}
          >
            {showCart ? 'Hide Cart' : 'View Cart'}
          </button>

          <button
            onClick={() => {

              setShowOrders(!showOrders)

              if (!showOrders) {
                loadOrders()
              }

            }}
          >
            {showOrders ? 'Hide Orders' : 'My Orders'}
          </button>


          <h2>Products</h2>

          <div>

            <label>Quantity: </label>

            <input
              type="number"
              min="1"
              value={quantity}
              onChange={event =>
                setQuantity(Number(event.target.value))
              }
            />

          </div>

          <br />


          {products.map(product => (

            <div key={product.id}>

              <h3>{product.name}</h3>

              <p>{product.description}</p>

              <p>Price: ₹{product.price}</p>

              <p>Stock: {product.stock}</p>

              <button
                onClick={() =>
                  addToCart(product.id)
                }
              >
                Add to Cart
              </button>

              <hr />

            </div>

          ))}


          {showCart && (

            <div>

              <h2>Your Cart</h2>

              {cartItems.length === 0 ? (

                <p>Your cart is empty.</p>

              ) : (

                <div>

                  {cartItems.map(item => (

                    <div key={item.id}>

                      <h3>
                        {item.product.name}
                      </h3>

                      <p>
                        Price: ₹{item.product.price}
                      </p>

                      <p>
                        Quantity: {item.quantity}
                      </p>

                      <button
                        onClick={() =>
                          removeCartItem(item.id)
                        }
                      >
                        Remove
                      </button>

                      <hr />

                    </div>

                  ))}

                  <button onClick={placeOrder}>
                    Place Order
                  </button>

                </div>

              )}

            </div>

          )}


          {showOrders && (

            <div>

              <h2>My Orders</h2>

              {orders.length === 0 ? (

                <p>No orders found.</p>

              ) : (

                <div>

                  {orders.map(order => (

                    <div key={order.id}>

                      <h3>
                        Order #{order.id}
                      </h3>

                      <p>
                        Status: {order.status}
                      </p>

                      <hr />

                    </div>

                  ))}

                </div>

              )}

            </div>

          )}

        </div>

      )}

    </div>
  )
}

export default App