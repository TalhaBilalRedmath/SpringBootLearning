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

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
     await fetch('/csrf', { 
      method: 'GET',
      credentials: 'include'
    });

    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    const csrfToken = await getCookie('XSRF-TOKEN');

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
    window.location.href = '/oauth2/authorization/google';
  };

  // Telephone Logo SVG Component
  const TelephoneLogo = () => (
    <div style={{
      width: '80px',
      height: '80px',
      backgroundColor: 'rgba(255, 255, 255, 0.15)',
      borderRadius: '20px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      marginBottom: '24px',
      backdropFilter: 'blur(10px)',
      border: '1px solid rgba(255, 255, 255, 0.2)',
      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)'
    }}>
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" fill="#1e3a8a" stroke="#1e3a8a"/>
      </svg>
    </div>
  );

  // Inline styles
  const containerStyle: React.CSSProperties = {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #0ea5e9 0%, #06b6d4 25%, #10b981 50%, #3b82f6 75%, #6366f1 100%)',
    backgroundSize: '400% 400%',
    animation: 'gradientShift 8s ease infinite',
    position: 'relative',
    overflow: 'hidden'
  };

  const boxStyle: React.CSSProperties = {
    background: 'rgba(255, 255, 255, 0.95)',
    borderRadius: '24px',
    backdropFilter: 'blur(20px)',
    border: '1px solid rgba(255, 255, 255, 0.2)',
    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.1)',
    padding: '48px 36px',
    minWidth: '380px',
    maxWidth: '90vw',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    position: 'relative',
    zIndex: 2
  };

  const inputStyle: React.CSSProperties = {
    width: '100%',
    padding: '16px 20px',
    marginBottom: '20px',
    borderRadius: '12px',
    border: '2px solid transparent',
    fontSize: '1rem',
    outline: 'none',
    background: 'rgba(248, 250, 252, 0.8)',
    boxSizing: 'border-box',
    display: 'block',
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)'
  };

  const buttonStyle: React.CSSProperties = {
    width: '100%',
    padding: '16px 0',
    borderRadius: '12px',
    border: 'none',
    background: 'linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%)',
    color: 'white',
    fontWeight: 600,
    fontSize: '1.1rem',
    cursor: loading ? 'not-allowed' : 'pointer',
    marginBottom: '24px',
    boxShadow: '0 8px 25px rgba(99, 102, 241, 0.3)',
    transition: 'all 0.3s ease',
    transform: loading ? 'scale(0.98)' : 'scale(1)',
    opacity: loading ? 0.8 : 1,
    position: 'relative',
    overflow: 'hidden'
  };

  const googleButtonStyle: React.CSSProperties = {
    width: '100%',
    padding: '14px 0',
    borderRadius: '12px',
    border: '2px solid rgba(59, 130, 246, 0.2)',
    background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%)',
    color: '#1e293b',
    fontWeight: 600,
    fontSize: '1rem',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '12px',
    boxShadow: '0 4px 15px rgba(59, 130, 246, 0.15)',
    transition: 'all 0.3s ease',
    backdropFilter: 'blur(10px)',
    position: 'relative',
    overflow: 'hidden'
  };

  const titleStyle: React.CSSProperties = {
    fontSize: '2.2rem',
    fontWeight: 700,
    marginBottom: '12px',
    background: 'linear-gradient(135deg, #1e40af 0%, #7c3aed 50%, #059669 100%)',
    WebkitBackgroundClip: 'text',
    WebkitTextFillColor: 'transparent',
    backgroundClip: 'text',
    letterSpacing: '0.5px',
    textAlign: 'center' as const
  };

  const subtitleStyle: React.CSSProperties = {
    fontSize: '0.95rem',
    color: '#64748b',
    marginBottom: '32px',
    textAlign: 'center' as const,
    fontWeight: 500
  };

  return (
    <>
      <style>
        {`
          @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
          }
          
          input:focus {
            border-color: #3b82f6 !important;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
            transform: translateY(-1px);
          }
          
          button:hover:not(:disabled) {
            transform: translateY(-2px) !important;
            box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4) !important;
          }
          
          .google-btn:hover {
            background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(241, 245, 249, 1) 100%) !important;
            box-shadow: 0 8px 25px rgba(59, 130, 246, 0.25) !important;
            transform: translateY(-2px) !important;
          }
          
          .google-btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
            transition: left 0.5s;
          }
          
          .google-btn:hover::before {
            left: 100%;
          }
        `}
      </style>
      <div style={containerStyle}>
        {/* Background decoration circles */}
        <div style={{
          position: 'absolute',
          top: '10%',
          left: '10%',
          width: '200px',
          height: '200px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.1)',
          filter: 'blur(40px)',
          zIndex: 1
        }} />
        <div style={{
          position: 'absolute',
          bottom: '15%',
          right: '15%',
          width: '150px',
          height: '150px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.08)',
          filter: 'blur(30px)',
          zIndex: 1
        }} />
        
        <div style={boxStyle}>
          <TelephoneLogo />
          <h1 style={titleStyle}>PhoneBook</h1>
          <p style={subtitleStyle}>Access your contacts securely</p>
          
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
              {loading ? (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                  <div style={{
                    width: '20px',
                    height: '20px',
                    border: '2px solid rgba(255, 255, 255, 0.3)',
                    borderTop: '2px solid white',
                    borderRadius: '50%',
                    animation: 'spin 1s linear infinite'
                  }} />
                  Logging in...
                </div>
              ) : 'Sign In'}
            </button>
          </form>
          
          <div style={{
            width: '100%',
            textAlign: 'center' as const,
            margin: '16px 0',
            color: '#64748b',
            fontSize: '0.9rem',
            position: 'relative'
          }}>
            <div style={{
              position: 'absolute',
              top: '50%',
              left: 0,
              right: 0,
              height: '1px',
              background: 'linear-gradient(to right, transparent, #cbd5e1, transparent)'
            }} />
            <span style={{
              background: 'rgba(255, 255, 255, 0.9)',
              padding: '0 20px',
              position: 'relative',
              zIndex: 1
            }}>or continue with</span>
          </div>
          
          <button 
            onClick={handleGoogleLogin} 
            style={googleButtonStyle}
            className="google-btn"
          >
            <svg width="20" height="20" viewBox="0 0 48 48" style={{ verticalAlign: 'middle' }}>
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
            Sign in with Google
          </button>
        </div>
      </div>
      <style>
        {`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}
      </style>
    </>
  );
};

export default Login;