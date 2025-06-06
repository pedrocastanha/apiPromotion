import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { Clock, Calendar, CheckCircle, XCircle, AlertCircle } from 'lucide-react';
import { useToast } from '../../hooks/use-toast';
import { useAuth } from '../../hooks/useAuth';
import { AgendamentoResponse, AgendamentoStatus } from '../../types/appointment.types';
import { Badge } from '../../components/ui/badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const DailySchedule: React.FC = () => {
  const { toast } = useToast();
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [appointments, setAppointments] = useState<AgendamentoResponse[]>([]);
  const [today] = useState(new Date());
  
  // Busca agendamentos para hoje
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
            dataHoraInicio: new Date(today).setHours(9, 0, 0).toString(),
            dataHoraFim: new Date(today).setHours(10, 0, 0).toString(),
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
            dataHoraInicio: new Date(today).setHours(11, 0, 0).toString(),
            dataHoraFim: new Date(today).setHours(12, 0, 0).toString(),
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
            dataHoraInicio: new Date(today).setHours(14, 0, 0).toString(),
            dataHoraFim: new Date(today).setHours(15, 0, 0).toString(),
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
            dataHoraInicio: new Date(today).setHours(16, 0, 0).toString(),
            dataHoraFim: new Date(today).setHours(17, 30, 0).toString(),
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
          description: "Não foi possível carregar os agendamentos para hoje.",
          variant: "destructive"
        });
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchAppointments();
  }, [today, toast]);
  
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
  
  // Função para confirmar agendamento
  const handleConfirmAppointment = (appointmentId: number) => {
    toast({
      title: "Agendamento confirmado",
      description: "O agendamento foi confirmado com sucesso."
    });
    // Em produção, implementaria a chamada à API
  };

  return (
    <MainLayout title="Agenda do Dia">
      <div className="mb-6">
        <h2 className="text-lg text-gray-600">
          {format(today, "EEEE, dd 'de' MMMM 'de' yyyy", { locale: ptBR })}
        </h2>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Clock className="mr-2 h-5 w-5" />
            Agendamentos de Hoje
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="text-center py-8">
              <p>Carregando agendamentos...</p>
            </div>
          ) : appointments.length > 0 ? (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Horário</TableHead>
                    <TableHead>Paciente</TableHead>
                    <TableHead>Procedimento</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {appointments.map((appointment) => {
                    const dataHoraInicio = new Date(appointment.dataHoraInicio);
                    const dataHoraFim = new Date(appointment.dataHoraFim);
                    const now = new Date();
                    const isPast = dataHoraInicio < now;
                    const isUpcoming = dataHoraInicio > now && dataHoraInicio < new Date(now.getTime() + 60 * 60 * 1000); // próxima hora
                    
                    return (
                      <TableRow key={appointment.id} className={isUpcoming ? 'bg-blue-50' : ''}>
                        <TableCell>
                          <div className="font-medium">
                            {format(dataHoraInicio, "HH:mm", { locale: ptBR })}
                          </div>
                          <div className="text-sm text-gray-500">
                            até {format(dataHoraFim, "HH:mm", { locale: ptBR })}
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="font-medium">{appointment.pacienteNome}</div>
                          {appointment.observacoes && (
                            <div className="text-sm text-gray-500 truncate max-w-xs" title={appointment.observacoes}>
                              {appointment.observacoes}
                            </div>
                          )}
                        </TableCell>
                        <TableCell>{appointment.procedimentoNome || '-'}</TableCell>
                        <TableCell>{renderStatusBadge(appointment.status)}</TableCell>
                        <TableCell>
                          <div className="flex space-x-2">
                            {appointment.status === AgendamentoStatus.AGENDADO && (
                              <Button 
                                size="sm" 
                                variant="outline"
                                className="text-blue-600 hover:text-blue-700 hover:bg-blue-50"
                                onClick={() => handleConfirmAppointment(appointment.id)}
                              >
                                <CheckCircle className="h-4 w-4 mr-1" />
                                Confirmar
                              </Button>
                            )}
                            
                            {(appointment.status === AgendamentoStatus.AGENDADO || 
                              appointment.status === AgendamentoStatus.CONFIRMADO) && (
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
                                  className="text-red-600 hover:text-red-700 hover:bg-red-50"
                                  onClick={() => handleMarkAsNoShow(appointment.id)}
                                >
                                  <XCircle className="h-4 w-4 mr-1" />
                                  Não Compareceu
                                </Button>
                              </>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </div>
          ) : (
            <div className="text-center py-8">
              <div className="flex flex-col items-center space-y-4">
                <Calendar className="h-12 w-12 text-gray-400" />
                <div>
                  <h3 className="text-lg font-medium">Nenhum agendamento para hoje</h3>
                  <p className="text-gray-500 mt-1">Sua agenda está livre.</p>
                </div>
              </div>
            </div>
          )}
        </CardContent>
        <CardFooter className="flex justify-between border-t pt-4">
          <Button 
            variant="outline" 
            onClick={() => window.location.reload()}
          >
            Atualizar
          </Button>
          <Button onClick={() => window.location.href = '/psicologa/agenda'}>
            Ver Agenda Completa
          </Button>
        </CardFooter>
      </Card>
    </MainLayout>
  );
};

export default DailySchedule;
