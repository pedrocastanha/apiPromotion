import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { Input } from '../../components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { Calendar, Search, Filter, Download } from 'lucide-react';
import { useToast } from '../../hooks/use-toast';
import { AgendamentoResponse, AgendamentoStatus } from '../../types/appointment.types';
import { Badge } from '../../components/ui/badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const AllAppointments: React.FC = () => {
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(true);
  const [appointments, setAppointments] = useState<AgendamentoResponse[]>([]);
  const [filteredAppointments, setFilteredAppointments] = useState<AgendamentoResponse[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [clinicFilter, setClinicFilter] = useState<string>('all');
  
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
            clinicaId: 2,
            clinicaNomeFantasia: 'Clínica Norte',
            pacienteId: 102,
            pacienteNome: 'Maria Santos',
            profissionalId: 202,
            profissionalNome: 'Dr. Carlos Mendes',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() + 1)).setHours(new Date().getHours() + 1).toString(),
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
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() - 7)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() - 7)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.REALIZADO,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 103,
            criadoPorUsuarioNome: 'Pedro Oliveira'
          },
          {
            id: 4,
            clinicaId: 3,
            clinicaNomeFantasia: 'Clínica Sul',
            pacienteId: 104,
            pacienteNome: 'Ana Pereira',
            profissionalId: 203,
            profissionalNome: 'Dra. Mariana Costa',
            procedimentoId: 303,
            procedimentoNome: 'Avaliação Psicológica',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() - 14)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() - 14)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.CANCELADO_PACIENTE,
            motivoCancelamento: 'Compromisso de trabalho',
            dataCancelamento: new Date(new Date().setDate(new Date().getDate() - 16)).toISOString(),
            canceladoPorUsuarioId: 104,
            canceladoPorUsuarioNome: 'Ana Pereira',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 104,
            criadoPorUsuarioNome: 'Ana Pereira'
          },
          {
            id: 5,
            clinicaId: 2,
            clinicaNomeFantasia: 'Clínica Norte',
            pacienteId: 105,
            pacienteNome: 'Carlos Souza',
            profissionalId: 202,
            profissionalNome: 'Dr. Carlos Mendes',
            procedimentoId: 302,
            procedimentoNome: 'Sessão de Terapia',
            dataHoraInicio: new Date(new Date().setDate(new Date().getDate() - 2)).toISOString(),
            dataHoraFim: new Date(new Date().setDate(new Date().getDate() - 2)).setHours(new Date().getHours() + 1).toString(),
            status: AgendamentoStatus.NAO_COMPARECEU,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            criadoPorUsuarioId: 105,
            criadoPorUsuarioNome: 'Carlos Souza'
          }
        ];
        
        setAppointments(mockAppointments);
        setFilteredAppointments(mockAppointments);
      } catch (error) {
        console.error('Erro ao buscar agendamentos:', error);
        toast({
          title: "Erro ao carregar agendamentos",
          description: "Não foi possível carregar os agendamentos. Tente novamente mais tarde.",
          variant: "destructive"
        });
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchAppointments();
  }, [toast]);
  
  // Filtra os agendamentos quando os filtros mudam
  useEffect(() => {
    let result = [...appointments];
    
    // Filtro por termo de busca
    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      result = result.filter(app => 
        app.pacienteNome?.toLowerCase().includes(term) || 
        app.profissionalNome?.toLowerCase().includes(term) ||
        app.procedimentoNome?.toLowerCase().includes(term) ||
        app.clinicaNomeFantasia?.toLowerCase().includes(term)
      );
    }
    
    // Filtro por status
    if (statusFilter !== 'all') {
      result = result.filter(app => app.status === statusFilter);
    }
    
    // Filtro por clínica
    if (clinicFilter !== 'all') {
      result = result.filter(app => app.clinicaId === Number(clinicFilter));
    }
    
    setFilteredAppointments(result);
  }, [searchTerm, statusFilter, clinicFilter, appointments]);
  
  // Lista de clínicas únicas para o filtro
  const clinicas = Array.from(new Set(appointments.map(app => app.clinicaId)))
    .map(id => {
      const app = appointments.find(a => a.clinicaId === id);
      return { id, nome: app?.clinicaNomeFantasia || `Clínica ${id}` };
    });
  
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
  
  // Função para exportar dados
  const handleExport = () => {
    toast({
      title: "Exportação iniciada",
      description: "Os dados serão exportados em formato CSV."
    });
    // Em produção, implementaria a exportação real
  };

  return (
    <MainLayout title="Todos os Agendamentos">
      <Card>
        <CardHeader>
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <CardTitle>Agendamentos</CardTitle>
            <div className="flex flex-col md:flex-row gap-2">
              <Button variant="outline" onClick={handleExport}>
                <Download className="mr-2 h-4 w-4" />
                Exportar
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {/* Filtros */}
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-500" />
                <Input
                  placeholder="Buscar por paciente, profissional..."
                  className="pl-8"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            <div className="w-full md:w-48">
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger>
                  <SelectValue placeholder="Status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos os status</SelectItem>
                  <SelectItem value={AgendamentoStatus.AGENDADO}>Agendado</SelectItem>
                  <SelectItem value={AgendamentoStatus.CONFIRMADO}>Confirmado</SelectItem>
                  <SelectItem value={AgendamentoStatus.REALIZADO}>Realizado</SelectItem>
                  <SelectItem value={AgendamentoStatus.CANCELADO_PACIENTE}>Cancelado pelo paciente</SelectItem>
                  <SelectItem value={AgendamentoStatus.CANCELADO_CLINICA}>Cancelado pela clínica</SelectItem>
                  <SelectItem value={AgendamentoStatus.NAO_COMPARECEU}>Não compareceu</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="w-full md:w-48">
              <Select value={clinicFilter} onValueChange={setClinicFilter}>
                <SelectTrigger>
                  <SelectValue placeholder="Clínica" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todas as clínicas</SelectItem>
                  {clinicas.map((clinica) => (
                    <SelectItem key={clinica.id} value={clinica.id.toString()}>
                      {clinica.nome}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          
          {/* Tabela de agendamentos */}
          {isLoading ? (
            <div className="text-center py-8">
              <p>Carregando agendamentos...</p>
            </div>
          ) : filteredAppointments.length > 0 ? (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Data/Hora</TableHead>
                    <TableHead>Paciente</TableHead>
                    <TableHead>Profissional</TableHead>
                    <TableHead>Procedimento</TableHead>
                    <TableHead>Clínica</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredAppointments.map((appointment) => (
                    <TableRow key={appointment.id}>
                      <TableCell>
                        <div className="font-medium">
                          {format(new Date(appointment.dataHoraInicio), "dd/MM/yyyy", { locale: ptBR })}
                        </div>
                        <div className="text-sm text-gray-500">
                          {format(new Date(appointment.dataHoraInicio), "HH:mm", { locale: ptBR })} - 
                          {format(new Date(appointment.dataHoraFim), "HH:mm", { locale: ptBR })}
                        </div>
                      </TableCell>
                      <TableCell>{appointment.pacienteNome}</TableCell>
                      <TableCell>{appointment.profissionalNome}</TableCell>
                      <TableCell>{appointment.procedimentoNome || '-'}</TableCell>
                      <TableCell>{appointment.clinicaNomeFantasia}</TableCell>
                      <TableCell>{renderStatusBadge(appointment.status)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-500">Nenhum agendamento encontrado com os filtros selecionados.</p>
            </div>
          )}
        </CardContent>
      </Card>
    </MainLayout>
  );
};

export default AllAppointments;
