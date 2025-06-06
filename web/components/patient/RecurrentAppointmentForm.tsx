import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import UserService from '../../services/user.service';
import AppointmentService from '../../services/appointment.service';
import { format, addDays } from 'date-fns';
import ptBR from 'date-fns/locale/pt-BR';

const RecurrentAppointmentForm: React.FC = () => {
  const [psychologists, setPsychologists] = useState<any[]>([]);
  const [selectedPsychologist, setSelectedPsychologist] = useState<number | ''>('');
  const [appointmentDate, setAppointmentDate] = useState<string>('');
  const [appointmentTime, setAppointmentTime] = useState<string>('');
  const [recurrenceCount, setRecurrenceCount] = useState<number>(4);
  const [observations, setObservations] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const [frequency, setFrequency] = useState<string>('');
  
  const navigate = useNavigate();
  
  // Calcular data mínima (dia seguinte)
  const tomorrow = addDays(new Date(), 1);
  const minDate = format(tomorrow, 'yyyy-MM-dd');

  useEffect(() => {
    const fetchPsychologists = async () => {
      try {
        const data = await UserService.getPsychologists();
        setPsychologists(data);
      } catch (err: any) {
        setError('Erro ao carregar psicólogas: ' + (err.response?.data?.mensagem || err.message));
      }
    };

    const fetchUserFrequency = async () => {
      try {
        const user = await UserService.getCurrentUserDetails();
        setFrequency(user.frequenciaAgendamento || 'VARIADO');
      } catch (err: any) {
        console.error('Erro ao obter frequência do usuário:', err);
        setFrequency('VARIADO');
      }
    };

    fetchPsychologists();
    fetchUserFrequency();
  }, []);

  const getFrequencyText = (freq: string) => {
    switch (freq) {
      case 'SEMANAL':
        return 'semanal (1x por semana)';
      case 'QUINZENAL':
        return 'quinzenal (1x a cada 15 dias)';
      case 'MENSAL':
        return 'mensal (1x por mês)';
      default:
        return 'variada (sem frequência definida)';
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    
    if (!selectedPsychologist || !appointmentDate || !appointmentTime) {
      setError('Por favor, preencha todos os campos obrigatórios.');
      setLoading(false);
      return;
    }
    
    try {
      // Combinar data e hora em um único formato ISO
      const dateTimeString = `${appointmentDate}T${appointmentTime}:00`;
      const appointmentDateTime = new Date(dateTimeString).toISOString();
      
      await AppointmentService.createRecurrentAppointment({
        psicologaId: Number(selectedPsychologist),
        dataHoraInicial: appointmentDateTime,
        quantidadeRecorrencias: recurrenceCount,
        observacoes: observations
      });
      
      setSuccess(true);
      setTimeout(() => {
        navigate('/meus-agendamentos');
      }, 2000);
    } catch (err: any) {
      setError('Erro ao agendar consultas recorrentes: ' + (err.response?.data?.mensagem || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden max-w-2xl mx-auto">
      <div className="p-4 border-b border-gray-200">
        <h2 className="text-xl font-semibold text-gray-800">Agendar Consultas Recorrentes</h2>
      </div>
      
      {error && (
        <div className="p-4 bg-red-100 text-red-700 border-b border-gray-200">
          {error}
        </div>
      )}
      
      {success && (
        <div className="p-4 bg-green-100 text-green-700 border-b border-gray-200">
          Consultas recorrentes agendadas com sucesso! Redirecionando...
        </div>
      )}
      
      <div className="p-4 bg-blue-50 text-blue-700 border-b border-gray-200">
        <p>Sua frequência de atendimento está configurada como <strong>{getFrequencyText(frequency)}</strong>.</p>
        <p>As consultas serão agendadas automaticamente seguindo esta frequência.</p>
      </div>
      
      <form onSubmit={handleSubmit} className="p-4">
        <div className="mb-4">
          <label htmlFor="psychologist" className="block text-sm font-medium text-gray-700 mb-1">
            Psicóloga *
          </label>
          <select
            id="psychologist"
            value={selectedPsychologist}
            onChange={(e) => setSelectedPsychologist(Number(e.target.value))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          >
            <option value="">Selecione uma psicóloga</option>
            {psychologists.map((psych) => (
              <option key={psych.id} value={psych.id}>
                {psych.nome}
              </option>
            ))}
          </select>
        </div>
        
        <div className="mb-4">
          <label htmlFor="date" className="block text-sm font-medium text-gray-700 mb-1">
            Data da Primeira Consulta *
          </label>
          <input
            id="date"
            type="date"
            value={appointmentDate}
            onChange={(e) => setAppointmentDate(e.target.value)}
            min={minDate}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
        
        <div className="mb-4">
          <label htmlFor="time" className="block text-sm font-medium text-gray-700 mb-1">
            Horário das Consultas *
          </label>
          <input
            id="time"
            type="time"
            value={appointmentTime}
            onChange={(e) => setAppointmentTime(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
        
        <div className="mb-4">
          <label htmlFor="recurrenceCount" className="block text-sm font-medium text-gray-700 mb-1">
            Quantidade de Consultas *
          </label>
          <input
            id="recurrenceCount"
            type="number"
            min="2"
            max="12"
            value={recurrenceCount}
            onChange={(e) => setRecurrenceCount(Number(e.target.value))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
          <p className="mt-1 text-sm text-gray-500">
            Número de consultas a serem agendadas (entre 2 e 12)
          </p>
        </div>
        
        <div className="mb-6">
          <label htmlFor="observations" className="block text-sm font-medium text-gray-700 mb-1">
            Observações
          </label>
          <textarea
            id="observations"
            value={observations}
            onChange={(e) => setObservations(e.target.value)}
            rows={3}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        
        <div className="flex justify-between">
          <button
            type="button"
            onClick={() => navigate('/meus-agendamentos')}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            Cancelar
          </button>
          
          <button
            type="submit"
            disabled={loading || success}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
          >
            {loading ? 'Agendando...' : 'Agendar Consultas Recorrentes'}
          </button>
        </div>
        
        <div className="mt-4 text-center">
          <a href="/novo-agendamento" className="text-sm text-blue-600 hover:text-blue-800">
            Deseja agendar apenas uma consulta? Clique aqui
          </a>
        </div>
      </form>
    </div>
  );
};

export default RecurrentAppointmentForm;
