import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Calendar, Clock, Users, FileText, CheckCircle, XCircle } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

// Componente para exibir um agendamento
const AppointmentCard = ({ appointment, onViewDetails }: { 
  appointment: any, 
  onViewDetails: (id: string) => void 
}) => (
  <Card className="mb-4">
    <CardHeader className="pb-2">
      <div className="flex justify-between items-start">
        <div>
          <CardTitle className="text-lg">{appointment.pacienteNome}</CardTitle>
          <CardDescription>
            {format(new Date(appointment.dataHoraInicio), "dd 'de' MMMM', às' HH:mm", { locale: ptBR })}
          </CardDescription>
        </div>
        <div className={`px-2 py-1 rounded-full text-xs font-medium ${
          appointment.status === 'CONFIRMADO' ? 'bg-green-100 text-green-800' : 
          appointment.status === 'AGENDADO' ? 'bg-blue-100 text-blue-800' : 
          appointment.status === 'CANCELADO_PACIENTE' || appointment.status === 'CANCELADO_CLINICA' ? 'bg-red-100 text-red-800' :
          'bg-gray-100 text-gray-800'
        }`}>
          {appointment.status === 'CONFIRMADO' ? 'Confirmado' : 
           appointment.status === 'AGENDADO' ? 'Agendado' : 
           appointment.status === 'CANCELADO_PACIENTE' ? 'Cancelado pelo paciente' :
           appointment.status === 'CANCELADO_CLINICA' ? 'Cancelado pela clínica' :
           appointment.status}
        </div>
      </div>
    </CardHeader>
    <CardContent className="pb-2">
      {appointment.procedimentoNome && (
        <p className="text-sm text-gray-600 mb-2">
          <span className="font-medium">Procedimento:</span> {appointment.procedimentoNome}
        </p>
      )}
      {appointment.observacoes && (
        <p className="text-sm text-gray-600">
          <span className="font-medium">Observações:</span> {appointment.observacoes}
        </p>
      )}
    </CardContent>
    <CardFooter className="pt-2 flex justify-between">
      <Button 
        variant="outline" 
        size="sm"
        onClick={() => onViewDetails(appointment.id)}
      >
        Ver Detalhes
      </Button>
      <div className="flex space-x-2">
        <Button 
          variant="ghost" 
          size="sm"
          className="text-green-600 hover:text-green-700 hover:bg-green-50"
          onClick={() => {/* Marcar como realizado */}}
        >
          <CheckCircle className="h-4 w-4 mr-1" />
          Realizado
        </Button>
        <Button 
          variant="ghost" 
          size="sm"
          className="text-red-600 hover:text-red-700 hover:bg-red-50"
          onClick={() => {/* Cancelar */}}
        >
          <XCircle className="h-4 w-4 mr-1" />
          Cancelar
        </Button>
      </div>
    </CardFooter>
  </Card>
);

const PsychologistDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [date, setDate] = useState(new Date());
  
  // Dados de exemplo - em produção viriam da API
  const todayAppointments = [
    {
      id: '1',
      pacienteId: '101',
      pacienteNome: 'Maria Silva',
      dataHoraInicio: '2025-05-31T09:00:00Z',
      dataHoraFim: '2025-05-31T10:00:00Z',
      status: 'CONFIRMADO',
      procedimentoNome: 'Consulta Inicial',
      observacoes: 'Primeira consulta'
    },
    {
      id: '2',
      pacienteId: '102',
      pacienteNome: 'João Santos',
      dataHoraInicio: '2025-05-31T11:00:00Z',
      dataHoraFim: '2025-05-31T12:00:00Z',
      status: 'AGENDADO',
      procedimentoNome: 'Sessão de Terapia',
      observacoes: ''
    },
    {
      id: '3',
      pacienteId: '103',
      pacienteNome: 'Ana Oliveira',
      dataHoraInicio: '2025-05-31T14:00:00Z',
      dataHoraFim: '2025-05-31T15:00:00Z',
      status: 'AGENDADO',
      procedimentoNome: 'Sessão de Terapia',
      observacoes: 'Paciente solicitou confirmação por WhatsApp'
    }
  ];
  
  const stats = {
    appointmentsToday: todayAppointments.length,
    appointmentsWeek: 15,
    patientsTotal: 28,
    completionRate: '92%'
  };

  const handleViewDetails = (appointmentId: string) => {
    // Em produção, navegaria para a página de detalhes do agendamento
    console.log(`Ver detalhes do agendamento ${appointmentId}`);
  };

  return (
    <MainLayout title={`Agenda do Dia`}>
      <div className="mb-6">
        <h2 className="text-lg text-gray-600">Olá, {user?.nome || 'Profissional'}</h2>
        <p className="text-sm text-gray-500">
          {format(date, "EEEE, dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
        </p>
      </div>
      
      <div className="grid gap-6 md:grid-cols-3">
        {/* Coluna da esquerda - Estatísticas */}
        <div className="space-y-6">
          <Card>
            <CardHeader className="bg-purple-50 rounded-t-lg">
              <CardTitle className="flex items-center text-purple-700">
                <Calendar className="mr-2 h-5 w-5" />
                Resumo
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Agendamentos hoje</span>
                  <span className="font-bold">{stats.appointmentsToday}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Agendamentos semana</span>
                  <span className="font-bold">{stats.appointmentsWeek}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Total de pacientes</span>
                  <span className="font-bold">{stats.patientsTotal}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Taxa de comparecimento</span>
                  <span className="font-bold">{stats.completionRate}</span>
                </div>
              </div>
            </CardContent>
            <CardFooter className="border-t pt-4">
              <Button 
                variant="outline" 
                className="w-full"
                onClick={() => navigate('/psicologa/agenda')}
              >
                Ver Agenda Completa
              </Button>
            </CardFooter>
          </Card>
          
          <Card>
            <CardHeader className="bg-blue-50 rounded-t-lg">
              <CardTitle className="flex items-center text-blue-700">
                <Clock className="mr-2 h-5 w-5" />
                Ações Rápidas
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <div className="space-y-3">
                <Button 
                  className="w-full justify-start" 
                  onClick={() => navigate('/psicologa/pacientes')}
                >
                  <Users className="mr-2 h-5 w-5" />
                  Ver Pacientes
                </Button>
                <Button 
                  variant="outline" 
                  className="w-full justify-start"
                  onClick={() => navigate('/psicologa/atendimentos')}
                >
                  <FileText className="mr-2 h-5 w-5" />
                  Registrar Atendimento
                </Button>
                <Button 
                  variant="outline" 
                  className="w-full justify-start"
                  onClick={() => navigate('/psicologa/horarios')}
                >
                  <Clock className="mr-2 h-5 w-5" />
                  Configurar Horários
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
        
        {/* Coluna da direita - Agendamentos do dia */}
        <div className="md:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <Calendar className="mr-2 h-5 w-5" />
                Agendamentos de Hoje
              </CardTitle>
              <CardDescription>
                {todayAppointments.length > 0 
                  ? `${todayAppointments.length} agendamentos para hoje` 
                  : 'Nenhum agendamento para hoje'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {todayAppointments.length > 0 ? (
                  todayAppointments.map(appointment => (
                    <AppointmentCard 
                      key={appointment.id} 
                      appointment={appointment} 
                      onViewDetails={handleViewDetails} 
                    />
                  ))
                ) : (
                  <p className="text-center text-gray-500 py-8">
                    Você não tem agendamentos para hoje.
                  </p>
                )}
              </div>
            </CardContent>
            <CardFooter className="border-t pt-4 flex justify-between">
              <Button 
                variant="outline"
                onClick={() => navigate('/psicologa/agenda')}
              >
                Ver Agenda Completa
              </Button>
              <Button onClick={() => navigate('/psicologa/horarios')}>
                Gerenciar Horários
              </Button>
            </CardFooter>
          </Card>
        </div>
      </div>
    </MainLayout>
  );
};

export default PsychologistDashboard;
