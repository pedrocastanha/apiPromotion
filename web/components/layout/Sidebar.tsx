import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  Home, 
  Calendar, 
  Users, 
  Settings, 
  FileText, 
  Clock, 
  User, 
  Building, 
  LogOut,
  X
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { Button } from '../ui/button';

interface SidebarProps {
  isOpen: boolean;
  userRole: string;
  onClose: () => void;
}

interface SidebarItem {
  title: string;
  icon: React.ReactNode;
  path: string;
  roles: string[];
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, userRole, onClose }) => {
  const { logout } = useAuth();
  const location = useLocation();
  
  const menuItems: SidebarItem[] = [
    // Itens comuns a todos os perfis
    {
      title: 'Dashboard',
      icon: <Home className="h-5 w-5" />,
      path: `/${userRole.toLowerCase() === 'psicologa' ? 'psicologa' : userRole.toLowerCase() === 'admin' ? 'admin' : 'paciente'}/dashboard`,
      roles: ['PACIENTE', 'PSICOLOGA', 'ADMIN']
    },
    {
      title: 'Meu Perfil',
      icon: <User className="h-5 w-5" />,
      path: `/${userRole.toLowerCase() === 'psicologa' ? 'psicologa' : userRole.toLowerCase() === 'admin' ? 'admin' : 'paciente'}/perfil`,
      roles: ['PACIENTE', 'PSICOLOGA', 'ADMIN']
    },
    
    // Itens específicos para pacientes
    {
      title: 'Novo Agendamento',
      icon: <Calendar className="h-5 w-5" />,
      path: '/paciente/novo-agendamento',
      roles: ['PACIENTE']
    },
    {
      title: 'Meus Agendamentos',
      icon: <Clock className="h-5 w-5" />,
      path: '/paciente/meus-agendamentos',
      roles: ['PACIENTE']
    },
    {
      title: 'Agendamento Recorrente',
      icon: <Calendar className="h-5 w-5" />,
      path: '/paciente/agendamento-recorrente',
      roles: ['PACIENTE']
    },
    
    // Itens específicos para psicólogos
    {
      title: 'Agenda do Dia',
      icon: <Calendar className="h-5 w-5" />,
      path: '/psicologa/dashboard',
      roles: ['PSICOLOGA']
    },
    {
      title: 'Agenda Completa',
      icon: <Calendar className="h-5 w-5" />,
      path: '/psicologa/agenda',
      roles: ['PSICOLOGA']
    },
    {
      title: 'Pacientes',
      icon: <Users className="h-5 w-5" />,
      path: '/psicologa/pacientes',
      roles: ['PSICOLOGA']
    },
    {
      title: 'Atendimentos',
      icon: <FileText className="h-5 w-5" />,
      path: '/psicologa/atendimentos',
      roles: ['PSICOLOGA']
    },
    {
      title: 'Horários',
      icon: <Clock className="h-5 w-5" />,
      path: '/psicologa/horarios',
      roles: ['PSICOLOGA']
    },
    
    // Itens específicos para administradores
    {
      title: 'Clínicas',
      icon: <Building className="h-5 w-5" />,
      path: '/admin/clinicas',
      roles: ['ADMIN']
    },
    {
      title: 'Profissionais',
      icon: <Users className="h-5 w-5" />,
      path: '/admin/cadastrar-psicologa',
      roles: ['ADMIN']
    },
    {
      title: 'Agendamentos',
      icon: <Calendar className="h-5 w-5" />,
      path: '/admin/agendamentos',
      roles: ['ADMIN']
    },
    {
      title: 'Relatórios',
      icon: <FileText className="h-5 w-5" />,
      path: '/admin/relatorios',
      roles: ['ADMIN']
    },
    {
      title: 'Configurações',
      icon: <Settings className="h-5 w-5" />,
      path: '/admin/configuracoes',
      roles: ['ADMIN']
    },
  ];

  const filteredMenuItems = menuItems.filter(item => item.roles.includes(userRole));

  const handleLogout = () => {
    logout();
    // Redirecionamento para login é tratado pelo AuthContext
  };

  return (
    <>
      {/* Overlay para mobile */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-20 md:hidden"
          onClick={onClose}
        />
      )}
      
      {/* Sidebar */}
      <aside 
        className={`fixed top-0 left-0 z-30 h-full w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        } md:translate-x-0 pt-16`}
      >
        <div className="p-4">
          <div className="flex justify-between items-center mb-6 md:hidden">
            <h2 className="text-xl font-semibold text-gray-800">Menu</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
              <X className="h-5 w-5" />
            </button>
          </div>
          
          <nav>
            <ul className="space-y-2">
              {filteredMenuItems.map((item, index) => (
                <li key={index}>
                  <Link
                    to={item.path}
                    className={`flex items-center p-2 rounded-lg ${
                      location.pathname === item.path
                        ? 'bg-blue-100 text-blue-700'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    {item.icon}
                    <span className="ml-3">{item.title}</span>
                  </Link>
                </li>
              ))}
              
              <li className="pt-4 mt-4 border-t border-gray-200">
                <Button
                  variant="ghost"
                  className="flex w-full items-center p-2 text-gray-700 hover:bg-gray-100 rounded-lg"
                  onClick={handleLogout}
                >
                  <LogOut className="h-5 w-5" />
                  <span className="ml-3">Sair</span>
                </Button>
              </li>
            </ul>
          </nav>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
