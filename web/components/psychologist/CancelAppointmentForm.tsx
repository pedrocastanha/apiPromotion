import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import AppointmentService from '../../services/appointment.service';

const CancelAppointmentForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [reason, setReason] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    
    if (!reason.trim()) {
      setError('Por favor, informe o motivo do cancelamento.');
      setLoading(false);
      return;
    }
    
    try {
      await AppointmentService.cancelAppointmentByPsychologist({
        agendamentoId: Number(id),
        motivo: reason
      });
      
      setSuccess(true);
      setTimeout(() => {
        navigate('/psicologa/agenda');
      }, 2000);
    } catch (err: any) {
      setError('Erro ao cancelar consulta: ' + (err.response?.data?.mensagem || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden max-w-2xl mx-auto">
      <div className="p-4 border-b border-gray-200">
        <h2 className="text-xl font-semibold text-gray-800">Cancelar Consulta</h2>
      </div>
      
      {error && (
        <div className="p-4 bg-red-100 text-red-700 border-b border-gray-200">
          {error}
        </div>
      )}
      
      {success && (
        <div className="p-4 bg-green-100 text-green-700 border-b border-gray-200">
          Consulta cancelada com sucesso! Redirecionando...
        </div>
      )}
      
      <div className="p-4 bg-yellow-50 text-yellow-700 border-b border-gray-200">
        <p>Atenção: O paciente será notificado sobre este cancelamento por WhatsApp e email.</p>
        <p>Por favor, forneça um motivo claro para o cancelamento.</p>
      </div>
      
      <form onSubmit={handleSubmit} className="p-4">
        <div className="mb-6">
          <label htmlFor="reason" className="block text-sm font-medium text-gray-700 mb-1">
            Motivo do Cancelamento *
          </label>
          <textarea
            id="reason"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            rows={4}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
            placeholder="Por favor, informe o motivo do cancelamento"
          />
        </div>
        
        <div className="flex justify-between">
          <button
            type="button"
            onClick={() => navigate('/psicologa/agenda')}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            Voltar
          </button>
          
          <button
            type="submit"
            disabled={loading || success}
            className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
          >
            {loading ? 'Cancelando...' : 'Confirmar Cancelamento'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CancelAppointmentForm;
