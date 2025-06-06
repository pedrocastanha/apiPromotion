import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Calendar as CalendarIcon, Clock, CheckCircle, XCircle, FileText } from 'lucide-react';
import { Calendar } from '../../components/ui/calendar';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { useToast } from '../../hooks/use-toast';
import { useAuth } from '../../hooks/useAuth';
import { AgendamentoResponse, AgendamentoStatus } from '../../types/appointment.types';
import { Badge } from '../../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../../components/ui/tabs';

const DateSchedule: React.FC = () => {
  const { toast } = useToast();
  const { user } = useAuth();
  const [date, setDate] = useState<Date>(new Date());
  const [isLoading, setIsLoading] = useState(true);
  const [appointments, setAppointments] = useState<AgendamentoResponse[]>([]);
  
  // Busca agendamentos para a data selecionada
  useEffect(() => {
    const fetchAppointments = async () => {
      setIsLoading(true);
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
            dataHoraInicio: new Date(date).setHours(9, 0, 0).toString(),
            dataHoraFim: new Date(date).setHours(10, 0, 0).toString(),
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
            pacienteId: 102,
            pacienteNome: 'Maria Santos',
            profissionalId: 201,
            profissionalNome: 'Dra. Ana Oliveira',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(date).setHours(11, 0, 0).toString(),
            dataHoraFim: new Date(date).setHours(12, 0, 0).toString(),
            status: AgendamentoStatus.AGENDADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 102,
            criadoPorUsuarioNome: 'Maria Santos'
          },
          {
            id: 3,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 103,
            pacienteNome: 'Pedro Oliveira',
            profissionalId: 201,
            profissionalNome: 'Dra. Ana Oliveira',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(date).setHours(14, 0, 0).toString(),
            dataHoraFim: new Date(date).setHours(15, 0, 0).toString(),
            status: AgendamentoStatus.CONFIRMADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 103,
            criadoPorUsuarioNome: 'Pedro Oliveira'
          },
          {
            id: 4,
            clinicaId: 1,
            clinicaNomeFantasia: 'Clínica Central',
            pacienteId: 104,
            pacienteNome: 'Ana Pereira',
            profissionalId: 201,
            profissionalNome: 'Dra. Ana Oliveira',
            procedimentoId: 303,
            procedimentoNome: 'Avaliação Psicológica',
            dataHoraInicio: new Date(date).setHours(16, 0, 0).toString(),
            dataHoraFim: new Date(date).setHours(17, 30, 0).toString(),
            status: AgendamentoStatus.CONFIRMADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 104,
            criadoPorUsuarioNome: 'Ana Pereira'
          }
        ];
        
        setAppointments(mockAppointments);
      } catch (error) {
        console.error('Erro ao buscar agendamentos:', error);
        toast({
          title: "Erro ao carregar agenda",
          description: "Não foi possível carregar os agendamentos para a data selecionada.",
          variant: "destructive"
        });
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchAppointments();
  }, [date, toast]);
  
  // Função para renderizar o badge de status
  const renderStatusBadge = (status: AgendamentoStatus) => {
    switch (status) {
      case AgendamentoStatus.CONFIRMADO:
        return <Badge className="bg-green-100 text-green-800">Confirmado</Badge>;
      case AgendamentoStatus.AGENDADO:
        return <Badge className="bg-blue-100 text-blue-800">Agendado</Badge>;
      case AgendamentoStatus.CANCELADO_PACIENTE:
        return <Badge className="bg-red-100 text-red-800">Cancelado pelo paciente</Badge>;
      case AgendamentoStatus.CANCELADO_CLINICA:
        return <Badge className="bg-red-100 text-red-800">Cancelado pela clínica</Badge>;
      case AgendamentoStatus.REALIZADO:
        return <Badge className="bg-purple-100 text-purple-800">Realizado</Badge>;
      case AgendamentoStatus.NAO_COMPARECEU:
        return <Badge className="bg-gray-100 text-gray-800">Não compareceu</Badge>;
      default:
        return <Badge>{status}</Badge>;
    }
  };
  
  // Função para marcar como realizado
  const handleMarkAsCompleted = (appointmentId: number) => {
    toast({
      title: "Atendimento registrado",
      description: "O atendimento foi marcado como realizado."
    });
    // Em produção, implementaria a chamada à API
  };
  
  // Função para marcar como não compareceu
  const handleMarkAsNoShow = (appointmentId: number) => {
    toast({
      title: "Não comparecimento registrado",
      description: "O paciente foi marcado como não compareceu."
    });
    // Em produção, implementaria a chamada à API
  };
  
  // Função para cancelar agendamento
  const handleCancelAppointment = (appointmentId: number) => {
    toast({
      title: "Cancelamento iniciado",
      description: "Preencha o motivo do cancelamento na próxima tela."
    });
    // Em produção, redirecionaria para a tela de cancelamento
  };
  
  // Função para registrar atendimento
  const handleRegisterAttendance = (appointmentId: number) => {
    toast({
      title: "Registro de atendimento",
      description: "Preencha os detalhes do atendimento na próxima tela."
    });
    // Em produção, redirecionaria para a tela de registro de atendimento
  };

  return (
    <MainLayout title="Agenda">
      <div className="grid gap-6 md:grid-cols-3">
        {/* Coluna da esquerda - Calendário */}
        <div className="md:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <CalendarIcon className="mr-2 h-5 w-5" />
                Calendário
              </CardTitle>
            </CardHeader>
            <CardContent className="flex justify-center pb-6">
              <Calendar
                mode="single"
                selected={date}
                onSelect={(newDate) => newDate && setDate(newDate)}
                className="rounded-md border"
                locale={ptBR}
              />
            </CardContent>
            <CardFooter className="flex justify-center border-t pt-4">
              <Button 
                variant="outline" 
                onClick={() => setDate(new Date())}
              >
                Hoje
              </Button>
            </CardFooter>
          </Card>
        </div>
        
        {/* Coluna da direita - Agendamentos do dia */}
        <div className="md:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <Clock className="mr-2 h-5 w-5" />
                Agendamentos: {format(date, "dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="text-center py-8">
                  <p>Carregando agendamentos...</p>
                </div>
              ) : appointments.length > 0 ? (
                <div className="space-y-4">
                  {appointments.map((appointment) => {
                    const dataHoraInicio = new Date(appointment.dataHoraInicio);
                    const dataHoraFim = new Date(appointment.dataHoraFim);
                    const isPast = dataHoraInicio < new Date();
                    const isCompletable = !isPast && 
                      (appointment.status === AgendamentoStatus.AGENDADO || 
                       appointment.status === AgendamentoStatus.CONFIRMADO);
                    
                    return (
                      <Card key={appointment.id} className="overflow-hidden">
                        <div className="flex border-l-4 border-blue-500">
                          <div className="w-24 bg-blue-50 flex flex-col items-center justify-center p-4">
                            <span className="text-lg font-bold">
                              {format(dataHoraInicio, "HH:mm", { locale: ptBR })}
                            </span>
                            <span className="text-sm text-gray-500">
                              {format(dataHoraFim, "HH:mm", { locale: ptBR })}
                            </span>
                          </div>
                          <div className="flex-1 p-4">
                            <div className="flex justify-between items-start">
                              <div>
                                <h3 className="font-medium">{appointment.pacienteNome}</h3>
                                <p className="text-sm text-gray-500">{appointment.procedimentoNome}</p>
                              </div>
                              {renderStatusBadge(appointment.status)}
                            </div>
                            
                            {appointment.observacoes && (
                              <p className="text-sm mt-2 text-gray-600">
                                <span className="font-medium">Observações:</span> {appointment.observacoes}
                              </p>
                            )}
                            
                            <div className="mt-4 flex flex-wrap gap-2">
                              {isCompletable && (
                                <>
                                  <Button 
                                    size="sm" 
                                    variant="outline"
                                    className="text-green-600 hover:text-green-700 hover:bg-green-50"
                                    onClick={() => handleMarkAsCompleted(appointment.id)}
                                  >
                                    <CheckCircle className="h-4 w-4 mr-1" />
                                    Realizado
                                  </Button>
                                  <Button 
                                    size="sm" 
                                    variant="outline"
                                    className="text-gray-600 hover:text-gray-700 hover:bg-gray-50"
                                    onClick={() => handleMarkAsNoShow(appointment.id)}
                                  >
                                    <XCircle className="h-4 w-4 mr-1" />
                                    Não Compareceu
                                  </Button>
                                  <Button 
                                    size="sm" 
                                    variant="outline"
                                    className="text-red-600 hover:text-red-700 hover:bg-red-50"
                                    onClick={() => handleCancelAppointment(appointment.id)}
                                  >
                                    Cancelar
                                  </Button>
                                </>
                              )}
                              {appointment.status === AgendamentoStatus.CONFIRMADO && (
                                <Button 
                                  size="sm"
                                  onClick={() => handleRegisterAttendance(appointment.id)}
                                >
                                  <FileText className="h-4 w-4 mr-1" />
                                  Registrar Atendimento
                                </Button>
                              )}
                            </div>
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              ) : (
                <div className="text-center py-8">
                  <p className="text-gray-500">Nenhum agendamento para esta data.</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </MainLayout>
  );
};

export default DateSchedule;
