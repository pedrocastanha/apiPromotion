import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter, CardDescription } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/use-toast';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../../components/ui/tabs';
import { Badge } from '../../components/ui/badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Calendar, Clock, FileText, AlertCircle, CheckCircle, XCircle } from 'lucide-react';
import { AgendamentoResponse, AgendamentoStatus } from '../../types/appointment.types';

// Componente para exibir um agendamento
const AppointmentCard = ({ appointment, onCancel }: { 
  appointment: AgendamentoResponse, 
  onCancel: (id: number) => void 
}) => {
  const navigate = useNavigate();
  
  const getStatusBadge = (status: AgendamentoStatus) => {
    switch (status) {
      case AgendamentoStatus.CONFIRMADO:
        return <Badge className="bg-green-100 text-green-800 hover:bg-green-200">Confirmado</Badge>;
      case AgendamentoStatus.AGENDADO:
        return <Badge className="bg-blue-100 text-blue-800 hover:bg-blue-200">Agendado</Badge>;
      case AgendamentoStatus.CANCELADO_PACIENTE:
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-200">Cancelado por você</Badge>;
      case AgendamentoStatus.CANCELADO_CLINICA:
        return <Badge className="bg-red-100 text-red-800 hover:bg-red-200">Cancelado pela clínica</Badge>;
      case AgendamentoStatus.REALIZADO:
        return <Badge className="bg-purple-100 text-purple-800 hover:bg-purple-200">Realizado</Badge>;
      case AgendamentoStatus.NAO_COMPARECEU:
        return <Badge className="bg-gray-100 text-gray-800 hover:bg-gray-200">Não compareceu</Badge>;
      default:
        return <Badge className="bg-gray-100 text-gray-800 hover:bg-gray-200">{status}</Badge>;
    }
  };
  
  const dataHoraInicio = new Date(appointment.dataHoraInicio);
  const dataHoraFim = new Date(appointment.dataHoraFim);
  const isPast = dataHoraInicio < new Date();
  const isCancellable = !isPast && 
    (appointment.status === AgendamentoStatus.AGENDADO || 
     appointment.status === AgendamentoStatus.CONFIRMADO);

  return (
    <Card className="mb-4">
      <CardHeader className="pb-2">
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-lg">{format(dataHoraInicio, "dd 'de' MMMM", { locale: ptBR })}</CardTitle>
            <CardDescription>
              {format(dataHoraInicio, "HH:mm", { locale: ptBR })} - {format(dataHoraFim, "HH:mm", { locale: ptBR })}
            </CardDescription>
          </div>
          {getStatusBadge(appointment.status)}
        </div>
      </CardHeader>
      <CardContent className="pb-2">
        <div className="space-y-2">
          <p className="text-sm">
            <span className="font-medium">Profissional:</span> {appointment.profissionalNome || 'Não informado'}
          </p>
          {appointment.procedimentoNome && (
            <p className="text-sm">
              <span className="font-medium">Procedimento:</span> {appointment.procedimentoNome}
            </p>
          )}
          {appointment.clinicaNomeFantasia && (
            <p className="text-sm">
              <span className="font-medium">Clínica:</span> {appointment.clinicaNomeFantasia}
            </p>
          )}
          {appointment.observacoes && (
            <p className="text-sm">
              <span className="font-medium">Observações:</span> {appointment.observacoes}
            </p>
          )}
          {appointment.motivoCancelamento && (
            <p className="text-sm text-red-600">
              <span className="font-medium">Motivo do cancelamento:</span> {appointment.motivoCancelamento}
            </p>
          )}
        </div>
      </CardContent>
      <CardFooter className="pt-2">
        {isCancellable ? (
          <Button 
            variant="outline" 
            className="text-red-600 hover:text-red-700 hover:bg-red-50 w-full"
            onClick={() => onCancel(appointment.id)}
          >
            <XCircle className="h-4 w-4 mr-2" />
            Cancelar Agendamento
          </Button>
        ) : (
          <Button 
            variant="outline" 
            className="w-full"
            disabled={!isPast && appointment.status !== AgendamentoStatus.CANCELADO_PACIENTE && appointment.status !== AgendamentoStatus.CANCELADO_CLINICA}
            onClick={() => navigate('/paciente/novo-agendamento')}
          >
            {isPast ? (
              <>
                <Calendar className="h-4 w-4 mr-2" />
                Agendar Nova Consulta
              </>
            ) : (
              <>
                <CheckCircle className="h-4 w-4 mr-2" />
                Confirmado
              </>
            )}
          </Button>
        )}
      </CardFooter>
    </Card>
  );
};

const AppointmentList: React.FC = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('upcoming');
  const [isLoading, setIsLoading] = useState(true);
  const [appointments, setAppointments] = useState<AgendamentoResponse[]>([]);
  
  // Dados de exemplo - em produção viriam da API
  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        // Simula chamada à API
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // Dados de exemplo
        const mockAppointments: AgendamentoResponse[] = [
          {
            id: 1,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 101,
            pacienteNome: 'João Silva',
            profissionalId: 201,
            profissionalNome: 'Dra. Ana Oliveira',
            procedimentoId: 301,
            procedimentoNome: 'Consulta Inicial',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() + 3)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() + 3)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.CONFIRMADO,
            observacoes: 'Primeira consulta',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 101,
            criadoPorUsuarioNome: 'João Silva'
          },
          {
            id: 2,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 101,
            pacienteNome: 'João Silva',
            profissionalId: 202,
            profissionalNome: 'Dr. Carlos Mendes',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() + 10)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() + 10)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.AGENDADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 101,
            criadoPorUsuarioNome: 'João Silva'
          },
          {
            id: 3,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 101,
            pacienteNome: 'João Silva',
            profissionalId: 201,
            profissionalNome: 'Dra. Ana Oliveira',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() - 7)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() - 7)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.REALIZADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 101,
            criadoPorUsuarioNome: 'João Silva'
          },
          {
            id: 4,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 101,
            pacienteNome: 'João Silva',
            profissionalId: 202,
            profissionalNome: 'Dr. Carlos Mendes',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() - 14)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() - 14)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.CANCELADO_PACIENTE,
            motivoCancelamento: 'Compromisso de trabalho',
            dataCancelamento: new Date(new Date().setDate(new Date().getDate() - 16)).toISOString(),
            canceladoPorUsuarioId: 101,
            canceladoPorUsuarioNome: 'João Silva',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 101,
            criadoPorUsuarioNome: 'João Silva'
          }
        ];
        
        setAppointments(mockAppointments);
      } catch (error) {
        console.error('Erro ao buscar agendamentos:', error);
        toast({
          title: "Erro ao carregar agendamentos",
          description: "Não foi possível carregar seus agendamentos. Tente novamente mais tarde.",
          variant: "destructive"
        });
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchAppointments();
  }, [toast]);
  
  const handleCancelAppointment = (appointmentId: number) => {
    navigate(`/paciente/cancelar-agendamento/${appointmentId}`);
  };
  
  const upcomingAppointments = appointments.filter(
    app => new Date(app.dataHoraInicio) > new Date() && 
    (app.status === AgendamentoStatus.AGENDADO || app.status === AgendamentoStatus.CONFIRMADO)
  );
  
  const pastAppointments = appointments.filter(
    app => new Date(app.dataHoraInicio) < new Date() || 
    (app.status !== AgendamentoStatus.AGENDADO && app.status !== AgendamentoStatus.CONFIRMADO)
  );

  return (
    <MainLayout title="Meus Agendamentos">
      <div className="max-w-3xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-lg font-medium">Gerencie suas consultas</h2>
          <Button onClick={() => navigate('/paciente/novo-agendamento')}>
            Novo Agendamento
          </Button>
        </div>
        
        <Tabs defaultValue={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-2 mb-6">
            <TabsTrigger value="upcoming">
              Próximos Agendamentos
              {upcomingAppointments.length > 0 && (
                <Badge className="ml-2 bg-blue-100 text-blue-800 hover:bg-blue-100">
                  {upcomingAppointments.length}
                </Badge>
              )}
            </TabsTrigger>
            <TabsTrigger value="past">
              Histórico
              {pastAppointments.length > 0 && (
                <Badge className="ml-2 bg-gray-100 text-gray-800 hover:bg-gray-100">
                  {pastAppointments.length}
                </Badge>
              )}
            </TabsTrigger>
          </TabsList>
          
          <TabsContent value="upcoming">
            {isLoading ? (
              <div className="text-center py-8">
                <p>Carregando agendamentos...</p>
              </div>
            ) : upcomingAppointments.length > 0 ? (
              <div className="space-y-4">
                {upcomingAppointments.map(appointment => (
                  <AppointmentCard 
                    key={appointment.id} 
                    appointment={appointment} 
                    onCancel={handleCancelAppointment} 
                  />
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="text-center py-8">
                  <div className="flex flex-col items-center space-y-4">
                    <Calendar className="h-12 w-12 text-gray-400" />
                    <div>
                      <h3 className="text-lg font-medium">Nenhum agendamento futuro</h3>
                      <p className="text-gray-500 mt-1">Você não possui consultas agendadas.</p>
                    </div>
                    <Button onClick={() => navigate('/paciente/novo-agendamento')}>
                      Agendar Consulta
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )}
          </TabsContent>
          
          <TabsContent value="past">
            {isLoading ? (
              <div className="text-center py-8">
                <p>Carregando histórico...</p>
              </div>
            ) : pastAppointments.length > 0 ? (
              <div className="space-y-4">
                {pastAppointments.map(appointment => (
                  <AppointmentCard 
                    key={appointment.id} 
                    appointment={appointment} 
                    onCancel={handleCancelAppointment} 
                  />
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="text-center py-8">
                  <div className="flex flex-col items-center space-y-4">
                    <FileText className="h-12 w-12 text-gray-400" />
                    <div>
                      <h3 className="text-lg font-medium">Nenhum histórico</h3>
                      <p className="text-gray-500 mt-1">Você ainda não realizou nenhuma consulta.</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </MainLayout>
  );
};

export default AppointmentList;
