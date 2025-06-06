import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { Checkbox } from '../../components/ui/checkbox';
import { useToast } from '../../hooks/use-toast';
import { useAuth } from '../../hooks/useAuth';

const PsychologistForm: React.FC = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  
  // Form state
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: '',
    cpf: '',
    registroProfissional: '',
    especialidade: '',
    clinicaId: '',
    senha: '',
    confirmarSenha: '',
    aceitaTermos: false
  });
  
  // Clinicas de exemplo - em produção viriam da API
  const clinicas = [
    { id: 1, nome: 'Clínica Central' },
    { id: 2, nome: 'Clínica Norte' },
    { id: 3, nome: 'Clínica Sul' }
  ];
  
  // Especialidades de exemplo
  const especialidades = [
    'Psicologia Clínica',
    'Terapia Cognitivo-Comportamental',
    'Psicanálise',
    'Psicologia Infantil',
    'Neuropsicologia'
  ];
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };
  
  const handleSelectChange = (name: string, value: string) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };
  
  const handleCheckboxChange = (checked: boolean) => {
    setFormData(prev => ({ ...prev, aceitaTermos: checked }));
  };
  
  const validateForm = () => {
    if (!formData.nome || !formData.email || !formData.telefone || !formData.cpf || 
        !formData.registroProfissional || !formData.especialidade || !formData.clinicaId || 
        !formData.senha || !formData.confirmarSenha) {
      toast({
        title: "Campos obrigatórios",
        description: "Por favor, preencha todos os campos obrigatórios.",
        variant: "destructive"
      });
      return false;
    }
    
    if (formData.senha !== formData.confirmarSenha) {
      toast({
        title: "Senhas não conferem",
        description: "A senha e a confirmação de senha devem ser iguais.",
        variant: "destructive"
      });
      return false;
    }
    
    if (!formData.aceitaTermos) {
      toast({
        title: "Termos de uso",
        description: "É necessário aceitar os termos de uso para continuar.",
        variant: "destructive"
      });
      return false;
    }
    
    return true;
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setIsLoading(true);
    
    try {
      // Em produção, isso seria uma chamada à API
      console.log('Enviando dados do profissional:', formData);
      
      // Simula resposta da API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      toast({
        title: "Profissional cadastrado com sucesso",
        description: `${formData.nome} foi cadastrado(a) como profissional.`
      });
      
      // Redireciona para a lista de profissionais
      navigate('/admin/dashboard');
    } catch (error) {
      console.error('Erro ao cadastrar profissional:', error);
      toast({
        title: "Erro no cadastro",
        description: "Não foi possível cadastrar o profissional. Tente novamente mais tarde.",
        variant: "destructive"
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <MainLayout title="Cadastrar Profissional">
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <CardTitle>Novo Profissional</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Dados Pessoais */}
              <div className="space-y-2">
                <Label htmlFor="nome">Nome Completo</Label>
                <Input 
                  id="nome" 
                  name="nome" 
                  value={formData.nome} 
                  onChange={handleChange} 
                  placeholder="Nome completo do profissional"
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="email">E-mail</Label>
                <Input 
                  id="email" 
                  name="email" 
                  type="email" 
                  value={formData.email} 
                  onChange={handleChange} 
                  placeholder="email@exemplo.com"
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="telefone">Telefone</Label>
                <Input 
                  id="telefone" 
                  name="telefone" 
                  value={formData.telefone} 
                  onChange={handleChange} 
                  placeholder="(00) 00000-0000"
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="cpf">CPF</Label>
                <Input 
                  id="cpf" 
                  name="cpf" 
                  value={formData.cpf} 
                  onChange={handleChange} 
                  placeholder="000.000.000-00"
                  required
                />
              </div>
              
              {/* Dados Profissionais */}
              <div className="space-y-2">
                <Label htmlFor="registroProfissional">Registro Profissional</Label>
                <Input 
                  id="registroProfissional" 
                  name="registroProfissional" 
                  value={formData.registroProfissional} 
                  onChange={handleChange} 
                  placeholder="CRP/CRM"
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="especialidade">Especialidade</Label>
                <Select 
                  value={formData.especialidade} 
                  onValueChange={(value) => handleSelectChange('especialidade', value)}
                >
                  <SelectTrigger id="especialidade">
                    <SelectValue placeholder="Selecione uma especialidade" />
                  </SelectTrigger>
                  <SelectContent>
                    {especialidades.map((esp, index) => (
                      <SelectItem key={index} value={esp}>
                        {esp}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="clinica">Clínica</Label>
                <Select 
                  value={formData.clinicaId} 
                  onValueChange={(value) => handleSelectChange('clinicaId', value)}
                >
                  <SelectTrigger id="clinica">
                    <SelectValue placeholder="Selecione uma clínica" />
                  </SelectTrigger>
                  <SelectContent>
                    {clinicas.map((clinica) => (
                      <SelectItem key={clinica.id} value={clinica.id.toString()}>
                        {clinica.nome}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              
              {/* Dados de Acesso */}
              <div className="space-y-2">
                <Label htmlFor="senha">Senha</Label>
                <Input 
                  id="senha" 
                  name="senha" 
                  type="password" 
                  value={formData.senha} 
                  onChange={handleChange} 
                  placeholder="Senha de acesso"
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="confirmarSenha">Confirmar Senha</Label>
                <Input 
                  id="confirmarSenha" 
                  name="confirmarSenha" 
                  type="password" 
                  value={formData.confirmarSenha} 
                  onChange={handleChange} 
                  placeholder="Confirme a senha"
                  required
                />
              </div>
            </div>
            
            {/* Termos de uso */}
            <div className="flex items-center space-x-2">
              <Checkbox 
                id="termos" 
                checked={formData.aceitaTermos} 
                onCheckedChange={handleCheckboxChange} 
              />
              <Label htmlFor="termos" className="text-sm">
                Concordo com os termos de uso e políticas de privacidade
              </Label>
            </div>
          </form>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button 
            variant="outline" 
            onClick={() => navigate('/admin/dashboard')}
            disabled={isLoading}
          >
            Cancelar
          </Button>
          <Button 
            onClick={handleSubmit}
            disabled={isLoading}
          >
            {isLoading ? "Cadastrando..." : "Cadastrar Profissional"}
          </Button>
        </CardFooter>
      </Card>
    </MainLayout>
  );
};

export default PsychologistForm;
