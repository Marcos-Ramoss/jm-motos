import { User } from './usuario.model';

export interface LoginResponse {
  token: string;
  tokenType: string;
  usuario: User;
}

