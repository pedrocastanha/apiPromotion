import React, { ReactNode, useState } from 'react';
import Navbar from '../common/Navbar';
import Sidebar from './Sidebar';
import { useAuth } from '../../hooks/useAuth';
import { useMobile } from '../../hooks/use-mobile';

interface MainLayoutProps {
  children: ReactNode;
  title?: string;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children, title }) => {
  const { user, userRole } = useAuth();
  const isMobile = useMobile();
  const [sidebarOpen, setSidebarOpen] = useState(!isMobile);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar 
        toggleSidebar={toggleSidebar} 
        title={title || 'Clínica Psico'} 
      />
      
      <div className="flex">
        <Sidebar 
          isOpen={sidebarOpen} 
          userRole={userRole || 'PACIENTE'} 
          onClose={() => isMobile && setSidebarOpen(false)}
        />
        
        <main className={`flex-1 p-4 transition-all duration-300 ${sidebarOpen && !isMobile ? 'ml-64' : 'ml-0'}`}>
          <div className="container mx-auto">
            {title && (
              <h1 className="text-2xl font-bold mb-6 text-gray-800">{title}</h1>
            )}
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
