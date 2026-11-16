export interface User {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'OPERADOR' | 'INVESTIDOR' | 'MECANICO' | 'LOCATARIO';
  ativo: boolean;
}

