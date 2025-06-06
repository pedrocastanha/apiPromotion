import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { Calendar } from '../../components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from '../../components/ui/popover';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { CalendarIcon, Clock } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/use-toast';
import { AgendamentoRequest } from '../../types/appointment.types';

// Dados de exemplo - em produção viriam da API
const profissionais = [
  { id: 1, nome: 'Dra. Ana Silva', especialidade: 'Psicologia Clínica' },
  { id: 2, nome: 'Dr. Carlos Mendes', especialidade: 'Terapia Cognitivo-Comportamental' },
  { id: 3, nome: 'Dra. Mariana Costa', especialidade: 'Psicanálise' }
];

const procedimentos = [
  { id: 1, nome: 'Consulta Inicial', duracao: 60, valor: 200 },
  { id: 2, nome: 'Sessão de Terapia', duracao: 50, valor: 180 },
  { id: 3, nome: 'Avaliação Psicológica', duracao: 90, valor: 250 }
];

// Horários disponíveis de exemplo
const getHorariosDisponiveis = (profissionalId: number, data: Date) => {
  // Em produção, isso seria uma chamada à API
  return [
    '08:00', '09:00', '10:00', '11:00', 
    '14:00', '15:00', '16:00', '17:00'
  ];
};

const AppointmentForm: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { toast } = useToast();
  
  const [date, setDate] = useState<Date | undefined>(undefined);
  const [profissionalId, setProfissionalId] = useState<number | undefined>(undefined);
  const [procedimentoId, setProcedimentoId] = useState<number | undefined>(undefined);
  const [horario, setHorario] = useState<string | undefined>(undefined);
  const [observacoes, setObservacoes] = useState<string>('');
  const [horariosDisponiveis, setHorariosDisponiveis] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  
  // Atualiza horários disponíveis quando profissional ou data mudam
  useEffect(() => {
    if (profissionalId && date) {
      setHorariosDisponiveis(getHorariosDisponiveis(profissionalId, date));
      setHorario(undefined); // Reseta o horário selecionado
    } else {
      setHorariosDisponiveis([]);
      setHorario(undefined);
    }
  }, [profissionalId, date]);
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!profissionalId || !date || !horario) {
      toast({
        title: "Erro no agendamento",
        description: "Por favor, preencha todos os campos obrigatórios.",
        variant: "destructive"
      });
      return;
    }
    
    setIsLoading(true);
    
    try {
      // Formata a data e hora para o formato ISO 8601
      const [hora, minuto] = horario.split(':').map(Number);
      const dataHoraInicio = new Date(date);
      dataHoraInicio.setHours(hora, minuto, 0);
      
      const agendamentoRequest: AgendamentoRequest = {
        profissionalId,
        dataHoraInicio: dataHoraInicio.toISOString(),
        procedimentoId,
        observacoes: observacoes.trim() || undefined
      };
      
      // Em produção, isso seria uma chamada à API
      console.log('Enviando agendamento:', agendamentoRequest);
      
      // Simula resposta da API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      toast({
        title: "Agendamento realizado com sucesso!",
        description: `Sua consulta foi agendada para ${format(dataHoraInicio, "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}.`,
      });
      
      // Redireciona para a lista de agendamentos
      navigate('/paciente/meus-agendamentos');
    } catch (error) {
      console.error('Erro ao agendar:', error);
      toast({
        title: "Erro no agendamento",
        description: "Não foi possível realizar o agendamento. Tente novamente mais tarde.",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };
  
  const procedimentoSelecionado = procedimentoId 
    ? procedimentos.find(p => p.id === procedimentoId) 
    : undefined;

  return (
    <MainLayout title="Novo Agendamento">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Agendar Nova Consulta</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Seleção de Profissional */}
            <div className="space-y-2">
              <Label htmlFor="profissional">Profissional</Label>
              <Select 
                value={profissionalId?.toString()} 
                onValueChange={(value) => setProfissionalId(Number(value))}
              >
                <SelectTrigger id="profissional">
                  <SelectValue placeholder="Selecione um profissional" />
                </SelectTrigger>
                <SelectContent>
                  {profissionais.map((prof) => (
                    <SelectItem key={prof.id} value={prof.id.toString()}>
                      {prof.nome} - {prof.especialidade}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            
            {/* Seleção de Procedimento */}
            <div className="space-y-2">
              <Label htmlFor="procedimento">Procedimento (opcional)</Label>
              <Select 
                value={procedimentoId?.toString()} 
                onValueChange={(value) => setProcedimentoId(Number(value))}
              >
                <SelectTrigger id="procedimento">
                  <SelectValue placeholder="Selecione um procedimento" />
                </SelectTrigger>
                <SelectContent>
                  {procedimentos.map((proc) => (
                    <SelectItem key={proc.id} value={proc.id.toString()}>
                      {proc.nome} - {proc.duracao} min - R$ {proc.valor.toFixed(2)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {procedimentoSelecionado && (
                <p className="text-sm text-gray-500 mt-1">
                  Duração: {procedimentoSelecionado.duracao} minutos | 
                  Valor: R$ {procedimentoSelecionado.valor.toFixed(2)}
                </p>
              )}
            </div>
            
            {/* Seleção de Data */}
            <div className="space-y-2">
              <Label>Data</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full justify-start text-left font-normal"
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {date ? format(date, "dd/MM/yyyy", { locale: ptBR }) : "Selecione uma data"}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={setDate}
                    disabled={(date) => date < new Date() || date > new Date(new Date().setMonth(new Date().getMonth() + 3))}
                    locale={ptBR}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
            
            {/* Seleção de Horário */}
            <div className="space-y-2">
              <Label htmlFor="horario">Horário</Label>
              <Select 
                value={horario} 
                onValueChange={setHorario}
                disabled={!profissionalId || !date || horariosDisponiveis.length === 0}
              >
                <SelectTrigger id="horario">
                  <SelectValue placeholder={
                    !profissionalId || !date 
                      ? "Selecione profissional e data primeiro" 
                      : horariosDisponiveis.length === 0 
                        ? "Nenhum horário disponível" 
                        : "Selecione um horário"
                  } />
                </SelectTrigger>
                <SelectContent>
                  {horariosDisponiveis.map((hora) => (
                    <SelectItem key={hora} value={hora}>
                      {hora}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            
            {/* Observações */}
            <div className="space-y-2">
              <Label htmlFor="observacoes">Observações (opcional)</Label>
              <Textarea 
                id="observacoes" 
                placeholder="Informe detalhes adicionais para o profissional"
                value={observacoes}
                onChange={(e) => setObservacoes(e.target.value)}
                rows={3}
              />
            </div>
          </form>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button 
            variant="outline" 
            onClick={() => navigate('/paciente/dashboard')}
            disabled={isLoading}
          >
            Cancelar
          </Button>
          <Button 
            onClick={handleSubmit}
            disabled={!profissionalId || !date || !horario || isLoading}
          >
            {isLoading ? "Agendando..." : "Agendar Consulta"}
          </Button>
        </CardFooter>
      </Card>
    </MainLayout>
  );
};

export default AppointmentForm;
