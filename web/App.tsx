import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext'; // Corrected path
import { useAuth } from './hooks/useAuth'; // Corrected path

// Common Components
import Login from './components/auth/LoginForm'; // Corrected path
import Register from './components/auth/RegisterForm'; // Corrected path
import MainLayout from './components/layout/MainLayout'; // Assuming a layout component exists

// Role-Specific Pages/Components (Adjust paths as needed based on actual structure)
import PatientDashboard from './pages/Patient/Dashboard'; // Corrected path
// import NewAppointment from './components/patient/AppointmentForm';
// import RecurrentAppointment from './components/patient/RecurrentAppointmentForm';
// import MyAppointments from './components/patient/AppointmentList';
// import CancelAppointment from './components/patient/CancelAppointmentForm';

import PsychologistDashboard from './pages/Psychologist/Dashboard'; // Corrected path
// import PsychologistSchedule from './components/psychologist/DateSchedule';
// import PsychologistCancelAppointment from './components/psychologist/CancelAppointmentForm';

import AdminDashboard from './pages/Admin/Dashboard'; // Corrected path
// import RegisterPsychologist from './components/admin/PsychologistForm';
// import AllAppointmentsPage from './components/admin/AllAppointments';

// --- DONO_CLINICA Components (Create these) ---
import DonoClinicaDashboard from './pages/DonoClinica/Dashboard'; // Example path
import DonoClinicaAgenda from './pages/DonoClinica/Agenda'; // Example path
import DonoClinicaNovoAgendamento from './pages/DonoClinica/NovoAgendamento'; // Example path
import DonoClinicaDetalhesAgendamento from './pages/DonoClinica/DetalhesAgendamento'; // Example path

// Componente para rotas protegidas
const ProtectedRoute = ({ children, allowedRoles }: { children: JSX.Element, allowedRoles: string[] }) => {
  const { isAuthenticated, userRole, loading } = useAuth();

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  if (!isAuthenticated) {
    console.log('ProtectedRoute: Not authenticated, redirecting to /login');
    return <Navigate to="/login" replace />;
  }

  // Ensure userRole is one of the expected enum values from backend
  const validRole = userRole && allowedRoles.includes(userRole);
  console.log(`ProtectedRoute: Role check - UserRole: ${userRole}, Allowed: ${allowedRoles}, Valid: ${validRole}`);

  if (validRole) {
    return children;
  }

  console.log('ProtectedRoute: Role not allowed, redirecting to /');
  // Redirect to a generic dashboard or home if role doesn't match
  // Or potentially show an 'Unauthorized' page
  return <Navigate to="/" replace />;
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
          
          {/* Rotas Protegidas com Layout */}
          <Route element={<MainLayout />}> {/* Wrap protected routes in a layout */}
            {/* Rotas de paciente */}
            <Route 
              path="/paciente/dashboard" 
              element={
                <ProtectedRoute allowedRoles={['PACIENTE']}>
                  <PatientDashboard />
                </ProtectedRoute>
              } 
            />
            {/* Add other patient routes here... */}
            
            {/* Rotas de psicóloga/profissional */}
            <Route 
              path="/profissional/dashboard" 
              element={
                // Assuming PROFISSIONAL is the role name used in backend
                <ProtectedRoute allowedRoles={['PROFISSIONAL']}> 
                  <PsychologistDashboard />
                </ProtectedRoute>
              } 
            />
             {/* Add other professional routes here... */}

            {/* Rotas de DONO_CLINICA */}
            <Route 
              path="/dono-clinica/dashboard" 
              element={
                <ProtectedRoute allowedRoles={['DONO_CLINICA']}>
                  <DonoClinicaDashboard />
                </ProtectedRoute>
              } 
            />
             <Route 
              path="/dono-clinica/agenda" 
              element={
                <ProtectedRoute allowedRoles={['DONO_CLINICA']}>
                  <DonoClinicaAgenda />
                </ProtectedRoute>
              } 
            />
             <Route 
              path="/dono-clinica/agendamentos/novo" 
              element={
                <ProtectedRoute allowedRoles={['DONO_CLINICA', 'ADMIN', 'ATENDENTE']}> {/* Allow others too? */} 
                  <DonoClinicaNovoAgendamento />
                </ProtectedRoute>
              } 
            />
             <Route 
              path="/dono-clinica/agendamentos/:id" 
              element={
                <ProtectedRoute allowedRoles={['DONO_CLINICA', 'ADMIN', 'ATENDENTE']}> {/* Allow others too? */} 
                  <DonoClinicaDetalhesAgendamento />
                </ProtectedRoute>
              } 
            />
            {/* Add other dono_clinica routes here (e.g., cancelamento) */}
            
            {/* Rotas de admin */}
            <Route 
              path="/admin/dashboard" 
              element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminDashboard />
                </ProtectedRoute>
              } 
            />
            {/* Add other admin routes here... */}
          </Route> {/* End of MainLayout routes */}

          {/* Catch-all or Not Found route - Optional */}
          {/* <Route path="*" element={<NotFound />} /> */}
        </Routes>
      </Router>
    </AuthProvider>
  );
}

// Componente para redirecionar com base no papel do usuário após login
const RoleBasedRedirect = () => {
  const { isAuthenticated, userRole, loading } = useAuth();

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  console.log(`RoleBasedRedirect: Authenticated, UserRole: ${userRole}`);

  // Use the role names exactly as defined in the backend enum/database
  switch (userRole) {
    case 'PACIENTE':
      return <Navigate to="/paciente/dashboard" replace />;
    case 'PROFISSIONAL': // Changed from PSICOLOGA to match backend service likely role
      return <Navigate to="/profissional/dashboard" replace />;
    case 'DONO_CLINICA':
      return <Navigate to="/dono-clinica/dashboard" replace />;
    case 'ADMIN':
      return <Navigate to="/admin/dashboard" replace />;
    case 'ATENDENTE': // Add atendente if they have a specific dashboard
       return <Navigate to="/atendente/dashboard" replace />; // Example path
    default:
      console.warn(`RoleBasedRedirect: Unknown or unhandled role: ${userRole}, redirecting to login.`);
      // Fallback for unknown roles or if userRole is null/undefined after auth
      return <Navigate to="/login" replace />;
  }
};

export default App;

