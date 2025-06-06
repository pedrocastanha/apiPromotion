import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Textarea } from '../../components/ui/textarea';
import { Label } from '../../components/ui/label';
import { AlertCircle } from 'lucide-react';
import { useToast } from '../../hooks/use-toast';
import { Alert, AlertDescription, AlertTitle } from '../../components/ui/alert';
import { CancelamentoRequest } from '../../types/appointment.types';

const CancelAppointmentForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [motivo, setMotivo] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  // Em produção, buscaríamos os detalhes do agendamento da API
  const appointmentDetails = {
    id: Number(id),
    profissionalNome: 'Dra. Ana Oliveira',
    dataHoraInicio: new Date(new Date().setDate(new Date().getDate() + 3)).toISOString(),
    procedimentoNome: 'Consulta Inicial'
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!motivo.trim()) {
      toast({
        title: "Campo obrigatório",
        description: "Por favor, informe o motivo do cancelamento.",
        variant: "destructive"
      });
      return;
    }
    
    setIsLoading(true);
    
    try {
      // Em produção, isso seria uma chamada à API
      const cancelRequest: CancelamentoRequest = {
        agendamentoId: Number(id),
        motivo: motivo.trim()
      };
      
      console.log('Enviando cancelamento:', cancelRequest);
      
      // Simula resposta da API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      toast({
        title: "Agendamento cancelado com sucesso",
        description: "Seu agendamento foi cancelado conforme solicitado."
      });
      
      // Redireciona para a lista de agendamentos
      navigate('/paciente/meus-agendamentos');
    } catch (error) {
      console.error('Erro ao cancelar agendamento:', error);
      toast({
        title: "Erro ao cancelar",
        description: "Não foi possível cancelar o agendamento. Tente novamente mais tarde.",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <MainLayout title="Cancelar Agendamento">
      <div className="max-w-2xl mx-auto">
        <Alert className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertTitle>Atenção</AlertTitle>
          <AlertDescription>
            O cancelamento com menos de 24 horas de antecedência pode estar sujeito a cobrança conforme política da clínica.
          </AlertDescription>
        </Alert>
        
        <Card>
          <CardHeader>
            <CardTitle>Confirmar Cancelamento</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="border rounded-md p-4 bg-gray-50">
                <h3 className="font-medium mb-2">Detalhes do Agendamento</h3>
                <p className="text-sm"><span className="font-medium">Profissional:</span> {appointmentDetails.profissionalNome}</p>
                <p className="text-sm"><span className="font-medium">Data/Hora:</span> {new Date(appointmentDetails.dataHoraInicio).toLocaleString('pt-BR')}</p>
                {appointmentDetails.procedimentoNome && (
                  <p className="text-sm"><span className="font-medium">Procedimento:</span> {appointmentDetails.procedimentoNome}</p>
                )}
              </div>
              
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="motivo" className="required">Motivo do Cancelamento</Label>
                  <Textarea 
                    id="motivo" 
                    placeholder="Por favor, informe o motivo do cancelamento"
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                    required
                    rows={4}
                  />
                </div>
              </form>
            </div>
          </CardContent>
          <CardFooter className="flex justify-between">
            <Button 
              variant="outline" 
              onClick={() => navigate('/paciente/meus-agendamentos')}
              disabled={isLoading}
            >
              Voltar
            </Button>
            <Button 
              variant="destructive"
              onClick={handleSubmit}
              disabled={!motivo.trim() || isLoading}
            >
              {isLoading ? "Cancelando..." : "Confirmar Cancelamento"}
            </Button>
          </CardFooter>
        </Card>
      </div>
    </MainLayout>
  );
};

export default CancelAppointmentForm;
