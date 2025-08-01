import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import Login from './components/Login.tsx';

const API_BASE = 'http://localhost:8080';

function Contacts({ onLogout }: { onLogout: () => void }) {
  const jwt = localStorage.getItem('jwt');
  const userEmail = localStorage.getItem('userEmail');
  const [userInfo, setUserInfo] = useState<{ name: string; role: string } | null>(null);
  const [contacts, setContacts] = useState<{id: number, name: string, number: string}[]>([]);
  const [name, setName] = useState('');
  const [number, setNumber] = useState('');

  // Fetch user info (name and role) from backend
  useEffect(() => {
    if (!jwt) return;
    fetch(`${API_BASE}/api/user-info`, {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(res => res.ok ? res.json() : null)
      .then(data => {
        if (data && data.name && data.role) {
          setUserInfo({ name: data.name, role: data.role });
        } else if (userEmail) {
          // fallback if backend doesn't provide name/role
          setUserInfo({ name: userEmail, role: 'User' });
        }
      })
      .catch(() => {
        if (userEmail) setUserInfo({ name: userEmail, role: 'User' });
      });
  }, [jwt, userEmail]);

  // Fetch contacts from backend
  const getContacts = () => {
    fetch(`${API_BASE}/api/getContacts`, {
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(res => res.json())
      .then(data => setContacts(data))
      .catch(() => setContacts([]));
  };

  useEffect(() => {
    getContacts();
  }, []);

  // Add new contact
  const addContact = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !number.trim()) {
      alert('Please enter both name and number.');
      return;
    }
    fetch(`${API_BASE}/api/saveContact`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json','Authorization': `Bearer ${jwt}` },
      body: JSON.stringify({ name: name.trim(), number: number.trim() })
    })
      .then(async response => {
        if (response.ok) {
          alert('Contact Saved!');
          setName('');
          setNumber('');
          getContacts();
        } else if (response.status === 400) {
          const errorData = await response.json();
          let messages = Object.values(errorData).join('\n');
          alert('Validation Error:\n' + messages);
        } else {
          alert('Failed to save contact.');
        }
      })
      .catch(err => alert('Error: ' + err.message));
  };

  // Delete all contacts
  const deleteAll = () => {
    if (!window.confirm('Are you sure you want to delete all contacts?')) return;
    fetch(`${API_BASE}/api/deleteAll`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(response => {
        if (response.ok) {
          alert('Deleted All Contacts');
          getContacts();
        } else {
          alert('Failed to delete all Contacts');
        }
      })
      .catch(err => alert('Error: ' + err));
  };

  // Delete a single contact
  const deleteContact = (name: string, id: number) => {
    if (!window.confirm(`Are you sure you want to delete contact: ${name}?`)) return;
    fetch(`${API_BASE}/api/deleteContact/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(response => {
        if (response.ok) {
          alert(`Deleted ${name}`);
          getContacts();
        } else {
          alert('Failed to delete contact');
        }
      })
      .catch(err => alert('Error: ' + err));
  };

  // Update a contact
  const updateContactPrompt = (id: number, currentName: string, currentNumber: string) => {
    const newName = prompt('Enter new name:', currentName);
    const newNumber = prompt('Enter new number:', currentNumber);
    if (newName && newNumber) {
      updateContact(id, newName.trim(), newNumber.trim());
    }
  };

  const updateContact = (id: number, name: string, number: string) => {
    fetch(`${API_BASE}/api/updateContact`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` },
      body: JSON.stringify({ id, name, number })
    })
      .then(response => {
        if (response.ok) {
          alert(`Updated contact: ${name}`);
          getContacts();
        } else {
          alert('Failed to update contact');
        }
      })
      .catch(err => alert('Error: ' + err));
  };

  // Logout handler
  const handleLogout = () => {
    localStorage.removeItem('jwt');
    onLogout();
  };

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', background: '#fff', borderRadius: 16, boxShadow: '0 4px 24px #0001', padding: 32, position: 'relative' }}>
      <button
        onClick={handleLogout}
        style={{
          position: 'absolute',
          top: 24,
          right: 24, // Changed from 32 to 24 for better alignment
          background: 'linear-gradient(270deg, #ffb347 0%, #ff7e5f 100%)',
          color: '#fff',
          border: 'none',
          borderRadius: 8,
          padding: '8px 20px',
          fontWeight: 600,
          fontSize: 16,
          cursor: 'pointer',
          boxShadow: '0 2px 8px #0002',
          transition: 'background 0.2s',
          zIndex: 2 // Ensure button is above content
        }}
      >
        Logout
      </button>
      {userInfo && (
        <div
          style={{
            marginBottom: 32,
            padding: '18px 0 10px 0',
            borderRadius: 12,
            background: 'linear-gradient(90deg, #e0e7ff 0%, #f0fdfa 100%)',
            textAlign: 'center',
            boxShadow: '0 2px 8px #6366f122',
            position: 'relative',
            zIndex: 1
          }}
        >
          <div style={{ fontSize: 22, fontWeight: 700, color: '#1e293b', marginBottom: 4 }}>
            {userInfo.name}
          </div>
          <div style={{ fontSize: 15, color: '#6366f1', fontWeight: 600, letterSpacing: 1 }}>
            {userInfo.role}
          </div>
        </div>
      )}
      <h2 style={{ textAlign: 'center', marginBottom: 24, fontSize: 32, fontWeight: 700 }}>Contacts</h2>
      <form onSubmit={addContact} style={{ display: 'flex', gap: 12, marginBottom: 24 }}>
        <input
          type="text"
          placeholder="Name"
          value={name}
          onChange={e => setName(e.target.value)}
          style={{ flex: 2, padding: 10, borderRadius: 8, border: '1px solid #ccc', fontSize: 16 }}
        />
        <input
          type="text"
          placeholder="Number"
          value={number}
          onChange={e => setNumber(e.target.value)}
          style={{ flex: 2, padding: 10, borderRadius: 8, border: '1px solid #ccc', fontSize: 16 }}
        />
        <button type="submit" style={{ flex: 1, padding: 10, borderRadius: 8, background: '#6366f1', color: '#fff', border: 'none', fontWeight: 600, fontSize: 16 }}>
          Add
        </button>
      </form>
      <button onClick={deleteAll} style={{ marginBottom: 20, background: '#ef4444', color: '#fff', border: 'none', borderRadius: 8, padding: '8px 16px', fontWeight: 600, cursor: 'pointer' }}>
        Delete All Contacts
      </button>
      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: 10 }}>
        <thead>
          <tr style={{ background: '#f3f4f6' }}>
            <th style={{ padding: 10, borderRadius: 8 }}>ID</th>
            <th style={{ padding: 10, borderRadius: 8 }}>Name</th>
            <th style={{ padding: 10, borderRadius: 8 }}>Number</th>
            <th style={{ padding: 10, borderRadius: 8 }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {contacts.length === 0 ? (
            <tr>
              <td colSpan={4} style={{ textAlign: 'center', padding: 20, color: '#888' }}>No contacts found.</td>
            </tr>
          ) : (
            contacts.map(contact => (
              <tr key={contact.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: 10, textAlign: 'center' }}>{contact.id}</td>
                <td style={{ padding: 10, textAlign: 'center' }}>{contact.name}</td>
                <td style={{ padding: 10, textAlign: 'center' }}>{contact.number}</td>
                <td style={{ padding: 10 }}>
                  <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }}>
                    <button
                      onClick={() => updateContactPrompt(contact.id, contact.name, contact.number)}
                      style={{
                        background: '#1DA1F2', // Twitter blue
                        color: '#fff',
                        border: 'none',
                        borderRadius: 6,
                        padding: '6px 12px',
                        cursor: 'pointer',
                        fontWeight: 500
                      }}
                    >
                      Update
                    </button>
                    <button
                      onClick={() => deleteContact(contact.name, contact.id)}
                      style={{
                        background: '#ef4444',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 6,
                        padding: '6px 12px',
                        cursor: 'pointer',
                        fontWeight: 500
                      }}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

const Root = () => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => !!localStorage.getItem('jwt'));

  return (
    <React.StrictMode>
      {!isLoggedIn ? <Login setIsLoggedIn={setIsLoggedIn} /> : <Contacts onLogout={() => setIsLoggedIn(false)} />}
    </React.StrictMode>
  );
};

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(<Root />);

reportWebVitals();
