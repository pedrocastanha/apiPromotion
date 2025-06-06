import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../../components/ui/tabs';
import { Users, Building, Calendar, TrendingUp, AlertCircle } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

// Componente para estatísticas
const StatCard = ({ title, value, icon, description, color }: { 
  title: string, 
  value: string | number, 
  icon: React.ReactNode, 
  description?: string,
  color: string 
}) => (
  <Card>
    <CardHeader className={`bg-${color}-50 rounded-t-lg`}>
      <CardTitle className={`flex items-center text-${color}-700 text-lg`}>
        {icon}
        <span className="ml-2">{title}</span>
      </CardTitle>
    </CardHeader>
    <CardContent className="pt-4">
      <p className="text-3xl font-bold">{value}</p>
      {description && <p className="text-sm text-gray-500 mt-1">{description}</p>}
    </CardContent>
  </Card>
);

// Componente para alertas
const AlertCard = ({ title, alerts }: { title: string, alerts: { message: string, type: string }[] }) => (
  <Card>
    <CardHeader className="bg-red-50 rounded-t-lg">
      <CardTitle className="flex items-center text-red-700">
        <AlertCircle className="mr-2 h-5 w-5" />
        {title}
      </CardTitle>
    </CardHeader>
    <CardContent className="pt-4">
      {alerts.length > 0 ? (
        <ul className="space-y-2">
          {alerts.map((alert, index) => (
            <li key={index} className={`p-2 rounded-md ${
              alert.type === 'high' ? 'bg-red-100 text-red-800' : 
              alert.type === 'medium' ? 'bg-yellow-100 text-yellow-800' : 
              'bg-blue-100 text-blue-800'
            }`}>
              {alert.message}
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500">Nenhum alerta no momento.</p>
      )}
    </CardContent>
  </Card>
);

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  
  // Dados de exemplo - em produção viriam da API
  const stats = {
    clinics: 3,
    professionals: 12,
    patients: 145,
    appointmentsToday: 28,
    appointmentsWeek: 124,
    occupancyRate: '78%'
  };
  
  const alerts = [
    { message: '3 profissionais sem horários configurados', type: 'high' },
    { message: '5 agendamentos sem confirmação para hoje', type: 'medium' },
    { message: 'Atualização de sistema disponível', type: 'low' }
  ];
  
  // Lista de clínicas de exemplo
  const clinics = [
    { id: 1, name: 'Clínica Central', professionals: 5, patients: 78, appointmentsWeek: 42 },
    { id: 2, name: 'Clínica Norte', professionals: 4, patients: 45, appointmentsWeek: 36 },
    { id: 3, name: 'Clínica Sul', professionals: 3, patients: 22, appointmentsWeek: 18 }
  ];

  return (
    <MainLayout title={`Painel Administrativo`}>
      <div className="mb-6">
        <h2 className="text-lg text-gray-600">Olá, {user?.nome || 'Administrador'}</h2>
        <p className="text-sm text-gray-500">Aqui está o resumo do sistema</p>
      </div>
      
      <Tabs defaultValue={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="overview">Visão Geral</TabsTrigger>
          <TabsTrigger value="clinics">Clínicas</TabsTrigger>
          <TabsTrigger value="appointments">Agendamentos</TabsTrigger>
        </TabsList>
        
        {/* Tab: Visão Geral */}
        <TabsContent value="overview" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            <StatCard 
              title="Clínicas" 
              value={stats.clinics} 
              icon={<Building className="h-5 w-5" />}
              color="blue"
            />
            <StatCard 
              title="Profissionais" 
              value={stats.professionals} 
              icon={<Users className="h-5 w-5" />}
              color="purple"
            />
            <StatCard 
              title="Pacientes" 
              value={stats.patients} 
              icon={<Users className="h-5 w-5" />}
              color="green"
            />
            <StatCard 
              title="Agendamentos Hoje" 
              value={stats.appointmentsToday} 
              icon={<Calendar className="h-5 w-5" />}
              color="amber"
            />
            <StatCard 
              title="Agendamentos Semana" 
              value={stats.appointmentsWeek} 
              icon={<Calendar className="h-5 w-5" />}
              color="indigo"
            />
            <StatCard 
              title="Taxa de Ocupação" 
              value={stats.occupancyRate} 
              icon={<TrendingUp className="h-5 w-5" />}
              description="Média de todas as clínicas"
              color="emerald"
            />
          </div>
          
          <div className="grid gap-6 md:grid-cols-3">
            <div className="md:col-span-2">
              <Card>
                <CardHeader>
                  <CardTitle>Ações Rápidas</CardTitle>
                  <CardDescription>Acesse as principais funcionalidades</CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4 md:grid-cols-2">
                  <Button 
                    className="w-full justify-start" 
                    onClick={() => navigate('/admin/cadastrar-psicologa')}
                  >
                    <Users className="mr-2 h-5 w-5" />
                    Cadastrar Profissional
                  </Button>
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => navigate('/admin/clinicas')}
                  >
                    <Building className="mr-2 h-5 w-5" />
                    Gerenciar Clínicas
                  </Button>
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => navigate('/admin/agendamentos')}
                  >
                    <Calendar className="mr-2 h-5 w-5" />
                    Ver Agendamentos
                  </Button>
                  <Button 
                    variant="outline" 
                    className="w-full justify-start"
                    onClick={() => navigate('/admin/relatorios')}
                  >
                    <TrendingUp className="mr-2 h-5 w-5" />
                    Gerar Relatórios
                  </Button>
                </CardContent>
              </Card>
            </div>
            
            <div className="md:col-span-1">
              <AlertCard title="Alertas" alerts={alerts} />
            </div>
          </div>
        </TabsContent>
        
        {/* Tab: Clínicas */}
        <TabsContent value="clinics">
          <div className="space-y-6">
            <div className="flex justify-between items-center">
              <h3 className="text-lg font-medium">Clínicas Cadastradas</h3>
              <Button onClick={() => navigate('/admin/clinicas')}>
                Ver Todas
              </Button>
            </div>
            
            <div className="grid gap-6 md:grid-cols-3">
              {clinics.map(clinic => (
                <Card key={clinic.id}>
                  <CardHeader>
                    <CardTitle>{clinic.name}</CardTitle>
                    <CardDescription>ID: {clinic.id}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      <div className="flex justify-between">
                        <span className="text-gray-500">Profissionais:</span>
                        <span>{clinic.professionals}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-500">Pacientes:</span>
                        <span>{clinic.patients}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-500">Agendamentos (semana):</span>
                        <span>{clinic.appointmentsWeek}</span>
                      </div>
                    </div>
                  </CardContent>
                  <CardFooter>
                    <Button variant="outline" className="w-full" onClick={() => navigate(`/admin/clinicas/${clinic.id}`)}>
                      Gerenciar
                    </Button>
                  </CardFooter>
                </Card>
              ))}
            </div>
            
            <div className="flex justify-center mt-4">
              <Button onClick={() => navigate('/admin/clinicas/nova')}>
                Adicionar Nova Clínica
              </Button>
            </div>
          </div>
        </TabsContent>
        
        {/* Tab: Agendamentos */}
        <TabsContent value="appointments">
          <div className="space-y-6">
            <div className="flex justify-between items-center">
              <h3 className="text-lg font-medium">Agendamentos Recentes</h3>
              <Button onClick={() => navigate('/admin/agendamentos')}>
                Ver Todos
              </Button>
            </div>
            
            <Card>
              <CardHeader>
                <CardTitle>Resumo de Agendamentos</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center p-2 bg-green-50 rounded-md">
                    <span>Agendamentos confirmados hoje</span>
                    <span className="font-bold">{stats.appointmentsToday - 5}</span>
                  </div>
                  <div className="flex justify-between items-center p-2 bg-yellow-50 rounded-md">
                    <span>Agendamentos pendentes de confirmação</span>
                    <span className="font-bold">5</span>
                  </div>
                  <div className="flex justify-between items-center p-2 bg-red-50 rounded-md">
                    <span>Cancelamentos (últimos 7 dias)</span>
                    <span className="font-bold">8</span>
                  </div>
                </div>
              </CardContent>
              <CardFooter>
                <Button variant="outline" className="w-full" onClick={() => navigate('/admin/relatorios')}>
                  Gerar Relatório Detalhado
                </Button>
              </CardFooter>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </MainLayout>
  );
};

export default AdminDashboard;
