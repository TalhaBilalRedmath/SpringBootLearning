import React, { useState, useEffect } from 'react';

const Login = ({ setIsLoggedIn }: { setIsLoggedIn: (isLoggedIn: boolean) => void }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  // Check for OAuth login on component mount
  useEffect(() => {
    const checkOAuthLogin = () => {
      // Method 1: If using URL parameter approach
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get('token');
      const email = urlParams.get('email');
      console.log("API HIT", token, email);
      if (token) {
        // Store token and set logged in state
        localStorage.setItem('jwt', token);
        if (email) {
          localStorage.setItem('userEmail', email);
        }
        
        // Clean up URL
        window.history.replaceState({}, document.title, window.location.pathname);
        
        // Set logged in state
        setIsLoggedIn(true);
        return;
      }

      // Method 2: If using cookie approach
      const userEmail = getCookie('user-email');
      if (userEmail) {
        // User is logged in via OAuth
        localStorage.setItem('userEmail', userEmail);
        setIsLoggedIn(true);
        return;
      }

      // Method 3: Check if JWT exists in localStorage from previous session
      const existingToken = localStorage.getItem('jwt');
      if (existingToken) {
        // Optionally verify token is still valid by making a test request
        verifyToken(existingToken);
      }
    };

    checkOAuthLogin();
  }, [setIsLoggedIn]);

  // Helper function to get cookie value
  const getCookie = (name: string): string | null => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      const cookieValue = parts.pop()?.split(';').shift();
      return cookieValue || null;
    }
    return null;
  };

  // Helper function to verify token validity
  const verifyToken = async (token: string) => {
    try {
      const response = await fetch('/api/verify-token', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
      });

      if (response.ok) {
        setIsLoggedIn(true);
      } else {
        // Token is invalid, remove it
        localStorage.removeItem('jwt');
        localStorage.removeItem('userEmail');
      }
    } catch (error) {
      console.error('Token verification failed:', error);
      localStorage.removeItem('jwt');
      localStorage.removeItem('userEmail');
    }
  };

  

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    // Create FormData for form login (Spring Security expects form data, not JSON)
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    const csrfToken = getCookie('XSRF-TOKEN');

    fetch('/login', { // Using relative URL since proxy handles routing
      method: 'POST',
      body: formData,
      credentials: 'include', // Important for session handling
      headers: {
        'X-XSRF-TOKEN': csrfToken || ''
      }
    })
      .then(async res => {
        setLoading(false);
        if (res.ok) {
          const data = await res.json();
          console.log('Login successful:', data);
          alert('Login successful');
          
          // Store JWT token
          if (data.access_token) {
            localStorage.setItem('jwt', data.access_token);
          }
          if (data.email) {
            localStorage.setItem('userEmail', data.email);
          }
          setIsLoggedIn(true);
        } else if (res.status === 401) {
          const errorData = await res.json().catch(() => ({}));
          alert(errorData.error || 'Invalid credentials');
        } else {
          const errorData = await res.json().catch(() => ({}));
          alert(errorData.error || 'Login failed');
        }
      })
      .catch(err => {
        setLoading(false);
        console.error('Login error:', err);
        alert('Error during login');
      });
  };

  const handleGoogleLogin = () => {
    // Temporarily use full URL to bypass proxy issues
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  // Inline styles for centering and aligning inputs
  const containerStyle: React.CSSProperties = {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #e0e7ff 0%, #f0fdfa 100%)',
  };

  const boxStyle: React.CSSProperties = {
    background: 'white',
    borderRadius: '24px',
    boxShadow: '0 8px 32px rgba(31, 41, 55, 0.15)',
    padding: '48px 36px',
    minWidth: '350px',
    maxWidth: '90vw',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  };

  const inputStyle: React.CSSProperties = {
    width: '100%',
    padding: '14px 16px',
    marginBottom: '18px',
    borderRadius: '10px',
    border: '1px solid #cbd5e1',
    fontSize: '1.1rem',
    outline: 'none',
    background: '#f8fafc',
    boxSizing: 'border-box',
    display: 'block',
  };

  const buttonStyle: React.CSSProperties = {
    width: '100%',
    padding: '14px 0',
    borderRadius: '10px',
    border: 'none',
    background: '#6366f1',
    color: 'white',
    fontWeight: 600,
    fontSize: '1.1rem',
    cursor: loading ? 'not-allowed' : 'pointer',
    marginBottom: '18px',
    boxShadow: '0 2px 8px rgba(99, 102, 241, 0.08)',
    transition: 'background 0.2s',
  };

  const googleButtonStyle: React.CSSProperties = {
    padding: '8px 0',
    borderRadius: '8px',
    border: '1px solid #cbd5e1',
    background: '#fff',
    color: '#1e293b',
    fontWeight: 600,
    fontSize: '1rem',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    boxShadow: '0 2px 8px rgba(16, 185, 129, 0.06)',
    width: '160px',
    margin: '0 auto',
  };

  return (
    <div style={containerStyle}>
      <div style={boxStyle}>
        <h1
          style={{
            fontSize: '2.5rem',
            fontWeight: 700,
            marginBottom: '32px',
            color: '#1e293b',
            letterSpacing: '1px',
            textAlign: 'center',
          }}
        >
          PhoneBook Login
        </h1>
        <form onSubmit={handleLogin} style={{ width: '100%' }}>
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            style={inputStyle}
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            style={inputStyle}
          />
          <button type="submit" disabled={loading} style={buttonStyle}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <div
          style={{
            width: '100%',
            textAlign: 'center',
            margin: '12px 0',
            color: '#64748b',
            fontSize: '0.95rem',
          }}
        >
          or
        </div>
        <button onClick={handleGoogleLogin} style={googleButtonStyle}>
          <svg width="18" height="18" viewBox="0 0 48 48" style={{ verticalAlign: 'middle' }}>
            <g>
              <path
                fill="#4285F4"
                d="M44.5 20H24v8.5h11.7C34.7 33.1 29.8 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c2.6 0 5 .8 7 2.3l6.4-6.4C33.5 6.5 28.1 4 22 4 11.5 4 3 12.5 3 23s8.5 19 19 19c10.5 0 18.5-7.5 18.5-18 0-1.2-.1-2.1-.3-3z"
              />
              <path
                fill="#34A853"
                d="M6.3 14.7l7 5.1C15.3 17.1 19.3 14 24 14c2.6 0 5 .8 7 2.3l6.4-6.4C33.5 6.5 28.1 4 22 4c-7.2 0-13.2 4.1-16.2 10.7z"
              />
              <path
                fill="#FBBC05"
                d="M24 44c5.6 0 10.3-1.8 13.7-4.9l-6.3-5.2C29.8 36 27 37 24 37c-5.7 0-10.6-3.9-12.3-9.1l-7 5.4C7.8 39.9 15.3 44 24 44z"
              />
              <path
                fill="#EA4335"
                d="M44.5 20H24v8.5h11.7c-1.1 3.1-4.1 5.5-7.7 5.5-2.2 0-4.2-.7-5.7-2l-7 5.4C17.7 41.1 20.7 44 24 44c10.5 0 18.5-7.5 18.5-18 0-1.2-.1-2.1-.3-3z"
              />
            </g>
          </svg>
          Google
        </button>
      </div>
    </div>
  );
};

export default Login;