import React from 'react';
import { useAuth } from '../../hooks/useAuth';

const Navbar: React.FC = () => {
  const { isAuthenticated, userRole, logout } = useAuth();

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <nav className="bg-blue-600 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <span className="text-xl font-semibold">Psico Agendamentos</span>
          </div>
          
          <div className="flex items-center">
            {userRole === 'PACIENTE' && (
              <div className="hidden md:flex space-x-4 mr-4">
                <a href="/paciente/dashboard" className="px-3 py-2 rounded-md hover:bg-blue-700">Dashboard</a>
                <a href="/paciente/meus-agendamentos" className="px-3 py-2 rounded-md hover:bg-blue-700">Meus Agendamentos</a>
                <a href="/paciente/novo-agendamento" className="px-3 py-2 rounded-md hover:bg-blue-700">Novo Agendamento</a>
              </div>
            )}
            
            {userRole === 'PSICOLOGA' && (
              <div className="hidden md:flex space-x-4 mr-4">
                <a href="/psicologa/dashboard" className="px-3 py-2 rounded-md hover:bg-blue-700">Dashboard</a>
                <a href="/psicologa/agenda" className="px-3 py-2 rounded-md hover:bg-blue-700">Agenda</a>
              </div>
            )}
            
            {userRole === 'ADMIN' && (
              <div className="hidden md:flex space-x-4 mr-4">
                <a href="/admin/dashboard" className="px-3 py-2 rounded-md hover:bg-blue-700">Dashboard</a>
                <a href="/admin/agendamentos" className="px-3 py-2 rounded-md hover:bg-blue-700">Agendamentos</a>
                <a href="/admin/cadastrar-psicologa" className="px-3 py-2 rounded-md hover:bg-blue-700">Cadastrar Psicóloga</a>
              </div>
            )}
            
            <button
              onClick={handleLogout}
              className="px-3 py-2 rounded-md bg-red-600 hover:bg-red-700"
            >
              Sair
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
