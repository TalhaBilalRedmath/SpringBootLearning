import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import Login from './components/Login.tsx'; // Your updated Login component

function Contacts({ onLogout }: { onLogout: () => void }) {
  const jwt = localStorage.getItem('jwt');
  const [contacts, setContacts] = useState<{id: number, name: string, number: string}[]>([]);
  const [name, setName] = useState('');
  const [number, setNumber] = useState('');

  // Fetch contacts from backend
  const getContacts = () => {
    fetch('/api/getContacts', { // Using relative URL
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(res => {
        if (res.status === 401) {
          // JWT expired or invalid, logout user
          alert('Session expired. Please login again.');
          onLogout();
          return;
        }
        return res.json();
      })
      .then(data => {
        if (data) {
          setContacts(data);
        }
      })
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
    fetch('/api/saveContact', { // Using relative URL
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}` 
      },
      body: JSON.stringify({ name: name.trim(), number: number.trim() })
    })
      .then(async response => {
        if (response.status === 401) {
          alert('Session expired. Please login again.');
          onLogout();
          return;
        }
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
    fetch('/api/deleteAll', { // Using relative URL
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(response => {
        if (response.status === 401) {
          alert('Session expired. Please login again.');
          onLogout();
          return;
        }
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
    fetch(`/api/deleteContact/${encodeURIComponent(id)}`, { // Using relative URL
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${jwt}` }
    })
      .then(response => {
        if (response.status === 401) {
          alert('Session expired. Please login again.');
          onLogout();
          return;
        }
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
    fetch('/api/updateContact', { // Using relative URL
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json', 
        'Authorization': `Bearer ${jwt}` 
      },
      body: JSON.stringify({ id, name, number })
    })
      .then(response => {
        if (response.status === 401) {
          alert('Session expired. Please login again.');
          onLogout();
          return;
        }
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
    localStorage.removeItem('userEmail');
    onLogout();
  };

  return (
    <>
      <style>
        {`
          @keyframes fadeIn {
            0% { opacity: 0; transform: translateY(20px); }
            100% { opacity: 1; transform: translateY(0); }
          }
          
          @keyframes slideIn {
            0% { opacity: 0; transform: translateX(-20px); }
            100% { opacity: 1; transform: translateX(0); }
          }
          
          @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
          }
          
          .contacts-container {
            min-height: 100vh;
            background: linear-gradient(135deg, #0ea5e9 0%, #06b6d4 25%, #10b981 50%, #3b82f6 75%, #6366f1 100%);
            background-size: 400% 400%;
            animation: gradientShift 8s ease infinite;
            padding: 40px 20px;
            position: relative;
            overflow-x: hidden;
          }
          
          .contacts-card {
            max-width: 900px;
            margin: 0 auto;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 24px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.1);
            padding: 40px;
            position: relative;
            animation: fadeIn 0.6s ease-out;
          }
          
          .header-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
            flex-wrap: wrap;
            gap: 16px;
          }
          
          .title-container {
            display: flex;
            align-items: center;
            gap: 16px;
          }
          
          .phone-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
          }
          
          .main-title {
            font-size: 2.5rem;
            font-weight: 700;
            background: linear-gradient(135deg, #1e40af 0%, #7c3aed 50%, #059669 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin: 0;
          }
          
          .logout-btn {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            color: white;
            border: none;
            border-radius: 12px;
            padding: 12px 24px;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
          }
          
          .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(239, 68, 68, 0.4);
          }
          
          .add-form {
            display: grid;
            grid-template-columns: 1fr 1fr auto;
            gap: 16px;
            margin-bottom: 32px;
            padding: 24px;
            background: rgba(248, 250, 252, 0.6);
            border-radius: 16px;
            border: 1px solid rgba(148, 163, 184, 0.2);
            backdrop-filter: blur(10px);
          }
          
          .form-input {
            padding: 14px 18px;
            border-radius: 12px;
            border: 2px solid transparent;
            background: rgba(255, 255, 255, 0.8);
            font-size: 16px;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
          }
          
          .form-input:focus {
            outline: none;
            border-color: #3b82f6;
            background: rgba(255, 255, 255, 0.95);
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
            transform: translateY(-1px);
          }
          
          .add-btn {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
            border: none;
            border-radius: 12px;
            padding: 14px 24px;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
            transition: all 0.3s ease;
            white-space: nowrap;
          }
          
          .add-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
          }
          
          .delete-all-btn {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            color: white;
            border: none;
            border-radius: 12px;
            padding: 12px 20px;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
            transition: all 0.3s ease;
            margin-bottom: 24px;
          }
          
          .delete-all-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(239, 68, 68, 0.4);
          }
          
          .table-container {
            background: rgba(255, 255, 255, 0.7);
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
            border: 1px solid rgba(148, 163, 184, 0.2);
            backdrop-filter: blur(10px);
          }
          
          .contacts-table {
            width: 100%;
            border-collapse: collapse;
          }
          
          .table-header {
            background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
          }
          
          .table-header th {
            padding: 18px 16px;
            font-weight: 600;
            color: #334155;
            text-align: left;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }
          
          .table-row {
            border-bottom: 1px solid rgba(148, 163, 184, 0.1);
            transition: all 0.3s ease;
            animation: slideIn 0.4s ease-out;
          }
          
          .table-row:hover {
            background: rgba(59, 130, 246, 0.05);
            transform: translateX(4px);
          }
          
          .table-row:last-child {
            border-bottom: none;
          }
          
          .table-cell {
            padding: 16px;
            color: #475569;
            font-weight: 500;
          }
          
          .action-btn {
            border: none;
            border-radius: 8px;
            padding: 8px 16px;
            cursor: pointer;
            font-weight: 500;
            font-size: 14px;
            margin-right: 8px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
          }
          
          .update-btn {
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
          }
          
          .update-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
          }
          
          .delete-btn {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
          }
          
          .delete-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
          }
          
          .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #64748b;
          }
          
          .empty-icon {
            width: 80px;
            height: 80px;
            margin: 0 auto 20px;
            background: rgba(148, 163, 184, 0.1);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
          }
          
          .contact-count {
            background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
          }
          
          @media (max-width: 768px) {
            .add-form {
              grid-template-columns: 1fr;
              gap: 12px;
            }
            
            .header-section {
              flex-direction: column;
              align-items: flex-start;
            }
            
            .main-title {
              font-size: 2rem;
            }
            
            .contacts-card {
              padding: 24px 20px;
              margin: 20px 10px;
            }
            
            .table-container {
              overflow-x: auto;
            }
            
            .contacts-table {
              min-width: 600px;
            }
          }
        `}
      </style>
      
      <div className="contacts-container">
        {/* Background decoration */}
        <div style={{
          position: 'absolute',
          top: '10%',
          left: '5%',
          width: '200px',
          height: '200px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.1)',
          filter: 'blur(40px)',
          zIndex: 1
        }} />
        <div style={{
          position: 'absolute',
          bottom: '20%',
          right: '10%',
          width: '150px',
          height: '150px',
          borderRadius: '50%',
          background: 'rgba(255, 255, 255, 0.08)',
          filter: 'blur(30px)',
          zIndex: 1
        }} />
        
        <div className="contacts-card">
          <div className="header-section">
            <div className="title-container">
              <div className="phone-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                </svg>
              </div>
              <div>
                <h1 className="main-title">My Contacts</h1>
                <div className="contact-count">{contacts.length} contact{contacts.length !== 1 ? 's' : ''}</div>
              </div>
            </div>
            <button onClick={handleLogout} className="logout-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '8px' }}>
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16,17 21,12 16,7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
              Logout
            </button>
          </div>
          
          <form onSubmit={addContact} className="add-form">
            <input
              type="text"
              placeholder="Enter contact name"
              value={name}
              onChange={e => setName(e.target.value)}
              className="form-input"
              required
            />
            <input
              type="text"
              placeholder="Enter phone number"
              value={number}
              onChange={e => setNumber(e.target.value)}
              className="form-input"
              required
            />
            <button type="submit" className="add-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '8px' }}>
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="16"/>
                <line x1="8" y1="12" x2="16" y2="12"/>
              </svg>
              Add Contact
            </button>
          </form>
          
          {contacts.length > 0 && (
            <button onClick={deleteAll} className="delete-all-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '8px' }}>
                <polyline points="3,6 5,6 21,6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
              Delete All Contacts
            </button>
          )}
          
          <div className="table-container">
            <table className="contacts-table">
              <thead className="table-header">
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Phone Number</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {contacts.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="table-cell">
                      <div className="empty-state">
                        <div className="empty-icon">
                          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="1.5">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                          </svg>
                        </div>
                        <h3 style={{ margin: '0 0 8px 0', color: '#475569', fontSize: '18px' }}>No contacts yet</h3>
                        <p style={{ margin: 0, fontSize: '14px' }}>Add your first contact using the form above</p>
                      </div>
                    </td>
                  </tr>
                ) : (
                  contacts.map((contact, index) => (
                    <tr key={contact.id} className="table-row" style={{ animationDelay: `${index * 0.1}s` }}>
                      <td className="table-cell">
                        <span style={{ 
                          background: 'linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%)',
                          color: '#475569',
                          padding: '4px 12px',
                          borderRadius: '12px',
                          fontSize: '13px',
                          fontWeight: '600'
                        }}>
                          #{contact.id}
                        </span>
                      </td>
                      <td className="table-cell">
                        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                          <div style={{
                            width: '36px',
                            height: '36px',
                            borderRadius: '50%',
                            background: 'linear-gradient(135deg, #3b82f6 0%, #6366f1 100%)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: 'white',
                            fontWeight: '600',
                            fontSize: '14px'
                          }}>
                            {contact.name.charAt(0).toUpperCase()}
                          </div>
                          <span style={{ fontWeight: '600', color: '#1e293b' }}>{contact.name}</span>
                        </div>
                      </td>
                      <td className="table-cell">
                        <span style={{ 
                          fontFamily: 'monospace',
                          background: 'rgba(59, 130, 246, 0.1)',
                          color: '#1e40af',
                          padding: '4px 8px',
                          borderRadius: '6px',
                          fontSize: '14px'
                        }}>
                          {contact.number}
                        </span>
                      </td>
                      <td className="table-cell">
                        <button
                          onClick={() => updateContactPrompt(contact.id, contact.name, contact.number)}
                          className="action-btn update-btn"
                        >
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '4px' }}>
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                          </svg>
                          Edit
                        </button>
                        <button
                          onClick={() => deleteContact(contact.name, contact.id)}
                          className="action-btn delete-btn"
                        >
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '4px' }}>
                            <polyline points="3,6 5,6 21,6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                            <line x1="10" y1="11" x2="10" y2="17"/>
                            <line x1="14" y1="11" x2="14" y2="17"/>
                          </svg>
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </>
  );
}

const Root = () => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => !!localStorage.getItem('jwt'));

  return (
    <React.StrictMode>
      {!isLoggedIn ? (
        <Login setIsLoggedIn={setIsLoggedIn} />
      ) : (
        <Contacts onLogout={() => setIsLoggedIn(false)} />
      )}
    </React.StrictMode>
  );
};

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(<Root />);

reportWebVitals();