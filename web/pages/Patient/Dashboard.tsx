import React from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Calendar, Clock, FileText, AlertCircle } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const PatientDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  
  // Dados de exemplo - em produção viriam da API
  const nextAppointment = {
    date: '15/06/2025',
    time: '14:30',
    professional: 'Dra. Ana Silva',
    status: 'confirmado'
  };
  
  const appointmentCount = {
    upcoming: 2,
    past: 5,
    cancelled: 1
  };

  return (
    <MainLayout title={`Bem-vindo(a), ${user?.nome || 'Paciente'}`}>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {/* Card de próximo agendamento */}
        <Card className="col-span-1">
          <CardHeader className="bg-blue-50 rounded-t-lg">
            <CardTitle className="flex items-center text-blue-700">
              <Calendar className="mr-2 h-5 w-5" />
              Próximo Agendamento
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-6">
            {nextAppointment ? (
              <div className="space-y-2">
                <p className="text-2xl font-bold">{nextAppointment.date}</p>
                <p className="text-gray-500 flex items-center">
                  <Clock className="mr-2 h-4 w-4" />
                  {nextAppointment.time}
                </p>
                <p className="font-medium">{nextAppointment.professional}</p>
                <div className="flex items-center mt-2">
                  <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">
                    {nextAppointment.status}
                  </span>
                </div>
              </div>
            ) : (
              <p className="text-gray-500">Você não possui agendamentos futuros.</p>
            )}
          </CardContent>
          <CardFooter className="border-t pt-4">
            <Button 
              variant="outline" 
              className="w-full"
              onClick={() => navigate('/paciente/meus-agendamentos')}
            >
              Ver todos os agendamentos
            </Button>
          </CardFooter>
        </Card>

        {/* Card de ações rápidas */}
        <Card className="col-span-1">
          <CardHeader className="bg-purple-50 rounded-t-lg">
            <CardTitle className="text-purple-700">Ações Rápidas</CardTitle>
            <CardDescription>O que você deseja fazer hoje?</CardDescription>
          </CardHeader>
          <CardContent className="pt-6">
            <div className="space-y-4">
              <Button 
                className="w-full justify-start" 
                onClick={() => navigate('/paciente/novo-agendamento')}
              >
                <Calendar className="mr-2 h-5 w-5" />
                Agendar Consulta
              </Button>
              <Button 
                variant="outline" 
                className="w-full justify-start"
                onClick={() => navigate('/paciente/agendamento-recorrente')}
              >
                <Clock className="mr-2 h-5 w-5" />
                Agendar Consultas Recorrentes
              </Button>
              <Button 
                variant="outline" 
                className="w-full justify-start text-red-600 hover:text-red-700 hover:bg-red-50"
                onClick={() => navigate('/paciente/meus-agendamentos')}
              >
                <AlertCircle className="mr-2 h-5 w-5" />
                Cancelar Agendamento
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Card de resumo */}
        <Card className="col-span-1">
          <CardHeader className="bg-green-50 rounded-t-lg">
            <CardTitle className="flex items-center text-green-700">
              <FileText className="mr-2 h-5 w-5" />
              Resumo
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-6">
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-gray-600">Agendamentos futuros</span>
                <span className="font-bold">{appointmentCount.upcoming}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-600">Consultas realizadas</span>
                <span className="font-bold">{appointmentCount.past}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-600">Cancelamentos</span>
                <span className="font-bold">{appointmentCount.cancelled}</span>
              </div>
            </div>
          </CardContent>
          <CardFooter className="border-t pt-4">
            <Button 
              variant="ghost" 
              className="w-full"
              onClick={() => navigate('/paciente/perfil')}
            >
              Meu Perfil
            </Button>
          </CardFooter>
        </Card>
      </div>
    </MainLayout>
  );
};

export default PatientDashboard;
