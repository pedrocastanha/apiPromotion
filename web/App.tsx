import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { useAuth } from './hooks/useAuth';

// Páginas
import Login from './components/auth/LoginForm';
import Register from './components/auth/RegisterForm';
import PatientDashboard from './pages/Patient/Dashboard';
import NewAppointment from './components/patient/AppointmentForm';
import RecurrentAppointment from './components/patient/RecurrentAppointmentForm';
import MyAppointments from './components/patient/AppointmentList';
import CancelAppointment from './components/patient/CancelAppointmentForm';
import PsychologistDashboard from './components/psychologist/DailySchedule';
import PsychologistSchedule from './components/psychologist/DateSchedule';
import PsychologistCancelAppointment from './components/psychologist/CancelAppointmentForm';
import AdminDashboard from './pages/Admin/Dashboard';
import RegisterPsychologist from './components/admin/PsychologistForm';
import AllAppointmentsPage from './components/admin/AllAppointments';

// Componente para rotas protegidas
const ProtectedRoute = ({ children, allowedRoles }: { children: JSX.Element, allowedRoles: string[] }) => {
  const { isAuthenticated, userRole, loading } = useAuth();

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (userRole && allowedRoles.includes(userRole)) {
    return children;
  }

  return <Navigate to="/" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Rotas públicas */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Rota padrão - redireciona com base no papel do usuário */}
          <Route path="/" element={<RoleBasedRedirect />} />
          
          {/* Rotas de paciente */}
          <Route 
            path="/paciente/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['PACIENTE']}>
                <PatientDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/paciente/novo-agendamento" 
            element={
              <ProtectedRoute allowedRoles={['PACIENTE']}>
                <NewAppointment />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/paciente/agendamento-recorrente" 
            element={
              <ProtectedRoute allowedRoles={['PACIENTE']}>
                <RecurrentAppointment />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/paciente/meus-agendamentos" 
            element={
              <ProtectedRoute allowedRoles={['PACIENTE']}>
                <MyAppointments />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/paciente/cancelar-agendamento/:id" 
            element={
              <ProtectedRoute allowedRoles={['PACIENTE']}>
                <CancelAppointment />
              </ProtectedRoute>
            } 
          />
          
          {/* Rotas de psicóloga */}
          <Route 
            path="/psicologa/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['PSICOLOGA']}>
                <PsychologistDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/psicologa/agenda" 
            element={
              <ProtectedRoute allowedRoles={['PSICOLOGA']}>
                <PsychologistSchedule />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/psicologa/cancelar-agendamento/:id" 
            element={
              <ProtectedRoute allowedRoles={['PSICOLOGA']}>
                <PsychologistCancelAppointment />
              </ProtectedRoute>
            } 
          />
          
          {/* Rotas de admin */}
          <Route 
            path="/admin/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin/cadastrar-psicologa" 
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <RegisterPsychologist />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin/agendamentos" 
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AllAppointmentsPage />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

// Componente para redirecionar com base no papel do usuário
const RoleBasedRedirect = () => {
  const { isAuthenticated, userRole, loading } = useAuth();

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  switch (userRole) {
    case 'PACIENTE':
      return <Navigate to="/paciente/dashboard" />;
    case 'PSICOLOGA':
      return <Navigate to="/psicologa/dashboard" />;
    case 'ADMIN':
      return <Navigate to="/admin/dashboard" />;
    default:
      return <Navigate to="/login" />;
  }
};

export default App;
