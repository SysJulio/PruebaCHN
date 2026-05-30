import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BadgeCheck, Banknote, Check, CircleDollarSign, ClipboardList, Pencil, Trash2, UserPlus, Users, X } from 'lucide-react';
import './styles.css';

const API = '/api';
const emptyClient = { firstName: '', lastName: '', identificationNumber: '', birthDate: '', address: '', email: '', phone: '' };
const emptyApplication = { clientId: '', requestedAmount: '', termMonths: '', purpose: '' };

// Funcion generica para realizar peticiones HTTP al backend
async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.message || 'No fue posible completar la operacion');
  }
  if (response.status === 204) return null;
  return response.json();
}

// Convierte un valor numerico a formato de moneda GTQ

function money(value) {
  return new Intl.NumberFormat('es-GT', { style: 'currency', currency: 'GTQ' }).format(Number(value || 0));
}

// Obtiene la cuota actual del prestamo

function installmentAmount(loan) {
  return loan?.currentInstallmentAmount || loan?.monthlyPayment || '';
}

// Genera una simulacion del plan de pagos del prestamo

function buildPaymentPlan(loan) {
  const monthlyPayment = Number(loan.monthlyPayment || 0);
  const annualRate = Number(loan.annualInterestRate || 0);
  const monthlyRate = annualRate / 100 / 12;
  let balance = Number(loan.principalAmount || 0);
  const rows = [];

  for (let month = 1; month <= Number(loan.termMonths || 0) && balance > 0.01; month += 1) {
    const interest = monthlyRate === 0 ? 0 : balance * monthlyRate;
    const principal = Math.min(balance, Math.max(monthlyPayment - interest, 0));
    balance = Math.max(balance - principal, 0);
    rows.push({ month, payment: principal + interest, principal, interest, balance });
  }

  return rows;
}

function App() {
  const [tab, setTab] = useState('clients');
  const [clients, setClients] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loans, setLoans] = useState([]);
  const [clientForm, setClientForm] = useState(emptyClient);
  const [editingClientId, setEditingClientId] = useState(null);
  const [applicationForm, setApplicationForm] = useState(emptyApplication);
  const [paymentForm, setPaymentForm] = useState({ loanId: '', amount: '', receiptNumber: '', paymentType: 'CUOTA' });
  const [decisionForms, setDecisionForms] = useState({});
  const [message, setMessage] = useState('');

  const selectedClient = useMemo(() => clients.find(c => c.id === Number(applicationForm.clientId)), [clients, applicationForm.clientId]);
  const selectedLoan = useMemo(() => loans.find(loan => loan.id === Number(paymentForm.loanId)), [loans, paymentForm.loanId]);

 // Carga inicial de datos desde la API

  async function load() {
    const [clientData, applicationData, loanData] = await Promise.all([
      request('/clients'),
      request('/loan-applications'),
      request('/approved-loans')
    ]);
    setClients(clientData);
    setApplications(applicationData);
    setLoans(loanData);
  }

  useEffect(() => { load().catch(err => setMessage(err.message)); }, []);

 // Guarda o actualiza un cliente

  async function saveClient(event) {
    event.preventDefault();
    const path = editingClientId ? `/clients/${editingClientId}` : '/clients';
    const method = editingClientId ? 'PUT' : 'POST';
    await request(path, { method, body: JSON.stringify(clientForm) });
    setClientForm(emptyClient);
    setEditingClientId(null);
    setMessage('Cliente guardado correctamente');
    await load();
  }

 // Elimina un cliente

  async function deleteClient(id) {
    await request(`/clients/${id}`, { method: 'DELETE' });
    setMessage('Cliente eliminado con sus solicitudes asociadas');
    await load();
  }

  // Carga los datos del cliente para edicion

  function editClient(client) {
    setClientForm(client);
    setEditingClientId(client.id);
    setTab('clients');
  }

  // Registra una nueva solicitud de prestamo

  async function createApplication(event) {
    event.preventDefault();
    await request('/loan-applications', { method: 'POST', body: JSON.stringify(applicationForm) });
    setApplicationForm(emptyApplication);
    setMessage('Solicitud registrada');
    await load();
  }

  // Actualiza los campos del formulario de decision

  function updateDecisionForm(id, field, value) {
    setDecisionForms(forms => ({
      ...forms,
      [id]: { details: '', annualInterestRate: '12', ...(forms[id] || {}), [field]: value }
    }));
  }

// Aprueba o rechaza una solicitud

  async function decide(id, approved) {
    const form = decisionForms[id] || {};
    const details = form.details?.trim();
    if (!details) {
      setMessage('Ingresa el detalle de la decision');
      return;
    }
    await request(`/loan-applications/${id}/${approved ? 'approve' : 'reject'}`, {
      method: 'POST',
      body: JSON.stringify({ details, annualInterestRate: approved ? Number(form.annualInterestRate || 0) : 0 })
    });
    setDecisionForms(forms => {
      const next = { ...forms };
      delete next[id];
      return next;
    });
    setMessage(approved ? 'Solicitud aprobada' : 'Solicitud rechazada');
    await load();
  }

// Registra un pago de prestamo

  async function registerPayment(event) {
    event.preventDefault();
    await request(`/approved-loans/${paymentForm.loanId}/payments`, {
      method: 'POST',
      body: JSON.stringify({ amount: paymentForm.amount, receiptNumber: paymentForm.receiptNumber, paymentType: paymentForm.paymentType })
    });
    setPaymentForm({ loanId: '', amount: '', receiptNumber: '', paymentType: 'CUOTA' });
    setMessage('Pago registrado');
    await load();
  }

  // Selecciona un prestamo para realizar pagos

  function selectLoanForPayment(loanId) {
    const loan = loans.find(item => item.id === Number(loanId));
    setPaymentForm(form => ({
      ...form,
      loanId,
      amount: loan && form.paymentType === 'CUOTA' ? installmentAmount(loan) : form.amount
    }));
  }

// Cambia el tipo de pago

  function changePaymentType(paymentType) {
    setPaymentForm(form => ({
      ...form,
      paymentType,
      amount: paymentType === 'CUOTA' && selectedLoan ? installmentAmount(selectedLoan) : ''
    }));
  }

// Retorna toda la interfaz grafica de la aplicacion

  return (
    <main className="shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Credito Hipotecario Nacional</p>
          <h1>Sistema de prestamos bancarios</h1>
        </div>
        <nav className="tabs">
          <button className={tab === 'clients' ? 'active' : ''} onClick={() => setTab('clients')}><Users size={18} /> Clientes</button>
          <button className={tab === 'applications' ? 'active' : ''} onClick={() => setTab('applications')}><ClipboardList size={18} /> Solicitudes</button>
          <button className={tab === 'loans' ? 'active' : ''} onClick={() => setTab('loans')}><Banknote size={18} /> Prestamos</button>
        </nav>
      </header>

      {message && <button className="toast" onClick={() => setMessage('')}>{message}</button>}

      {tab === 'clients' && (
        <section className="grid">
          <form className="panel form" onSubmit={saveClient}>
            <h2><UserPlus size={20} /> {editingClientId ? 'Editar cliente' : 'Nuevo cliente'}</h2>
            {Object.entries({
              firstName: 'Nombre', lastName: 'Apellido', identificationNumber: 'Identificacion',
              birthDate: 'Fecha de nacimiento', address: 'Direccion', email: 'Correo electronico', phone: 'Telefono'
            }).map(([key, label]) => (
              <label key={key}>{label}
                <input required type={key === 'birthDate' ? 'date' : key === 'email' ? 'email' : 'text'} value={clientForm[key] || ''} onChange={e => setClientForm({ ...clientForm, [key]: e.target.value })} />
              </label>
            ))}
            <button className="primary"><Check size={18} /> Guardar</button>
          </form>

          <section className="panel table-panel">
            <h2><Users size={20} /> Clientes registrados</h2>
            <div className="table">
              {clients.map(client => (
                <article className="row" key={client.id}>
                  <div><strong>{client.firstName} {client.lastName}</strong><span>{client.email} | {client.phone}</span><span>{client.identificationNumber}</span></div>
                  <div className="actions">
                    <button title="Editar" onClick={() => editClient(client)}><Pencil size={18} /></button>
                    <button title="Eliminar" onClick={() => deleteClient(client.id)}><Trash2 size={18} /></button>
                  </div>
                </article>
              ))}
            </div>
          </section>
        </section>
      )}

      {tab === 'applications' && (
        <section className="grid">
          <form className="panel form" onSubmit={createApplication}>
            <h2><CircleDollarSign size={20} /> Solicitar prestamo</h2>
            <label>Cliente
              <select required value={applicationForm.clientId} onChange={e => setApplicationForm({ ...applicationForm, clientId: e.target.value })}>
                <option value="">Seleccione</option>
                {clients.map(client => <option key={client.id} value={client.id}>{client.firstName} {client.lastName}</option>)}
              </select>
            </label>
            <label>Monto solicitado<input required type="number" min="1" step="0.01" value={applicationForm.requestedAmount} onChange={e => setApplicationForm({ ...applicationForm, requestedAmount: e.target.value })} /></label>
            <label>Plazo en meses<input required type="number" min="1" value={applicationForm.termMonths} onChange={e => setApplicationForm({ ...applicationForm, termMonths: e.target.value })} /></label>
            <label>Destino<textarea required value={applicationForm.purpose} onChange={e => setApplicationForm({ ...applicationForm, purpose: e.target.value })} /></label>
            <button className="primary"><Check size={18} /> Registrar solicitud</button>
            {selectedClient && <p className="hint">Solicitud para {selectedClient.firstName} {selectedClient.lastName}</p>}
          </form>

          <section className="panel table-panel">
            <h2><ClipboardList size={20} /> Solicitudes</h2>
            <div className="cards">
              {applications.map(app => (
                <article className="loan-card" key={app.id}>
                  <div><strong>{app.client?.firstName} {app.client?.lastName}</strong><span>{money(app.requestedAmount)} | {app.termMonths} meses</span></div>
                  <span className={`status ${app.status}`}>{app.status.replace('_', ' ')}</span>
                  <p>{app.purpose}</p>
                  {app.status === 'EN_PROCESO' && (
                    <div className="decision-box">
                      <label>Detalle de decision
                        <input value={decisionForms[app.id]?.details || ''} onChange={e => updateDecisionForm(app.id, 'details', e.target.value)} placeholder="Ej. Aprobado por capacidad de pago" />
                      </label>
                      <label>Tasa anual (%)
                        <input type="number" min="0" step="0.01" value={decisionForms[app.id]?.annualInterestRate || '12'} onChange={e => updateDecisionForm(app.id, 'annualInterestRate', e.target.value)} />
                      </label>
                      <div className="actions"><button onClick={() => decide(app.id, true)}><BadgeCheck size={18} /> Aprobar</button><button onClick={() => decide(app.id, false)}><X size={18} /> Rechazar</button></div>
                    </div>
                  )}
                  {app.decisionDetails && <small>{app.decisionDetails}</small>}
                </article>
              ))}
            </div>
          </section>
        </section>
      )}

      {tab === 'loans' && (
        <section className="grid">
          <form className="panel form" onSubmit={registerPayment}>
            <h2><Banknote size={20} /> Registrar pago</h2>
            <label>Prestamo
              <select required value={paymentForm.loanId} onChange={e => selectLoanForPayment(e.target.value)}>
                <option value="">Seleccione</option>
                {loans.filter(loan => loan.paymentStatus !== 'PAGADO').map(loan => <option key={loan.id} value={loan.id}>#{loan.id} - {loan.application?.client?.firstName} - cuota {money(installmentAmount(loan))}</option>)}
              </select>
            </label>
            <label>Tipo de pago
              <select required value={paymentForm.paymentType} onChange={e => changePaymentType(e.target.value)}>
                <option value="CUOTA">Cuota fija</option>
                <option value="ABONO_CAPITAL">Abono a capital</option>
              </select>
            </label>
            <label>Monto<input required readOnly={paymentForm.paymentType === 'CUOTA'} type="number" min="0.01" step="0.01" value={paymentForm.amount} onChange={e => setPaymentForm({ ...paymentForm, amount: e.target.value })} /></label>
            <label>Recibo<input required value={paymentForm.receiptNumber} onChange={e => setPaymentForm({ ...paymentForm, receiptNumber: e.target.value })} /></label>
            {selectedLoan && <p className="hint">Cuota que toca: {money(installmentAmount(selectedLoan))} | Saldo capital: {money(selectedLoan.pendingBalance)}</p>}
            <button className="primary"><Check size={18} /> Registrar pago</button>
          </form>

          <section className="panel table-panel">
            <h2><BadgeCheck size={20} /> Prestamos aprobados</h2>
            <div className="cards">
              {loans.map(loan => (
                <article className="loan-card" key={loan.id}>
                  <div><strong>{loan.application?.client?.firstName} {loan.application?.client?.lastName}</strong><span>Capital {money(loan.principalAmount)} | tasa {loan.annualInterestRate}%</span></div>
                  <span className={`status ${loan.paymentStatus}`}>{loan.paymentStatus}</span>
                  <p>Cuota fija: <strong>{money(loan.monthlyPayment)}</strong> | Cuota que toca: <strong>{money(installmentAmount(loan))}</strong> | Saldo capital: <strong>{money(loan.pendingBalance)}</strong></p>
                  <small>Capital pagado: {money(loan.principalPaid)} | Interes pagado: {money(loan.interestPaid)} | Total recibido: {money(loan.totalPaid)}</small>
                  <details className="payment-plan">
                    <summary>Plan de pagos estimado</summary>
                    <div className="mini-table">
                      <div className="mini-head"><span>Mes</span><span>Cuota</span><span>Capital</span><span>Interes</span><span>Saldo</span></div>
                      {buildPaymentPlan(loan).map(row => (
                        <div className="mini-row" key={row.month}>
                          <span>{row.month}</span><span>{money(row.payment)}</span><span>{money(row.principal)}</span><span>{money(row.interest)}</span><span>{money(row.balance)}</span>
                        </div>
                      ))}
                    </div>
                  </details>
                  {loan.payments?.length > 0 && (
                    <details className="payment-plan">
                      <summary>Pagos realizados</summary>
                      <div className="mini-table">
                        <div className="mini-head"><span>Fecha</span><span>Tipo</span><span>Monto</span><span>Capital</span><span>Interes</span></div>
                        {loan.payments.map(payment => (
                          <div className="mini-row" key={payment.id}>
                            <span>{payment.paymentDate}</span><span>{payment.paymentType}</span><span>{money(payment.amount)}</span><span>{money(payment.principalApplied)}</span><span>{money(payment.interestApplied)}</span>
                          </div>
                        ))}
                      </div>
                    </details>
                  )}
                </article>
              ))}
            </div>
          </section>
        </section>
      )}
    </main>
  );
}

createRoot(document.getElementById('root')).render(<App />);
