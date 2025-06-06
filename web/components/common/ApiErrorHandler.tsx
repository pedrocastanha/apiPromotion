import React from 'react';
import { useToast } from '../hooks/use-toast';

// Componente de alto nível para tratamento de erros de API
const ApiErrorHandler: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { toast } = useToast();

  // Função para tratar erros comuns de API
  const handleApiError = (error: any) => {
    console.error('API Error:', error);
    
    // Extrai a mensagem de erro da resposta da API, se disponível
    const errorMessage = error.response?.data?.message || 
                         error.response?.data?.error || 
                         'Ocorreu um erro na comunicação com o servidor.';
    
    // Trata diferentes códigos de status HTTP
    switch (error.response?.status) {
      case 400:
        toast({
          title: "Dados inválidos",
          description: errorMessage,
          variant: "destructive"
        });
        break;
      case 401:
        toast({
          title: "Não autorizado",
          description: "Sua sessão expirou ou você não tem permissão para acessar este recurso.",
          variant: "destructive"
        });
        // Redireciona para login se não estiver na página de login
        if (window.location.pathname !== '/login') {
          setTimeout(() => {
            window.location.href = '/login';
          }, 2000);
        }
        break;
      case 403:
        toast({
          title: "Acesso negado",
          description: "Você não tem permissão para realizar esta ação.",
          variant: "destructive"
        });
        break;
      case 404:
        toast({
          title: "Não encontrado",
          description: "O recurso solicitado não foi encontrado.",
          variant: "destructive"
        });
        break;
      case 409:
        toast({
          title: "Conflito",
          description: errorMessage,
          variant: "destructive"
        });
        break;
      case 422:
        toast({
          title: "Validação falhou",
          description: errorMessage,
          variant: "destructive"
        });
        break;
      case 500:
      case 502:
      case 503:
      case 504:
        toast({
          title: "Erro no servidor",
          description: "Ocorreu um erro no servidor. Por favor, tente novamente mais tarde.",
          variant: "destructive"
        });
        break;
      default:
        toast({
          title: "Erro",
          description: errorMessage,
          variant: "destructive"
        });
    }
  };

  // Adiciona o manipulador de erros ao objeto window para uso global
  React.useEffect(() => {
    (window as any).handleApiError = handleApiError;
    
    return () => {
      delete (window as any).handleApiError;
    };
  }, []);

  return <>{children}</>;
};

export default ApiErrorHandler;
